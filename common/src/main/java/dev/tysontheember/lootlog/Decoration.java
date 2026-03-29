package dev.tysontheember.lootlog;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Named decoration presets that bundle a texture path, implied background style,
 * and layer configuration. Overrides can reference these by name via
 * {@code "decoration": "default_banner"} instead of specifying raw texture paths.
 */
public enum Decoration {

    /** Default two-layer banner: body (layer 0) + accent (layer 1). */
    DEFAULT_BANNER(
            "lootlog:textures/gui/lootlog/banner_body.png",
            BackgroundStyle.BANNER,
            Collections.unmodifiableList(Arrays.asList(
                    BannerLayer.builder(TextureSpec.banner(
                            "lootlog:textures/gui/lootlog/banner_body.png", 12)
                            .setPngDimensions(256, 12))
                            .animSpeed(4).build(),
                    BannerLayer.builder(TextureSpec.banner(
                            "lootlog:textures/gui/lootlog/banner_accent.png", 10)
                            .setPngDimensions(256, 10))
                            .anchor(BannerLayer.Anchor.ICON)
                            .animSpeed(4).build()
            ))
    );

    private final String texture;
    private final BackgroundStyle impliedStyle;
    private final List<BannerLayer> layers;

    Decoration(String texture, BackgroundStyle impliedStyle, List<BannerLayer> layers) {
        this.texture = texture;
        this.impliedStyle = impliedStyle;
        this.layers = layers;
    }

    public String getTexture() { return texture; }

    public BackgroundStyle getImpliedStyle() { return impliedStyle; }

    /** All layers in back-to-front order. Layer 0 = body. */
    public List<BannerLayer> getLayers() { return layers; }

    /** Layer 0's TextureSpec (body). Never null. */
    public TextureSpec getBodySpec() {
        return layers.get(0).getTexture();
    }

    /** Layer 1's TextureSpec (accent), or null if only one layer. */
    public TextureSpec getAccentSpec() {
        return layers.size() > 1 ? layers.get(1).getTexture() : null;
    }

    /** Shorthand for {@code getBodySpec()}. */
    public TextureSpec getTextureSpec() { return getBodySpec(); }

    /**
     * Case-insensitive lookup by name. Returns null for unknown names.
     */
    public static Decoration byName(String name) {
        if (name == null || name.isEmpty()) return null;
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
