package dev.tysontheember.lootlog;

import java.util.ArrayList;
import java.util.List;

/**
 * Unified popup renderer with two layout paths:
 * <ul>
 *   <li><b>Banner path</b> (BANNER, FLAT): multi-layer textures at native size,
 *       elements positioned via configurable insets, extends to screen edge.</li>
 *   <li><b>Standard path</b> (SOLID, TOOLTIP, TEXTURE, NONE): content-sized
 *       background with padding.</li>
 * </ul>
 */
public final class PopupRenderer {

    private PopupRenderer() {}

    /** Render a single popup entry. */
    public static void render(PickupEntry entry, int index,
                              LootLogConfig config, RenderBridge bridge,
                              HudAnchor anchor, PopupLayout layout,
                              BackgroundStyle bgStyle, TextureSpec textureSpec,
                              int screenW, int screenH,
                              float alpha, float totalSlide,
                              long timeMs, long entryAgeMs,
                              LayoutPreset preset) {
        ResolvedOverride override = entry.getOverride();
        boolean reversed = HudLayout.isRightAnchor(anchor);

        // --- Resolve effective background style ---
        BackgroundStyle effectiveBgStyle = bgStyle;
        if (override != null && override.getBackgroundStyle() != null) {
            effectiveBgStyle = override.getBackgroundStyle();
        }

        // --- Format text ---
        // Standard layout uses combined left/right text; banner uses separate elements
        boolean isBannerPath = (override != null && override.getBackgroundStyle() != null)
                ? (override.getBackgroundStyle() == BackgroundStyle.BANNER
                        || override.getBackgroundStyle() == BackgroundStyle.FLAT)
                : (bgStyle == BackgroundStyle.BANNER || bgStyle == BackgroundStyle.FLAT);

        boolean hasMarkup = override != null && override.getNameMarkup() != null;
        String markupResolved = null;
        if (hasMarkup) {
            markupResolved = override.getNameMarkup().replace("{name}", entry.getDisplayName());
        }

        // Banner-specific text: separate pickup count, name, total count
        String pickupCountText = "";
        int pickupCountWidth = 0;
        String itemNameText = "";
        int itemNameWidth = 0;
        String totalCountText = "";
        int totalCountWidth = 0;

        // Standard layout text (combined)
        String nameText = "";
        int nameWidth = 0;
        String countText = "";
        int countTextWidth = 0;

        if (isBannerPath) {
            pickupCountText = TextFormatter.formatPickupCount(entry, config);
            pickupCountWidth = pickupCountText.isEmpty() ? 0 : bridge.getTextWidth(pickupCountText);

            itemNameText = TextFormatter.formatItemName(entry);
            itemNameText = TextFormatter.truncateName(itemNameText, config.getMaxNameWidth(), bridge);
            if (hasMarkup) {
                TextEffectBridge textBridge = LootLog.getTextEffectBridge();
                itemNameWidth = textBridge.getMarkupTextWidth(bridge, markupResolved);
            } else {
                itemNameWidth = bridge.getTextWidth(itemNameText);
            }

            totalCountText = TextFormatter.formatTotalCount(entry, config);
            totalCountWidth = totalCountText.isEmpty() ? 0 : bridge.getTextWidth(totalCountText);
        } else {
            nameText = TextFormatter.formatLeftText(entry, config);
            nameText = TextFormatter.truncateName(nameText, config.getMaxNameWidth(), bridge);
            if (hasMarkup && layout.getItemName().isEnabled()) {
                TextEffectBridge textBridge = LootLog.getTextEffectBridge();
                nameWidth = textBridge.getMarkupTextWidth(bridge, markupResolved);
            } else if (layout.getItemName().isEnabled()) {
                nameWidth = bridge.getTextWidth(nameText);
            }

            countText = layout.getTotalCount().isEnabled()
                    ? TextFormatter.formatRightText(entry, config) : "";
            countTextWidth = countText.isEmpty() ? 0 : bridge.getTextWidth(countText);
        }

        // --- Resolve texture ---
        String texturePath = textureSpec.getTexturePath();
        if (override != null && override.getBackgroundTexture() != null) {
            texturePath = override.getBackgroundTexture();
        }

        // --- Compute effect modifiers ---
        EffectModifiers mods = EffectModifiers.IDENTITY;

        // --- Colors ---
        int textColor = ColorUtil.applyAlpha(
                ColorUtil.resolveTextColor(entry, config, timeMs), alpha);
        int countColor = ColorUtil.applyAlpha(config.getCountColor(), alpha);

        // --- Screen positioning (height and spacing scaled to match rendered size) ---
        int scaledHeight = Math.round(layout.getEntryHeight() * config.getScale());
        int scaledSpacing = Math.round(config.getEntrySpacing() * config.getScale());
        int baseY = anchor.anchorY(screenH, index, scaledHeight,
                scaledSpacing, config.getYOffset(), config.getGrowthDirection());
        float renderY = baseY + entry.getVerticalOffset();

        float slideX = reversed ? totalSlide : -totalSlide;

        // --- Apply transforms ---
        bridge.pushPose();

        boolean isBannerLayout = isBannerPath;

        if (isBannerLayout) {
            float originX = reversed
                    ? screenW - config.getXOffset() + slideX
                    : config.getXOffset() + slideX;
            bridge.translate(originX, renderY, 0);
        } else {
            int entryWidth = computeStandardEntryWidth(layout, nameWidth, countTextWidth);
            int baseX = anchor.anchorX(screenW, entryWidth, config.getXOffset());
            float renderX = baseX + slideX;
            if (config.isClampToScreen()) {
                renderX = Math.max(0, Math.min(renderX, screenW - entryWidth));
                renderY = Math.max(0, Math.min(renderY, screenH - layout.getEntryHeight()));
            }
            bridge.translate(renderX, renderY, 0);
        }

        // --- Per-part pickup pulse ---
        float pulseDuration = IconEffectRenderer.resolvePulseDuration(config, override);
        long bounceStart = entry.getBounceStartMs();

        float nameScale = 1.0f, nameAlpha = 1.0f;
        float totalCountScale = 1.0f, totalCountAlpha = 1.0f;
        float bodyScale = 1.0f, bodyAlpha = 1.0f;
        float accentScale = 1.0f, accentAlpha = 1.0f;
        float overallScale = 1.0f, overallAlpha = 1.0f;

        if (config.isPickupPulseEnabled()) {
            nameScale = IconEffectRenderer.computePulse(bounceStart,
                    resolveStrength(config.getPickupPulseNameScaleStrength(), override != null ? override.getPickupPulseNameScaleStrength() : null), pulseDuration);
            nameAlpha = IconEffectRenderer.computePulse(bounceStart,
                    resolveStrength(config.getPickupPulseNameAlphaStrength(), override != null ? override.getPickupPulseNameAlphaStrength() : null), pulseDuration);
            totalCountScale = IconEffectRenderer.computePulse(bounceStart,
                    resolveStrength(config.getPickupPulseTotalCountScaleStrength(), override != null ? override.getPickupPulseTotalCountScaleStrength() : null), pulseDuration);
            totalCountAlpha = IconEffectRenderer.computePulse(bounceStart,
                    resolveStrength(config.getPickupPulseTotalCountAlphaStrength(), override != null ? override.getPickupPulseTotalCountAlphaStrength() : null), pulseDuration);
            bodyScale = IconEffectRenderer.computePulse(bounceStart,
                    resolveStrength(config.getPickupPulseBodyScaleStrength(), override != null ? override.getPickupPulseBodyScaleStrength() : null), pulseDuration);
            bodyAlpha = IconEffectRenderer.computePulse(bounceStart,
                    resolveStrength(config.getPickupPulseBodyAlphaStrength(), override != null ? override.getPickupPulseBodyAlphaStrength() : null), pulseDuration);
            accentScale = IconEffectRenderer.computePulse(bounceStart,
                    resolveStrength(config.getPickupPulseAccentScaleStrength(), override != null ? override.getPickupPulseAccentScaleStrength() : null), pulseDuration);
            accentAlpha = IconEffectRenderer.computePulse(bounceStart,
                    resolveStrength(config.getPickupPulseAccentAlphaStrength(), override != null ? override.getPickupPulseAccentAlphaStrength() : null), pulseDuration);
            overallScale = IconEffectRenderer.computePulse(bounceStart,
                    resolveStrength(config.getPickupPulseOverallScaleStrength(), override != null ? override.getPickupPulseOverallScaleStrength() : null), pulseDuration);
            overallAlpha = IconEffectRenderer.computePulse(bounceStart,
                    resolveStrength(config.getPickupPulseOverallAlphaStrength(), override != null ? override.getPickupPulseOverallAlphaStrength() : null), pulseDuration);
        }

        // Apply overall pulse
        alpha *= overallAlpha;

        // --- Scale ---
        float scale = computeScale(config, override, timeMs, entryAgeMs) * overallScale;
        if (scale != 1.0f) {
            bridge.scale(scale, scale, 1.0f);
        }

        // --- Compute content width for effects ---
        if (override != null && !override.getBackgroundEffects().isEmpty()) {
            int contentW;
            if (isBannerLayout) {
                int[] widths = {pickupCountWidth, itemNameWidth, PopupLayout.ICON_SIZE, totalCountWidth};
                contentW = computeBannerContentWidth(config, widths);
            } else {
                contentW = computeStandardEntryWidth(layout, nameWidth, countTextWidth);
            }
            mods = EffectComputer.compute(override.getBackgroundEffects(),
                    alpha, timeMs, entryAgeMs, contentW, layout.getEntryHeight());
        }

        // --- Dispatch to layout-specific rendering ---
        if (isBannerLayout) {
            renderBanner(bridge, entry, effectiveBgStyle, texturePath, textureSpec,
                    pickupCountText, itemNameText, totalCountText,
                    markupResolved, hasMarkup,
                    pickupCountWidth, itemNameWidth, totalCountWidth,
                    textColor, countColor, alpha, timeMs,
                    config, override, mods,
                    nameScale, nameAlpha, totalCountScale, totalCountAlpha,
                    bodyScale, bodyAlpha, accentScale, accentAlpha,
                    layout, reversed);
        } else {
            renderStandard(bridge, entry, effectiveBgStyle, texturePath, textureSpec,
                    nameText, countText, markupResolved,
                    hasMarkup && layout.getItemName().isEnabled(),
                    nameWidth, countTextWidth, textColor, countColor, alpha, timeMs,
                    config, override, mods,
                    nameScale, nameAlpha, totalCountScale, totalCountAlpha,
                    bodyScale, bodyAlpha,
                    layout, preset);
        }

        // --- Progress bar ---
        int bannerTotalWidth = 0;
        if (isBannerLayout) {
            int[] widths = {pickupCountWidth, itemNameWidth, PopupLayout.ICON_SIZE, totalCountWidth};
            bannerTotalWidth = computeBannerContentWidth(config, widths);
        }
        if (config.isShowProgressBar()) {
            renderProgressBar(bridge, entry, config, override, entryAgeMs, alpha, layout,
                    isBannerLayout, reversed,
                    isBannerLayout ? bannerTotalWidth : 0,
                    nameWidth, countTextWidth);
        }

        bridge.popPose();
    }

