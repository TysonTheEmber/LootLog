package dev.tysontheember.lootlog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HudLayoutTest {

    // --- resolvePreset ---

    @Test
    void resolvePreset_classicStyle_returnsClassic() {
        LootLogConfig config = new LootLogConfig();
        config.setBackgroundStyle(BackgroundStyle.BANNER);
        assertEquals(LayoutPreset.CLASSIC, HudLayout.resolvePreset(config));
    }

    @Test
    void resolvePreset_flatStyle_returnsClassic() {
        LootLogConfig config = new LootLogConfig();
        config.setBackgroundStyle(BackgroundStyle.FLAT);
        assertEquals(LayoutPreset.CLASSIC, HudLayout.resolvePreset(config));
    }

    @Test
    void resolvePreset_explicitPreset_overridesStyle() {
        LootLogConfig config = new LootLogConfig();
        config.setBackgroundStyle(BackgroundStyle.BANNER);
        config.setLayoutPreset("STANDARD_RIGHT");
        assertEquals(LayoutPreset.STANDARD_RIGHT, HudLayout.resolvePreset(config));
    }

    @Test
    void resolvePreset_standardStyle_iconRight() {
        LootLogConfig config = new LootLogConfig();
        config.setBackgroundStyle(BackgroundStyle.TEXTURE);
        config.setIconOnRight(true);
        assertEquals(LayoutPreset.STANDARD_RIGHT, HudLayout.resolvePreset(config));
    }

    @Test
    void resolvePreset_standardStyle_iconLeft() {
        LootLogConfig config = new LootLogConfig();
        config.setBackgroundStyle(BackgroundStyle.TEXTURE);
        config.setIconOnRight(false);
        assertEquals(LayoutPreset.STANDARD_LEFT, HudLayout.resolvePreset(config));
    }

    // --- resolveTextureSpec ---

    @Test
    void resolveTextureSpec_classicStyle_returnsBanner() {
        LootLogConfig config = new LootLogConfig();
        config.setBackgroundStyle(BackgroundStyle.BANNER);
        TextureSpec spec = HudLayout.resolveTextureSpec(config, null);
        assertEquals(RenderMode.STRETCH, spec.getRenderMode());
        assertEquals(12, spec.getSourceHeight());
    }

    @Test
    void resolveTextureSpec_texturedStyle_returnsNineSlice() {
        LootLogConfig config = new LootLogConfig();
        config.setBackgroundStyle(BackgroundStyle.TEXTURE);
        TextureSpec spec = HudLayout.resolveTextureSpec(config, null);
        assertEquals(RenderMode.NINE_SLICE, spec.getRenderMode());
        assertEquals(16, spec.getSourceHeight());
    }

    @Test
    void resolveTextureSpec_decoration_returnsDecoSpec() {
        LootLogConfig config = new LootLogConfig();
        config.setDecoration("default_banner");
        TextureSpec spec = HudLayout.resolveTextureSpec(config, null);
        assertEquals(Decoration.DEFAULT_BANNER.getBodySpec().getTexturePath(), spec.getTexturePath());
    }

    // --- BackgroundStyle config integration ---

    @Test
    void backgroundStyle_none_disablesBackground() {
        LootLogConfig config = new LootLogConfig();
        config.setBackgroundStyle(BackgroundStyle.NONE);
        assertFalse(config.isBackgroundEnabled());
    }

    @Test
    void backgroundStyle_solid_enablesBackground() {
        LootLogConfig config = new LootLogConfig();
        config.setBackgroundStyle(BackgroundStyle.SOLID);
        assertTrue(config.isBackgroundEnabled());
    }

    @Test
    void backgroundStyle_tooltip_enablesBackground() {
        LootLogConfig config = new LootLogConfig();
        config.setBackgroundStyle(BackgroundStyle.TOOLTIP);
        assertTrue(config.isBackgroundEnabled());
    }

    @Test
    void backgroundStyle_textured_enablesBackground() {
        LootLogConfig config = new LootLogConfig();
        config.setBackgroundStyle(BackgroundStyle.TEXTURE);
        assertTrue(config.isBackgroundEnabled());
    }

    @Test
    void backgroundStyle_classic_enablesBackground() {
        LootLogConfig config = new LootLogConfig();
        config.setBackgroundStyle(BackgroundStyle.BANNER);
        assertTrue(config.isBackgroundEnabled());
    }

    @Test
    void backgroundStyle_flat_enablesBackground() {
        LootLogConfig config = new LootLogConfig();
        config.setBackgroundStyle(BackgroundStyle.FLAT);
        assertTrue(config.isBackgroundEnabled());
    }

    // --- Anchor utilities ---

    @Test
    void isRightAnchor() {
        assertTrue(HudLayout.isRightAnchor(HudAnchor.TOP_RIGHT));
        assertTrue(HudLayout.isRightAnchor(HudAnchor.BOTTOM_RIGHT));
        assertFalse(HudLayout.isRightAnchor(HudAnchor.TOP_LEFT));
        assertFalse(HudLayout.isRightAnchor(HudAnchor.BOTTOM_LEFT));
    }

    @Test
    void isBottomAnchor() {
        assertTrue(HudLayout.isBottomAnchor(HudAnchor.BOTTOM_LEFT));
        assertTrue(HudLayout.isBottomAnchor(HudAnchor.BOTTOM_RIGHT));
        assertFalse(HudLayout.isBottomAnchor(HudAnchor.TOP_LEFT));
        assertFalse(HudLayout.isBottomAnchor(HudAnchor.TOP_RIGHT));
    }
}
