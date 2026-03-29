package dev.tysontheember.lootlog;

import java.util.List;

/**
 * Renders background styles for popup entries. All dimensions are read from
 * {@link TextureSpec} — no hardcoded texture constants.
 */
public final class BackgroundRenderer {

    private BackgroundRenderer() {}

    // Tooltip colors (match Minecraft's TooltipRenderUtil)
    private static final int TOOLTIP_FILL = 0xF0100010;
    private static final int TOOLTIP_BORDER_TOP = 0x505000FF;
    private static final int TOOLTIP_BORDER_BOTTOM = 0x5028007F;

    // ======================================================================
    // Dispatch
    // ======================================================================

    /** Dispatch background rendering (no effects). */
    public static void render(RenderBridge bridge, BackgroundStyle style,
                              int x, int y, int w, int h, int bgColor, float alpha) {
        render(bridge, style, x, y, w, h, bgColor, alpha, EffectModifiers.IDENTITY);
    }

    /** Dispatch background rendering with effect modifiers. */
    public static void render(RenderBridge bridge, BackgroundStyle style,
                              int x, int y, int w, int h, int bgColor, float alpha,
                              EffectModifiers mods) {
        switch (style) {
            case SOLID:
                renderSolid(bridge, x, y, w, h, bgColor, alpha, mods);
                break;
            case TOOLTIP:
                renderTooltip(bridge, x, y, w, h, alpha, mods);
                break;
            case TEXTURE:
                renderTextured(bridge, TextureSpec.DEFAULT_NINE_SLICE, x, y, w, h, alpha, mods);
                break;
            default:
                break;
        }
    }

    // ======================================================================
    // SOLID
    // ======================================================================

    private static void renderSolid(RenderBridge bridge, int x, int y, int w, int h,
                                     int bgColor, float alpha, EffectModifiers mods) {
        int color = ColorUtil.multiplyAlpha(bgColor, alpha * mods.bodyAlphaMultiplier);
        color = mods.applyBody(color);

        if (mods.sweepPosition >= 0) {
            renderRectWithSweep(bridge, x, y, w, h, color, mods, w);
        } else {
            bridge.renderRect(x, y, w, h, color);
        }
    }

    // ======================================================================
    // TOOLTIP
    // ======================================================================

    private static void renderTooltip(RenderBridge bridge, int x, int y, int w, int h,
                                       float alpha, EffectModifiers mods) {
        int fillColor = ColorUtil.multiplyAlpha(TOOLTIP_FILL, alpha * mods.bodyAlphaMultiplier);
        fillColor = mods.applyBody(fillColor);

        int borderTop = ColorUtil.multiplyAlpha(TOOLTIP_BORDER_TOP, alpha * mods.borderAlphaMultiplier);
        borderTop = mods.applyBorder(borderTop);
        int borderBottom = ColorUtil.multiplyAlpha(TOOLTIP_BORDER_BOTTOM, alpha * mods.borderAlphaMultiplier);
        borderBottom = mods.applyBorder(borderBottom);

        // Fill (3 rects for the rounded shape)
        if (mods.sweepPosition >= 0) {
            renderRectWithSweep(bridge, x + 1, y, w - 2, 1, fillColor, mods, w);
            renderRectWithSweep(bridge, x, y + 1, w, h - 2, fillColor, mods, w);
            renderRectWithSweep(bridge, x + 1, y + h - 1, w - 2, 1, fillColor, mods, w);
        } else {
            bridge.renderRect(x + 1, y, w - 2, 1, fillColor);
            bridge.renderRect(x, y + 1, w, h - 2, fillColor);
            bridge.renderRect(x + 1, y + h - 1, w - 2, 1, fillColor);
        }

        // Borders
        bridge.renderGradientRect(x, y + 1, 1, h - 2, borderTop, borderBottom);
        bridge.renderGradientRect(x + w - 1, y + 1, 1, h - 2, borderTop, borderBottom);
        bridge.renderRect(x + 1, y, w - 2, 1, borderTop);
        bridge.renderRect(x + 1, y + h - 1, w - 2, 1, borderBottom);
    }

    // ======================================================================
    // TEXTURE (9-slice)
    // ======================================================================

