package dev.tysontheember.lootlog;

/**
 * Renders icon-level visual effects: glow, shadow, and bounce.
 * Called from {@link HudLayout#renderIcon} when an entry has icon effects.
 * Parallel to {@link BackgroundRenderer} which handles background-level effects.
 */
public final class IconEffectRenderer {

    private IconEffectRenderer() {}

    static final String GLOW_CIRCLE_TEXTURE = "lootlog:textures/gui/lootlog/glow_circle.png";
    static final int GLOW_TEX_SIZE = 32;

    /**
     * Render icon effects (shadow, glow) and the actual icon.
     * Render order: shadow -> glow -> icon.
     */
    public static void render(RenderBridge bridge, PickupEntry entry,
                              int x, int y, float alpha, long timeMs,
                              LootLogConfig config) {
        ResolvedOverride override = entry.getOverride();

        // --- Resolve shadow params (override > global config) ---
        boolean shadowActive = false;
        int shadowColor = config.getIconShadowColor();
        int shadowOffsetX = config.getIconShadowOffsetX();
        int shadowOffsetY = config.getIconShadowOffsetY();
        int shadowRadius = config.getIconShadowRadius();
        IconShape shadowShape = config.getIconShadowShape();
        float shadowSoftness = config.getIconShadowSoftness();

        if (override != null && override.getIconShadowColor() != null) {
            shadowActive = true;
            shadowColor = override.getIconShadowColor();
            if (override.getIconShadowOffsetX() != null) shadowOffsetX = override.getIconShadowOffsetX();
            if (override.getIconShadowOffsetY() != null) shadowOffsetY = override.getIconShadowOffsetY();
            if (override.getIconShadowRadius() != null) shadowRadius = override.getIconShadowRadius();
            if (override.getIconShadowShape() != null) shadowShape = IconShape.fromString(override.getIconShadowShape());
            if (override.getIconShadowSoftness() != null) shadowSoftness = override.getIconShadowSoftness();
        } else if (config.isIconShadowEnabled()) {
            shadowActive = true;
        }

        // --- Resolve glow params (override > global config) ---
        boolean glowActive = false;
        int glowColor = config.getIconGlowColor();
        int glowRadius = config.getIconGlowRadius();
        IconShape glowShape = config.getIconGlowShape();
        float glowSoftness = config.getIconGlowSoftness();
        float glowPulseSpeed = config.getIconGlowPulseSpeed();
        float glowPulseMin = config.getIconGlowPulseMin();
        float glowPulseMax = config.getIconGlowPulseMax();

        if (override != null && override.getIconGlowColor() != null) {
            glowActive = true;
            glowColor = override.getIconGlowColor();
            if (override.getIconGlowRadius() != null) glowRadius = override.getIconGlowRadius();
            if (override.getIconGlowShape() != null) glowShape = IconShape.fromString(override.getIconGlowShape());
            if (override.getIconGlowSoftness() != null) glowSoftness = override.getIconGlowSoftness();
            if (override.getIconGlowPulseSpeed() != null) glowPulseSpeed = override.getIconGlowPulseSpeed();
            if (override.getIconGlowPulseMin() != null) glowPulseMin = override.getIconGlowPulseMin();
            if (override.getIconGlowPulseMax() != null) glowPulseMax = override.getIconGlowPulseMax();
        } else if (config.isIconGlowEnabled()) {
            glowActive = true;
        }

        // --- XP orb default glow (hardcoded green glow for XP entries) ---
        if (!glowActive && entry.getType() == PickupType.XP) {
            glowActive = true;
            glowColor = 0x6640FF40;
            glowRadius = 3;
            glowShape = IconShape.CIRCLE;
            glowPulseSpeed = 1.0f;
            glowPulseMin = 0.4f;
            glowPulseMax = 1.0f;
        }

        // --- Render shadow ---
        if (shadowActive && shadowRadius > 0) {
            float shadowAlpha = ((shadowColor >> 24) & 0xFF) / 255f * alpha;
            renderGlow(bridge, entry, x + shadowOffsetX, y + shadowOffsetY,
                    shadowAlpha, shadowColor, shadowRadius, shadowShape, shadowSoftness,
                    shadowOffsetX, shadowOffsetY);
        }

        // --- Render glow ---
        if (glowActive && glowRadius > 0) {
            float pulseIntensity = computeGlowPulse(timeMs, glowPulseSpeed, glowPulseMin, glowPulseMax);
            float glowAlpha = ((glowColor >> 24) & 0xFF) / 255f * alpha * pulseIntensity;
            renderGlow(bridge, entry, x, y, glowAlpha, glowColor, glowRadius, glowShape, glowSoftness,
                    0, 0);
        }

        // --- Bounce + spin transforms ---
        float bounceIntensity = resolveBounceIntensity(config, override);
        float pulseDuration = resolvePulseDuration(config, override);
        float bounceScale = computePulse(entry.getBounceStartMs(), bounceIntensity, pulseDuration);

        // --- Icon alpha pulse ---
        float iconAlphaStrength = resolveIconAlphaStrength(config, override);
        float iconAlphaPulse = computePulse(entry.getBounceStartMs(), iconAlphaStrength, pulseDuration);
        alpha *= iconAlphaPulse;

        float spinSpeed = override != null && override.getIconSpinSpeed() != null
                ? override.getIconSpinSpeed() : 0;

        boolean hasTransform = bounceScale != 1.0f || spinSpeed != 0;

        if (hasTransform) {
            int halfIcon = PickupAnimator.ICON_SIZE / 2;
            bridge.pushPose();
            bridge.translate(x + halfIcon, y + halfIcon, 0);
            if (bounceScale != 1.0f) {
                bridge.scale(bounceScale, bounceScale, 1.0f);
            }
            if (spinSpeed != 0) {
                float angle = (timeMs * spinSpeed / 1000.0f) % 360.0f;
                bridge.rotateZ(angle);
            }
            bridge.translate(-halfIcon, -halfIcon, 0);
            // Render at (0,0) since we've translated to the icon's position
            HudLayout.renderIconDirect(bridge, entry, 0, 0, alpha, timeMs);
            bridge.popPose();
        } else {
            HudLayout.renderIconDirect(bridge, entry, x, y, alpha, timeMs);
        }
    }

