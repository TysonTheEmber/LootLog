package dev.tysontheember.lootlog;

/**
 * Abstraction over platform-specific rendering.
 * Implementations wrap GuiGraphics (Forge/NeoForge) or DrawContext (Fabric).
 * Set by each platform's entrypoint at mod init time.
 *
 * All methods are called on the client render thread only.
 * The itemStack parameter is an opaque Object — platform implementations
 * cast it back to the real ItemStack type.
 */
public interface RenderBridge {

    void renderRect(int x, int y, int width, int height, int argbColor);

    /** Render a vertical gradient rectangle (colorTop at y, colorBottom at y+height). */
    default void renderGradientRect(int x, int y, int width, int height, int colorTop, int colorBottom) {
        throw new UnsupportedOperationException("renderGradientRect not implemented");
    }

    /** Render a horizontal gradient rectangle (colorLeft at x, colorRight at x+width). */
    default void renderHorizontalGradientRect(int x, int y, int width, int height,
                                               int colorLeft, int colorRight) {
        if (width <= 0) return;
        // Per-pixel columns for smooth interpolation
        for (int i = 0; i < width; i++) {
            float t = width == 1 ? 0.5f : (float) i / (width - 1);
            int color = ColorUtil.lerpColor(colorLeft, colorRight, t);
            renderRect(x + i, y, 1, height, color);
        }
    }

    /**
     * Render a region from a mod texture spritesheet.
     * The resourcePath is a namespaced string (e.g. "lootlog:textures/gui/widgets.png")
     * because the common module has no Minecraft dependency — platform implementations
     * parse it to a ResourceLocation.
     */
    default void renderTexture(String resourcePath, int x, int y, int width, int height,
                               float u, float v, float regionWidth, float regionHeight,
                               int textureWidth, int textureHeight, float alpha) {
        throw new UnsupportedOperationException("renderTexture not implemented");
    }

    /** Render a tinted texture region. RGB values are 0.0-1.0 color multipliers. */
    default void renderTintedTexture(String resourcePath, int x, int y, int width, int height,
                                     float u, float v, float regionWidth, float regionHeight,
                                     int textureWidth, int textureHeight, float alpha,
                                     float red, float green, float blue) {
        // Default: ignore tint, fall back to untinted
        renderTexture(resourcePath, x, y, width, height, u, v, regionWidth, regionHeight,
                textureWidth, textureHeight, alpha);
    }

    /** Render a solid rectangle with RGB tint multipliers applied to the base color. */
    default void renderTintedRect(int x, int y, int width, int height,
                                  int argbColor, float tintR, float tintG, float tintB) {
        renderRect(x, y, width, height, ColorUtil.tintRgb(argbColor, tintR, tintG, tintB));
    }

    /** Render a vertical gradient rectangle with RGB tint multipliers applied to both colors. */
    default void renderTintedGradientRect(int x, int y, int width, int height,
                                          int colorTop, int colorBottom,
                                          float tintR, float tintG, float tintB) {
        renderGradientRect(x, y, width, height,
                ColorUtil.tintRgb(colorTop, tintR, tintG, tintB),
                ColorUtil.tintRgb(colorBottom, tintR, tintG, tintB));
    }

    void renderItemIcon(Object itemStack, int x, int y, float alpha);

    /**
     * Render an item icon with an RGB color tint applied via shader color.
     * Used for item-shape glow: render the item at offsets in the glow color.
     * Default falls back to untinted renderItemIcon.
     */
    default void renderTintedItemIcon(Object itemStack, int x, int y, float alpha,
                                      float red, float green, float blue) {
        renderItemIcon(itemStack, x, y, alpha);
    }

    void renderText(String text, int x, int y, int argbColor, boolean shadow);

    int getTextWidth(String text);

    int getScreenWidth();

    int getScreenHeight();

    void pushPose();

    void popPose();

    void translate(float x, float y, float z);

    void scale(float x, float y, float z);

    /** Rotate the current pose around the Z axis (screen plane). */
    default void rotateZ(float degrees) {
        // No-op default; platforms implement via poseStack.mulPose()
    }
}
