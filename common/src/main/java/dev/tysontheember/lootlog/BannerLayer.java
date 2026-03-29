package dev.tysontheember.lootlog;

/**
 * Immutable data describing one layer of a multi-layer banner background.
 * Layer 0 is the body (determines entry height and banner width).
 * Additional layers are purely visual overlays.
 *
 * <p>Each layer's decorative edge can be anchored to a named element
 * (EDGE, ICON, NAME, COUNT) with an additional pixel offset.
 */
public final class BannerLayer {

    /** Where the layer's decorative edge is anchored. */
    public enum Anchor {
        /** Decorative edge of the banner (default for body/layer 0). */
        EDGE,
        /** Aligns to the icon element position. */
        ICON,
        /** Aligns to the name text position. */
        NAME,
        /** Aligns to the count text position. */
        COUNT
    }

    private final TextureSpec texture;
    private final Anchor anchor;
    private final int xOffset;
    private final int yOffset;
    private final float alpha;
    private final int tint;
    private final int animSpeed;
    private final boolean visible;

    private BannerLayer(TextureSpec texture, Anchor anchor, int xOffset, int yOffset,
                        float alpha, int tint, int animSpeed, boolean visible) {
        this.texture = texture;
        this.anchor = anchor;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.alpha = alpha;
        this.tint = tint;
        this.animSpeed = animSpeed;
        this.visible = visible;
    }

    public TextureSpec getTexture() { return texture; }
    public Anchor getAnchor() { return anchor; }
    public int getXOffset() { return xOffset; }
    public int getYOffset() { return yOffset; }
    public float getAlpha() { return alpha; }
    public int getTint() { return tint; }
    public int getAnimSpeed() { return animSpeed; }
    public boolean isVisible() { return visible; }

    public static Builder builder(TextureSpec texture) {
        return new Builder(texture);
    }

    public static final class Builder {
        private final TextureSpec texture;
        private Anchor anchor = Anchor.EDGE;
        private int xOffset;
        private int yOffset;
        private float alpha = 1.0f;
        private int tint = 0xFFFFFFFF;
        private int animSpeed;
        private boolean visible = true;

        Builder(TextureSpec texture) {
            this.texture = texture;
        }

        public Builder anchor(Anchor v) { this.anchor = v; return this; }
        public Builder xOffset(int v) { this.xOffset = v; return this; }
        public Builder yOffset(int v) { this.yOffset = v; return this; }
        public Builder alpha(float v) { this.alpha = v; return this; }
        public Builder tint(int v) { this.tint = v; return this; }
        public Builder animSpeed(int v) { this.animSpeed = v; return this; }
        public Builder visible(boolean v) { this.visible = v; return this; }

        public BannerLayer build() {
            return new BannerLayer(texture, anchor, xOffset, yOffset, alpha, tint, animSpeed, visible);
        }
    }
}
