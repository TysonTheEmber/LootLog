package dev.tysontheember.lootlog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PickupAnimatorTest {

    private LootLogConfig config;

    @BeforeEach
    void setUp() {
        config = new LootLogConfig();
        config.setFadeInMs(200);
        config.setDisplayDurationMs(1000);
        config.setFadeOutMs(300);
        config.setSlideDistance(20.0f);
    }

    // --- computeAlpha ---

    @Test
    void computeAlpha_atStart_isZero() {
        assertEquals(0.0f, PickupAnimator.computeAlpha(0, config), 0.01f);
    }

    @Test
    void computeAlpha_midFadeIn_isHalf() {
        assertEquals(0.5f, PickupAnimator.computeAlpha(100, config), 0.01f);
    }

    @Test
    void computeAlpha_endOfFadeIn_isFull() {
        assertEquals(1.0f, PickupAnimator.computeAlpha(200, config), 0.01f);
    }

    @Test
    void computeAlpha_duringHold_isFull() {
        assertEquals(1.0f, PickupAnimator.computeAlpha(700, config), 0.01f);
    }

    @Test
    void computeAlpha_midFadeOut_isHalf() {
        // fadeIn(200) + display(1000) + fadeOut/2(150) = 1350
        assertEquals(0.5f, PickupAnimator.computeAlpha(1350, config), 0.01f);
    }

    @Test
    void computeAlpha_pastLifetime_isZero() {
        assertEquals(0.0f, PickupAnimator.computeAlpha(1500, config), 0.01f);
    }

    @Test
    void computeAlpha_negativeAge_isZero() {
        assertEquals(0.0f, PickupAnimator.computeAlpha(-1, config), 0.01f);
    }

    @Test
    void computeAlpha_zeroFadeIn_snapsToFull() {
        config.setFadeInMs(0);
        assertEquals(1.0f, PickupAnimator.computeAlpha(0, config), 0.01f);
    }

    // --- computeSlideOffset (slide-in) ---

    @Test
    void computeSlideOffset_atStart_isMaxDistance() {
        assertEquals(20.0f, PickupAnimator.computeSlideOffset(0, config), 0.01f);
    }

    @Test
    void computeSlideOffset_midFadeIn_usesEaseOut() {
        // Quadratic ease-out: 20 * (1 - 0.5)^2 = 20 * 0.25 = 5.0
        assertEquals(5.0f, PickupAnimator.computeSlideOffset(100, config), 0.01f);
    }

    @Test
    void computeSlideOffset_afterFadeIn_isZero() {
        assertEquals(0.0f, PickupAnimator.computeSlideOffset(200, config), 0.01f);
    }

    @Test
    void computeSlideOffset_zeroFadeIn_isZero() {
        config.setFadeInMs(0);
        assertEquals(0.0f, PickupAnimator.computeSlideOffset(0, config), 0.01f);
    }

    @Test
    void computeSlideOffset_respectsConfigDistance() {
        config.setSlideDistance(40.0f);
        assertEquals(40.0f, PickupAnimator.computeSlideOffset(0, config), 0.01f);
    }

    // --- computeFadeOutSlideOffset ---

    @Test
    void computeFadeOutSlideOffset_disabled_returnsZero() {
        config.setFadeOutSlide(false);
        // At mid fade-out: fadeIn(200) + display(1000) + 150 = 1350
        assertEquals(0.0f, PickupAnimator.computeFadeOutSlideOffset(1350, config), 0.01f);
    }

    @Test
    void computeFadeOutSlideOffset_beforeFadeOut_returnsZero() {
        config.setFadeOutSlide(true);
        // During hold phase
        assertEquals(0.0f, PickupAnimator.computeFadeOutSlideOffset(700, config), 0.01f);
    }

    @Test
    void computeFadeOutSlideOffset_midFadeOut_returnsPartial() {
        config.setFadeOutSlide(true);
        // fadeIn(200) + display(1000) + 150 = 1350 → progress = 0.5
        // Ease-in: 20 * 0.5^2 = 5.0
        assertEquals(5.0f, PickupAnimator.computeFadeOutSlideOffset(1350, config), 0.01f);
    }

    @Test
    void computeFadeOutSlideOffset_afterFadeOut_returnsFullDistance() {
        config.setFadeOutSlide(true);
        // fadeIn(200) + display(1000) + fadeOut(300) = 1500
        assertEquals(20.0f, PickupAnimator.computeFadeOutSlideOffset(1500, config), 0.01f);
    }

    @Test
    void computeFadeOutSlideOffset_zeroFadeOut_snapsToFullDistance() {
        config.setFadeOutMs(0);
        config.setFadeOutSlide(true);
        // At exactly fadeIn + display boundary
        assertEquals(20.0f, PickupAnimator.computeFadeOutSlideOffset(1200, config), 0.01f);
    }

    // --- decayVerticalOffset ---

    @Test
    void decayVerticalOffset_zero_returnsZero() {
        assertEquals(0.0f, PickupAnimator.decayVerticalOffset(0.0f, 16.667f, 0.3f));
    }

    @Test
    void decayVerticalOffset_decaysTowardZero() {
        float result = PickupAnimator.decayVerticalOffset(20.0f, 16.667f, 0.3f);
        assertTrue(result > 0.0f && result < 20.0f, "Should decay toward zero: " + result);
    }

    @Test
    void decayVerticalOffset_snapsToZero() {
        // Very small offset should snap to 0
        assertEquals(0.0f, PickupAnimator.decayVerticalOffset(0.4f, 16.667f, 0.3f));
    }

    @Test
    void decayVerticalOffset_negativeOffset_decaysUpward() {
        float result = PickupAnimator.decayVerticalOffset(-20.0f, 16.667f, 0.3f);
        assertTrue(result < 0.0f && result > -20.0f, "Negative offset should decay toward zero: " + result);
    }

    @Test
    void decayVerticalOffset_highSpeed_decaysFaster() {
        float slow = PickupAnimator.decayVerticalOffset(20.0f, 16.667f, 0.1f);
        float fast = PickupAnimator.decayVerticalOffset(20.0f, 16.667f, 0.8f);
        assertTrue(fast < slow, "Higher speed should produce smaller result: slow=" + slow + " fast=" + fast);
    }

    // --- smoothstep ---

    @Test
    void smoothstep_atZero_isZero() {
        assertEquals(0.0f, PickupAnimator.smoothstep(0.0f), 0.001f);
    }

    @Test
    void smoothstep_atHalf_isHalf() {
        assertEquals(0.5f, PickupAnimator.smoothstep(0.5f), 0.001f);
    }

    @Test
    void smoothstep_atOne_isOne() {
        assertEquals(1.0f, PickupAnimator.smoothstep(1.0f), 0.001f);
    }

}
