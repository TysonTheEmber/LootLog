package dev.tysontheember.lootlog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IconEffectRendererTest {

    @Test
    void glowPulseReturnsOneWhenSpeedIsZero() {
        assertEquals(1.0f, IconEffectRenderer.computeGlowPulse(5000, 0.0f, 0.3f, 1.0f));
    }

    @Test
    void glowPulseReturnsOneWhenSpeedIsNegative() {
        assertEquals(1.0f, IconEffectRenderer.computeGlowPulse(5000, -1.0f, 0.3f, 1.0f));
    }

    @Test
    void glowPulseOscillatesBetweenMinAndMax() {
        float min = 0.2f;
        float max = 0.8f;
        // Sample at many time points, verify all within bounds
        for (long t = 0; t < 5000; t += 17) {
            float v = IconEffectRenderer.computeGlowPulse(t, 2.0f, min, max);
            assertTrue(v >= min - 0.001f && v <= max + 0.001f,
                    "Pulse value " + v + " at t=" + t + " outside [" + min + "," + max + "]");
        }
    }

    @Test
    void glowPulseAtTimeZeroReturnsMax() {
        // cosineWave(0, speed) = cos(0) * 0.5 + 0.5 = 1.0 → returns max
        float result = IconEffectRenderer.computeGlowPulse(0, 1.0f, 0.3f, 1.0f);
        assertEquals(1.0f, result, 0.01f);
    }

    @Test
    void bounceReturnsOneWhenIntensityIsZero() {
        assertEquals(1.0f, IconEffectRenderer.computeBounce(System.currentTimeMillis(), 0.0f));
    }

    @Test
    void bounceReturnsOneWhenExpired() {
        // Bounce started 500ms ago, well past the 200ms duration
        long startMs = System.currentTimeMillis() - 500;
        assertEquals(1.0f, IconEffectRenderer.computeBounce(startMs, 0.15f));
    }

    @Test
    void bounceReturnsGreaterThanOneWhenActive() {
        // Bounce just started
        long startMs = System.currentTimeMillis();
        float scale = IconEffectRenderer.computeBounce(startMs, 0.15f);
        assertTrue(scale >= 1.0f, "Bounce scale should be >= 1.0 at start, got " + scale);
    }
}
