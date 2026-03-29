package dev.tysontheember.lootlog;

public enum EffectTarget {
    ALL,
    ITEMS,
    XP;

    public static EffectTarget fromString(String s) {
        if (s == null || s.isEmpty()) return ALL;
        try {
            return valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ALL;
        }
    }
}