    // ======================================================================
    // Banner path (BANNER, FLAT)
    // ======================================================================

    /** Identifiers for banner content elements. */
    private enum BannerElement { PICKUP_COUNT, NAME, ICON, TOTAL_COUNT }

    private static void renderBanner(
            RenderBridge bridge, PickupEntry entry,
            BackgroundStyle effectiveBgStyle,
            String texturePath, TextureSpec tex,
            String pickupCountText, String itemNameText, String totalCountText,
            String markupResolved, boolean hasMarkup,
            int pickupCountWidth, int itemNameWidth, int totalCountWidth,
            int textColor, int countColor,
            float alpha, long timeMs,
            LootLogConfig config, ResolvedOverride override,
            EffectModifiers mods,
            float nameScale, float nameAlpha,
            float totalCountScale, float totalCountAlpha,
            float bodyScale, float bodyAlpha,
            float accentScale, float accentAlpha,
            PopupLayout layout, boolean reversed) {

        int entryHeight = layout.getEntryHeight();
        int inset = config.getDecorativeEdgeInset();
        int gap = config.getIconToNameGap();
        boolean shadow = config.isTextShadow();

        // Vertical centering
        int centerY = entryHeight / 2;
        int iconY = centerY - PopupLayout.ICON_SIZE / 2;
        int textY = iconY + (PopupLayout.ICON_SIZE - PopupLayout.FONT_HEIGHT + 1) / 2;

        // Parse element order
        List<BannerElement> order = parseElementOrder(config.getBannerElementOrder());

        // Compute width of each element
        int[] elemWidths = new int[order.size()];
        for (int i = 0; i < order.size(); i++) {
            switch (order.get(i)) {
                case PICKUP_COUNT: elemWidths[i] = pickupCountWidth; break;
                case NAME:         elemWidths[i] = itemNameWidth; break;
                case ICON:         elemWidths[i] = PopupLayout.ICON_SIZE; break;
                case TOTAL_COUNT:  elemWidths[i] = totalCountWidth; break;
            }
        }

        // Position elements. Order is decorativeEdge → screenEdge.
        // For right-anchored: last element nearest origin (X≈0), first element most negative.
        // For left-anchored: last element furthest right, first element nearest origin (X≈0).
        int[] elemX = new int[order.size()];
        int bannerX;

        // Named positions for layer anchoring
        int iconAnchorX = 0, nameAnchorX = 0, countAnchorX = 0;

        if (reversed) {
            int cursor = 0;
            for (int i = order.size() - 1; i >= 0; i--) {
                if (elemWidths[i] <= 0 && order.get(i) != BannerElement.ICON) {
                    elemX[i] = cursor;
                    continue;
                }
                cursor -= elemWidths[i];
                elemX[i] = cursor;
                if (i > 0) cursor -= gap;
            }
            bannerX = cursor + (order.size() > 0 ? gap : 0) - inset;
            // Adjust: if first element had width, bannerX should be firstElem.x - inset
            if (order.size() > 0 && (elemWidths[0] > 0 || order.get(0) == BannerElement.ICON)) {
                bannerX = elemX[0] - inset;
            }
        } else {
            int cursor = 0;
            for (int i = order.size() - 1; i >= 0; i--) {
                if (elemWidths[i] <= 0 && order.get(i) != BannerElement.ICON) {
                    elemX[i] = cursor;
                    continue;
                }
                elemX[i] = cursor;
                cursor += elemWidths[i];
                if (i > 0) cursor += gap;
            }
            bannerX = cursor - (order.size() > 0 ? gap : 0) + inset;
            if (order.size() > 0 && (elemWidths[0] > 0 || order.get(0) == BannerElement.ICON)) {
                bannerX = elemX[0] + elemWidths[0] + inset;
                // Recompute: decorative edge is past the first element
                int lastIdx = -1;
                for (int i = 0; i < order.size(); i++) {
                    if (elemWidths[i] > 0 || order.get(i) == BannerElement.ICON) lastIdx = i;
                }
                if (lastIdx >= 0) {
                    bannerX = elemX[lastIdx] + elemWidths[lastIdx] + inset;
                }
            }
        }

        // Record named positions for layer anchoring.
        // For right-anchored: anchor is the element's left edge (decorative edge faces left).
        // For left-anchored: anchor is the element's RIGHT edge (decorative edge faces right).
        for (int i = 0; i < order.size(); i++) {
            int rightEdge = reversed ? elemX[i] : elemX[i] + elemWidths[i];
            switch (order.get(i)) {
                case ICON:         iconAnchorX = rightEdge; break;
                case NAME:         nameAnchorX = rightEdge; break;
                case PICKUP_COUNT:
                case TOTAL_COUNT:  countAnchorX = rightEdge; break;
            }
        }

        // --- Background layers ---
        if (effectiveBgStyle == BackgroundStyle.BANNER) {
            List<BannerLayer> layers = resolveBannerLayers(config, override);

            if (layers != null && !layers.isEmpty()) {
                for (int i = 0; i < layers.size(); i++) {
                    BannerLayer layer = layers.get(i);
                    if (!layer.isVisible()) continue;

                    BannerLayer effectiveLayer = applyConfigOverrides(layer, i, config, reversed);

                    int layerH = effectiveLayer.getTexture().getRenderHeight();
                    int layerY = centerY - layerH / 2 + effectiveLayer.getYOffset();

                    int anchorPos = resolveAnchorX(effectiveLayer.getAnchor(),
                            reversed, bannerX, iconAnchorX, nameAnchorX, countAnchorX);
                    int layerX;
                    if (reversed) {
                        layerX = anchorPos + effectiveLayer.getXOffset();
                    } else {
                        int layerW = effectiveLayer.getTexture().getRenderWidth();
                        layerX = anchorPos - layerW + effectiveLayer.getXOffset();
                    }

                    // Apply per-layer pickup pulse (body = layer 0, accent = layer 1)
                    float layerScale = (i == 0) ? bodyScale : (i == 1) ? accentScale : 1.0f;
                    float layerAlphaMul = (i == 0) ? bodyAlpha : (i == 1) ? accentAlpha : 1.0f;

                    if (layerScale != 1.0f) {
                        int layerW = effectiveLayer.getTexture().getRenderWidth();
                        float cx = layerX + layerW / 2f;
                        float cy = layerY + layerH / 2f;
                        bridge.pushPose();
                        bridge.translate(cx, cy, 0);
                        bridge.scale(layerScale, layerScale, 1.0f);
                        bridge.translate(-cx, -cy, 0);
                        BackgroundRenderer.renderBannerLayer(bridge, effectiveLayer,
                                layerX, layerY, alpha * layerAlphaMul, timeMs, !reversed);
                        bridge.popPose();
                    } else {
                        BackgroundRenderer.renderBannerLayer(bridge, effectiveLayer,
                                layerX, layerY, alpha * layerAlphaMul, timeMs, !reversed);
                    }
                }
            }
        } else {
            // FLAT
            float flatAlpha = alpha * config.getBodyAlpha() * bodyAlpha;
            int flatColor = ColorUtil.multiplyAlpha(config.getBackgroundColor(), flatAlpha);
            flatColor = mods.applyBody(flatColor);

            if (reversed) {
                bridge.renderRect(bannerX, 0, -bannerX, entryHeight, flatColor);
            } else {
                bridge.renderRect(0, 0, bannerX, entryHeight, flatColor);
            }
        }

        // --- Content elements ---
        for (int i = 0; i < order.size(); i++) {
            int x = elemX[i];
            switch (order.get(i)) {
                case PICKUP_COUNT:
                    if (pickupCountWidth > 0) {
                        int pcColor = applyAlphaPulse(countColor, totalCountAlpha);
                        renderTextWithBounce(bridge, pickupCountText, x, textY,
                                pickupCountWidth, pcColor, shadow, totalCountScale);
                    }
                    break;
                case NAME:
                    if (itemNameWidth > 0) {
                        if (hasMarkup) {
                            TextEffectBridge textBridge = LootLog.getTextEffectBridge();
                            textBridge.renderMarkupText(bridge, markupResolved,
                                    x, textY, textColor, shadow, timeMs);
                        } else {
                            int nmColor = applyAlphaPulse(textColor, nameAlpha);
                            renderTextWithBounce(bridge, itemNameText, x, textY, itemNameWidth,
                                    nmColor, shadow, nameScale);
                        }
                    }
                    break;
                case ICON:
                    HudLayout.renderIcon(bridge, entry, x, iconY, alpha, timeMs, config);
                    break;
                case TOTAL_COUNT:
                    if (totalCountWidth > 0) {
                        int tcColor = applyAlphaPulse(countColor, totalCountAlpha);
                        renderTextWithBounce(bridge, totalCountText, x, textY,
                                totalCountWidth, tcColor, shadow, totalCountScale);
                    }
                    break;
            }
        }
    }