    /** Render a 9-slice textured background using dimensions from a TextureSpec. */
    public static void renderTextured(RenderBridge bridge, TextureSpec tex,
                                       int x, int y, int w, int h,
                                       float alpha, EffectModifiers mods) {
        int b = tex.getSliceBorder();
        if (b > 0 && (w < 2 * b || h < 2 * b)) {
            bridge.renderRect(x, y, w, h, ColorUtil.multiplyAlpha(0xAA000000, alpha));
            return;
        }

        float tR = mods.texTintR, tG = mods.texTintG, tB = mods.texTintB;
        float a = alpha * mods.bodyAlphaMultiplier;
        int srcW = tex.getRenderWidth();
        int srcH = tex.getSourceHeight();
        int texW = tex.getPngWidth();
        int texH = tex.getPngHeight();

        if (b > 0) {
            renderNineSliceTinted(bridge, tex.getTexturePath(), x, y, w, h,
                    0, 0, srcW, srcH, texW, texH, b, a, tR, tG, tB,
                    mods.borderTintR * tR, mods.borderTintG * tG, mods.borderTintB * tB,
                    alpha * mods.borderAlphaMultiplier);
        } else {
            // STRETCH mode — render as single quad
            bridge.renderTintedTexture(tex.getTexturePath(), x, y, w, h,
                    0, 0, srcW, srcH, texW, texH, a, tR, tG, tB);
        }
    }

    /** Render a custom textured background with animation support. */
    public static void renderCustomTextured(RenderBridge bridge, TextureSpec tex,
                                             String texturePath,
                                             int x, int y, int w, int h,
                                             float alpha, long timeMs,
                                             ResolvedOverride.FrameAnimation anim,
                                             EffectModifiers mods) {
        float tR = mods.texTintR, tG = mods.texTintG, tB = mods.texTintB;
        float a = alpha * mods.bodyAlphaMultiplier;

        int srcW = tex.getRenderWidth();
        int srcH = tex.getSourceHeight();
        int texW = tex.getPngWidth();

        if (anim != null && anim.getFrames() > 1) {
            int totalTexH = srcH * anim.getFrames();
            int frameIndex = (int) ((timeMs / anim.getFrameTimeMs()) % anim.getFrames());
            int frameV = frameIndex * srcH;

            if (anim.isInterpolate()) {
                int nextFrame = (frameIndex + 1) % anim.getFrames();
                int nextV = nextFrame * srcH;
                float t = (timeMs % anim.getFrameTimeMs()) / (float) anim.getFrameTimeMs();
                if (tex.getSliceBorder() > 0) {
                    renderNineSliceTinted(bridge, texturePath, x, y, w, h,
                            0, frameV, srcW, srcH, texW, totalTexH, tex.getSliceBorder(),
                            a * (1 - t), tR, tG, tB, tR, tG, tB, a * (1 - t));
                    renderNineSliceTinted(bridge, texturePath, x, y, w, h,
                            0, nextV, srcW, srcH, texW, totalTexH, tex.getSliceBorder(),
                            a * t, tR, tG, tB, tR, tG, tB, a * t);
                } else {
                    bridge.renderTintedTexture(texturePath, x, y, w, h,
                            0, frameV, srcW, srcH, texW, totalTexH, a * (1 - t), tR, tG, tB);
                    bridge.renderTintedTexture(texturePath, x, y, w, h,
                            0, nextV, srcW, srcH, texW, totalTexH, a * t, tR, tG, tB);
                }
            } else {
                if (tex.getSliceBorder() > 0) {
                    renderNineSliceTinted(bridge, texturePath, x, y, w, h,
                            0, frameV, srcW, srcH, texW, totalTexH, tex.getSliceBorder(),
                            a, tR, tG, tB, tR, tG, tB, a);
                } else {
                    bridge.renderTintedTexture(texturePath, x, y, w, h,
                            0, frameV, srcW, srcH, texW, totalTexH, a, tR, tG, tB);
                }
            }
        } else {
            if (tex.getSliceBorder() > 0) {
                renderNineSliceTinted(bridge, texturePath, x, y, w, h,
                        0, 0, srcW, srcH, texW, tex.getPngHeight(), tex.getSliceBorder(),
                        a, tR, tG, tB, tR, tG, tB, a);
            } else {
                bridge.renderTintedTexture(texturePath, x, y, w, h,
                        0, 0, srcW, srcH, texW, tex.getPngHeight(), a, tR, tG, tB);
            }
        }
    }

