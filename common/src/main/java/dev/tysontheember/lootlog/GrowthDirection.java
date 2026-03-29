package dev.tysontheember.lootlog;

/**
 * Controls whether new entries grow in the natural direction (away from the
 * anchor edge) or the inverse direction. Bottom anchors naturally grow up;
 * top anchors naturally grow down. INVERSE reverses this.
 */
public enum GrowthDirection {
    NORMAL,
    INVERSE
}
