package dev.tysontheember.lootlog;

/** Color manipulation utilities for HUD rendering. */
public final class ColorUtil {

    private ColorUtil() {}

    /** Replace the alpha channel of an ARGB color. */
    public static int applyAlpha(int argb, float alpha) {
        int a = Math.round(alpha * 255) & 0xFF;
        return (a << 24) | (argb & 0x00FFFFFF);
    }

    /** Multiply the existing alpha channel by a factor (0-1). */
    public static int multiplyAlpha(int argb, float factor) {
        int existing = (argb >> 24) & 0xFF;
        int scaled = Math.round(existing * factor) & 0xFF;
        return (scaled << 24) | (argb & 0x00FFFFFF);
    }

    /** Pulsing green color for XP entries using cosine oscillation (~1.26s period). */
    public static int computeXpColor(long timeMs) {
        float t = (float) Math.cos(timeMs * 0.005);
        int green = (int) (200 + 55 * t);  // 145-255
        int red = (int) (80 + 40 * t);     // 40-120
        int blue = (int) (40 + 20 * t);    // 20-60
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }

    /** Multiply RGB channels by tint factors (0.0-1.0), preserving alpha. */
    public static int tintRgb(int argb, float tintR, float tintG, float tintB) {
        int a = (argb >> 24) & 0xFF;
        int r = clamp255(Math.round(((argb >> 16) & 0xFF) * tintR));
        int g = clamp255(Math.round(((argb >> 8) & 0xFF) * tintG));
        int b = clamp255(Math.round((argb & 0xFF) * tintB));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /** Linearly interpolate between two ARGB colors by factor t (0.0-1.0). */
    public static int lerpColor(int colorA, int colorB, float t) {
        float u = 1.0f - t;
        int a = Math.round(((colorA >> 24) & 0xFF) * u + ((colorB >> 24) & 0xFF) * t) & 0xFF;
        int r = Math.round(((colorA >> 16) & 0xFF) * u + ((colorB >> 16) & 0xFF) * t) & 0xFF;
        int g = Math.round(((colorA >> 8) & 0xFF) * u + ((colorB >> 8) & 0xFF) * t) & 0xFF;
        int b = Math.round((colorA & 0xFF) * u + (colorB & 0xFF) * t) & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /** Brighten RGB channels by an additive amount (clamped to 255), preserving alpha. */
    public static int brighten(int argb, int amount) {
        if (amount <= 0) return argb;
        int a = (argb >> 24) & 0xFF;
        int r = clamp255(((argb >> 16) & 0xFF) + amount);
        int g = clamp255(((argb >> 8) & 0xFF) + amount);
        int b = clamp255((argb & 0xFF) + amount);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int clamp255(int value) {
        return Math.max(0, Math.min(255, value));
    }

    /**
     * Resolve the text color for an entry based on config and override settings.
     * Priority: Override textColor > XP animated > rarity > name color override > white.
     */
    public static int resolveTextColor(PickupEntry entry, LootLogConfig config, long timeMs) {
        ResolvedOverride override = entry.getOverride();
        if (override != null && override.getTextColor() != null) {
            return override.getTextColor();
        }
        if (entry.getType() == PickupType.XP && config.isAnimateXpColor()) {
            return computeXpColor(timeMs);
        }
        if (config.isUseRarityColors()) {
            return entry.getRarityColor();
        }
        return config.getNameColorOverride();
    }
}