    /**
     * Dispatch to the appropriate glow shape renderer.
     * @param shadowOffX shadow offset from icon (0 for glow, non-zero for shadow)
     * @param shadowOffY shadow offset from icon (0 for glow, non-zero for shadow)
     */
    private static void renderGlow(RenderBridge bridge, PickupEntry entry,
                                   int x, int y, float alpha, int color,
                                   int radius, IconShape shape, float softness,
                                   int shadowOffX, int shadowOffY) {
        if (alpha <= 0.01f || radius <= 0) return;

        switch (shape != null ? shape : IconShape.CIRCLE) {
            case ITEM:
                renderItemShapedGlow(bridge, entry, x, y, alpha, color, radius, softness,
                        shadowOffX, shadowOffY);
                break;
            case SQUARE:
                renderSquareGlow(bridge, x, y, alpha, color, radius, softness);
                break;
            case DIAMOND:
                renderDiamondGlow(bridge, x, y, alpha, color, radius, softness);
                break;
            case CIRCLE:
            default:
                renderCircularGlow(bridge, x, y, alpha, color, radius);
                break;
        }
    }

    /**
     * Item-shaped glow: render the item at pixel offsets in the glow color.
     * For radius R, iterate rings 1..R with 8 offsets per ring.
     * Alpha decays per ring based on softness.
     *
     * For shadows, samples that would overlap the actual icon's 16x16 area are skipped.
     * shadowOffX/Y indicate how far this effect center is from the icon origin.
     */
    private static void renderItemShapedGlow(RenderBridge bridge, PickupEntry entry,
                                             int x, int y, float alpha, int color,
                                             int radius, float softness,
                                             int shadowOffX, int shadowOffY) {
        Object itemStack = entry.getItemStack();
        if (itemStack == null) return;

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        boolean isShadow = shadowOffX != 0 || shadowOffY != 0;
        int iconSize = PickupAnimator.ICON_SIZE;

        // 8 directional offsets (N, NE, E, SE, S, SW, W, NW)
        int[][] directions = {
                {0, -1}, {1, -1}, {1, 0}, {1, 1},
                {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}
        };

        for (int ring = 1; ring <= radius; ring++) {
            // Alpha falls off with distance: (1 - ring/radius)^softness
            float falloff = (float) Math.pow(1.0f - (float) ring / (radius + 1), softness);
            float ringAlpha = alpha * falloff;
            if (ringAlpha < 0.01f) continue;

            for (int[] dir : directions) {
                // For shadows: skip samples that overlap the icon's area.
                // Sample absolute offset from icon origin = shadowOff + dir*ring
                if (isShadow) {
                    int dx = shadowOffX + dir[0] * ring;
                    int dy = shadowOffY + dir[1] * ring;
                    if (dx >= 0 && dx < iconSize && dy >= 0 && dy < iconSize) {
                        continue;
                    }
                }

                bridge.renderTintedItemIcon(itemStack,
                        x + dir[0] * ring, y + dir[1] * ring,
                        ringAlpha, r, g, b);
            }
        }
    }

    /**
     * Circular glow: render a soft circle texture, tinted to the glow color.
     */
    private static void renderCircularGlow(RenderBridge bridge,
                                           int x, int y, float alpha, int color,
                                           int radius) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        int size = PickupAnimator.ICON_SIZE + 2 * radius;
        int drawX = x - radius;
        int drawY = y - radius;

