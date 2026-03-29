package dev.tysontheember.lootlog;

/**
 * Immutable descriptor for a popup background texture.
 * Each texture is its own file — PNG dimensions are read at load time.
 * {@code sourceHeight} defines one animation frame's height and the render height.
 */
public final class TextureSpec {

    /** Default banner body texture. */
    public static final TextureSpec DEFAULT_BANNER =
            banner("lootlog:textures/gui/lootlog/banner_body.png", 12)
                    .setPngDimensions(256, 12);

    /** Default 9-slice texture. */
    public static final TextureSpec DEFAULT_NINE_SLICE =
            nineSlice("lootlog:textures/gui/lootlog/popup_bg.png", 16, 4)
                    .setPngDimensions(16, 16);

    private final String texturePath;
    private final int sourceHeight;
    private final RenderMode renderMode;
    private final int sliceBorder;

    // Resolved at load time (read from PNG)
    private int pngWidth;
    private int pngHeight;

    private TextureSpec(Builder b) {
        this.texturePath = b.texturePath;
        this.sourceHeight = b.sourceHeight;
        this.renderMode = b.renderMode;
        this.sliceBorder = b.sliceBorder;
        // Default PNG dimensions to match sourceHeight until actual PNG is loaded
        this.pngWidth = b.pngWidth;
        this.pngHeight = b.pngHeight;
    }

    // --- Static factories ---

    /** Banner texture: STRETCH mode, no slicing. */
    public static TextureSpec banner(String path, int sourceHeight) {
        return new Builder(path)
                .sourceHeight(sourceHeight)
                .renderMode(RenderMode.STRETCH)
                .build();
    }

    /** 9-slice texture with configurable slice border. */
    public static TextureSpec nineSlice(String path, int sourceHeight, int sliceBorder) {
        return new Builder(path)
                .sourceHeight(sourceHeight)
                .renderMode(RenderMode.NINE_SLICE)
                .sliceBorder(sliceBorder)
                .build();
    }

    /** Fully custom texture spec. */
    public static Builder custom(String path) {
        return new Builder(path);
    }

    // --- Getters ---

    public String getTexturePath() { return texturePath; }
    public int getSourceHeight() { return sourceHeight; }
    public RenderMode getRenderMode() { return renderMode; }
    public int getSliceBorder() { return sliceBorder; }
    public int getPngWidth() { return pngWidth; }
    public int getPngHeight() { return pngHeight; }

    // --- Derived values ---

    /** Number of animation frames (vertically stacked in the PNG). */
    public int getFrameCount() {
        return pngHeight > 0 && sourceHeight > 0 ? pngHeight / sourceHeight : 1;
    }

    /** Render width = PNG width. */
    public int getRenderWidth() { return pngWidth; }

    /** Render height = one frame's height. */
    public int getRenderHeight() { return sourceHeight; }

    /**
     * Set actual PNG pixel dimensions. Called once at texture load time.
     * Returns this for chaining on static constants.
     */
    public TextureSpec setPngDimensions(int width, int height) {
        this.pngWidth = width;
        this.pngHeight = height;
        return this;
    }

    /**
     * Create a copy with a different texture path but same dimensions.
     */
    public TextureSpec withPath(String newPath) {
        TextureSpec copy = new Builder(newPath)
                .sourceHeight(sourceHeight)
                .renderMode(renderMode)
                .sliceBorder(sliceBorder)
                .pngSize(pngWidth, pngHeight)
                .build();
        return copy;
    }

    // --- Builder ---

    public static final class Builder {
        private final String texturePath;
        private int sourceHeight = 12;
        private RenderMode renderMode = RenderMode.STRETCH;
        private int sliceBorder = 0;
        private int pngWidth = 256;
        private int pngHeight = 12;

        public Builder(String texturePath) {
            this.texturePath = texturePath;
        }

        public Builder sourceHeight(int h) {
            this.sourceHeight = h;
            this.pngHeight = h; // default pngHeight to sourceHeight (single frame)
            return this;
        }

        public Builder renderMode(RenderMode mode) {
            this.renderMode = mode; return this;
        }

        public Builder sliceBorder(int border) {
            this.sliceBorder = border; return this;
        }

        public Builder pngSize(int w, int h) {
            this.pngWidth = w; this.pngHeight = h; return this;
        }

        public TextureSpec build() {
            return new TextureSpec(this);
        }
    }
}
