package dev.tysontheember.lootlog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LootLogConfigTest {

    // --- Clamping validation ---

    @Test
    void displayDurationMs_clampsBelow() {
        LootLogConfig c = new LootLogConfig();
        c.setDisplayDurationMs(100);
        assertEquals(500, c.getDisplayDurationMs());
    }

    @Test
    void displayDurationMs_clampsAbove() {
        LootLogConfig c = new LootLogConfig();
        c.setDisplayDurationMs(99999);
        assertEquals(30000, c.getDisplayDurationMs());
    }

    @Test
    void displayDurationMs_acceptsInRange() {
        LootLogConfig c = new LootLogConfig();
        c.setDisplayDurationMs(3000);
        assertEquals(3000, c.getDisplayDurationMs());
    }

    @Test
    void fadeInMs_clampsAbove() {
        LootLogConfig c = new LootLogConfig();
        c.setFadeInMs(5000);
        assertEquals(2000, c.getFadeInMs());
    }

    @Test
    void maxEntries_clampsBelow() {
        LootLogConfig c = new LootLogConfig();
        c.setMaxEntries(0);
        assertEquals(1, c.getMaxEntries());
    }

    @Test
    void scale_clampsBounds() {
        LootLogConfig c = new LootLogConfig();
        c.setScale(0.1f);
        assertEquals(0.25f, c.getScale());
        c.setScale(10f);
        assertEquals(4.0f, c.getScale());
    }

    @Test
    void verticalAnimSpeed_clampsBounds() {
        LootLogConfig c = new LootLogConfig();
        c.setVerticalAnimSpeed(-0.5f);
        assertEquals(0f, c.getVerticalAnimSpeed());
        c.setVerticalAnimSpeed(2f);
        assertEquals(1f, c.getVerticalAnimSpeed());
    }

    // --- Defaults ---

    @Test
    void defaults_general() {
        LootLogConfig c = new LootLogConfig();
        assertEquals(5000, c.getDisplayDurationMs());
        assertEquals(10, c.getMaxEntries());
        assertEquals(CombineMode.ALWAYS, c.getCombineMode());
        assertTrue(c.isShowItems());
        assertTrue(c.isShowXp());
    }

    @Test
    void defaults_position() {
        LootLogConfig c = new LootLogConfig();
        assertEquals(HudAnchor.BOTTOM_RIGHT, c.getAnchor());
        assertEquals(5, c.getXOffset());
        assertEquals(5, c.getYOffset());
        assertEquals(2, c.getEntrySpacing());
        assertEquals(1.0f, c.getScale());
        assertTrue(c.isClampToScreen());
        assertEquals(GrowthDirection.NORMAL, c.getGrowthDirection());
    }

    @Test
    void defaults_animation() {
        LootLogConfig c = new LootLogConfig();
        assertEquals(300, c.getFadeInMs());
        assertEquals(500, c.getFadeOutMs());
        assertEquals(220f, c.getSlideDistance());
        assertTrue(c.isFadeOutSlide());
        assertEquals(0.3f, c.getVerticalAnimSpeed());
    }

    @Test
    void defaults_appearance() {
        LootLogConfig c = new LootLogConfig();
        assertEquals(BackgroundStyle.BANNER, c.getBackgroundStyle());
        assertEquals(0xFFAAAAAA, c.getCountColor());
        assertEquals(0xFFFFFFFF, c.getNameColorOverride());
        assertTrue(c.isShowCount());
        assertTrue(c.isUseRarityColors());
        assertTrue(c.isAnimateXpColor());
        assertTrue(c.isAbbreviateCounts());
    }

    @Test
    void defaults_iconGlow() {
        LootLogConfig c = new LootLogConfig();
        assertFalse(c.isIconGlowEnabled());
        assertEquals(3, c.getIconGlowRadius());
        assertEquals(IconShape.CIRCLE, c.getIconGlowShape());
        assertEquals(1.5f, c.getIconGlowSoftness());
        assertEquals(0.0f, c.getIconGlowPulseSpeed());
    }

    @Test
    void defaults_iconShadow() {
        LootLogConfig c = new LootLogConfig();
        assertFalse(c.isIconShadowEnabled());
        assertEquals(1, c.getIconShadowOffsetX());
        assertEquals(1, c.getIconShadowOffsetY());
        assertEquals(1, c.getIconShadowRadius());
        assertEquals(IconShape.ITEM, c.getIconShadowShape());
    }

    @Test
    void iconGlowRadius_clamps() {
        LootLogConfig c = new LootLogConfig();
        c.setIconGlowRadius(-1);
        assertEquals(0, c.getIconGlowRadius());
        c.setIconGlowRadius(20);
        assertEquals(8, c.getIconGlowRadius());
    }

    @Test
    void iconGlowSoftness_clamps() {
        LootLogConfig c = new LootLogConfig();
        c.setIconGlowSoftness(0.1f);
        assertEquals(0.5f, c.getIconGlowSoftness());
        c.setIconGlowSoftness(10.0f);
        assertEquals(5.0f, c.getIconGlowSoftness());
    }

    @Test
    void iconShadowRadius_clamps() {
        LootLogConfig c = new LootLogConfig();
        c.setIconShadowRadius(-1);
        assertEquals(0, c.getIconShadowRadius());
        c.setIconShadowRadius(10);
        assertEquals(4, c.getIconShadowRadius());
    }

    @Test
    void pickupPulseScaleStrength_clamps() {
        LootLogConfig c = new LootLogConfig();
        c.setPickupPulseIconScaleStrength(-0.5f);
        assertEquals(0.0f, c.getPickupPulseIconScaleStrength());
        c.setPickupPulseIconScaleStrength(1.0f);
        assertEquals(0.5f, c.getPickupPulseIconScaleStrength());
    }

    @Test
    void pickupPulseDuration_clamps() {
        LootLogConfig c = new LootLogConfig();
        c.setPickupPulseDurationMs(10);
        assertEquals(50, c.getPickupPulseDurationMs());
        c.setPickupPulseDurationMs(5000);
        assertEquals(1000, c.getPickupPulseDurationMs());
    }

    @Test
    void progressBarHeight_clamps() {
        LootLogConfig c = new LootLogConfig();
        c.setProgressBarHeight(0);
        assertEquals(1, c.getProgressBarHeight());
        c.setProgressBarHeight(5);
        assertEquals(3, c.getProgressBarHeight());
    }

    @Test
    void totalLifetimeMs_computed() {
        LootLogConfig c = new LootLogConfig();
        assertEquals(300 + 5000 + 500, c.getTotalLifetimeMs());
    }

    // --- Per-type toggles in PickupTracker ---

    @Test
    void perTypeToggle_itemsDisabled_filtersItems() {
        LootLogConfig config = new LootLogConfig();
        config.setShowItems(false);
        PickupTracker tracker = new PickupTracker(config);

        tracker.addEntry(PickupEntry.builder("Diamond", 1, PickupType.ITEM).build());
        assertEquals(0, tracker.size());
    }

    @Test
    void perTypeToggle_xpDisabled_filtersXp() {
        LootLogConfig config = new LootLogConfig();
        config.setShowXp(false);
        PickupTracker tracker = new PickupTracker(config);

        tracker.addEntry(PickupEntry.builder("Experience", 10, PickupType.XP).build());
        assertEquals(0, tracker.size());
    }

    @Test
    void perTypeToggle_itemsDisabled_allowsXp() {
        LootLogConfig config = new LootLogConfig();
        config.setShowItems(false);
        PickupTracker tracker = new PickupTracker(config);

        tracker.addEntry(PickupEntry.builder("Experience", 10, PickupType.XP).build());
        assertEquals(1, tracker.size());
    }
}