    private static List<BannerElement> parseElementOrder(String orderStr) {
        List<BannerElement> result = new ArrayList<>();
        if (orderStr == null || orderStr.isEmpty()) {
            result.add(BannerElement.PICKUP_COUNT);
            result.add(BannerElement.NAME);
            result.add(BannerElement.ICON);
            result.add(BannerElement.TOTAL_COUNT);
            return result;
        }
        for (String part : orderStr.split(",")) {
            String trimmed = part.trim().toUpperCase();
            try {
                result.add(BannerElement.valueOf(trimmed));
            } catch (IllegalArgumentException ignored) {}
        }
        if (result.isEmpty()) {
            result.add(BannerElement.PICKUP_COUNT);
            result.add(BannerElement.NAME);
            result.add(BannerElement.ICON);
            result.add(BannerElement.TOTAL_COUNT);
        }
        return result;
    }

    // ======================================================================
    // Standard path (SOLID, TOOLTIP, TEXTURE, NONE)
    // ======================================================================

    private static void renderStandard(
            RenderBridge bridge, PickupEntry entry,
            BackgroundStyle effectiveBgStyle,
            String texturePath, TextureSpec tex,
            String nameText, String countText,
            String markupResolved, boolean hasMarkup,
            int nameWidth, int countTextWidth,
            int textColor, int countColor,
            float alpha, long timeMs,
            LootLogConfig config, ResolvedOverride override,
            EffectModifiers mods,
            float nameScale, float nameAlpha,
            float totalCountScale, float totalCountAlpha,
            float bodyScale, float bodyAlpha,
            PopupLayout layout, LayoutPreset preset) {

        boolean bgEnabled = effectiveBgStyle != BackgroundStyle.NONE;
        int pad = layout.getPadding();
        int textY = layout.getItemName().getOffsetY();
        int iconY = layout.getIcon().getOffsetY();
        int entryWidth = computeStandardEntryWidth(layout, nameWidth, countTextWidth);

        // --- Background ---
        float bgAlpha = alpha * bodyAlpha;
        if (bgEnabled) {
            String customTexture = (override != null && override.getBackgroundTexture() != null)
                    ? override.getBackgroundTexture() : config.getDecorationTexture();

            boolean needsScalePulse = bodyScale != 1.0f;
            if (needsScalePulse) {
                float cx = entryWidth / 2f;
                float cy = layout.getEntryHeight() / 2f;
                bridge.pushPose();
                bridge.translate(cx, cy, 0);
                bridge.scale(bodyScale, bodyScale, 1.0f);
                bridge.translate(-cx, -cy, 0);
            }

            if (effectiveBgStyle == BackgroundStyle.TEXTURE ||
                    (customTexture != null && effectiveBgStyle != BackgroundStyle.SOLID
                            && effectiveBgStyle != BackgroundStyle.TOOLTIP)) {
                String bgTex = customTexture != null ? customTexture : texturePath;
                ResolvedOverride.FrameAnimation anim = override != null ? override.getFrameAnimation() : null;
                BackgroundRenderer.renderCustomTextured(bridge, tex, bgTex,
                        0, 0, entryWidth, layout.getEntryHeight(),
                        bgAlpha, timeMs, anim, mods);
            } else {
                BackgroundRenderer.render(bridge, effectiveBgStyle, 0, 0,
                        entryWidth, layout.getEntryHeight(),
                        config.getBackgroundColor(), bgAlpha, mods);
            }

            // Background color tint overlay
            if (override != null && override.getBackgroundColor() != null
                    && override.getBackgroundTexture() == null) {
                bridge.renderRect(0, 0, entryWidth, layout.getEntryHeight(),
                        ColorUtil.multiplyAlpha(override.getBackgroundColor(), bgAlpha));
            }

            if (needsScalePulse) {
                bridge.popPose();
            }
        }

        // --- Elements ---
        boolean iconRight = (preset == LayoutPreset.STANDARD_RIGHT);
        int nmColor = applyAlphaPulse(textColor, nameAlpha);
        int tcColor = applyAlphaPulse(countColor, totalCountAlpha);

        if (iconRight) {
            int cursorX = pad;

            if (layout.getItemName().isEnabled() && nameWidth > 0) {
                if (hasMarkup) {
                    TextEffectBridge textBridge = LootLog.getTextEffectBridge();
                    textBridge.renderMarkupText(bridge, markupResolved,
                            cursorX, textY, textColor, config.isTextShadow(), timeMs);
                } else {
                    renderTextWithBounce(bridge, nameText, cursorX, textY, nameWidth,
                            nmColor, config.isTextShadow(), nameScale);
                }
                cursorX += nameWidth + PopupLayout.ICON_TEXT_GAP;
            }

            if (layout.getIcon().isEnabled()) {
                HudLayout.renderIcon(bridge, entry, cursorX, iconY, alpha, timeMs, config);
                cursorX += PopupLayout.ICON_SIZE;
            }

            if (layout.getTotalCount().isEnabled() && !countText.isEmpty()) {
                cursorX += PopupLayout.ICON_TEXT_GAP;
                renderTextWithBounce(bridge, countText, cursorX, textY,
                        countTextWidth, tcColor, config.isTextShadow(), totalCountScale);
            }
        } else {
            int cursorX = pad;

            if (layout.getIcon().isEnabled()) {
                HudLayout.renderIcon(bridge, entry, cursorX, iconY, alpha, timeMs, config);
                cursorX += PopupLayout.ICON_SIZE + PopupLayout.ICON_TEXT_GAP;
            }

            if (layout.getItemName().isEnabled() && nameWidth > 0) {
                if (hasMarkup) {
                    TextEffectBridge textBridge = LootLog.getTextEffectBridge();
                    textBridge.renderMarkupText(bridge, markupResolved,
                            cursorX, textY, textColor, config.isTextShadow(), timeMs);
                } else {
                    renderTextWithBounce(bridge, nameText, cursorX, textY, nameWidth,
                            nmColor, config.isTextShadow(), nameScale);
                }
            }
        }
    }

