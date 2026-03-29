package dev.tysontheember.lootlog;

public enum AccentAnchor {
    EDGE,
    ICON,
    NAME,
    COUNT;

    public static AccentAnchor fromString(String s) {
        if (s == null || s.isEmpty()) return ICON;
        try {
            return valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ICON;
        }
    }
}
