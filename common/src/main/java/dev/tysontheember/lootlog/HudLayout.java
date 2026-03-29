package dev.tysontheember.lootlog;

import java.util.List;

/**
 * Orchestrates HUD rendering by iterating entries, computing animation state,
 * and dispatching to {@link PopupRenderer}.
 */
public final class HudLayout {

    private long lastRenderTimeMs;
    private long animationTimeMs;

    // Vanilla XP orb spritesheet: textures/entity/experience_orb.png (64x64)
    // 4 columns x 4 rows of 16x16 frames, sized by XP value (0=tiny, 10=largest).
    // We animate between frames 3 and 4 (medium orbs) and tint green.
    private static final String XP_ORB_TEXTURE = "minecraft:textures/entity/experience_orb.png";
    private static final int XP_TEX_W = 64;
    private static final int XP_TEX_H = 64;
    private static final int XP_FRAME_SIZE = 16;
    private static final int XP_FRAME_COLS = 4;
    private static final int XP_FRAME_A = 3;
    private static final int XP_FRAME_B = 4;
    private static final int XP_DISPLAY_SIZE = 12;  // render orb at 12x12, centered in 16x16
    private static final int XP_INSET = (16 - XP_DISPLAY_SIZE) / 2;  // 2px inset

    /**
     * Render all active pickup entries on the HUD.
     * Called each frame from the platform render event handler.
     */
    public void render(List<PickupEntry> entries, LootLogConfig config, RenderBridge bridge) {
        if (entries.isEmpty()) return;

        long now = System.currentTimeMillis();
        float deltaMs = computeDeltaMs(now);
        animationTimeMs += (long) deltaMs;

        HudAnchor anchor = config.getAnchor();
        int screenW = bridge.getScreenWidth();
        int screenH = bridge.getScreenHeight();

        // Resolve global layout and texture
        LayoutPreset globalPreset = resolvePreset(config);
        TextureSpec globalTexSpec = resolveTextureSpec(config, null);
        PopupLayout globalLayout = globalPreset.createLayout(globalTexSpec);
        // Apply global element visibility
        globalLayout = globalLayout.withVisibility(
                null, null,
                config.isShowCount() ? null : false,
                config.isShowCountRight() ? null : false);

        for (int i = 0; i < entries.size(); i++) {
            PickupEntry entry = entries.get(i);
            long rawAge = now - entry.getCreatedAtMs();
            // Apply stagger delay for cascading entrance animations
            long age = PickupAnimator.applyStaggerDelay(rawAge, i, config);

            // Use per-entry override duration if available
            long displayMs = config.getDisplayDurationMs();
            ResolvedOverride override = entry.getOverride();
            if (override != null && override.getDisplayDurationMs() != null) {
                displayMs = override.getDisplayDurationMs();
            }
            float alpha = PickupAnimator.computeAlpha(age, config, displayMs);

            // Minecraft's Font.adjustColor() treats alpha bytes 0-3 as fully opaque.
            // Skip to prevent a single-frame flash at fade boundaries.
            if (Math.round(alpha * 255) < 4) continue;

            // --- Per-entry layout resolution ---
            LayoutPreset entryPreset = globalPreset;
            TextureSpec entryTexSpec = resolveTextureSpec(config, entry);
            PopupLayout entryLayout = globalLayout;

            // If override changes the background style, re-resolve layout
            if (override != null && override.getBackgroundStyle() != null) {
                entryTexSpec = resolveTextureSpec(config, entry);
                entryLayout = entryPreset.createLayout(entryTexSpec);
                entryLayout = entryLayout.withVisibility(
                        null, null,
                        config.isShowCount() ? null : false,
                        config.isShowCountRight() ? null : false);
            }

            BackgroundStyle entryBgStyle = config.getBackgroundStyle();
            if (override != null && override.getBackgroundStyle() != null) {
                entryBgStyle = override.getBackgroundStyle();
            }

            // --- Vertical animation ---
            int scaledHeight = Math.round(entryLayout.getEntryHeight() * config.getScale());
            int scaledSpacing = Math.round(config.getEntrySpacing() * config.getScale());
            updateVerticalOffset(entry, i, scaledHeight, scaledSpacing, anchor);
            entry.setVerticalOffset(
                    PickupAnimator.decayVerticalOffset(entry.getVerticalOffset(), deltaMs,
                            config.getVerticalAnimSpeed()));

            // --- Slide offsets ---
            float slideIn = PickupAnimator.computeSlideOffset(age, config);
            float slideOut = PickupAnimator.computeFadeOutSlideOffset(age, config);
            float totalSlide = slideIn + slideOut;

            // --- Dispatch to unified renderer ---
            PopupRenderer.render(entry, i, config, bridge, anchor, entryLayout,
                    entryBgStyle, entryTexSpec, screenW, screenH,
                    alpha, totalSlide, animationTimeMs, age, entryPreset);
        }
    }

    // --- Layout resolution ---

    /**
     * Resolve the LayoutPreset from config. Maps legacy settings to presets.
     */
    static LayoutPreset resolvePreset(LootLogConfig config) {
        // Check for explicit layout preset
        String presetName = config.getLayoutPreset();
        if (presetName != null && !presetName.isEmpty()) {
            return LayoutPreset.byName(presetName);
        }

        // Legacy mapping: backgroundStyle drives preset selection
        BackgroundStyle style = config.getBackgroundStyle();
        if (style == BackgroundStyle.BANNER || style == BackgroundStyle.FLAT) {
            return LayoutPreset.CLASSIC;
        }

        // Standard layout: use iconOnRight to pick variant
        return config.isIconOnRight() ? LayoutPreset.STANDARD_RIGHT : LayoutPreset.STANDARD_LEFT;
    }

