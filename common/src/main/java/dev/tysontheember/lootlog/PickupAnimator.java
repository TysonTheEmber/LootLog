package dev.tysontheember.lootlog;

/**
 * Pure math for entry animations.
 * Given an entry's age and config durations, computes alpha, slide offset,
 * entrance scale, and stagger delays.
 */
public final class PickupAnimator {

    /** Horizontal icon size in pixels. */
    public static final int ICON_SIZE = 16;

    /** Gap between icon and text. */
    public static final int ICON_TEXT_GAP = 4;

    /** Entry height matches the icon. */
    public static final int ENTRY_HEIGHT = 16;

    private PickupAnimator() {}

    /**
     * Compute alpha (0.0-1.0) for an entry based on its age.
     * Timeline: [fadeIn] -> [hold at full alpha] -> [fadeOut]
     * Uses smoothstep easing for perceptually smooth transitions.
     */
    public static float computeAlpha(long entryAgeMs, LootLogConfig config) {
        return computeAlpha(entryAgeMs, config, config.getDisplayDurationMs());
    }

    /**
     * Compute alpha with an explicit display duration (for per-entry overrides).
     */
    public static float computeAlpha(long entryAgeMs, LootLogConfig config, long displayMs) {
        if (entryAgeMs < 0) return 0.0f;

        long fadeIn = config.getFadeInMs();
        long fadeOut = config.getFadeOutMs();
        long total = fadeIn + displayMs + fadeOut;

        if (entryAgeMs >= total) return 0.0f;

        // Fade-in phase
        if (entryAgeMs < fadeIn) {
            float t = fadeIn == 0 ? 1.0f : (float) entryAgeMs / fadeIn;
            return smoothstep(t);
        }

        // Hold phase
        if (entryAgeMs < fadeIn + displayMs) {
            return 1.0f;
        }

        // Fade-out phase
        long fadeOutElapsed = entryAgeMs - fadeIn - displayMs;
        float t = fadeOut == 0 ? 0.0f : 1.0f - (float) fadeOutElapsed / fadeOut;
        return smoothstep(t);
    }

    /**
     * Compute horizontal slide-in offset in pixels.
     * Starts at slideDistance (off-screen edge), slides to 0 during fade-in.
     * Uses the configured easing function for the slide motion.
     */
    public static float computeSlideOffset(long entryAgeMs, LootLogConfig config) {
        return computeSlideOffset(entryAgeMs, config, config.getSlideEasing());
    }

    /**
     * Compute horizontal slide-in offset with an explicit easing function.
     */
    public static float computeSlideOffset(long entryAgeMs, LootLogConfig config,
                                            Easing easing) {
        float slideDistance = config.getSlideDistance();
        long fadeIn = config.getFadeInMs();

        if (entryAgeMs < 0) return slideDistance;
        if (fadeIn == 0 || entryAgeMs >= fadeIn) return 0.0f;

        float progress = (float) entryAgeMs / fadeIn;
        float eased = easing.apply(progress);
        return slideDistance * (1.0f - eased);
    }

    /**
     * Compute horizontal slide-out offset in pixels during fade-out.
     * Returns 0 when not in fade-out or when fadeOutSlide is disabled.
     * Uses ease-in (accelerating) motion -- mirror of the slide-in.
     */
    public static float computeFadeOutSlideOffset(long entryAgeMs, LootLogConfig config) {
        if (!config.isFadeOutSlide()) return 0.0f;

        float slideDistance = config.getSlideDistance();
        long fadeIn = config.getFadeInMs();
        long display = config.getDisplayDurationMs();
        long fadeOut = config.getFadeOutMs();

        if (fadeOut == 0) {
            return entryAgeMs >= fadeIn + display ? slideDistance : 0.0f;
        }
        if (entryAgeMs < fadeIn + display) return 0.0f;

        long fadeOutElapsed = entryAgeMs - fadeIn - display;
        if (fadeOutElapsed >= fadeOut) return slideDistance;

        float progress = (float) fadeOutElapsed / fadeOut;
        return slideDistance * progress * progress;
    }

    /**
     * Compute entrance scale (0.0-1.0 range mapped from entranceScaleStart to 1.0).
     * During fade-in, the entry scales up from entranceScaleStart to 1.0.
     * Returns 1.0 outside the fade-in phase or if scale entrance is disabled.
     */
    public static float computeEntranceScale(long entryAgeMs, LootLogConfig config) {
        if (!config.isScaleEntrance()) return 1.0f;

        long fadeIn = config.getFadeInMs();
        if (fadeIn == 0 || entryAgeMs >= fadeIn || entryAgeMs < 0) return 1.0f;

        float progress = (float) entryAgeMs / fadeIn;
        float eased = smoothstep(progress);
        float start = config.getEntranceScaleStart();
        return start + (1.0f - start) * eased;
    }

    /**
     * Compute stagger delay for cascading entry animations.
     * Each entry is delayed by index * staggerDelayMs.
     * Returns the effective age adjusted for the stagger offset.
     */
    public static long applyStaggerDelay(long entryAgeMs, int index,
                                          LootLogConfig config) {
        int stagger = config.getStaggerDelayMs();
        if (stagger <= 0) return entryAgeMs;
        return Math.max(0, entryAgeMs - (long) index * stagger);
    }

    /**
     * Decay a vertical offset toward zero using framerate-independent exponential decay.
     * Normalized to 60fps so the speed parameter feels consistent regardless of framerate.
     * Snaps to 0 when |offset| < 0.5 pixels.
     */
    public static float decayVerticalOffset(float currentOffset, float deltaMs, float speed) {
        if (currentOffset == 0.0f) return 0.0f;
        float factor = (float) Math.pow(1.0 - speed, deltaMs / 16.667);
        float result = currentOffset * factor;
        return Math.abs(result) < 0.5f ? 0.0f : result;
    }

    /** Hermite smoothstep: ease-in-out curve for natural-looking transitions. */
    static float smoothstep(float t) {
        return t * t * (3.0f - 2.0f * t);
    }
}
