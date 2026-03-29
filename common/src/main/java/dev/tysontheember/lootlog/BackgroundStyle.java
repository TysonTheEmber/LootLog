package dev.tysontheember.lootlog;

/**
 * Controls the visual style of entry backgrounds on the HUD.
 */
public enum BackgroundStyle {
    /** No background rendered. */
    NONE,
    /** Flat solid-color rectangle. */
    SOLID,
    /** Vanilla tooltip look: dark fill with purple gradient border. */
    TOOLTIP,
    /** Custom spritesheet texture with 9-slice rendering (resource pack overridable). */
    TEXTURE,
    /** Multi-layer textured banner at native pixel size. */
    BANNER,
    /** Solid color extending to screen edge, uses banner element positioning. */
    FLAT
}