    /**
     * Resolve TextureSpec for an entry, falling back through override > decoration > default.
     */
    static TextureSpec resolveTextureSpec(LootLogConfig config, PickupEntry entry) {
        ResolvedOverride override = entry != null ? entry.getOverride() : null;

        // Override texture: determine spec from the override's background style
        if (override != null && override.getBackgroundTexture() != null) {
            // Check if the texture matches a known decoration
            for (Decoration deco : Decoration.values()) {
                if (deco.getTexture().equals(override.getBackgroundTexture())) {
                    return deco.getTextureSpec();
                }
            }
            // Guess spec from the override's or config's background style
            BackgroundStyle overrideStyle = override.getBackgroundStyle();
            if (overrideStyle != null) {
                return guessTextureSpec(override.getBackgroundTexture(), overrideStyle);
            }
            return guessTextureSpec(override.getBackgroundTexture(), config.getBackgroundStyle());
        }

        // Global decoration
        String decoName = config.getDecoration();
        if (decoName != null) {
            Decoration deco = Decoration.byName(decoName);
            if (deco != null) return deco.getTextureSpec();
        }

        // Default based on background style
        BackgroundStyle style = config.getBackgroundStyle();
        if (style == BackgroundStyle.BANNER || style == BackgroundStyle.FLAT) {
            return TextureSpec.DEFAULT_BANNER;
        }
        if (style == BackgroundStyle.TEXTURE) {
            return TextureSpec.DEFAULT_NINE_SLICE;
        }

        // For NONE, SOLID, TOOLTIP — use banner spec for entry height calculation
        return TextureSpec.DEFAULT_BANNER;
    }

    /** Guess a TextureSpec for a custom texture path based on a background style. */
    private static TextureSpec guessTextureSpec(String texturePath, BackgroundStyle style) {
        if (style == BackgroundStyle.TEXTURE) {
            return TextureSpec.nineSlice(texturePath, 20, 4);
        }
        return TextureSpec.banner(texturePath, 12);
    }

    // --- Vertical offset animation ---

    /**
     * Detect when an entry has shifted position in the list and apply
     * a vertical offset so it can animate smoothly to the new position.
     */
    private void updateVerticalOffset(PickupEntry entry, int currentIndex,
                                      int entryHeight, int spacing, HudAnchor anchor) {
        int lastIndex = entry.getLastRenderedIndex();
        if (lastIndex != -1 && lastIndex != currentIndex) {
            int indexDelta = currentIndex - lastIndex;
            int pixelDelta = indexDelta * (entryHeight + spacing);
            if (isBottomAnchor(anchor)) {
                pixelDelta = -pixelDelta;
            }
            entry.setVerticalOffset(entry.getVerticalOffset() + pixelDelta);
        }
        entry.setLastRenderedIndex(currentIndex);
    }

    /** Compute capped delta time between frames. */
    private float computeDeltaMs(long now) {
        float delta = lastRenderTimeMs == 0 ? 16.667f : Math.min(now - lastRenderTimeMs, 100);
        lastRenderTimeMs = now;
        return delta;
    }

    // --- Shared utilities used by PopupRenderer ---

    static boolean isRightAnchor(HudAnchor anchor) {
        return anchor == HudAnchor.TOP_RIGHT || anchor == HudAnchor.BOTTOM_RIGHT;
    }

    static boolean isBottomAnchor(HudAnchor anchor) {
        return anchor == HudAnchor.BOTTOM_LEFT || anchor == HudAnchor.BOTTOM_RIGHT;
    }

    /**
     * Render item icon with effects (glow, shadow). Shared by PopupRenderer.
     * Delegates to {@link IconEffectRenderer} when icon effects are active.
     */
    static void renderIcon(RenderBridge bridge, PickupEntry entry,
                           int x, int y, float alpha, long timeMs,
                           LootLogConfig config) {
        ResolvedOverride override = entry.getOverride();
        boolean hasEffects = config.isIconGlowEnabled() || config.isIconShadowEnabled()
                || config.isPickupPulseEnabled()
                || (override != null && (override.getIconGlowColor() != null
                        || override.getIconShadowColor() != null
                        || override.getPickupPulseIconScaleStrength() != null));

        if (hasEffects) {
            IconEffectRenderer.render(bridge, entry, x, y, alpha, timeMs, config);
        } else {
            renderIconDirect(bridge, entry, x, y, alpha, timeMs);
        }
    }

    /** Render the raw icon without effects. Called by {@link IconEffectRenderer} after drawing glow/shadow. */
    static void renderIconDirect(RenderBridge bridge, PickupEntry entry,
                                 int x, int y, float alpha, long timeMs) {
        if (entry.getType() == PickupType.XP) {
            // Animate between two orb frames
            int frame = ((timeMs / 400) % 2 == 0) ? XP_FRAME_A : XP_FRAME_B;
            int u = (frame % XP_FRAME_COLS) * XP_FRAME_SIZE;
            int v = (frame / XP_FRAME_COLS) * XP_FRAME_SIZE;

            // Vanilla-matching bright green-yellow tint with gentle pulse.
            float t = (float) Math.cos(timeMs * 0.004);
            float red = 0.6f + 0.15f * t;
            float green = 1.0f;
            float blue = 0.2f + 0.1f * t;

            bridge.renderTintedTexture(XP_ORB_TEXTURE,
                    x + XP_INSET, y + XP_INSET, XP_DISPLAY_SIZE, XP_DISPLAY_SIZE,
                    u, v, XP_FRAME_SIZE, XP_FRAME_SIZE,
                    XP_TEX_W, XP_TEX_H, alpha, red, green, blue);
        } else {
            bridge.renderItemIcon(entry.getItemStack(), x, y, alpha);
        }
    }
}
