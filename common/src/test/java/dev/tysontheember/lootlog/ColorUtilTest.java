package dev.tysontheember.lootlog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColorUtilTest {

    @Test
    void applyAlpha_fullAlpha() {
        assertEquals(0xFFAABBCC, ColorUtil.applyAlpha(0xFFAABBCC, 1.0f));
    }

    @Test
    void applyAlpha_halfAlpha() {
        int result = ColorUtil.applyAlpha(0xFFAABBCC, 0.5f);
        int alpha = (result >> 24) & 0xFF;
        assertTrue(alpha >= 127 && alpha <= 128);
        assertEquals(0xAABBCC, result & 0x00FFFFFF);
    }

    @Test
    void applyAlpha_zeroAlpha() {
        assertEquals(0x00AABBCC, ColorUtil.applyAlpha(0xFFAABBCC, 0.0f));
    }

    @Test
    void multiplyAlpha_fullFactor() {
        assertEquals(0xAA000000, ColorUtil.multiplyAlpha(0xAA000000, 1.0f));
    }

    @Test
    void multiplyAlpha_halfFactor() {
        assertEquals(0x55000000, ColorUtil.multiplyAlpha(0xAA000000, 0.5f));
    }

    @Test
    void multiplyAlpha_zeroFactor() {
        assertEquals(0x00000000, ColorUtil.multiplyAlpha(0xAA000000, 0.0f));
    }

    @Test
    void multiplyAlpha_tooltipFill() {
        assertEquals(0xF0100010, ColorUtil.multiplyAlpha(0xF0100010, 1.0f));
    }

    @Test
    void multiplyAlpha_tooltipBorder() {
        int result = ColorUtil.multiplyAlpha(0x505000FF, 0.5f);
        assertEquals(40, (result >> 24) & 0xFF);
        assertEquals(0x005000FF, result & 0x00FFFFFF);
    }

    @Test
    void computeXpColor_validArgb() {
        int color = ColorUtil.computeXpColor(0);
        assertEquals(0xFF, (color >> 24) & 0xFF);
        int green = (color >> 8) & 0xFF;
        assertTrue(green >= 145 && green <= 255);
    }

    @Test
    void computeXpColor_variesOverTime() {
        assertNotEquals(ColorUtil.computeXpColor(0), ColorUtil.computeXpColor(628));
    }

    @Test
    void computeXpColor_greenDominates() {
        int color = ColorUtil.computeXpColor(0);
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        assertTrue(green > red);
        assertTrue(green > blue);
    }

    @Test
    void resolveTextColor_xpAnimated() {
        LootLogConfig config = new LootLogConfig();
        config.setAnimateXpColor(true);
        PickupEntry entry = PickupEntry.builder("XP", 10, PickupType.XP).build();
        int color = ColorUtil.resolveTextColor(entry, config, 0);
        // Should be the XP animated color
        assertEquals(ColorUtil.computeXpColor(0), color);
    }

    @Test
    void resolveTextColor_rarityColor() {
        LootLogConfig config = new LootLogConfig();
        config.setUseRarityColors(true);
        PickupEntry entry = PickupEntry.builder("Diamond", 1, PickupType.ITEM)
                .rarityColor(0xFF55FFFF).build();
        assertEquals(0xFF55FFFF, ColorUtil.resolveTextColor(entry, config, 0));
    }

    @Test
    void resolveTextColor_nameColorOverride() {
        LootLogConfig config = new LootLogConfig();
        config.setUseRarityColors(false);
        config.setNameColorOverride(0xFFFF0000);
        PickupEntry entry = PickupEntry.builder("Diamond", 1, PickupType.ITEM).build();
        assertEquals(0xFFFF0000, ColorUtil.resolveTextColor(entry, config, 0));
    }
}