    // ======================================================================
    // Progress bar
    // ======================================================================

    private static void renderProgressBar(RenderBridge bridge, PickupEntry entry,
                                           LootLogConfig config, ResolvedOverride override,
                                           long entryAgeMs, float alpha,
                                           PopupLayout layout, boolean isBannerLayout,
                                           boolean reversed, int bannerContentW,
                                           int nameWidth, int countTextWidth) {
        long displayMs = config.getDisplayDurationMs();
        if (override != null && override.getDisplayDurationMs() != null) {
            displayMs = override.getDisplayDurationMs();
        }
        long totalMs = config.getFadeInMs() + displayMs + config.getFadeOutMs();
        float progress = Math.max(0, 1.0f - (float) entryAgeMs / totalMs);

        int barH = config.getProgressBarHeight();
        int barColor = ColorUtil.applyAlpha(config.getProgressBarColor(), alpha);

        if (isBannerLayout) {
            int barW = Math.round(bannerContentW * progress);
            int barX = reversed ? -bannerContentW : 0;
            bridge.renderRect(barX, layout.getEntryHeight() - barH, barW, barH, barColor);
        } else {
            int entryWidth = computeStandardEntryWidth(layout, nameWidth, countTextWidth);
            int barW = Math.round(entryWidth * progress);
            bridge.renderRect(0, layout.getEntryHeight() - barH, barW, barH, barColor);
        }
    }