        bridge.renderTintedTexture(GLOW_CIRCLE_TEXTURE,
                drawX, drawY, size, size,
                0, 0, GLOW_TEX_SIZE, GLOW_TEX_SIZE,
                GLOW_TEX_SIZE, GLOW_TEX_SIZE, alpha, r, g, b);
    }

    /**
     * Square glow: concentric rectangles with decreasing alpha.
     */
    private static void renderSquareGlow(RenderBridge bridge,
                                         int x, int y, float alpha, int color,
                                         int radius, float softness) {
        for (int ring = radius; ring >= 1; ring--) {
            float falloff = (float) Math.pow(1.0f - (float) ring / (radius + 1), softness);
            float ringAlpha = alpha * falloff;
            if (ringAlpha < 0.01f) continue;

            int glowColor = ColorUtil.applyAlpha(color, ringAlpha);
            bridge.renderRect(x - ring, y - ring,
                    PickupAnimator.ICON_SIZE + 2 * ring,
                    PickupAnimator.ICON_SIZE + 2 * ring, glowColor);
        }
    }

    /**
     * Diamond glow: layered horizontal rects approximating a rotated square.
     * Widest at center, tapering to points at top/bottom.
     */
    private static void renderDiamondGlow(RenderBridge bridge,
                                          int x, int y, float alpha, int color,
                                          int radius, float softness) {
        int iconSize = PickupAnimator.ICON_SIZE;
        int centerX = x + iconSize / 2;
        int centerY = y + iconSize / 2;
        int halfBase = iconSize / 2 + radius;

        // Draw horizontal strips from top to bottom of the diamond
        for (int dy = -halfBase; dy <= halfBase; dy++) {
            float distFromCenter = Math.abs(dy) / (float) halfBase;
            int halfWidth = Math.round(halfBase * (1.0f - distFromCenter));
            if (halfWidth <= 0) continue;

            // Only draw the glow portion (outside the icon area)
            float falloff = (float) Math.pow(1.0f - distFromCenter, softness);
            float stripAlpha = alpha * falloff;
            if (stripAlpha < 0.01f) continue;

            int glowColor = ColorUtil.applyAlpha(color, stripAlpha);
            bridge.renderRect(centerX - halfWidth, centerY + dy, halfWidth * 2, 1, glowColor);
        }
    }

    /**
     * Resolve effective icon scale pulse intensity from config and override.
     * Package-private so renderers can reuse for count text bounce.
     */
    static float resolveBounceIntensity(LootLogConfig config, ResolvedOverride override) {
        if (!config.isPickupPulseEnabled()) return 0;
        float intensity = config.getPickupPulseIconScaleStrength();
        if (override != null && override.getPickupPulseIconScaleStrength() != null) {
            intensity = override.getPickupPulseIconScaleStrength();
        }
        return intensity;
    }

    /**
     * Resolve icon alpha pulse strength from config and override.
     */
    static float resolveIconAlphaStrength(LootLogConfig config, ResolvedOverride override) {
        if (!config.isPickupPulseEnabled()) return 0;
        float strength = config.getPickupPulseIconAlphaStrength();
        if (override != null && override.getPickupPulseIconAlphaStrength() != null) {
            strength = override.getPickupPulseIconAlphaStrength();
        }
        return strength;
    }

    /**
     * Resolve the pulse duration in ms from config and override.
     */
    static float resolvePulseDuration(LootLogConfig config, ResolvedOverride override) {
        int duration = config.getPickupPulseDurationMs();
        if (override != null && override.getPickupPulseDurationMs() != null) {
            duration = override.getPickupPulseDurationMs();
        }
        return duration;
    }

    /**
     * Compute glow pulse intensity using cosine wave.
     * Returns a value between pulseMin and pulseMax.
     * If speed is 0, returns 1.0 (no pulse).
     */
    static float computeGlowPulse(long timeMs, float speed, float min, float max) {
        if (speed <= 0) return 1.0f;
        float t = EffectComputer.cosineWave(timeMs, speed);
        return min + (max - min) * t;
    }

    /**
     * Compute bounce scale factor.
     * Brief overshoot on pickup/stack, settles back to 1.0.
     */
    private static final float BOUNCE_DURATION_MS = 200.0f;

    static float computeBounce(long bounceStartMs, float intensity) {
        return computePulse(bounceStartMs, intensity, BOUNCE_DURATION_MS);
    }

    /**
     * Generalized pulse: brief overshoot on pickup, settles back to 1.0.
     * Used for both scale and alpha channels.
     */
    static float computePulse(long bounceStartMs, float intensity, float durationMs) {
        if (intensity <= 0) return 1.0f;
        long now = System.currentTimeMillis();
        float elapsed = now - bounceStartMs;
        if (elapsed >= durationMs || elapsed < 0) return 1.0f;
        float t = elapsed / durationMs;
        return 1.0f + intensity * (float) (Math.sin(t * Math.PI) * (1.0 - t));
    }
}
