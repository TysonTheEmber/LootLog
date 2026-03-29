package dev.tysontheember.lootlog;

/**
 * How a background texture is rendered onto the popup.
 */
public enum RenderMode {
    /** Stretch the full source region to fill the popup width. */
    STRETCH,
    /** 9-slice the source region so borders stay crisp at any size. */
    NINE_SLICE
}