    // ======================================================================
    // Width computation
    // ======================================================================

    private static int computeBannerContentWidth(LootLogConfig config, int[] elemWidths) {
        int gap = config.getIconToNameGap();
        int w = config.getDecorativeEdgeInset();
        boolean first = true;
        for (int ew : elemWidths) {
            if (ew > 0) {
                if (!first) w += gap;
                w += ew;
                first = false;
            }
        }
        return w;
    }

    private static int computeStandardEntryWidth(PopupLayout layout,
                                                  int nameWidth, int countTextWidth) {
        int pad = layout.getPadding();
        int w = pad;
        if (layout.getItemName().isEnabled() && nameWidth > 0) {
            w += nameWidth + PopupLayout.ICON_TEXT_GAP;
        }
        if (layout.getIcon().isEnabled()) {
            w += PopupLayout.ICON_SIZE;
        }
        if (layout.getTotalCount().isEnabled() && countTextWidth > 0) {
            w += PopupLayout.ICON_TEXT_GAP + countTextWidth;
        }
        w += pad;
        return w;
    }

    // ======================================================================
    // Banner layer resolution
    // ======================================================================

    private static List<BannerLayer> resolveBannerLayers(LootLogConfig config,
                                                          ResolvedOverride override) {
        // Override layers take precedence
        if (override != null && override.getLayers() != null && !override.getLayers().isEmpty()) {
            return override.getLayers();
        }

        // Config decoration
        String decoName = config.getDecoration();
        if (decoName != null) {
            Decoration deco = Decoration.byName(decoName);
            if (deco != null) return deco.getLayers();
        }

        // Default
        return Decoration.DEFAULT_BANNER.getLayers();
    }