    // ======================================================================
    // BANNER layer rendering
    // ======================================================================

    /**
     * Render a single banner layer at native pixel size.
     *
     * @param flipped true for left-anchored (UV-flip decorative edge to face right)
     */
    public static void renderBannerLayer(RenderBridge bridge, BannerLayer layer,
                                          int x, int y, float alpha, long timeMs,
                                          boolean flipped) {
        if (!layer.isVisible()) return;

        TextureSpec tex = layer.getTexture();
        int renderW = tex.getRenderWidth();
        int renderH = tex.getRenderHeight();
        int texW = tex.getPngWidth();
        int texH = tex.getPngHeight();
        float layerAlpha = alpha * layer.getAlpha();
        if (layerAlpha <= 0) return;

        // Extract tint components
        int tint = layer.getTint();
        float tR = ((tint >> 16) & 0xFF) / 255f;
        float tG = ((tint >> 8) & 0xFF) / 255f;
        float tB = (tint & 0xFF) / 255f;

        int renderX = x + layer.getXOffset();
        int renderY = y + layer.getYOffset();

        int animSpeed = layer.getAnimSpeed();
        int frameCount = tex.getFrameCount();

        if (animSpeed > 0 && frameCount > 1) {
            // Animated: vertically stacked frames with interpolation
            int frameIndex = (int) ((timeMs / (animSpeed * 50L)) % frameCount);
            int nextFrame = (frameIndex + 1) % frameCount;
            float blend = ((timeMs % (animSpeed * 50L)) / (float) (animSpeed * 50L));

            int frameV = frameIndex * renderH;
            int nextV = nextFrame * renderH;

            if (flipped) {
                // UV-flip: swap U coordinates (u = renderW, regionW = -renderW)
                bridge.renderTintedTexture(tex.getTexturePath(),
                        renderX, renderY, renderW, renderH,
                        renderW, frameV, -renderW, renderH, texW, texH,
                        layerAlpha * (1 - blend), tR, tG, tB);
                bridge.renderTintedTexture(tex.getTexturePath(),
                        renderX, renderY, renderW, renderH,
                        renderW, nextV, -renderW, renderH, texW, texH,
                        layerAlpha * blend, tR, tG, tB);
            } else {
                bridge.renderTintedTexture(tex.getTexturePath(),
                        renderX, renderY, renderW, renderH,
                        0, frameV, renderW, renderH, texW, texH,
                        layerAlpha * (1 - blend), tR, tG, tB);
                bridge.renderTintedTexture(tex.getTexturePath(),
                        renderX, renderY, renderW, renderH,
                        0, nextV, renderW, renderH, texW, texH,
                        layerAlpha * blend, tR, tG, tB);
            }
        } else {
            // Static (single frame)
            if (flipped) {
                bridge.renderTintedTexture(tex.getTexturePath(),
                        renderX, renderY, renderW, renderH,
                        renderW, 0, -renderW, renderH, texW, texH,
                        layerAlpha, tR, tG, tB);
            } else {
                bridge.renderTintedTexture(tex.getTexturePath(),
                        renderX, renderY, renderW, renderH,
                        0, 0, renderW, renderH, texW, texH,
                        layerAlpha, tR, tG, tB);
            }
        }
    }

    /**
     * Render all banner layers in back-to-front order.
     * Each layer is vertically centered on the entry center line.
     */
    public static void renderBannerLayers(RenderBridge bridge, List<BannerLayer> layers,
                                            int bannerX, int entryY, int entryHeight,
                                            float alpha, long timeMs, boolean flipped,
                                            EffectModifiers mods) {
        for (int i = 0; i < layers.size(); i++) {
            BannerLayer layer = layers.get(i);
            if (!layer.isVisible()) continue;

            int layerH = layer.getTexture().getRenderHeight();
            int centerY = entryY + entryHeight / 2;
            int layerY = centerY - layerH / 2;

            renderBannerLayer(bridge, layer, bannerX, layerY, alpha, timeMs, flipped);
        }
    }

