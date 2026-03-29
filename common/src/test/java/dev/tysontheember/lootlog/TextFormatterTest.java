package dev.tysontheember.lootlog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextFormatterTest {

    @Test
    void formatLeftText_singleItem_noCount() {
        LootLogConfig config = new LootLogConfig();
        config.setShowCount(true);
        PickupEntry entry = PickupEntry.builder("Diamond", 1, PickupType.ITEM).totalCount(10).build();
        assertEquals("Diamond", TextFormatter.formatLeftText(entry, config));
    }

    @Test
    void formatLeftText_multipleItems_showsPickupCount() {
        LootLogConfig config = new LootLogConfig();
        config.setShowCount(true);
        PickupEntry entry = PickupEntry.builder("Cobblestone", 3, PickupType.ITEM).totalCount(64).build();
        assertEquals("3x Cobblestone", TextFormatter.formatLeftText(entry, config));
    }

    @Test
    void formatLeftText_countDisabled() {
        LootLogConfig config = new LootLogConfig();
        config.setShowCount(false);
        PickupEntry entry = PickupEntry.builder("Cobblestone", 3, PickupType.ITEM).totalCount(64).build();
        assertEquals("Cobblestone", TextFormatter.formatLeftText(entry, config));
    }

    @Test
    void formatLeftText_abbreviated() {
        LootLogConfig config = new LootLogConfig();
        config.setShowCount(true);
        config.setAbbreviateCounts(true);
        PickupEntry entry = PickupEntry.builder("Cobblestone", 1500, PickupType.ITEM).totalCount(64000).build();
        assertEquals("1.5Kx Cobblestone", TextFormatter.formatLeftText(entry, config));
    }

    @Test
    void formatRightText_showsInventoryTotal() {
        LootLogConfig config = new LootLogConfig();
        PickupEntry entry = PickupEntry.builder("Cobblestone", 3, PickupType.ITEM).totalCount(64).build();
        assertEquals("x64", TextFormatter.formatRightText(entry, config));
    }

    @Test
    void formatRightText_abbreviated() {
        LootLogConfig config = new LootLogConfig();
        config.setAbbreviateCounts(true);
        PickupEntry entry = PickupEntry.builder("Cobblestone", 3, PickupType.ITEM).totalCount(64000).build();
        assertEquals("x64.0K", TextFormatter.formatRightText(entry, config));
    }

    @Test
    void abbreviateCount_smallNumbers() {
        assertEquals("0", TextFormatter.abbreviateCount(0));
        assertEquals("1", TextFormatter.abbreviateCount(1));
        assertEquals("999", TextFormatter.abbreviateCount(999));
    }

    @Test
    void abbreviateCount_thousands() {
        assertEquals("1.0K", TextFormatter.abbreviateCount(1000));
        assertEquals("1.5K", TextFormatter.abbreviateCount(1500));
        assertEquals("64.0K", TextFormatter.abbreviateCount(64000));
    }

    @Test
    void abbreviateCount_millions() {
        assertEquals("1.0M", TextFormatter.abbreviateCount(1_000_000));
        assertEquals("2.5M", TextFormatter.abbreviateCount(2_500_000));
    }

    @Test
    void abbreviateCount_billions() {
        assertEquals("1.0B", TextFormatter.abbreviateCount(1_000_000_000L));
        assertEquals("2.5B", TextFormatter.abbreviateCount(2_500_000_000L));
    }

    // --- truncateName ---

    private static final RenderBridge MOCK_BRIDGE = new RenderBridge() {
        @Override public void renderRect(int x, int y, int w, int h, int color) {}
        @Override public void renderItemIcon(Object itemStack, int x, int y, float alpha) {}
        @Override public void renderText(String text, int x, int y, int color, boolean shadow) {}
        @Override public int getTextWidth(String text) { return text.length() * 6; }
        @Override public int getScreenWidth() { return 800; }
        @Override public int getScreenHeight() { return 600; }
        @Override public void pushPose() {}
        @Override public void popPose() {}
        @Override public void translate(float x, float y, float z) {}
        @Override public void scale(float x, float y, float z) {}
    };

    @Test
    void truncateName_shortName_unchanged() {
        assertEquals("Diamond", TextFormatter.truncateName("Diamond", 150, MOCK_BRIDGE));
    }

    @Test
    void truncateName_longName_truncated() {
        String result = TextFormatter.truncateName("Waxed Oxidised Cut Copper Stairs", 150, MOCK_BRIDGE);
        assertTrue(result.endsWith("..."));
        assertTrue(MOCK_BRIDGE.getTextWidth(result) <= 150);
    }

    @Test
    void truncateName_unlimitedWidth_unchanged() {
        assertEquals("Waxed Oxidised Cut Copper Stairs",
                TextFormatter.truncateName("Waxed Oxidised Cut Copper Stairs", 0, MOCK_BRIDGE));
    }

    @Test
    void truncateName_exactFit_unchanged() {
        assertEquals("Hello", TextFormatter.truncateName("Hello", 30, MOCK_BRIDGE));
    }
}