    /**
     * Apply config-level overrides to a layer (body alpha/tint for layer 0,
     * accent visibility/alpha/tint/offset/anchor for layer 1).
     */
    private static BannerLayer applyConfigOverrides(BannerLayer layer, int index,
                                                     LootLogConfig config, boolean reversed) {
        if (index == 0) {
            int bodyTint = config.getBodyTint() != 0xFFFFFFFF ? config.getBodyTint() : layer.getTint();
            return BannerLayer.builder(layer.getTexture())
                    .anchor(layer.getAnchor())
                    .xOffset(layer.getXOffset())
                    .yOffset(layer.getYOffset())
                    .alpha(layer.getAlpha() * config.getBodyAlpha())
                    .tint(bodyTint)
                    .animSpeed(config.getBodyAnimSpeed())
                    .visible(layer.isVisible())
                    .build();
        } else if (index == 1) {
            int accentTint = config.getAccentTint() != 0xFFFFFFFF ? config.getAccentTint() : layer.getTint();
            BannerLayer.Anchor anchor = toBannerAnchor(config.getAccentAnchor(), layer.getAnchor());
            return BannerLayer.builder(layer.getTexture())
                    .anchor(anchor)
                    .xOffset(layer.getXOffset() + (reversed ? config.getAccentXOffset() - 1 : -config.getAccentXOffset() + 1))
                    .yOffset(layer.getYOffset() + config.getAccentYOffset())
                    .alpha(layer.getAlpha() * config.getAccentAlpha())
                    .tint(accentTint)
                    .animSpeed(config.getAccentAnimSpeed())
                    .visible(config.isShowAccent() && layer.isVisible())
                    .build();
        }
        return layer;
    }

