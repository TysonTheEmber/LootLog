package dev.tysontheember.lootlog;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OverrideResolverTest {

    private OverrideRegistry registry;
    private final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        registry = new OverrideRegistry();
    }

    // --- Helper to create overrides from JSON ---

    private ItemOverride fromJson(String json) {
        return gson.fromJson(json, ItemOverride.class);
    }

    // --- No matches returns null ---

    @Test
    void noOverrides_returnsNull() {
        ResolvedOverride result = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);
        assertNull(result);
    }

    @Test
    void noMatchingOverrides_returnsNull() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:emerald\"}," +
                "\"text\":{\"color\":\"FF0000\"}}")));
        ResolvedOverride result = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);
        assertNull(result);
    }

    // --- Exact item ID match ---

    @Test
    void itemMatch_resolvesFields() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"text\":{\"color\":\"B9F2FF\",\"prefix\":\"[!] \"}," +
                "\"sound\":{\"soundId\":\"minecraft:entity.player.levelup\",\"volume\":0.6}," +
                "\"display\":{\"durationMs\":8000}," +
                "\"behavior\":{\"priority\":5,\"forceShow\":true}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals("minecraft:entity.player.levelup", r.getSoundId());
        assertEquals(0.6f, r.getSoundVolume());
        assertNull(r.getSoundPitch()); // not set
        assertEquals(0xFFB9F2FF, r.getTextColor()); // 6-char hex gets FF alpha
        assertEquals("[!] ", r.getNamePrefix());
        assertEquals(8000L, r.getDisplayDurationMs());
        assertEquals(5, r.getPriority());
        assertTrue(r.isForceShow());
    }

    // --- Tag match ---

    @Test
    void tagMatch_resolvesFields() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"tag\",\"id\":\"c:ores\"}," +
                "\"text\":{\"color\":\"FFD700\"}}")));

        Set<String> tags = Set.of("c:ores", "minecraft:iron_ores");
        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:iron_ore", tags, registry);

        assertNotNull(r);
        assertEquals(0xFFFFD700, r.getTextColor());
    }

    @Test
    void tagMatch_noIntersection_returnsNull() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"tag\",\"id\":\"c:ores\"}," +
                "\"text\":{\"color\":\"FFD700\"}}")));

        Set<String> tags = Set.of("minecraft:logs");
        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:oak_log", tags, registry);

        assertNull(r);
    }

    // --- Mod namespace match ---

    @Test
    void modMatch_resolvesFields() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"mod\",\"id\":\"create\"}," +
                "\"text\":{\"color\":\"D4A017\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "create:brass_ingot", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals(0xFFD4A017, r.getTextColor());
    }

    // --- Priority: item > tag > mod ---

    @Test
    void itemOverridesTag() {
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"tag\",\"id\":\"c:ores\"}," +
                        "\"text\":{\"color\":\"111111\"}}"),
                fromJson("{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond_ore\"}," +
                        "\"text\":{\"color\":\"222222\"}}")
        ));

        Set<String> tags = Set.of("c:ores");
        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond_ore", tags, registry);

        assertNotNull(r);
        assertEquals(0xFF222222, r.getTextColor()); // item wins
    }

    @Test
    void tagOverridesMod() {
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"mod\",\"id\":\"minecraft\"}," +
                        "\"text\":{\"color\":\"111111\"}}"),
                fromJson("{\"match\":{\"type\":\"tag\",\"id\":\"c:ores\"}," +
                        "\"text\":{\"color\":\"222222\"}}")
        ));

        Set<String> tags = Set.of("c:ores");
        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:iron_ore", tags, registry);

        assertNotNull(r);
        assertEquals(0xFF222222, r.getTextColor()); // tag wins over mod
    }

    // --- Field-level merge ---

    @Test
    void fieldLevelMerge_independentFields() {
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"mod\",\"id\":\"minecraft\"}," +
                        "\"sound\":{\"volume\":0.5}," +
                        "\"text\":{\"color\":\"111111\"}}"),
                fromJson("{\"match\":{\"type\":\"tag\",\"id\":\"c:ores\"}," +
                        "\"sound\":{\"pitch\":1.5}}")
        ));

        Set<String> tags = Set.of("c:ores");
        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:iron_ore", tags, registry);

        assertNotNull(r);
        // Mod set volume, tag set pitch -- both survive
        assertEquals(0.5f, r.getSoundVolume());
        assertEquals(1.5f, r.getSoundPitch());
        // Mod set text color, tag didn't touch it -- survives
        assertEquals(0xFF111111, r.getTextColor());
    }

    @Test
    void fieldLevelMerge_higherPriorityOverwritesSameField() {
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"mod\",\"id\":\"minecraft\"}," +
                        "\"sound\":{\"volume\":0.5}}"),
                fromJson("{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                        "\"sound\":{\"volume\":0.9}}")
        ));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals(0.9f, r.getSoundVolume()); // item overwrites mod
    }

    // --- Tag priority within same tier ---

    @Test
    void tagPriority_higherPriorityWinsPerField() {
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"tag\",\"id\":\"c:ores\"}," +
                        "\"text\":{\"color\":\"111111\"}," +
                        "\"behavior\":{\"priority\":1}}"),
                fromJson("{\"match\":{\"type\":\"tag\",\"id\":\"minecraft:diamond_ores\"}," +
                        "\"text\":{\"color\":\"222222\"}," +
                        "\"behavior\":{\"priority\":5}}")
        ));

        Set<String> tags = Set.of("c:ores", "minecraft:diamond_ores");
        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond_ore", tags, registry);

        assertNotNull(r);
        assertEquals(0xFF222222, r.getTextColor()); // priority 5 wins over priority 1
    }

    // --- All three layers merge ---

    @Test
    void allLayersMerge() {
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"mod\",\"id\":\"minecraft\"}," +
                        "\"text\":{\"color\":\"AAAAAA\"}," +
                        "\"sound\":{\"volume\":0.3}}"),
                fromJson("{\"match\":{\"type\":\"tag\",\"id\":\"c:ores\"}," +
                        "\"sound\":{\"pitch\":1.2}," +
                        "\"display\":{\"durationMs\":7000}}"),
                fromJson("{\"match\":{\"type\":\"item\",\"id\":\"minecraft:iron_ore\"}," +
                        "\"display\":{\"scale\":1.5}}")
        ));

        Set<String> tags = Set.of("c:ores");
        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:iron_ore", tags, registry);

        assertNotNull(r);
        assertEquals(0xFFAAAAAA, r.getTextColor());   // from mod
        assertEquals(0.3f, r.getSoundVolume());        // from mod
        assertEquals(1.2f, r.getSoundPitch());         // from tag
        assertEquals(7000L, r.getDisplayDurationMs()); // from tag
        assertEquals(1.5f, r.getDisplayScale());       // from item
    }

    // --- Color parsing ---

    @Test
    void colorParsing_6charHex_addsFullAlpha() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"text\":{\"color\":\"FFD700\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertEquals(0xFFFFD700, r.getTextColor());
    }

    @Test
    void colorParsing_8charHex_preservesAlpha() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"text\":{\"color\":\"AA112233\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertEquals(0xAA112233, r.getTextColor());
    }

    // --- Background + visual effects ---

    @Test
    void backgroundAndVisualEffects_resolve() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"background\":{\"style\":\"TEXTURE\",\"texture\":\"lootlog:textures/gui/custom.png\"," +
                "\"color\":\"CC000000\",\"animation\":{\"type\":\"spritesheet\",\"frames\":4,\"frameTimeMs\":200}}," +
                "\"visual\":{\"iconGlow\":{\"color\":\"FFD700\",\"radius\":3}," +
                "\"effects\":[{\"type\":\"pulse\",\"color\":\"44FFD700\",\"speed\":1.5,\"minAlpha\":0.3,\"maxAlpha\":0.8}]}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals(BackgroundStyle.TEXTURE, r.getBackgroundStyle());
        assertEquals("lootlog:textures/gui/custom.png", r.getBackgroundTexture());
        assertEquals(0xCC000000, r.getBackgroundColor());

        assertNotNull(r.getFrameAnimation());
        assertEquals(4, r.getFrameAnimation().getFrames());
        assertEquals(200, r.getFrameAnimation().getFrameTimeMs());
        assertFalse(r.getFrameAnimation().isInterpolate());

        assertEquals(0xFFFFD700, r.getIconGlowColor());
        assertEquals(3, r.getIconGlowRadius());

        assertEquals(1, r.getBackgroundEffects().size());
        ResolvedOverride.BgEffect effect = r.getBackgroundEffects().get(0);
        assertEquals("pulse", effect.getType());
        assertEquals(1.5f, effect.getSpeed());
        assertEquals(0.3f, effect.getMinAlpha());
        assertEquals(0.8f, effect.getMaxAlpha());
    }

    @Test
    void iconGlowNewFields_resolve() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"visual\":{\"iconGlow\":{" +
                "\"color\":\"5555CDFC\",\"radius\":3," +
                "\"shape\":\"diamond\",\"style\":\"dithered\",\"softness\":3.0," +
                "\"pulse\":{\"speed\":2.0,\"min\":0.3,\"max\":0.9}" +
                "}}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals(0x5555CDFC, r.getIconGlowColor());
        assertEquals(3, r.getIconGlowRadius());
        assertEquals("diamond", r.getIconGlowShape());
        assertEquals("dithered", r.getIconGlowStyle());
        assertEquals(3.0f, r.getIconGlowSoftness());
        assertEquals(2.0f, r.getIconGlowPulseSpeed());
        assertEquals(0.3f, r.getIconGlowPulseMin());
        assertEquals(0.9f, r.getIconGlowPulseMax());
    }

    // --- Pickup pulse overrides ---

    @Test
    void pickupPulse_resolvesAllFields() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"visual\":{\"pickupPulse\":{" +
                "\"enabled\":true,\"durationMs\":300," +
                "\"iconScaleStrength\":0.25,\"iconAlphaStrength\":0.5," +
                "\"nameScaleStrength\":0.2,\"nameAlphaStrength\":0.3," +
                "\"totalCountScaleStrength\":0.1,\"totalCountAlphaStrength\":0.4," +
                "\"bodyScaleStrength\":0.05,\"bodyAlphaStrength\":0.6," +
                "\"accentScaleStrength\":0.08,\"accentAlphaStrength\":0.7," +
                "\"overallScaleStrength\":0.12,\"overallAlphaStrength\":0.9" +
                "}}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals(true, r.getPickupPulseEnabled());
        assertEquals(300, r.getPickupPulseDurationMs());
        assertEquals(0.25f, r.getPickupPulseIconScaleStrength());
        assertEquals(0.5f, r.getPickupPulseIconAlphaStrength());
        assertEquals(0.2f, r.getPickupPulseNameScaleStrength());
        assertEquals(0.3f, r.getPickupPulseNameAlphaStrength());
        assertEquals(0.1f, r.getPickupPulseTotalCountScaleStrength());
        assertEquals(0.4f, r.getPickupPulseTotalCountAlphaStrength());
        assertEquals(0.05f, r.getPickupPulseBodyScaleStrength());
        assertEquals(0.6f, r.getPickupPulseBodyAlphaStrength());
        assertEquals(0.08f, r.getPickupPulseAccentScaleStrength());
        assertEquals(0.7f, r.getPickupPulseAccentAlphaStrength());
        assertEquals(0.12f, r.getPickupPulseOverallScaleStrength());
        assertEquals(0.9f, r.getPickupPulseOverallAlphaStrength());
    }

    @Test
    void pickupPulse_mergesAcrossLayers() {
        // Mod sets iconScaleStrength, item sets bodyAlphaStrength -- both survive
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"mod\",\"id\":\"minecraft\"}," +
                        "\"visual\":{\"pickupPulse\":{\"iconScaleStrength\":0.3,\"durationMs\":400}}}"),
                fromJson("{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                        "\"visual\":{\"pickupPulse\":{\"bodyAlphaStrength\":0.5}}}")
        ));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals(0.3f, r.getPickupPulseIconScaleStrength());
        assertEquals(400, r.getPickupPulseDurationMs());
        assertEquals(0.5f, r.getPickupPulseBodyAlphaStrength());
    }

    @Test
    void pickupPulse_higherPriorityOverwrites() {
        // Mod sets iconScaleStrength=0.1, item overrides to 0.4
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"mod\",\"id\":\"minecraft\"}," +
                        "\"visual\":{\"pickupPulse\":{\"iconScaleStrength\":0.1}}}"),
                fromJson("{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                        "\"visual\":{\"pickupPulse\":{\"iconScaleStrength\":0.4}}}")
        ));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals(0.4f, r.getPickupPulseIconScaleStrength());
    }

    // --- Display overrides ---

    @Test
    void displayOverrides_combineMode() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"display\":{\"combineMode\":\"NEVER\",\"scale\":1.3}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertEquals(CombineMode.NEVER, r.getCombineMode());
        assertEquals(1.3f, r.getDisplayScale());
    }

    // --- Text overrides ---

    @Test
    void textOverrides_fullName() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"text\":{\"fullName\":\"Shiny Rock\",\"markup\":\"<rainbow>{name}</rainbow>\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertEquals("Shiny Rock", r.getFullName());
        assertEquals("<rainbow>{name}</rainbow>", r.getNameMarkup());
    }

    // --- Invalid data handling ---

    @Test
    void invalidBackgroundStyle_ignored() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"background\":{\"style\":\"NOT_A_STYLE\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNotNull(r);
        assertNull(r.getBackgroundStyle()); // invalid enum ignored
    }

    @Test
    void invalidColor_returnsNull() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"text\":{\"color\":\"not_hex\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNotNull(r);
        assertNull(r.getTextColor()); // invalid hex ignored
    }

    // --- Decoration preset tests ---

    @Test
    void decorationOverride_setsTextureAndStyle() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"background\":{\"decoration\":\"default_banner\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals(BackgroundStyle.BANNER, r.getBackgroundStyle());
        assertEquals("lootlog:textures/gui/lootlog/banner_body.png", r.getBackgroundTexture());
    }

    @Test
    void decorationOverride_defaultBanner_setsBannerStyle() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"background\":{\"decoration\":\"default_banner\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals(BackgroundStyle.BANNER, r.getBackgroundStyle());
        assertEquals("lootlog:textures/gui/lootlog/banner_body.png", r.getBackgroundTexture());
    }

    @Test
    void decorationWithExplicitTexture_textureOverridesDecoration() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"background\":{\"decoration\":\"default_banner\",\"texture\":\"mymod:textures/custom.png\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals("mymod:textures/custom.png", r.getBackgroundTexture());
        assertEquals(BackgroundStyle.BANNER, r.getBackgroundStyle());
    }

    @Test
    void decorationWithExplicitStyle_styleOverridesDecoration() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"background\":{\"decoration\":\"default_banner\",\"style\":\"SOLID\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals(BackgroundStyle.SOLID, r.getBackgroundStyle());
    }

    @Test
    void invalidDecoration_ignored() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"background\":{\"decoration\":\"nonexistent\",\"style\":\"TOOLTIP\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals(BackgroundStyle.TOOLTIP, r.getBackgroundStyle());
        assertNull(r.getBackgroundTexture());
    }

    // --- Rarity match ---

    @Test
    void rarityMatch_resolvesFields() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"rarity\",\"id\":\"epic\"}," +
                "\"text\":{\"color\":\"FF55FF\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), "epic", registry);

        assertNotNull(r);
        assertEquals(0xFFFF55FF, r.getTextColor());
    }

    @Test
    void rarityMatch_noMatch_returnsNull() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"rarity\",\"id\":\"epic\"}," +
                "\"text\":{\"color\":\"FF55FF\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:stick", Collections.emptySet(), "common", registry);

        assertNull(r);
    }

    @Test
    void rarityOverridesMod() {
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"mod\",\"id\":\"minecraft\"}," +
                        "\"text\":{\"color\":\"111111\"}}"),
                fromJson("{\"match\":{\"type\":\"rarity\",\"id\":\"rare\"}," +
                        "\"text\":{\"color\":\"222222\"}}")
        ));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), "rare", registry);

        assertNotNull(r);
        assertEquals(0xFF222222, r.getTextColor()); // rarity wins over mod
    }

    @Test
    void tagOverridesRarity() {
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"rarity\",\"id\":\"rare\"}," +
                        "\"text\":{\"color\":\"111111\"}}"),
                fromJson("{\"match\":{\"type\":\"tag\",\"id\":\"c:gems\"}," +
                        "\"text\":{\"color\":\"222222\"}}")
        ));

        Set<String> tags = Set.of("c:gems");
        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", tags, "rare", registry);

        assertNotNull(r);
        assertEquals(0xFF222222, r.getTextColor()); // tag wins over rarity
    }

    // --- Regex match ---

    @Test
    void regexMatch_resolvesFields() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"regex\",\"id\":\"minecraft:.*_ore\"}," +
                "\"text\":{\"color\":\"AA5500\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:iron_ore", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals(0xFFAA5500, r.getTextColor());
    }

    @Test
    void regexMatch_noMatch_returnsNull() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"regex\",\"id\":\"minecraft:.*_ore\"}," +
                "\"text\":{\"color\":\"AA5500\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNull(r);
    }

    @Test
    void regexPriority_higherPriorityWins() {
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"regex\",\"id\":\"minecraft:.*\"}," +
                        "\"text\":{\"color\":\"111111\"}," +
                        "\"behavior\":{\"priority\":1}}"),
                fromJson("{\"match\":{\"type\":\"regex\",\"id\":\"minecraft:.*_ingot\"}," +
                        "\"text\":{\"color\":\"222222\"}," +
                        "\"behavior\":{\"priority\":5}}")
        ));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:iron_ingot", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals(0xFF222222, r.getTextColor()); // priority 5 wins
    }

    @Test
    void tagOverridesRegex() {
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"regex\",\"id\":\"minecraft:.*_ore\"}," +
                        "\"text\":{\"color\":\"111111\"}}"),
                fromJson("{\"match\":{\"type\":\"tag\",\"id\":\"c:ores\"}," +
                        "\"text\":{\"color\":\"222222\"}}")
        ));

        Set<String> tags = Set.of("c:ores");
        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:iron_ore", tags, registry);

        assertNotNull(r);
        assertEquals(0xFF222222, r.getTextColor()); // tag wins over regex
    }

    @Test
    void regexOverridesRarity() {
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"rarity\",\"id\":\"common\"}," +
                        "\"text\":{\"color\":\"111111\"}}"),
                fromJson("{\"match\":{\"type\":\"regex\",\"id\":\"minecraft:.*_ore\"}," +
                        "\"text\":{\"color\":\"222222\"}}")
        ));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:iron_ore", Collections.emptySet(), "common", registry);

        assertNotNull(r);
        assertEquals(0xFF222222, r.getTextColor()); // regex wins over rarity
    }

    // --- Full 5-layer merge ---

    @Test
    void fullFiveLayerMerge() {
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"mod\",\"id\":\"minecraft\"}," +
                        "\"sound\":{\"volume\":0.3}}"),
                fromJson("{\"match\":{\"type\":\"rarity\",\"id\":\"uncommon\"}," +
                        "\"sound\":{\"pitch\":1.2}}"),
                fromJson("{\"match\":{\"type\":\"regex\",\"id\":\"minecraft:.*_ore\"}," +
                        "\"text\":{\"prefix\":\"[Ore] \"}}"),
                fromJson("{\"match\":{\"type\":\"tag\",\"id\":\"c:ores\"}," +
                        "\"display\":{\"durationMs\":7000}}"),
                fromJson("{\"match\":{\"type\":\"item\",\"id\":\"minecraft:iron_ore\"}," +
                        "\"display\":{\"scale\":1.5}}")
        ));

        Set<String> tags = Set.of("c:ores");
        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:iron_ore", tags, "uncommon", registry);

        assertNotNull(r);
        assertEquals(0.3f, r.getSoundVolume());        // from mod
        assertEquals(1.2f, r.getSoundPitch());         // from rarity
        assertEquals("[Ore] ", r.getNamePrefix());     // from regex
        assertEquals(7000L, r.getDisplayDurationMs()); // from tag
        assertEquals(1.5f, r.getDisplayScale());       // from item
    }

    // --- Invalid regex skipped gracefully ---

    @Test
    void invalidRegex_skippedGracefully() {
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"regex\",\"id\":\"[invalid\"}," +
                        "\"text\":{\"color\":\"111111\"}}"),
                fromJson("{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                        "\"text\":{\"color\":\"222222\"}}")
        ));

        // Invalid regex is skipped, item override still works
        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNotNull(r);
        assertEquals(0xFF222222, r.getTextColor());
        assertEquals(1, registry.size()); // only the item override loaded
    }

    // --- Color parsing with # prefix ---

    @Test
    void colorParsing_hashPrefix6Char() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"text\":{\"color\":\"#FFD700\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertEquals(0xFFFFD700, r.getTextColor());
    }

    @Test
    void colorParsing_hashPrefix8Char() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"item\",\"id\":\"minecraft:diamond\"}," +
                "\"text\":{\"color\":\"#AA112233\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertEquals(0xAA112233, r.getTextColor());
    }

    // --- Backward compatibility: 3-arg resolve still works ---

    @Test
    void threeArgResolve_worksWithoutRarity() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"rarity\",\"id\":\"epic\"}," +
                "\"text\":{\"color\":\"FF55FF\"}}")));

        // 3-arg overload should skip rarity layer (rarityName=null)
        ResolvedOverride r = OverrideResolver.resolve(
                "minecraft:diamond", Collections.emptySet(), registry);

        assertNull(r); // rarity override exists but no rarity name provided
    }

    // --- Modded rarity match ---

    @Test
    void moddedRarityMatch_resolvesFields() {
        registry.load(List.of(fromJson(
                "{\"match\":{\"type\":\"rarity\",\"id\":\"mythic\"}," +
                "\"text\":{\"color\":\"FFD700\"}}")));

        ResolvedOverride r = OverrideResolver.resolve(
                "somemod:special_sword", Collections.emptySet(), "mythic", registry);

        assertNotNull(r);
        assertEquals(0xFFFFD700, r.getTextColor());
    }

    @Test
    void moddedRarityOverridesMod() {
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"mod\",\"id\":\"somemod\"}," +
                        "\"text\":{\"color\":\"111111\"}}"),
                fromJson("{\"match\":{\"type\":\"rarity\",\"id\":\"legendary\"}," +
                        "\"text\":{\"color\":\"222222\"}}")
        ));

        ResolvedOverride r = OverrideResolver.resolve(
                "somemod:special_sword", Collections.emptySet(), "legendary", registry);

        assertNotNull(r);
        assertEquals(0xFF222222, r.getTextColor()); // modded rarity wins over mod
    }

    @Test
    void multipleModdedRarities_resolveIndependently() {
        registry.load(List.of(
                fromJson("{\"match\":{\"type\":\"rarity\",\"id\":\"mythic\"}," +
                        "\"text\":{\"color\":\"FFD700\"}}"),
                fromJson("{\"match\":{\"type\":\"rarity\",\"id\":\"legendary\"}," +
                        "\"text\":{\"color\":\"FF4500\"}}")
        ));

        ResolvedOverride mythic = OverrideResolver.resolve(
                "somemod:item_a", Collections.emptySet(), "mythic", registry);
        ResolvedOverride legendary = OverrideResolver.resolve(
                "somemod:item_b", Collections.emptySet(), "legendary", registry);

        assertNotNull(mythic);
        assertNotNull(legendary);
        assertEquals(0xFFFFD700, mythic.getTextColor());
        assertEquals(0xFFFF4500, legendary.getTextColor());
    }
}
