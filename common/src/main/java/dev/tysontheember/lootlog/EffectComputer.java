package dev.tysontheember.lootlog;

import java.util.List;

/**
 * Computes {@link EffectModifiers} from a list of background effects.
 * Each effect type produces modifier values that the background renderer
 * uses when making its own render calls, so effects are integrated into
 * the background rather than overlaid as separate geometry.
 */
public final class EffectComputer {

    private EffectComputer() {}

    /**
     * Compute combined modifiers from all effects on an entry.
     *
     * @param effects      list of background effects (may be empty)
     * @param entryAlpha   current fade alpha (0-1)
     * @param timeMs       animation time in ms
     * @param entryAgeMs   age of the entry since creation
     * @param entryWidth   rendered width of entry in pixels
     * @param entryHeight  rendered height of entry in pixels
     * @return composed modifiers, or {@link EffectModifiers#IDENTITY} if no effects
     */
    public static EffectModifiers compute(List<ResolvedOverride.BgEffect> effects,
                                           float entryAlpha, long timeMs, long entryAgeMs,
                                           int entryWidth, int entryHeight) {
        if (effects == null || effects.isEmpty()) {
            return EffectModifiers.IDENTITY;
        }

        EffectModifiers result = EffectModifiers.IDENTITY;
        for (ResolvedOverride.BgEffect effect : effects) {
            EffectModifiers mods = computeSingle(effect, entryAlpha, timeMs, entryAgeMs,
                    entryWidth, entryHeight);
            result = EffectModifiers.compose(result, mods);
        }
        return result;
    }

    private static EffectModifiers computeSingle(ResolvedOverride.BgEffect effect,
                                                  float entryAlpha, long timeMs, long entryAgeMs,
                                                  int entryWidth, int entryHeight) {
        switch (effect.getType()) {
            case "tint":        return computeTint(effect);
            case "pulse":       return computePulse(effect, timeMs);
            case "border_glow": return computeBorderGlow(effect, timeMs);
            case "sweep":       return computeSweep(effect, timeMs, entryWidth);
            case "color_shift": return computeColorShift(effect, timeMs);
            case "flash":       return computeFlash(effect, entryAgeMs);
            // Legacy types from old system -- map to new equivalents
            case "shimmer":     return computeSweep(effect, timeMs, entryWidth);
            default:            return EffectModifiers.IDENTITY;
        }
    }

    /** Static color tint applied to the background. */
    private static EffectModifiers computeTint(ResolvedOverride.BgEffect effect) {
        float intensity = effect.getIntensity();
        float r = ((effect.getColor() >> 16) & 0xFF) / 255f;
        float g = ((effect.getColor() >> 8) & 0xFF) / 255f;
        float b = (effect.getColor() & 0xFF) / 255f;

        float tintR = lerp(1f, r, intensity);
        float tintG = lerp(1f, g, intensity);
        float tintB = lerp(1f, b, intensity);

        return new EffectModifiers.Builder()
                .bodyTint(tintR, tintG, tintB)
                .texTint(tintR, tintG, tintB)
                .borderTint(tintR, tintG, tintB)
                .build();
    }

    /** Background breathes: alpha and brightness oscillate. */
    private static EffectModifiers computePulse(ResolvedOverride.BgEffect effect, long timeMs) {
        float t = cosineWave(timeMs, effect.getSpeed());
        float alphaMul = lerp(effect.getMinAlpha(), effect.getMaxAlpha(), t);
        int brighten = Math.round(effect.getIntensity() * t * 60);

        // Optional color tint at peak
        float intensity = effect.getIntensity() * t;
        float r = ((effect.getColor() >> 16) & 0xFF) / 255f;
        float g = ((effect.getColor() >> 8) & 0xFF) / 255f;
        float b = (effect.getColor() & 0xFF) / 255f;
        float tintR = lerp(1f, r, intensity);
        float tintG = lerp(1f, g, intensity);
        float tintB = lerp(1f, b, intensity);

        return new EffectModifiers.Builder()
                .bodyAlpha(alphaMul)
                .bodyBrighten(brighten)
                .bodyTint(tintR, tintG, tintB)
                .texTint(tintR, tintG, tintB)
                .borderAlpha(alphaMul)
                .build();
    }