    /**
     * Resolve anchor X position for a layer's decorative edge.
     */
    private static int resolveAnchorX(BannerLayer.Anchor anchor, boolean reversed,
                                       int bannerX, int iconX, int nameX, int countX) {
        switch (anchor) {
            case ICON:  return iconX;
            case NAME:  return nameX;
            case COUNT: return countX;
            case EDGE:
            default:    return bannerX;
        }
    }

    private static BannerLayer.Anchor toBannerAnchor(AccentAnchor accentAnchor, BannerLayer.Anchor fallback) {
        if (accentAnchor == null) return fallback;
        switch (accentAnchor) {
            case EDGE:  return BannerLayer.Anchor.EDGE;
            case ICON:  return BannerLayer.Anchor.ICON;
            case NAME:  return BannerLayer.Anchor.NAME;
            case COUNT: return BannerLayer.Anchor.COUNT;
            default:    return fallback;
        }
    }

    // ======================================================================
    // Scale
    // ======================================================================

    private static float computeScale(LootLogConfig config, ResolvedOverride override,
                                       long timeMs, long entryAgeMs) {
        float scale = config.getScale();
        scale *= PickupAnimator.computeEntranceScale(entryAgeMs, config);
        if (override != null && override.getDisplayScale() != null) {
            scale *= override.getDisplayScale();
        }
        if (override != null && override.getScalePulseSpeed() != null
                && override.getScalePulseSpeed() > 0) {
            float spMin = override.getScalePulseMin() != null ? override.getScalePulseMin() : 0.95f;
            float spMax = override.getScalePulseMax() != null ? override.getScalePulseMax() : 1.05f;
            float pulse = EffectComputer.cosineWave(timeMs, override.getScalePulseSpeed());
            scale *= spMin + (spMax - spMin) * pulse;
        }
        return scale;
    }

