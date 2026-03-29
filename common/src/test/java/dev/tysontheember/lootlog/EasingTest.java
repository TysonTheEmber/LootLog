package dev.tysontheember.lootlog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EasingTest {

    @Test
    void allEasings_zeroReturnsZero() {
        for (Easing e : Easing.values()) {
            assertEquals(0f, e.apply(0f), 0.01f, e.name() + " at 0");
        }
    }

    @Test
    void allEasings_oneReturnsOne() {
        for (Easing e : Easing.values()) {
            assertEquals(1f, e.apply(1f), 0.01f, e.name() + " at 1");
        }
    }

    @Test
    void quadOut_midpoint() {
        // QUAD_OUT at 0.5: 1 - (0.5)^2 = 0.75
        assertEquals(0.75f, Easing.QUAD_OUT.apply(0.5f), 0.01f);
    }

    @Test
    void cubicOut_midpoint() {
        // CUBIC_OUT at 0.5: 1 - (0.5)^3 = 0.875
        assertEquals(0.875f, Easing.CUBIC_OUT.apply(0.5f), 0.01f);
    }

    @Test
    void backOut_overshoots() {
        // BACK_OUT should exceed 1.0 briefly before settling
        float peak = Easing.BACK_OUT.apply(0.5f);
        // At t=0.5, back_out should be noticeably > 0.5
        assertTrue(peak > 0.5f);
    }

    @Test
    void elasticOut_approaches1() {
        // ELASTIC_OUT should be close to 1.0 near the end
        float nearEnd = Easing.ELASTIC_OUT.apply(0.9f);
        assertTrue(nearEnd > 0.9f);
    }

    @Test
    void byName_caseInsensitive() {
        assertEquals(Easing.CUBIC_OUT, Easing.byName("cubic_out"));
        assertEquals(Easing.CUBIC_OUT, Easing.byName("CUBIC_OUT"));
    }

    @Test
    void byName_unknown_returnsDefault() {
        assertEquals(Easing.QUAD_OUT, Easing.byName("unknown"));
        assertEquals(Easing.QUAD_OUT, Easing.byName(null));
        assertEquals(Easing.QUAD_OUT, Easing.byName(""));
    }
}
