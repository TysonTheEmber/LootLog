package dev.tysontheember.lootlog.neoforge;

import dev.tysontheember.lootlog.RenderBridge;
import dev.tysontheember.lootlog.TextEffectBridge;
import net.tysontheember.emberstextapi.immersivemessages.api.MarkupParser;
import net.tysontheember.emberstextapi.immersivemessages.api.TextSpan;
import net.tysontheember.emberstextapi.immersivemessages.effects.Effect;
import net.tysontheember.emberstextapi.immersivemessages.effects.EffectContext;
import net.tysontheember.emberstextapi.immersivemessages.effects.EffectSettings;

import java.util.List;

/**
 * EmbersTextAPI-backed text effect rendering.
 * Only loaded by the JVM when EmbersTextAPI is detected at runtime.
 */
public class EmbersTextEffectBridge implements TextEffectBridge {

    @Override
    public int renderMarkupText(RenderBridge bridge, String markup,
                                 int x, int y, int baseColor, boolean shadow, long timeMs) {
        List<TextSpan> spans = MarkupParser.parse(markup);
        int cursorX = x;

        for (TextSpan span : spans) {
            String content = span.getContent();
            if (content == null || content.isEmpty()) continue;

            List<Effect> effects = span.getEffects();

            int spanColor = baseColor;
            if (span.getColor() != null) {
                spanColor = span.getColor().getValue() | 0xFF000000;
            }

            if (effects == null || effects.isEmpty()) {
                bridge.renderText(content, cursorX, y, spanColor, shadow);
                cursorX += bridge.getTextWidth(content);
            } else {
                float baseR = ((spanColor >> 16) & 0xFF) / 255f;
                float baseG = ((spanColor >> 8) & 0xFF) / 255f;
                float baseB = (spanColor & 0xFF) / 255f;
                float baseA = ((spanColor >> 24) & 0xFF) / 255f;

                for (int i = 0; i < content.length(); i++) {
                    String ch = String.valueOf(content.charAt(i));

                    EffectSettings settings = new EffectSettings();
                    settings.r = baseR;
                    settings.g = baseG;
                    settings.b = baseB;
                    settings.a = baseA;
                    settings.index = i;
                    settings.absoluteIndex = i;
                    settings.codepoint = content.charAt(i);
                    settings.isShadow = false;

                    EffectContext.applyEffects(effects, settings);

                    int effColor = packColor(settings.r, settings.g, settings.b, settings.a);
                    int charWidth = bridge.getTextWidth(ch);

                    if (settings.x != 0 || settings.y != 0 || settings.rot != 0
                            || settings.scale != 1.0f) {
                        bridge.pushPose();
                        bridge.translate(cursorX + settings.x, y + settings.y, 0);
                        if (settings.scale != 1.0f) {
                            bridge.scale(settings.scale, settings.scale, 1.0f);
                        }
                        bridge.renderText(ch, 0, 0, effColor, shadow);
                        bridge.popPose();
                    } else {
                        bridge.renderText(ch, cursorX, y, effColor, shadow);
                    }

                    cursorX += charWidth;
                }
            }
        }
        return cursorX - x;
    }

    @Override
    public int getMarkupTextWidth(RenderBridge bridge, String markup) {
        return bridge.getTextWidth(stripMarkup(markup));
    }

    @Override
    public String stripMarkup(String markup) {
        List<TextSpan> spans = MarkupParser.parse(markup);
        StringBuilder sb = new StringBuilder();
        for (TextSpan span : spans) {
            if (span.getContent() != null) {
                sb.append(span.getContent());
            }
        }
        return sb.toString();
    }

    private static int packColor(float r, float g, float b, float a) {
        int ri = Math.round(Math.max(0, Math.min(1, r)) * 255) & 0xFF;
        int gi = Math.round(Math.max(0, Math.min(1, g)) * 255) & 0xFF;
        int bi = Math.round(Math.max(0, Math.min(1, b)) * 255) & 0xFF;
        int ai = Math.round(Math.max(0, Math.min(1, a)) * 255) & 0xFF;
        return (ai << 24) | (ri << 16) | (gi << 8) | bi;
    }
}