    // ======================================================================
    // Text rendering with bounce
    // ======================================================================

    private static void renderTextWithBounce(RenderBridge bridge, String text,
            int x, int y, int textWidth, int color, boolean shadow, float bounceScale) {
        if (bounceScale != 1.0f) {
            bridge.pushPose();
            bridge.translate(x + textWidth / 2f, y + 4.5f, 0);
            bridge.scale(bounceScale, bounceScale, 1.0f);
            bridge.translate(-(x + textWidth / 2f), -(y + 4.5f), 0);
            bridge.renderText(text, x, y, color, shadow);
            bridge.popPose();
        } else {
            bridge.renderText(text, x, y, color, shadow);
        }
    }

    /**
     * Resolve a strength value, preferring override over config.
     */
    private static float resolveStrength(float configValue, Float overrideValue) {
        return overrideValue != null ? overrideValue : configValue;
    }

    /**
     * Apply alpha pulse to a color. Multiplier > 1.0 brightens RGB channels
     * since alpha is capped at 1.0.
     */
    private static int applyAlphaPulse(int color, float multiplier) {
        if (multiplier == 1.0f) return color;
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        if (multiplier > 1.0f) {
            // Brighten RGB for the excess above 1.0
            float excess = multiplier - 1.0f;
            int brighten = Math.round(excess * 80);
            r = Math.min(255, r + brighten);
            g = Math.min(255, g + brighten);
            b = Math.min(255, b + brighten);
        } else {
            a = Math.max(0, Math.min(255, Math.round(a * multiplier)));
        }
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
