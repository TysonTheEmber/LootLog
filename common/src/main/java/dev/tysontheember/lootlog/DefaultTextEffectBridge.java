package dev.tysontheember.lootlog;

import java.util.regex.Pattern;

/**
 * Default TextEffectBridge that strips markup tags and renders plain text.
 * Used when EmbersTextAPI is not installed.
 */
public class DefaultTextEffectBridge implements TextEffectBridge {

    private static final Pattern TAG_PATTERN = Pattern.compile("</?[a-zA-Z][^>]*>");

    @Override
    public int renderMarkupText(RenderBridge bridge, String markup,
                                 int x, int y, int baseColor, boolean shadow, long timeMs) {
        String plain = stripMarkup(markup);
        bridge.renderText(plain, x, y, baseColor, shadow);
        return bridge.getTextWidth(plain);
    }

    @Override
    public int getMarkupTextWidth(RenderBridge bridge, String markup) {
        return bridge.getTextWidth(stripMarkup(markup));
    }

    @Override
    public String stripMarkup(String markup) {
        return TAG_PATTERN.matcher(markup).replaceAll("");
    }
}
