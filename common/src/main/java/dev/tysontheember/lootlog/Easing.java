package dev.tysontheember.lootlog;

/**
 * Easing functions for animations.
 * Each function maps a linear progress value t (0-1) to an eased value.
 */
public enum Easing {

    /** Decelerating quadratic: (1-t)^2 reversed. Current default for slide-in. */
    QUAD_OUT {
        @Override public float apply(float t) {
            float r = 1f - t;
            return 1f - r * r;
        }
    },

    /** Smoother deceleration: 1-(1-t)^3. */
    CUBIC_OUT {
        @Override public float apply(float t) {
            float r = 1f - t;
            return 1f - r * r * r;
        }
    },

    /** Slight overshoot then settle. */
    BACK_OUT {
        @Override public float apply(float t) {
            float c = 1.70158f;
            float r = t - 1f;
            return 1f + (c + 1f) * r * r * r + c * r * r;
        }
    },

    /** Springy bounce effect. */
    ELASTIC_OUT {
        @Override public float apply(float t) {
            if (t <= 0f) return 0f;
            if (t >= 1f) return 1f;
            return (float) (Math.pow(2, -10 * t)
                    * Math.sin((t * 10 - 0.75) * (2 * Math.PI / 3)) + 1);
        }
    };

    /** Apply this easing function to a linear progress value (0-1). */
    public abstract float apply(float t);

    /** Case-insensitive lookup by name. Returns QUAD_OUT for unknown names. */
    public static Easing byName(String name) {
        if (name == null || name.isEmpty()) return QUAD_OUT;
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return QUAD_OUT;
        }
    }
}
