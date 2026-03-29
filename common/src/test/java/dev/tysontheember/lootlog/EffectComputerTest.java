package dev.tysontheember.lootlog;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EffectComputerTest {

    private static ResolvedOverride.BgEffect effect(String type, int color, float speed,
                                                      float intensity) {
        return new ResolvedOverride.BgEffect(type, color, speed, 0.8f, 1.0f, 0.2f,
                intensity, 400, 0, null);
    }

    private static ResolvedOverride.BgEffect effectFull(String type, int color, float speed,
                                                          float minAlpha, float maxAlpha,
                                                          float width, float intensity,
                                                          int durationMs, int pauseMs, int[] colors) {
        return new ResolvedOverride.BgEffect(type, color, speed, minAlpha, maxAlpha, width,
                intensity, durationMs, pauseMs, colors);
    }

    // --- Empty/null returns IDENTITY ---

    @Test
    void emptyEffects_returnsIdentity() {
        EffectModifiers mods = EffectComputer.compute(Collections.emptyList(), 1f, 0, 0, 100, 20);
        assertTrue(mods.isIdentity());
    }

    @Test
    void nullEffects_returnsIdentity() {
        EffectModifiers mods = EffectComputer.compute(null, 1f, 0, 0, 100, 20);
        assertTrue(mods.isIdentity());
    }

    // --- Tint ---

    @Test
    void tint_appliesColorTint() {
        // Pure red tint at 50% intensity
        ResolvedOverride.BgEffect tint = effect("tint", 0xFFFF0000, 0, 0.5f);
        EffectModifiers mods = EffectComputer.compute(List.of(tint), 1f, 0, 0, 100, 20);

        // bodyTintR should be lerp(1.0, 1.0, 0.5) = 1.0 (red channel is already 1.0)
        assertEquals(1.0f, mods.bodyTintR, 0.01f);
        // bodyTintG should be lerp(1.0, 0.0, 0.5) = 0.5 (green channel is 0)
        assertEquals(0.5f, mods.bodyTintG, 0.01f);
        assertEquals(0.5f, mods.bodyTintB, 0.01f);
    }

    @Test
    void tint_zeroIntensity_noChange() {
        ResolvedOverride.BgEffect tint = effect("tint", 0xFFFF0000, 0, 0.0f);
        EffectModifiers mods = EffectComputer.compute(List.of(tint), 1f, 0, 0, 100, 20);

        assertEquals(1.0f, mods.bodyTintR, 0.01f);
        assertEquals(1.0f, mods.bodyTintG, 0.01f);
        assertEquals(1.0f, mods.bodyTintB, 0.01f);
    }

    // --- Pulse ---

    @Test
    void pulse_oscillatesAlpha() {
        ResolvedOverride.BgEffect pulse = effectFull("pulse", 0xFFFFFFFF, 1.0f,
                0.5f, 1.0f, 0, 0.3f, 0, 0, null);

        // At t=0, cosine wave is at peak (1.0)
        EffectModifiers atPeak = EffectComputer.compute(List.of(pulse), 1f, 0, 0, 100, 20);
        // At half period (500ms for 1 Hz), cosine wave is at trough (0.0)
        EffectModifiers atTrough = EffectComputer.compute(List.of(pulse), 1f, 500, 0, 100, 20);

        // At peak: alpha = lerp(0.5, 1.0, 1.0) = 1.0
        assertEquals(1.0f, atPeak.bodyAlphaMultiplier, 0.01f);
        // At trough: alpha = lerp(0.5, 1.0, 0.0) = 0.5
        assertEquals(0.5f, atTrough.bodyAlphaMultiplier, 0.01f);
    }

    // --- Border Glow ---

    @Test
    void borderGlow_onlyAffectsBorders() {
        ResolvedOverride.BgEffect glow = effect("border_glow", 0xFFFF0000, 1.0f, 0.8f);
        EffectModifiers mods = EffectComputer.compute(List.of(glow), 1f, 0, 0, 100, 20);

        // Body tint should be unchanged (1.0)
        assertEquals(1.0f, mods.bodyTintR, 0.01f);
        assertEquals(1.0f, mods.bodyTintG, 0.01f);
        // Border should be modified
        assertTrue(mods.borderBrighten > 0);
    }

    // --- Sweep ---

    @Test
    void sweep_producesSweepData() {
        ResolvedOverride.BgEffect sweep = effectFull("sweep", 0xFFFFFFFF, 60f,
                0, 0, 0.2f, 0.6f, 0, 0, null);
        EffectModifiers mods = EffectComputer.compute(List.of(sweep), 1f, 100, 0, 200, 20);

        // Should have active sweep
        assertTrue(mods.sweepPosition >= -1f);
        assertEquals(0.2f, mods.sweepWidth, 0.01f);
        assertEquals(0.6f, mods.sweepIntensity, 0.01f);
    }

    // --- Color Shift ---

    @Test
    void colorShift_interpolatesBetweenColors() {
        int[] colors = { 0xFFFF0000, 0xFF00FF00 }; // red, green
        ResolvedOverride.BgEffect shift = effectFull("color_shift", 0, 1.0f,
                0, 0, 0, 0.8f, 0, 0, colors);

        // At t=0 (start): should be tinted toward red
        EffectModifiers mods = EffectComputer.compute(List.of(shift), 1f, 0, 0, 100, 20);
        assertFalse(mods.isIdentity());
        // Tint should lean toward red at this point
        assertTrue(mods.bodyTintR > mods.bodyTintG);
    }

    @Test
    void colorShift_fallsBackToTint_withoutColors() {
        ResolvedOverride.BgEffect shift = effect("color_shift", 0xFFFF0000, 1.0f, 0.5f);
        EffectModifiers mods = EffectComputer.compute(List.of(shift), 1f, 0, 0, 100, 20);

        // Should fall back to tint behavior
        assertFalse(mods.isIdentity());
    }

    // --- Flash ---

    @Test
    void flash_brightAtStart_decaysToIdentity() {
        ResolvedOverride.BgEffect flash = effectFull("flash", 0xFFFFFFFF, 0,
                0, 0, 0, 1.0f, 400, 0, null);

        // At age 0: full brightness
        EffectModifiers atStart = EffectComputer.compute(List.of(flash), 1f, 0, 0, 100, 20);
        assertTrue(atStart.bodyBrighten > 100);

        // At age 200 (half): reduced brightness
        EffectModifiers atHalf = EffectComputer.compute(List.of(flash), 1f, 0, 200, 100, 20);
        assertTrue(atHalf.bodyBrighten > 0);
        assertTrue(atHalf.bodyBrighten < atStart.bodyBrighten);

        // At age 400+: should be identity
        EffectModifiers atEnd = EffectComputer.compute(List.of(flash), 1f, 0, 500, 100, 20);
        assertTrue(atEnd.isIdentity());
    }

    // --- Composition ---

    @Test
    void composition_tintsThenFlash() {
        ResolvedOverride.BgEffect tint = effect("tint", 0xFFFF0000, 0, 0.3f);
        ResolvedOverride.BgEffect flash = effectFull("flash", 0xFFFFFFFF, 0,
                0, 0, 0, 1.0f, 400, 0, null);

        EffectModifiers mods = EffectComputer.compute(Arrays.asList(tint, flash), 1f, 0, 0, 100, 20);

        // Should have both tint (< 1.0 on G/B channels) AND brightness
        assertTrue(mods.bodyTintG < 1.0f);
        assertTrue(mods.bodyBrighten > 0);
    }

    // --- Cosine wave utility ---

    @Test
    void cosineWave_oscillatesBetweenZeroAndOne() {
        // At t=0: cos(0) = 1 → (1*0.5+0.5) = 1.0
        assertEquals(1.0f, EffectComputer.cosineWave(0, 1.0f), 0.01f);
        // At half period (500ms for 1 Hz): cos(PI) = -1 → (-1*0.5+0.5) = 0.0
        assertEquals(0.0f, EffectComputer.cosineWave(500, 1.0f), 0.01f);
    }
}
