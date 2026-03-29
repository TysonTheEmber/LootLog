package dev.tysontheember.lootlog;

public enum IconShape {
    CIRCLE,
    ITEM,
    SQUARE,
    DIAMOND;

    public static IconShape fromString(String s) {
        if (s == null || s.isEmpty()) return CIRCLE;
        try {
            return valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CIRCLE;
        }
    }
}