    // ======================================================================
    // Sweep helpers
    // ======================================================================

    /**
     * Render a rect, splitting into 3 segments if a sweep band is active.
     */
    static void renderRectWithSweep(RenderBridge bridge,
                                     int x, int y, int w, int h,
                                     int color, EffectModifiers mods,
                                     int entryWidth) {
        if (entryWidth <= 0) {
            bridge.renderRect(x, y, w, h, color);
            return;
        }

        int sweepLeft = (int) (mods.sweepPosition * entryWidth);
        int sweepRight = (int) ((mods.sweepPosition + mods.sweepWidth) * entryWidth);

        int bandStart = Math.max(x, sweepLeft);
        int bandEnd = Math.min(x + w, sweepRight);

        if (bandStart >= bandEnd) {
            bridge.renderRect(x, y, w, h, color);
            return;
        }

        int brightened = ColorUtil.brighten(color, Math.round(mods.sweepIntensity * 80));

        if (bandStart > x) {
            bridge.renderRect(x, y, bandStart - x, h, color);
        }
        bridge.renderRect(bandStart, y, bandEnd - bandStart, h, brightened);
        if (bandEnd < x + w) {
            bridge.renderRect(bandEnd, y, x + w - bandEnd, h, color);
        }
    }

    // ======================================================================
    // 9-slice (parametric)
    // ======================================================================

    private static void renderNineSliceTinted(RenderBridge bridge, String texture,
                                               int x, int y, int w, int h,
                                               int srcX, int srcY, int srcW, int srcH,
                                               int texW, int texH, int b,
                                               float bodyAlpha,
                                               float tR, float tG, float tB,
                                               float bR, float bG, float bB,
                                               float borderAlpha) {
        if (w < 2 * b || h < 2 * b) {
            bridge.renderRect(x, y, w, h, ColorUtil.multiplyAlpha(0xAA000000, bodyAlpha));
            return;
        }

        int innerW = w - 2 * b;
        int innerH = h - 2 * b;
        int srcInnerW = srcW - 2 * b;
        int srcInnerH = srcH - 2 * b;

        // 4 corners (border tint)
        bridge.renderTintedTexture(texture, x, y, b, b,
                srcX, srcY, b, b, texW, texH, borderAlpha, bR, bG, bB);
        bridge.renderTintedTexture(texture, x + w - b, y, b, b,
                srcX + srcW - b, srcY, b, b, texW, texH, borderAlpha, bR, bG, bB);
        bridge.renderTintedTexture(texture, x, y + h - b, b, b,
                srcX, srcY + srcH - b, b, b, texW, texH, borderAlpha, bR, bG, bB);
        bridge.renderTintedTexture(texture, x + w - b, y + h - b, b, b,
                srcX + srcW - b, srcY + srcH - b, b, b, texW, texH, borderAlpha, bR, bG, bB);

        // 4 edges (border tint)
        if (innerW > 0) {
            bridge.renderTintedTexture(texture, x + b, y, innerW, b,
                    srcX + b, srcY, srcInnerW, b, texW, texH, borderAlpha, bR, bG, bB);
            bridge.renderTintedTexture(texture, x + b, y + h - b, innerW, b,
                    srcX + b, srcY + srcH - b, srcInnerW, b, texW, texH, borderAlpha, bR, bG, bB);
        }
        if (innerH > 0) {
            bridge.renderTintedTexture(texture, x, y + b, b, innerH,
                    srcX, srcY + b, b, srcInnerH, texW, texH, borderAlpha, bR, bG, bB);
            bridge.renderTintedTexture(texture, x + w - b, y + b, b, innerH,
                    srcX + srcW - b, srcY + b, b, srcInnerH, texW, texH, borderAlpha, bR, bG, bB);
        }

        // Center (body tint)
        if (innerW > 0 && innerH > 0) {
            bridge.renderTintedTexture(texture, x + b, y + b, innerW, innerH,
                    srcX + b, srcY + b, srcInnerW, srcInnerH, texW, texH, bodyAlpha, tR, tG, tB);
        }
    }
}
