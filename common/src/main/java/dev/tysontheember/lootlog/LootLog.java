package dev.tysontheember.lootlog;

/**
 * Central static registry for the mod's shared state.
 * Platform entrypoints set the RenderBridge implementation at init time.
 */
public final class LootLog {

    public static final String MOD_ID = "lootlog";

    private static final LootLogConfig config = new LootLogConfig();
    private static final ItemFilter filter = new ItemFilter();
    private static final PickupTracker tracker = new PickupTracker(config, filter);
    private static final HudLayout hudLayout = new HudLayout();
    private static final OverrideRegistry overrideRegistry = new OverrideRegistry();
    private static RenderBridge renderBridge;
    private static SoundBridge soundBridge;
    private static TagBridge tagBridge;
    private static TextEffectBridge textEffectBridge = new DefaultTextEffectBridge();

    private LootLog() {}

    public static LootLogConfig getConfig() {
        return config;
    }

    public static ItemFilter getFilter() {
        return filter;
    }

    public static PickupTracker getTracker() {
        return tracker;
    }

    public static HudLayout getHudLayout() {
        return hudLayout;
    }

    public static RenderBridge getRenderBridge() {
        return renderBridge;
    }

    public static void setRenderBridge(RenderBridge bridge) {
        renderBridge = bridge;
    }

    public static SoundBridge getSoundBridge() {
        return soundBridge;
    }

    public static void setSoundBridge(SoundBridge bridge) {
        soundBridge = bridge;
    }

    public static OverrideRegistry getOverrideRegistry() {
        return overrideRegistry;
    }

    public static TagBridge getTagBridge() {
        return tagBridge;
    }

    public static void setTagBridge(TagBridge bridge) {
        tagBridge = bridge;
    }

    public static TextEffectBridge getTextEffectBridge() {
        return textEffectBridge;
    }

    public static void setTextEffectBridge(TextEffectBridge bridge) {
        textEffectBridge = bridge;
    }

    /**
     * Collect all texture resource paths used by the mod (for cache eviction on reload).
     * Returns paths like "lootlog:textures/gui/lootlog/banner_body.png".
     */
    public static java.util.Set<String> getTextureResourcePaths() {
        java.util.Set<String> paths = new java.util.LinkedHashSet<>();
        paths.add(TextureSpec.DEFAULT_BANNER.getTexturePath());
        paths.add(TextureSpec.DEFAULT_NINE_SLICE.getTexturePath());
        paths.add(IconEffectRenderer.GLOW_CIRCLE_TEXTURE);
        for (Decoration deco : Decoration.values()) {
            for (BannerLayer layer : deco.getLayers()) {
                paths.add(layer.getTexture().getTexturePath());
            }
        }
        return paths;
    }

    /**
     * In a dev environment, copy mod assets from source directories to build output
     * so F3+T picks up texture edits without a Gradle rebuild.
     * Detects dev by checking for common source dir relative to the working directory.
     */
    public static void syncDevResources() {
        // Dev layout: working dir is {platform}/run/, source at ../../common/src/main/resources/
        java.nio.file.Path workDir = java.nio.file.Paths.get("").toAbsolutePath();
        java.nio.file.Path commonSrc = workDir.resolve("../../common/src/main/resources/assets/" + MOD_ID);
        if (!java.nio.file.Files.isDirectory(commonSrc)) return;

        // Find the build output dir (../build/resources/main/assets/lootlog/)
        java.nio.file.Path buildOutput = workDir.resolve("../build/resources/main/assets/" + MOD_ID);
        if (!java.nio.file.Files.isDirectory(buildOutput)) return;

        try {
            java.nio.file.Files.walk(commonSrc)
                    .filter(p -> p.toString().endsWith(".png"))
                    .forEach(src -> {
                        java.nio.file.Path rel = commonSrc.relativize(src);
                        java.nio.file.Path dest = buildOutput.resolve(rel);
                        try {
                            if (!java.nio.file.Files.exists(dest)
                                    || java.nio.file.Files.getLastModifiedTime(src)
                                        .compareTo(java.nio.file.Files.getLastModifiedTime(dest)) > 0) {
                                java.nio.file.Files.createDirectories(dest.getParent());
                                java.nio.file.Files.copy(src, dest,
                                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            }
                        } catch (java.io.IOException ignored) {}
                    });
        } catch (java.io.IOException ignored) {}
    }
}
