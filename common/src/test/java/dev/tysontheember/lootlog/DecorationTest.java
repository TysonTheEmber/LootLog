package dev.tysontheember.lootlog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DecorationTest {

    @Test
    void byName_validName_returnsDecoration() {
        assertEquals(Decoration.DEFAULT_BANNER, Decoration.byName("default_banner"));
    }

    @Test
    void byName_caseInsensitive() {
        assertEquals(Decoration.DEFAULT_BANNER, Decoration.byName("DEFAULT_BANNER"));
        assertEquals(Decoration.DEFAULT_BANNER, Decoration.byName("Default_Banner"));
    }

    @Test
    void byName_invalidName_returnsNull() {
        assertNull(Decoration.byName("nonexistent"));
        assertNull(Decoration.byName(""));
        assertNull(Decoration.byName(null));
    }

    @Test
    void defaultBanner_hasNonNullFields() {
        Decoration deco = Decoration.DEFAULT_BANNER;
        assertNotNull(deco.getTexture());
        assertNotNull(deco.getImpliedStyle());
        assertNotNull(deco.getBodySpec());
        assertNotNull(deco.getLayers());
    }

    @Test
    void defaultBanner_impliesBannerStyle() {
        assertEquals(BackgroundStyle.BANNER, Decoration.DEFAULT_BANNER.getImpliedStyle());
    }

    @Test
    void defaultBanner_hasTwoLayers() {
        assertEquals(2, Decoration.DEFAULT_BANNER.getLayers().size());
    }

    @Test
    void defaultBanner_bodySpec_isStretch() {
        assertEquals(RenderMode.STRETCH, Decoration.DEFAULT_BANNER.getBodySpec().getRenderMode());
    }

    @Test
    void defaultBanner_accentSpec_isNotNull() {
        assertNotNull(Decoration.DEFAULT_BANNER.getAccentSpec());
    }

    @Test
    void defaultBanner_bodySpec_matchesTexturePath() {
        assertEquals(Decoration.DEFAULT_BANNER.getTexture(),
                Decoration.DEFAULT_BANNER.getBodySpec().getTexturePath());
    }

    @Test
    void defaultBanner_textureSpec_isBodySpec() {
        assertSame(Decoration.DEFAULT_BANNER.getBodySpec(),
                Decoration.DEFAULT_BANNER.getTextureSpec());
    }

    @Test
    void defaultBanner_bodyHeight_is12() {
        assertEquals(12, Decoration.DEFAULT_BANNER.getBodySpec().getSourceHeight());
    }

    @Test
    void defaultBanner_accentHeight_is10() {
        assertEquals(10, Decoration.DEFAULT_BANNER.getAccentSpec().getSourceHeight());
    }
}
