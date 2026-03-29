package dev.tysontheember.lootlog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PopupLayoutTest {

    // --- Vertical centering ---

    @Test
    void contentOffsetY_smallTexture_noOffset() {
        // 12px texture < 16px content -> overflow, offset = 0
        assertEquals(0, PopupLayout.computeContentOffsetY(12));
    }

    @Test
    void contentOffsetY_exactFit_noOffset() {
        assertEquals(0, PopupLayout.computeContentOffsetY(16));
    }

    @Test
    void contentOffsetY_largerTexture_centers() {
        // 20px texture, 16px content -> offset = 2
        assertEquals(2, PopupLayout.computeContentOffsetY(20));
    }

    @Test
    void contentOffsetY_tallTexture_centers() {
        assertEquals(8, PopupLayout.computeContentOffsetY(32));
    }

    @Test
    void textOffsetY_accountsForFontHeight() {
        // Content offset 0, text centered in 16px: (16-9)/2 = 3
        assertEquals(3, PopupLayout.computeTextOffsetY(0));
        // Content offset 2, text at 2 + 3 = 5
        assertEquals(5, PopupLayout.computeTextOffsetY(2));
    }

    // --- Presets ---

    @Test
    void classicPreset_12pxBanner_iconOverflows() {
        PopupLayout layout = LayoutPreset.CLASSIC.createLayout(TextureSpec.DEFAULT_BANNER);
        // 12px banner, icon is 16px -> entryHeight should be 16 (min content height)
        assertEquals(16, layout.getEntryHeight());
        assertEquals(0, layout.getContentOffsetY());
        assertEquals(0, layout.getPadding());
    }

    @Test
    void classicPreset_24pxBanner_centerContent() {
        TextureSpec tall = TextureSpec.custom("test:tall.png")
                .sourceHeight(24)
                .pngSize(256, 24)
                .renderMode(RenderMode.STRETCH)
                .build();
        PopupLayout layout = LayoutPreset.CLASSIC.createLayout(tall);
        assertEquals(24, layout.getEntryHeight());
        assertEquals(4, layout.getContentOffsetY()); // (24-16)/2
    }

    @Test
    void standardRightPreset_hasPadding() {
        PopupLayout layout = LayoutPreset.STANDARD_RIGHT.createLayout(TextureSpec.DEFAULT_NINE_SLICE);
        assertEquals(4, layout.getPadding());
        assertTrue(layout.getIcon().isEnabled());
        assertTrue(layout.getItemName().isEnabled());
        assertTrue(layout.getTotalCount().isEnabled());
    }

    @Test
    void standardLeftPreset_totalCountDisabled() {
        PopupLayout layout = LayoutPreset.STANDARD_LEFT.createLayout(TextureSpec.DEFAULT_NINE_SLICE);
        assertFalse(layout.getTotalCount().isEnabled());
    }

    // --- Visibility overrides ---

    @Test
    void withVisibility_overridesSpecifiedElements() {
        PopupLayout layout = LayoutPreset.CLASSIC.createLayout(TextureSpec.DEFAULT_BANNER);
        assertTrue(layout.getIcon().isEnabled());
        assertTrue(layout.getTotalCount().isEnabled());

        PopupLayout modified = layout.withVisibility(false, null, null, false);
        assertFalse(modified.getIcon().isEnabled());
        assertTrue(modified.getItemName().isEnabled()); // unchanged
        assertFalse(modified.getTotalCount().isEnabled());
    }

    @Test
    void withVisibility_nullDoesNotChange() {
        PopupLayout layout = LayoutPreset.STANDARD_LEFT.createLayout(TextureSpec.DEFAULT_NINE_SLICE);
        assertFalse(layout.getTotalCount().isEnabled());

        PopupLayout modified = layout.withVisibility(null, null, null, null);
        assertFalse(modified.getTotalCount().isEnabled()); // still false
    }

    // --- ElementSlot ---

    @Test
    void elementSlot_withEnabled() {
        PopupLayout.ElementSlot slot = new PopupLayout.ElementSlot(5, 10, true);
        PopupLayout.ElementSlot disabled = slot.withEnabled(false);
        assertFalse(disabled.isEnabled());
        assertEquals(5, disabled.getOffsetX());
        assertEquals(10, disabled.getOffsetY());
    }

    @Test
    void elementSlot_withOffset() {
        PopupLayout.ElementSlot slot = new PopupLayout.ElementSlot(0, 0, true);
        PopupLayout.ElementSlot moved = slot.withOffset(3, 7);
        assertEquals(3, moved.getOffsetX());
        assertEquals(7, moved.getOffsetY());
        assertTrue(moved.isEnabled());
    }
}
