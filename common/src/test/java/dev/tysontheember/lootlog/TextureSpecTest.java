package dev.tysontheember.lootlog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextureSpecTest {

    @Test
    void banner_defaults() {
        TextureSpec spec = TextureSpec.banner("test:texture.png", 12);
        assertEquals("test:texture.png", spec.getTexturePath());
        assertEquals(12, spec.getSourceHeight());
        assertEquals(RenderMode.STRETCH, spec.getRenderMode());
        assertEquals(0, spec.getSliceBorder());
        assertEquals(12, spec.getRenderHeight());
    }

    @Test
    void nineSlice_defaults() {
        TextureSpec spec = TextureSpec.nineSlice("test:texture.png", 20, 4);
        assertEquals("test:texture.png", spec.getTexturePath());
        assertEquals(20, spec.getSourceHeight());
        assertEquals(RenderMode.NINE_SLICE, spec.getRenderMode());
        assertEquals(4, spec.getSliceBorder());
    }

    @Test
    void custom_builder() {
        TextureSpec spec = TextureSpec.custom("custom:bg.png")
                .sourceHeight(24)
                .pngSize(128, 48)
                .renderMode(RenderMode.STRETCH)
                .build();
        assertEquals(24, spec.getSourceHeight());
        assertEquals(128, spec.getPngWidth());
        assertEquals(48, spec.getPngHeight());
        assertEquals(128, spec.getRenderWidth());
        assertEquals(24, spec.getRenderHeight());
    }

    @Test
    void getFrameCount_singleFrame() {
        TextureSpec spec = TextureSpec.banner("test:t.png", 12).setPngDimensions(256, 12);
        assertEquals(1, spec.getFrameCount());
    }

    @Test
    void getFrameCount_multipleFrames() {
        TextureSpec spec = TextureSpec.banner("test:t.png", 12).setPngDimensions(256, 36);
        assertEquals(3, spec.getFrameCount());
    }

    @Test
    void withPath_preservesDimensions() {
        TextureSpec original = TextureSpec.banner("original:path.png", 12)
                .setPngDimensions(256, 12);
        TextureSpec copy = original.withPath("new:path.png");
        assertEquals("new:path.png", copy.getTexturePath());
        assertEquals(original.getSourceHeight(), copy.getSourceHeight());
        assertEquals(original.getRenderMode(), copy.getRenderMode());
        assertEquals(original.getPngWidth(), copy.getPngWidth());
        assertEquals(original.getPngHeight(), copy.getPngHeight());
    }

    @Test
    void defaultBanner_matches() {
        TextureSpec def = TextureSpec.DEFAULT_BANNER;
        assertEquals("lootlog:textures/gui/lootlog/banner_body.png", def.getTexturePath());
        assertEquals(RenderMode.STRETCH, def.getRenderMode());
        assertEquals(12, def.getSourceHeight());
        assertEquals(256, def.getPngWidth());
        assertEquals(12, def.getPngHeight());
    }

    @Test
    void defaultNineSlice_matches() {
        TextureSpec def = TextureSpec.DEFAULT_NINE_SLICE;
        assertEquals("lootlog:textures/gui/lootlog/popup_bg.png", def.getTexturePath());
        assertEquals(RenderMode.NINE_SLICE, def.getRenderMode());
        assertEquals(16, def.getSourceHeight());
        assertEquals(4, def.getSliceBorder());
        assertEquals(16, def.getPngWidth());
        assertEquals(16, def.getPngHeight());
    }
}