    /** Border elements pulse independently of body. */
    private static EffectModifiers computeBorderGlow(ResolvedOverride.BgEffect effect, long timeMs) {
        float t = cosineWave(timeMs, effect.getSpeed());
        float intensity = lerp(effect.getMinAlpha(), effect.getMaxAlpha(), t);
        int brighten = Math.round(intensity * 80);

        float r = ((effect.getColor() >> 16) & 0xFF) / 255f;
        float g = ((effect.getColor() >> 8) & 0xFF) / 255f;
        float b = (effect.getColor() & 0xFF) / 255f;
        float tintR = lerp(1f, r, t * effect.getIntensity());
        float tintG = lerp(1f, g, t * effect.getIntensity());
        float tintB = lerp(1f, b, t * effect.getIntensity());

        return new EffectModifiers.Builder()
                .borderBrighten(brighten)
                .borderTint(tintR, tintG, tintB)
                .build();
    }

    /** Horizontal shine band sweeps through the background. */
    private static EffectModifiers computeSweep(ResolvedOverride.BgEffect effect,
                                                 long timeMs, int entryWidth) {
        if (entryWidth <= 0) return EffectModifiers.IDENTITY;

        float bandWidth = Math.max(0.05f, Math.min(effect.getWidth(), 1.0f));
        float period = 1.0f + bandWidth; // sweep from -bandWidth to 1.0
        float totalCycleMs = (period / (effect.getSpeed() * 0.001f * 60f));
        float pauseMs = effect.getPauseMs();
        float fullCycleMs = totalCycleMs + pauseMs;

        float elapsed = timeMs % (long) Math.max(1, fullCycleMs);
        if (elapsed >= totalCycleMs) {
            // In pause phase
            return EffectModifiers.IDENTITY;
        }

        float pos = (elapsed / totalCycleMs) * period - bandWidth;

        return new EffectModifiers.Builder()
                .sweep(pos, bandWidth, effect.getIntensity())
                .build();
    }

    /** Cycles background tint through multiple colors. */
    private static EffectModifiers computeColorShift(ResolvedOverride.BgEffect effect, long timeMs) {
        int[] colorStops = effect.getColors();
        if (colorStops == null || colorStops.length < 2) {
            return computeTint(effect); // fallback to static tint
        }

        float totalPhase = (timeMs * 0.001f * effect.getSpeed()) % 1.0f;
        if (totalPhase < 0) totalPhase += 1.0f;

        int segCount = colorStops.length;
        float scaled = totalPhase * segCount;
        int segIdx = (int) scaled % segCount;
        float segT = scaled - (int) scaled;

        int colorA = colorStops[segIdx];
        int colorB = colorStops[(segIdx + 1) % segCount];
        int current = ColorUtil.lerpColor(colorA, colorB, segT);

        float intensity = effect.getIntensity();
        float r = ((current >> 16) & 0xFF) / 255f;
        float g = ((current >> 8) & 0xFF) / 255f;
        float b = (current & 0xFF) / 255f;
        float tintR = lerp(1f, r, intensity);
        float tintG = lerp(1f, g, intensity);
        float tintB = lerp(1f, b, intensity);

        return new EffectModifiers.Builder()
                .bodyTint(tintR, tintG, tintB)
                .texTint(tintR, tintG, tintB)
                .borderTint(tintR, tintG, tintB)
                .build();
    }

    /** One-shot bright flash on pickup, rapid quadratic decay. */
    private static EffectModifiers computeFlash(ResolvedOverride.BgEffect effect, long entryAgeMs) {
        int durationMs = effect.getDurationMs();
        if (durationMs <= 0 || entryAgeMs >= durationMs) {
            return EffectModifiers.IDENTITY;
        }

        float t = 1.0f - ((float) entryAgeMs / durationMs);
        float eased = t * t; // quadratic ease-out

        float intensity = effect.getIntensity() * eased;
        int brighten = Math.round(intensity * 120);

        float r = ((effect.getColor() >> 16) & 0xFF) / 255f;
        float g = ((effect.getColor() >> 8) & 0xFF) / 255f;
        float b = (effect.getColor() & 0xFF) / 255f;
        float tintR = lerp(1f, r, intensity);
        float tintG = lerp(1f, g, intensity);
        float tintB = lerp(1f, b, intensity);

        return new EffectModifiers.Builder()
                .bodyBrighten(brighten)
                .bodyTint(tintR, tintG, tintB)
                .texTint(tintR, tintG, tintB)
                .borderBrighten(brighten)
                .borderTint(tintR, tintG, tintB)
                .build();
    }

    // --- Utility ---

    /** Cosine wave: returns 0.0-1.0 oscillating at the given speed (cycles/sec). */
    static float cosineWave(long timeMs, float speed) {
        return (float) (Math.cos(timeMs * 0.001 * speed * Math.PI * 2) * 0.5 + 0.5);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
