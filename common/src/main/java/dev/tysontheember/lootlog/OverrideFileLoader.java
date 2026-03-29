package dev.tysontheember.lootlog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Discovers and loads override JSON files from config/lootlog/overrides/.
 */
public final class OverrideFileLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(OverrideFileLoader.class);
    private static final Gson GSON = new GsonBuilder().create();

    private OverrideFileLoader() {}

    /**
     * Load all override files from the given directory.
     * Creates the directory and writes an example file if it doesn't exist.
     * Files starting with '_' are skipped.
     *
     * @param overridesDir path to config/lootlog/overrides/
     * @return list of all parsed overrides across all files
     */
    public static List<ItemOverride> loadAll(Path overridesDir) {
        try {
            if (!Files.exists(overridesDir)) {
                Files.createDirectories(overridesDir);
                generateExample(overridesDir);
                return Collections.emptyList();
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to create overrides directory: {}", overridesDir, e);
            return Collections.emptyList();
        }

        List<ItemOverride> allOverrides = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(overridesDir, "*.json")) {
            for (Path file : stream) {
                String fileName = file.getFileName().toString();
                if (fileName.startsWith("_")) continue;

                try {
                    List<ItemOverride> parsed = loadFile(file);
                    allOverrides.addAll(parsed);
                    LOGGER.info("Loaded {} overrides from {}", parsed.size(), fileName);
                } catch (Exception e) {
                    LOGGER.warn("Failed to parse override file: {}", fileName, e);
                }
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to list override files in: {}", overridesDir, e);
        }

        return allOverrides;
    }

    static List<ItemOverride> loadFile(Path file) throws IOException {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            OverrideFile wrapper = GSON.fromJson(reader, OverrideFile.class);
            if (wrapper == null || wrapper.overrides == null) {
                return Collections.emptyList();
            }
            // Filter out entries with invalid match rules
            List<ItemOverride> valid = new ArrayList<>();
            for (ItemOverride o : wrapper.overrides) {
                if (o.getMatch() != null && o.getMatch().getType() != null
                        && o.getMatch().getId() != null
                        && !o.getMatch().getId().isEmpty()) {
                    valid.add(o);
                }
            }
            return valid;
        }
    }

    /** Generate an example override file with documentation. */
    static void generateExample(Path overridesDir) {
        Path exampleFile = overridesDir.resolve("_example.json");
        String content = "{\n"
                + "  \"_docs\": {\n"
                + "    \"match.type\": \"item | tag | mod | rarity | regex\",\n"
                + "    \"match.id\": \"e.g. minecraft:diamond, c:ores, create, epic, minecraft:.*_ingot\",\n"
                + "    \"background.decoration\": \"default_banner (2-layer banner)\",\n"
                + "    \"background.style\": \"NONE | SOLID | TOOLTIP | TEXTURE | BANNER | FLAT\",\n"
                + "    \"background.textureWidth/Height\": \"custom texture dimensions (default: 256x12 for banners, 200x20 for 9-slice)\",\n"
                + "    \"background.renderMode\": \"STRETCH (banner) | NINE_SLICE (scalable)\",\n"
                + "    \"layout.iconEnabled\": \"show/hide item icon (true/false)\",\n"
                + "    \"layout.nameEnabled\": \"show/hide item name (true/false)\",\n"
                + "    \"layout.totalCountEnabled\": \"show/hide inventory total (true/false)\",\n"
                + "    \"layout.pickupCountEnabled\": \"show/hide pickup count (true/false)\",\n"
                + "    \"text.markup\": \"use {name} for the item name; supports EmbersTextAPI tags\",\n"
                + "    \"behavior.priority\": \"higher number = higher priority when multiple tags/regex match\",\n"
                + "    \"rarity values\": \"common | uncommon | rare | epic (vanilla). Modded rarities also work -- use the lowercased enum name (e.g., mythic). Set log level to DEBUG to discover rarity names.\",\n"
                + "    \"regex notes\": \"full match against item ID; use .* for wildcards\"\n"
                + "  },\n"
                + "  \"overrides\": [\n"
                + "    {\n"
                + "      \"_comment\": \"Example: custom diamond popup\",\n"
                + "      \"match\": { \"type\": \"item\", \"id\": \"minecraft:diamond\" },\n"
                + "      \"sound\": { \"soundId\": \"minecraft:entity.player.levelup\", \"volume\": 0.6, \"pitch\": 1.5 },\n"
                + "      \"text\": { \"markup\": \"<rainbow f=2.0>{name}</rainbow>\", \"color\": \"B9F2FF\" },\n"
                + "      \"display\": { \"durationMs\": 8000 },\n"
                + "      \"layout\": { \"totalCountEnabled\": false },\n"
                + "      \"behavior\": { \"priority\": 5 }\n"
                + "    },\n"
                + "    {\n"
                + "      \"_comment\": \"Example: shimmer effect on all ores\",\n"
                + "      \"match\": { \"type\": \"tag\", \"id\": \"c:ores\" },\n"
                + "      \"visual\": {\n"
                + "        \"effects\": [\n"
                + "          { \"type\": \"shimmer\", \"color\": \"22FFFFFF\", \"speed\": 2.0, \"width\": 0.3 }\n"
                + "        ]\n"
                + "      },\n"
                + "      \"behavior\": { \"priority\": 3 }\n"
                + "    },\n"
                + "    {\n"
                + "      \"_comment\": \"Example: gilded decoration for gems\",\n"
                + "      \"match\": { \"type\": \"tag\", \"id\": \"c:gems\" },\n"
                + "      \"background\": { \"decoration\": \"gilded\" }\n"
                + "    },\n"
                + "    {\n"
                + "      \"_comment\": \"Example: custom color for all epic items\",\n"
                + "      \"match\": { \"type\": \"rarity\", \"id\": \"epic\" },\n"
                + "      \"text\": { \"color\": \"FF55FF\" }\n"
                + "    },\n"
                + "    {\n"
                + "      \"_comment\": \"Example: custom popup for a modded rarity (use the lowercased enum name)\",\n"
                + "      \"match\": { \"type\": \"rarity\", \"id\": \"mythic\" },\n"
                + "      \"text\": { \"color\": \"FFD700\" },\n"
                + "      \"sound\": { \"soundId\": \"minecraft:ui.toast.challenge_complete\", \"volume\": 0.5 }\n"
                + "    },\n"
                + "    {\n"
                + "      \"_comment\": \"Example: prefix all ingots via regex\",\n"
                + "      \"match\": { \"type\": \"regex\", \"id\": \"minecraft:.*_ingot\" },\n"
                + "      \"text\": { \"prefix\": \"[Ingot] \" },\n"
                + "      \"behavior\": { \"priority\": 2 }\n"
                + "    }\n"
                + "  ]\n"
                + "}\n";
        try {
            Files.write(exampleFile, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.warn("Failed to write example override file", e);
        }
    }

    /** Top-level JSON wrapper. */
    private static class OverrideFile {
        List<ItemOverride> overrides;
    }
}
