package dev.tysontheember.lootlog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OverrideFileLoaderTest {

    @TempDir
    Path tempDir;

    private void writeFile(String name, String content) throws IOException {
        Files.write(tempDir.resolve(name), content.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void loadAll_emptyDirectory_returnsEmpty() {
        List<ItemOverride> result = OverrideFileLoader.loadAll(tempDir);
        assertTrue(result.isEmpty());
    }

    @Test
    void loadAll_createsDirectoryAndExample() {
        Path newDir = tempDir.resolve("overrides");
        assertFalse(Files.exists(newDir));

        List<ItemOverride> result = OverrideFileLoader.loadAll(newDir);

        assertTrue(result.isEmpty());
        assertTrue(Files.exists(newDir));
        assertTrue(Files.exists(newDir.resolve("_example.json")));
    }

    @Test
    void loadAll_skipsUnderscorePrefixedFiles() throws IOException {
        writeFile("_disabled.json", "{\"overrides\":[" +
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"},\"text\":{\"color\":\"FF0000\"}}" +
                "]}");
        writeFile("active.json", "{\"overrides\":[" +
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:emerald\"},\"text\":{\"color\":\"00FF00\"}}" +
                "]}");

        List<ItemOverride> result = OverrideFileLoader.loadAll(tempDir);

        assertEquals(1, result.size());
        assertEquals("minecraft:emerald", result.get(0).getMatch().getId());
    }

    @Test
    void loadAll_parsesMultipleFiles() throws IOException {
        writeFile("file1.json", "{\"overrides\":[" +
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"},\"text\":{\"color\":\"FF0000\"}}" +
                "]}");
        writeFile("file2.json", "{\"overrides\":[" +
                "{\"match\":{\"type\":\"tag\",\"id\":\"c:ores\"},\"text\":{\"color\":\"00FF00\"}}," +
                "{\"match\":{\"type\":\"mod\",\"id\":\"create\"},\"text\":{\"color\":\"0000FF\"}}" +
                "]}");

        List<ItemOverride> result = OverrideFileLoader.loadAll(tempDir);

        assertEquals(3, result.size());
    }

    @Test
    void loadAll_malformedFile_skippedGracefully() throws IOException {
        writeFile("bad.json", "{ this is not valid json }}}");
        writeFile("good.json", "{\"overrides\":[" +
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"},\"text\":{\"color\":\"FF0000\"}}" +
                "]}");

        List<ItemOverride> result = OverrideFileLoader.loadAll(tempDir);

        // Good file parsed, bad file skipped
        assertEquals(1, result.size());
    }

    @Test
    void loadAll_emptyOverridesArray_returnsEmpty() throws IOException {
        writeFile("empty.json", "{\"overrides\":[]}");

        List<ItemOverride> result = OverrideFileLoader.loadAll(tempDir);

        assertTrue(result.isEmpty());
    }

    @Test
    void loadAll_filtersInvalidEntries() throws IOException {
        writeFile("mixed.json", "{\"overrides\":[" +
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"},\"text\":{\"color\":\"FF0000\"}}," +
                "{\"match\":{\"type\":\"item\"},\"text\":{\"color\":\"00FF00\"}}," + // missing id
                "{\"match\":{\"id\":\"foo\"},\"text\":{\"color\":\"0000FF\"}}," +    // missing type
                "{\"text\":{\"color\":\"AAAAAA\"}}" +                                // missing match entirely
                "]}");

        List<ItemOverride> result = OverrideFileLoader.loadAll(tempDir);

        assertEquals(1, result.size());
        assertEquals("minecraft:diamond", result.get(0).getMatch().getId());
    }

    @Test
    void loadFile_parsesAllFieldTypes() throws IOException {
        writeFile("full.json", "{\"overrides\":[{" +
                "\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"sound\":{\"soundId\":\"minecraft:entity.player.levelup\",\"volume\":0.6,\"pitch\":1.5}," +
                "\"background\":{\"style\":\"TEXTURE\",\"texture\":\"mod:textures/gui/bg.png\"," +
                "\"color\":\"CC000000\",\"animation\":{\"type\":\"spritesheet\",\"frames\":4,\"frameTimeMs\":200,\"interpolate\":true}}," +
                "\"text\":{\"markup\":\"<rainbow>{name}</rainbow>\",\"color\":\"FFD700\",\"prefix\":\"[!] \",\"suffix\":\" [!]\",\"fullName\":\"Gem\"}," +
                "\"display\":{\"durationMs\":8000,\"scale\":1.2,\"combineMode\":\"NEVER\"}," +
                "\"visual\":{\"iconGlow\":{\"color\":\"FFD700\",\"radius\":2}," +
                "\"effects\":[{\"type\":\"pulse\",\"color\":\"44FFD700\",\"speed\":1.5,\"minAlpha\":0.3,\"maxAlpha\":0.8}]}," +
                "\"behavior\":{\"priority\":10,\"forceShow\":true}" +
                "}]}");

        List<ItemOverride> result = OverrideFileLoader.loadFile(tempDir.resolve("full.json"));

        assertEquals(1, result.size());
        ItemOverride o = result.get(0);

        assertEquals("item", o.getMatch().getType());
        assertEquals("minecraft:diamond", o.getMatch().getId());

        assertEquals("minecraft:entity.player.levelup", o.getSound().getSoundId());
        assertEquals(0.6f, o.getSound().getVolume());
        assertEquals(1.5f, o.getSound().getPitch());

        assertEquals("TEXTURE", o.getBackground().getStyle());
        assertEquals("mod:textures/gui/bg.png", o.getBackground().getTexture());
        assertEquals("CC000000", o.getBackground().getColor());
        assertEquals(4, o.getBackground().getAnimation().getFrames());
        assertEquals(200, o.getBackground().getAnimation().getFrameTimeMs());
        assertTrue(o.getBackground().getAnimation().getInterpolate());

        assertEquals("<rainbow>{name}</rainbow>", o.getText().getMarkup());
        assertEquals("FFD700", o.getText().getColor());
        assertEquals("[!] ", o.getText().getPrefix());
        assertEquals(" [!]", o.getText().getSuffix());
        assertEquals("Gem", o.getText().getFullName());

        assertEquals(8000L, o.getDisplay().getDurationMs());
        assertEquals(1.2f, o.getDisplay().getScale());
        assertEquals("NEVER", o.getDisplay().getCombineMode());

        assertEquals("FFD700", o.getVisual().getIconGlow().getColor());
        assertEquals(2, o.getVisual().getIconGlow().getRadius());
        assertEquals(1, o.getVisual().getEffects().size());

        assertEquals(10, o.getBehavior().getPriority());
        assertTrue(o.getBehavior().getForceShow());
    }

    @Test
    void loadFile_parsesNewIconGlowFields() throws IOException {
        writeFile("glow.json", "{\"overrides\":[{" +
                "\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"visual\":{\"iconGlow\":{" +
                "\"color\":\"5555CDFC\",\"radius\":3," +
                "\"shape\":\"circle\",\"style\":\"dithered\",\"softness\":2.5," +
                "\"pulse\":{\"speed\":1.0,\"min\":0.5,\"max\":1.0}" +
                "}}}]}");

        List<ItemOverride> result = OverrideFileLoader.loadFile(tempDir.resolve("glow.json"));
        assertEquals(1, result.size());
        ItemOverride.IconGlowDef glow = result.get(0).getVisual().getIconGlow();
        assertEquals("5555CDFC", glow.getColor());
        assertEquals(3, glow.getRadius());
        assertEquals("circle", glow.getShape());
        assertEquals("dithered", glow.getStyle());
        assertEquals(2.5f, glow.getSoftness());
        assertNotNull(glow.getPulse());
        assertEquals(1.0f, glow.getPulse().getSpeed());
        assertEquals(0.5f, glow.getPulse().getMin());
        assertEquals(1.0f, glow.getPulse().getMax());
    }

    @Test
    void loadAll_ignoresNonJsonFiles() throws IOException {
        writeFile("notes.txt", "not a json file");
        writeFile("valid.json", "{\"overrides\":[" +
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"},\"text\":{\"color\":\"FF0000\"}}" +
                "]}");

        List<ItemOverride> result = OverrideFileLoader.loadAll(tempDir);

        assertEquals(1, result.size());
    }

    // --- Rarity match type ---

    @Test
    void loadAll_parsesRarityMatchType() throws IOException {
        writeFile("rarity.json", "{\"overrides\":[" +
                "{\"match\":{\"type\":\"rarity\",\"id\":\"epic\"},\"text\":{\"color\":\"FF55FF\"}}" +
                "]}");

        List<ItemOverride> result = OverrideFileLoader.loadAll(tempDir);

        assertEquals(1, result.size());
        assertEquals("rarity", result.get(0).getMatch().getType());
        assertEquals("epic", result.get(0).getMatch().getId());
    }

    // --- Regex match type ---

    @Test
    void loadAll_parsesRegexMatchType() throws IOException {
        writeFile("regex.json", "{\"overrides\":[" +
                "{\"match\":{\"type\":\"regex\",\"id\":\"minecraft:.*_ore\"},\"text\":{\"color\":\"AA5500\"}}" +
                "]}");

        List<ItemOverride> result = OverrideFileLoader.loadAll(tempDir);

        assertEquals(1, result.size());
        assertEquals("regex", result.get(0).getMatch().getType());
        assertEquals("minecraft:.*_ore", result.get(0).getMatch().getId());
    }

    @Test
    void loadAll_invalidRegexLoadedByLoader_registrySkipsIt() throws IOException {
        writeFile("bad_regex.json", "{\"overrides\":[" +
                "{\"match\":{\"type\":\"regex\",\"id\":\"[invalid\"},\"text\":{\"color\":\"111111\"}}," +
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"},\"text\":{\"color\":\"222222\"}}" +
                "]}");

        List<ItemOverride> result = OverrideFileLoader.loadAll(tempDir);

        // Loader accepts both (it doesn't validate regex syntax)
        assertEquals(2, result.size());

        // Registry skips the invalid regex
        OverrideRegistry registry = new OverrideRegistry();
        registry.load(result);
        assertEquals(1, registry.size()); // only the item override survived
    }

    @Test
    void loadAll_parsesRarityAndRegexTogether() throws IOException {
        writeFile("mixed.json", "{\"overrides\":[" +
                "{\"match\":{\"type\":\"rarity\",\"id\":\"rare\"},\"text\":{\"color\":\"5555FF\"}}," +
                "{\"match\":{\"type\":\"regex\",\"id\":\"minecraft:.*_ingot\"},\"text\":{\"prefix\":\"[Ingot] \"}}," +
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"},\"text\":{\"color\":\"B9F2FF\"}}" +
                "]}");

        List<ItemOverride> result = OverrideFileLoader.loadAll(tempDir);

        assertEquals(3, result.size());

        OverrideRegistry registry = new OverrideRegistry();
        registry.load(result);
        assertEquals(3, registry.size());
    }
}
