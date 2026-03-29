package dev.tysontheember.lootlog.neoforge;

import dev.tysontheember.lootlog.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Collections;
import java.util.List;

public class NeoForgeConfig {

    public static final ModConfigSpec SPEC;

    // General
    private static final ModConfigSpec.LongValue DISPLAY_DURATION_MS;
    private static final ModConfigSpec.IntValue MAX_ENTRIES;
    private static final ModConfigSpec.EnumValue<CombineMode> COMBINE_MODE;
    private static final ModConfigSpec.BooleanValue SHOW_ITEMS;
    private static final ModConfigSpec.BooleanValue SHOW_XP;
    // Position
    private static final ModConfigSpec.EnumValue<HudAnchor> ANCHOR;
    private static final ModConfigSpec.IntValue X_OFFSET;
    private static final ModConfigSpec.IntValue Y_OFFSET;
    private static final ModConfigSpec.IntValue ENTRY_SPACING;
    private static final ModConfigSpec.DoubleValue SCALE;
    private static final ModConfigSpec.BooleanValue CLAMP_TO_SCREEN;
    private static final ModConfigSpec.ConfigValue<String> GROWTH_DIRECTION;

    // Animation
    private static final ModConfigSpec.LongValue FADE_IN_MS;
    private static final ModConfigSpec.LongValue FADE_OUT_MS;
    private static final ModConfigSpec.DoubleValue SLIDE_DISTANCE;
    private static final ModConfigSpec.BooleanValue FADE_OUT_SLIDE;
    private static final ModConfigSpec.DoubleValue VERTICAL_ANIM_SPEED;
    private static final ModConfigSpec.EnumValue<Easing> SLIDE_EASING;
    private static final ModConfigSpec.BooleanValue SCALE_ENTRANCE;
    private static final ModConfigSpec.DoubleValue ENTRANCE_SCALE_START;
    private static final ModConfigSpec.IntValue STAGGER_DELAY_MS;

    // Layout
    private static final ModConfigSpec.ConfigValue<String> LAYOUT_PRESET;

    // Appearance
    private static final ModConfigSpec.ConfigValue<String> DECORATION;
    private static final ModConfigSpec.ConfigValue<String> BACKGROUND_STYLE;
    private static final ModConfigSpec.LongValue BACKGROUND_COLOR;
    private static final ModConfigSpec.IntValue BACKGROUND_H_PADDING;
    private static final ModConfigSpec.IntValue BACKGROUND_V_PADDING;
    private static final ModConfigSpec.IntValue MAX_NAME_WIDTH;
    private static final ModConfigSpec.LongValue COUNT_COLOR;
    private static final ModConfigSpec.LongValue NAME_COLOR_OVERRIDE;
    private static final ModConfigSpec.BooleanValue SHOW_COUNT;
    private static final ModConfigSpec.BooleanValue USE_RARITY_COLORS;
    private static final ModConfigSpec.BooleanValue ICON_ON_RIGHT;
    private static final ModConfigSpec.BooleanValue TEXT_SHADOW;
    private static final ModConfigSpec.BooleanValue SHOW_COUNT_RIGHT;
    private static final ModConfigSpec.BooleanValue ANIMATE_XP_COLOR;
    private static final ModConfigSpec.BooleanValue ABBREVIATE_COUNTS;

    // Banner / FLAT layout
    private static final ModConfigSpec.ConfigValue<String> BANNER_ELEMENT_ORDER;
    private static final ModConfigSpec.IntValue DECORATIVE_EDGE_INSET;
    private static final ModConfigSpec.IntValue ICON_TO_NAME_GAP;
    private static final ModConfigSpec.IntValue NAME_TO_COUNT_GAP;

    // Banner layer 0 (body)
    private static final ModConfigSpec.DoubleValue BODY_ALPHA;
    private static final ModConfigSpec.LongValue BODY_TINT;
    private static final ModConfigSpec.IntValue BODY_ANIM_SPEED;

    // Banner layer 1 (accent)
    private static final ModConfigSpec.BooleanValue SHOW_ACCENT;
    private static final ModConfigSpec.DoubleValue ACCENT_ALPHA;
    private static final ModConfigSpec.LongValue ACCENT_TINT;
    private static final ModConfigSpec.IntValue ACCENT_ANIM_SPEED;
    private static final ModConfigSpec.IntValue ACCENT_X_OFFSET;
    private static final ModConfigSpec.IntValue ACCENT_Y_OFFSET;
    private static final ModConfigSpec.ConfigValue<String> ACCENT_ANCHOR;

    // Effect targeting
    private static final ModConfigSpec.ConfigValue<String> EFFECT_TARGET;

    // Pickup Pulse
    private static final ModConfigSpec.BooleanValue PICKUP_PULSE_ENABLED;
    private static final ModConfigSpec.IntValue PICKUP_PULSE_DURATION_MS;
    private static final ModConfigSpec.DoubleValue PICKUP_PULSE_ICON_SCALE_STRENGTH;
    private static final ModConfigSpec.DoubleValue PICKUP_PULSE_ICON_ALPHA_STRENGTH;
    private static final ModConfigSpec.DoubleValue PICKUP_PULSE_NAME_SCALE_STRENGTH;
    private static final ModConfigSpec.DoubleValue PICKUP_PULSE_NAME_ALPHA_STRENGTH;
    private static final ModConfigSpec.DoubleValue PICKUP_PULSE_TOTAL_COUNT_SCALE_STRENGTH;
    private static final ModConfigSpec.DoubleValue PICKUP_PULSE_TOTAL_COUNT_ALPHA_STRENGTH;
    private static final ModConfigSpec.DoubleValue PICKUP_PULSE_BODY_SCALE_STRENGTH;
    private static final ModConfigSpec.DoubleValue PICKUP_PULSE_BODY_ALPHA_STRENGTH;
    private static final ModConfigSpec.DoubleValue PICKUP_PULSE_ACCENT_SCALE_STRENGTH;
    private static final ModConfigSpec.DoubleValue PICKUP_PULSE_ACCENT_ALPHA_STRENGTH;
    private static final ModConfigSpec.DoubleValue PICKUP_PULSE_OVERALL_SCALE_STRENGTH;
    private static final ModConfigSpec.DoubleValue PICKUP_PULSE_OVERALL_ALPHA_STRENGTH;

    // Progress Bar
    private static final ModConfigSpec.BooleanValue SHOW_PROGRESS_BAR;
    private static final ModConfigSpec.LongValue PROGRESS_BAR_COLOR;
    private static final ModConfigSpec.IntValue PROGRESS_BAR_HEIGHT;

    // Icon Glow
    private static final ModConfigSpec.BooleanValue ICON_GLOW_ENABLED;
    private static final ModConfigSpec.LongValue ICON_GLOW_COLOR;
    private static final ModConfigSpec.IntValue ICON_GLOW_RADIUS;
    private static final ModConfigSpec.ConfigValue<String> ICON_GLOW_SHAPE;
    private static final ModConfigSpec.DoubleValue ICON_GLOW_SOFTNESS;
    private static final ModConfigSpec.DoubleValue ICON_GLOW_PULSE_SPEED;
    private static final ModConfigSpec.DoubleValue ICON_GLOW_PULSE_MIN;
    private static final ModConfigSpec.DoubleValue ICON_GLOW_PULSE_MAX;

    // Icon Shadow
    private static final ModConfigSpec.BooleanValue ICON_SHADOW_ENABLED;
    private static final ModConfigSpec.LongValue ICON_SHADOW_COLOR;
    private static final ModConfigSpec.IntValue ICON_SHADOW_OFFSET_X;
    private static final ModConfigSpec.IntValue ICON_SHADOW_OFFSET_Y;
    private static final ModConfigSpec.IntValue ICON_SHADOW_RADIUS;
    private static final ModConfigSpec.ConfigValue<String> ICON_SHADOW_SHAPE;
    private static final ModConfigSpec.DoubleValue ICON_SHADOW_SOFTNESS;

    // Filtering
    private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_BLACKLIST;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_WHITELIST;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> MOD_BLACKLIST;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> MOD_WHITELIST;

    // Sound
    private static final ModConfigSpec.BooleanValue SOUND_ENABLED;
    private static final ModConfigSpec.ConfigValue<String> SOUND_ID;
    private static final ModConfigSpec.DoubleValue SOUND_VOLUME;
    private static final ModConfigSpec.DoubleValue SOUND_PITCH;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("general");
        DISPLAY_DURATION_MS = builder
            .comment("How long entries stay visible (milliseconds)")
            .defineInRange("displayDurationMs", 5000L, 500L, 30000L);
        MAX_ENTRIES = builder
            .comment("Maximum number of entries shown at once")
            .defineInRange("maxEntries", 10, 1, 30);
        COMBINE_MODE = builder
            .comment("How duplicate pickups stack: ALWAYS, NEVER, EXCLUDE_NAMED")
            .defineEnum("combineMode", CombineMode.ALWAYS);
        SHOW_ITEMS = builder
            .comment("Show item pickup notifications")
            .define("showItems", true);
        SHOW_XP = builder
            .comment("Show XP pickup notifications")
            .define("showXp", true);
        builder.pop();

        builder.push("position");
        ANCHOR = builder
            .comment("Screen corner for the pickup list")
            .defineEnum("anchor", HudAnchor.BOTTOM_RIGHT);
        X_OFFSET = builder
            .comment("Horizontal offset from screen edge")
            .defineInRange("xOffset", 5, 0, 500);
        Y_OFFSET = builder
            .comment("Vertical offset from screen edge")
            .defineInRange("yOffset", 5, 0, 500);
        ENTRY_SPACING = builder
            .comment("Vertical spacing between entries")
            .defineInRange("entrySpacing", 2, 0, 20);
        SCALE = builder
            .comment("HUD scale multiplier")
            .defineInRange("scale", 1.0, 0.25, 4.0);
        CLAMP_TO_SCREEN = builder
            .comment("Prevent entries from rendering off-screen")
            .define("clampToScreen", true);
        GROWTH_DIRECTION = builder
            .comment("Direction entries grow from the anchor: NORMAL or INVERSE")
            .define("growthDirection", "NORMAL");
        builder.pop();

        builder.push("animation");
        FADE_IN_MS = builder
            .comment("Fade-in animation duration (milliseconds)")
            .defineInRange("fadeInMs", 300L, 0L, 2000L);
        FADE_OUT_MS = builder
            .comment("Fade-out animation duration (milliseconds)")
            .defineInRange("fadeOutMs", 500L, 0L, 2000L);
        SLIDE_DISTANCE = builder
            .comment("Horizontal slide distance in pixels for enter/exit animations")
            .defineInRange("slideDistance", 220.0, 0.0, 500.0);
        FADE_OUT_SLIDE = builder
            .comment("Slide entries off-screen while fading out")
            .define("fadeOutSlide", true);
        VERTICAL_ANIM_SPEED = builder
            .comment("Speed of vertical position animation when entries shift (0.0 = instant, 1.0 = very slow)")
            .defineInRange("verticalAnimSpeed", 0.3, 0.0, 1.0);
        SLIDE_EASING = builder
            .comment("Easing function for slide-in animation: QUAD_OUT, CUBIC_OUT, BACK_OUT, ELASTIC_OUT")
            .defineEnum("slideEasing", Easing.QUAD_OUT);
        SCALE_ENTRANCE = builder
            .comment("Scale entries up from small during fade-in")
            .define("scaleEntrance", false);
        ENTRANCE_SCALE_START = builder
            .comment("Starting scale factor for entrance animation (0.1-1.0)")
            .defineInRange("entranceScaleStart", 0.8, 0.1, 1.0);
        STAGGER_DELAY_MS = builder
            .comment("Delay between consecutive entry animations in ms (0 = no stagger)")
            .defineInRange("staggerDelayMs", 0, 0, 500);
        builder.pop();

        builder.push("layout");
        LAYOUT_PRESET = builder
            .comment("Named layout preset (leave empty for auto-detection from backgroundStyle)")
            .define("layoutPreset", "");
        builder.pop();

        builder.push("appearance");
        DECORATION = builder
            .comment("Named decoration preset (overrides backgroundStyle when set). Options: default_banner. Leave empty to use backgroundStyle.")
            .define("decoration", "");
        BACKGROUND_STYLE = builder
            .comment("Background style: NONE, SOLID, TOOLTIP, TEXTURE, BANNER, FLAT")
            .define("backgroundStyle", "BANNER");
        BACKGROUND_COLOR = builder
            .comment("Background color as ARGB (e.g., 2852126720 = 0xAA000000)")
            .defineInRange("backgroundColor", 0xAA000000L, 0L, 0xFFFFFFFFL);
        BACKGROUND_H_PADDING = builder
            .comment("Horizontal padding inside the background rectangle")
            .defineInRange("backgroundHPadding", 4, 0, 20);
        BACKGROUND_V_PADDING = builder
            .comment("Vertical padding inside the background rectangle")
            .defineInRange("backgroundVPadding", 2, 0, 20);
        MAX_NAME_WIDTH = builder
            .comment("Maximum pixel width for item names before truncation (0 = unlimited)")
            .defineInRange("maxNameWidth", 150, 0, 500);
        COUNT_COLOR = builder
            .comment("Color for the count text in CLASSIC/FLAT styles (ARGB)")
            .defineInRange("countColor", 0xFFAAAAAAL, 0L, 0xFFFFFFFFL);
        NAME_COLOR_OVERRIDE = builder
            .comment("Text color when rarity colors are disabled (ARGB)")
            .defineInRange("nameColorOverride", 0xFFFFFFFFL, 0L, 0xFFFFFFFFL);
        SHOW_COUNT = builder
            .comment("Show item count prefix in entry text (e.g., '64x Cobblestone')")
            .define("showCount", true);
        USE_RARITY_COLORS = builder
            .comment("Color item names by rarity")
            .define("useRarityColors", true);
        ICON_ON_RIGHT = builder
            .comment("Place icon to the right of the item name (true) or left (false)")
            .define("iconOnRight", true);
        TEXT_SHADOW = builder
            .comment("Render text with drop shadow")
            .define("textShadow", true);
        SHOW_COUNT_RIGHT = builder
            .comment("Show total count to the right of the icon (e.g., 'x64')")
            .define("showCountRight", true);
        ANIMATE_XP_COLOR = builder
            .comment("Animate XP entry text color with a pulsing green effect")
            .define("animateXpColor", true);
        ABBREVIATE_COUNTS = builder
            .comment("Abbreviate large counts (e.g., 1500 -> 1.5K)")
            .define("abbreviateCounts", true);
        builder.pop();

        builder.push("banner_layout");
        BANNER_ELEMENT_ORDER = builder
            .comment("Order of banner elements from decorative edge to screen edge. Comma-separated: PICKUP_COUNT, NAME, ICON, TOTAL_COUNT")
            .define("bannerElementOrder", "PICKUP_COUNT,NAME,ICON,TOTAL_COUNT");
        DECORATIVE_EDGE_INSET = builder
            .comment("Inset in pixels for the decorative edge of the banner")
            .defineInRange("decorativeEdgeInset", 6, 0, 50);
        ICON_TO_NAME_GAP = builder
            .comment("Gap in pixels between the icon and item name")
            .defineInRange("iconToNameGap", 4, 0, 20);
        NAME_TO_COUNT_GAP = builder
            .comment("Gap in pixels between the item name and count")
            .defineInRange("nameToCountGap", 4, 0, 20);
        builder.pop();

        builder.push("banner_body");
        BODY_ALPHA = builder
            .comment("Opacity of the banner body layer (0.0-1.0)")
            .defineInRange("bodyAlpha", 1.0, 0.0, 1.0);
        BODY_TINT = builder
            .comment("Tint color for the banner body layer as ARGB")
            .defineInRange("bodyTint", 0xFFFFFFFFL, 0L, 0xFFFFFFFFL);
        BODY_ANIM_SPEED = builder
            .comment("Animation speed for the banner body layer (0-100)")
            .defineInRange("bodyAnimSpeed", 4, 0, 100);
        builder.pop();

        builder.push("banner_accent");
        SHOW_ACCENT = builder
            .comment("Show the accent layer on the banner")
            .define("showAccent", true);
        ACCENT_ALPHA = builder
            .comment("Opacity of the accent layer (0.0-1.0)")
            .defineInRange("accentAlpha", 1.0, 0.0, 1.0);
        ACCENT_TINT = builder
            .comment("Tint color for the accent layer as ARGB")
            .defineInRange("accentTint", 0xFFFFFFFFL, 0L, 0xFFFFFFFFL);
        ACCENT_ANIM_SPEED = builder
            .comment("Animation speed for the accent layer (0-100)")
            .defineInRange("accentAnimSpeed", 4, 0, 100);
        ACCENT_X_OFFSET = builder
            .comment("Horizontal offset of the accent layer (-50 to 50)")
            .defineInRange("accentXOffset", 0, -50, 50);
        ACCENT_Y_OFFSET = builder
            .comment("Vertical offset of the accent layer (-50 to 50)")
            .defineInRange("accentYOffset", 0, -50, 50);
        ACCENT_ANCHOR = builder
            .comment("Anchor point for accent layer: EDGE, ICON, NAME, COUNT")
            .define("accentAnchor", "ICON");
        builder.pop();

        builder.push("effect_targeting");
        EFFECT_TARGET = builder
            .comment("Which entries receive icon/glow effects: ALL, ITEMS, XP")
            .define("effectTarget", "ALL");
        builder.pop();

        builder.push("pickup_pulse");
        PICKUP_PULSE_ENABLED = builder
            .comment("Enable per-part pickup pulse animation (brief scale + alpha pop on pickup). When enabled, replaces icon_bounce.")
            .define("enabled", true);
        PICKUP_PULSE_DURATION_MS = builder
            .comment("Pulse animation duration in milliseconds")
            .defineInRange("durationMs", 200, 50, 1000);
        PICKUP_PULSE_ICON_SCALE_STRENGTH = builder
            .comment("Icon scale pulse strength (0 = disabled)")
            .defineInRange("iconScaleStrength", 0.05, 0.0, 0.5);
        PICKUP_PULSE_ICON_ALPHA_STRENGTH = builder
            .comment("Icon brightness pulse strength (0 = disabled)")
            .defineInRange("iconAlphaStrength", 0.05, 0.0, 1.0);
        PICKUP_PULSE_NAME_SCALE_STRENGTH = builder
            .comment("Item name text scale pulse strength")
            .defineInRange("nameScaleStrength", 0.05, 0.0, 0.5);
        PICKUP_PULSE_NAME_ALPHA_STRENGTH = builder
            .comment("Item name text brightness pulse strength")
            .defineInRange("nameAlphaStrength", 0.0, 0.0, 1.0);
        PICKUP_PULSE_TOTAL_COUNT_SCALE_STRENGTH = builder
            .comment("Total count text scale pulse strength")
            .defineInRange("totalCountScaleStrength", 0.05, 0.0, 0.5);
        PICKUP_PULSE_TOTAL_COUNT_ALPHA_STRENGTH = builder
            .comment("Total count text brightness pulse strength")
            .defineInRange("totalCountAlphaStrength", 0.0, 0.0, 1.0);
        PICKUP_PULSE_BODY_SCALE_STRENGTH = builder
            .comment("Background body scale pulse strength")
            .defineInRange("bodyScaleStrength", 0.0, 0.0, 0.5);
        PICKUP_PULSE_BODY_ALPHA_STRENGTH = builder
            .comment("Background body brightness pulse strength")
            .defineInRange("bodyAlphaStrength", 0.0, 0.0, 1.0);
        PICKUP_PULSE_ACCENT_SCALE_STRENGTH = builder
            .comment("Background accent scale pulse strength")
            .defineInRange("accentScaleStrength", 0.0, 0.0, 0.5);
        PICKUP_PULSE_ACCENT_ALPHA_STRENGTH = builder
            .comment("Background accent brightness pulse strength")
            .defineInRange("accentAlphaStrength", 0.0, 0.0, 1.0);
        PICKUP_PULSE_OVERALL_SCALE_STRENGTH = builder
            .comment("Whole entry scale pulse strength")
            .defineInRange("overallScaleStrength", 0.05, 0.0, 0.5);
        PICKUP_PULSE_OVERALL_ALPHA_STRENGTH = builder
            .comment("Whole entry brightness pulse strength")
            .defineInRange("overallAlphaStrength", 0.0, 0.0, 1.0);
        builder.pop();

        builder.push("progress_bar");
        SHOW_PROGRESS_BAR = builder
            .comment("Show a thin bar at the bottom of entries indicating remaining display time")
            .define("enabled", false);
        PROGRESS_BAR_COLOR = builder
            .comment("Progress bar color as ARGB")
            .defineInRange("color", 0x80FFFFFFL, 0L, 0xFFFFFFFFL);
        PROGRESS_BAR_HEIGHT = builder
            .comment("Progress bar height in pixels (1-3)")
            .defineInRange("height", 1, 1, 3);
        builder.pop();

        builder.push("icon_glow");
        ICON_GLOW_ENABLED = builder
            .comment("Enable a glow effect behind item icons")
            .define("enabled", false);
        ICON_GLOW_COLOR = builder
            .comment("Glow color as ARGB (e.g., 2868903935 = 0xAAFFFFFF)")
            .defineInRange("color", 0xAAFFFFFFL, 0L, 0xFFFFFFFFL);
        ICON_GLOW_RADIUS = builder
            .comment("Glow radius in pixels (0-8)")
            .defineInRange("radius", 3, 0, 8);
        ICON_GLOW_SHAPE = builder
            .comment("Glow shape: circle, item, square, diamond")
            .define("shape", "circle");
        ICON_GLOW_SOFTNESS = builder
            .comment("Glow falloff softness (0.5-5.0, higher = more gradual)")
            .defineInRange("softness", 1.5, 0.5, 5.0);
        ICON_GLOW_PULSE_SPEED = builder
            .comment("Glow pulse speed in cycles/sec (0 = no pulse)")
            .defineInRange("pulseSpeed", 0.0, 0.0, 10.0);
        ICON_GLOW_PULSE_MIN = builder
            .comment("Minimum pulse intensity (0.0-1.0)")
            .defineInRange("pulseMin", 0.5, 0.0, 1.0);
        ICON_GLOW_PULSE_MAX = builder
            .comment("Maximum pulse intensity (0.0-1.0)")
            .defineInRange("pulseMax", 1.0, 0.0, 1.0);
        builder.pop();

        builder.push("icon_shadow");
        ICON_SHADOW_ENABLED = builder
            .comment("Enable a shadow behind item icons")
            .define("enabled", false);
        ICON_SHADOW_COLOR = builder
            .comment("Shadow color as ARGB (e.g., 2147483648 = 0x80000000)")
            .defineInRange("color", 0x80000000L, 0L, 0xFFFFFFFFL);
        ICON_SHADOW_OFFSET_X = builder
            .comment("Shadow horizontal offset in pixels (0-4)")
            .defineInRange("offsetX", 1, 0, 4);
        ICON_SHADOW_OFFSET_Y = builder
            .comment("Shadow vertical offset in pixels (0-4)")
            .defineInRange("offsetY", 1, 0, 4);
        ICON_SHADOW_RADIUS = builder
            .comment("Shadow blur radius in pixels (0-4)")
            .defineInRange("radius", 1, 0, 4);
        ICON_SHADOW_SHAPE = builder
            .comment("Shadow shape: item, circle, square, diamond")
            .define("shape", "item");
        ICON_SHADOW_SOFTNESS = builder
            .comment("Shadow falloff softness (0.5-5.0)")
            .defineInRange("softness", 1.5, 0.5, 5.0);
        builder.pop();

        builder.push("filtering");
        ITEM_BLACKLIST = builder
            .comment("Item IDs to hide (e.g., [\"minecraft:cobblestone\", \"minecraft:dirt\"])")
            .defineList("itemBlacklist", Collections.emptyList(), s -> s instanceof String);
        ITEM_WHITELIST = builder
            .comment("If non-empty, only show these item IDs")
            .defineList("itemWhitelist", Collections.emptyList(), s -> s instanceof String);
        MOD_BLACKLIST = builder
            .comment("Mod namespaces to hide (e.g., [\"create\"])")
            .defineList("modBlacklist", Collections.emptyList(), s -> s instanceof String);
        MOD_WHITELIST = builder
            .comment("If non-empty, only show items from these mods")
            .defineList("modWhitelist", Collections.emptyList(), s -> s instanceof String);
        builder.pop();

        builder.push("sound");
        SOUND_ENABLED = builder
            .comment("Play a sound on pickup notifications")
            .define("soundEnabled", false);
        SOUND_ID = builder
            .comment("Sound to play (namespaced, e.g., minecraft:entity.item.pickup)")
            .define("soundId", "minecraft:entity.item.pickup");
        SOUND_VOLUME = builder
            .comment("Sound volume (0.0 - 1.0)")
            .defineInRange("soundVolume", 0.5, 0.0, 1.0);
        SOUND_PITCH = builder
            .comment("Sound pitch (0.5 - 2.0)")
            .defineInRange("soundPitch", 1.0, 0.5, 2.0);
        builder.pop();

        SPEC = builder.build();
    }

    @SuppressWarnings("unchecked")
    public static void saveFromPojo() {
        LootLogConfig config = LootLog.getConfig();
        ItemFilter filter = LootLog.getFilter();

        DISPLAY_DURATION_MS.set(config.getDisplayDurationMs());
        MAX_ENTRIES.set(config.getMaxEntries());
        COMBINE_MODE.set(config.getCombineMode());
        SHOW_ITEMS.set(config.isShowItems());
        SHOW_XP.set(config.isShowXp());
        ANCHOR.set(config.getAnchor());
        X_OFFSET.set(config.getXOffset());
        Y_OFFSET.set(config.getYOffset());
        ENTRY_SPACING.set(config.getEntrySpacing());
        SCALE.set((double) config.getScale());
        CLAMP_TO_SCREEN.set(config.isClampToScreen());
        GROWTH_DIRECTION.set(config.getGrowthDirection().name());
        FADE_IN_MS.set(config.getFadeInMs());
        FADE_OUT_MS.set(config.getFadeOutMs());
        SLIDE_DISTANCE.set((double) config.getSlideDistance());
        FADE_OUT_SLIDE.set(config.isFadeOutSlide());
        VERTICAL_ANIM_SPEED.set((double) config.getVerticalAnimSpeed());
        SLIDE_EASING.set(config.getSlideEasing());
        SCALE_ENTRANCE.set(config.isScaleEntrance());
        ENTRANCE_SCALE_START.set((double) config.getEntranceScaleStart());
        STAGGER_DELAY_MS.set(config.getStaggerDelayMs());
        LAYOUT_PRESET.set(config.getLayoutPreset() != null ? config.getLayoutPreset() : "");
        DECORATION.set(config.getDecoration() != null ? config.getDecoration() : "");
        BACKGROUND_STYLE.set(config.getBackgroundStyle().name());
        BACKGROUND_COLOR.set((long) config.getBackgroundColor() & 0xFFFFFFFFL);
        BACKGROUND_H_PADDING.set(config.getBackgroundHPadding());
        BACKGROUND_V_PADDING.set(config.getBackgroundVPadding());
        MAX_NAME_WIDTH.set(config.getMaxNameWidth());
        COUNT_COLOR.set((long) config.getCountColor() & 0xFFFFFFFFL);
        NAME_COLOR_OVERRIDE.set((long) config.getNameColorOverride() & 0xFFFFFFFFL);
        SHOW_COUNT.set(config.isShowCount());
        USE_RARITY_COLORS.set(config.isUseRarityColors());
        ICON_ON_RIGHT.set(config.isIconOnRight());
        TEXT_SHADOW.set(config.isTextShadow());
        SHOW_COUNT_RIGHT.set(config.isShowCountRight());
        ANIMATE_XP_COLOR.set(config.isAnimateXpColor());
        ABBREVIATE_COUNTS.set(config.isAbbreviateCounts());
        BANNER_ELEMENT_ORDER.set(config.getBannerElementOrder());
        DECORATIVE_EDGE_INSET.set(config.getDecorativeEdgeInset());
        ICON_TO_NAME_GAP.set(config.getIconToNameGap());
        NAME_TO_COUNT_GAP.set(config.getNameToCountGap());
        BODY_ALPHA.set((double) config.getBodyAlpha());
        BODY_TINT.set((long) config.getBodyTint() & 0xFFFFFFFFL);
        BODY_ANIM_SPEED.set(config.getBodyAnimSpeed());
        SHOW_ACCENT.set(config.isShowAccent());
        ACCENT_ALPHA.set((double) config.getAccentAlpha());
        ACCENT_TINT.set((long) config.getAccentTint() & 0xFFFFFFFFL);
        ACCENT_ANIM_SPEED.set(config.getAccentAnimSpeed());
        ACCENT_X_OFFSET.set(config.getAccentXOffset());
        ACCENT_Y_OFFSET.set(config.getAccentYOffset());
        ACCENT_ANCHOR.set(config.getAccentAnchor().name());
        EFFECT_TARGET.set(config.getEffectTarget().name());
        PICKUP_PULSE_ENABLED.set(config.isPickupPulseEnabled());
        PICKUP_PULSE_DURATION_MS.set(config.getPickupPulseDurationMs());
        PICKUP_PULSE_ICON_SCALE_STRENGTH.set((double) config.getPickupPulseIconScaleStrength());
        PICKUP_PULSE_ICON_ALPHA_STRENGTH.set((double) config.getPickupPulseIconAlphaStrength());
        PICKUP_PULSE_NAME_SCALE_STRENGTH.set((double) config.getPickupPulseNameScaleStrength());
        PICKUP_PULSE_NAME_ALPHA_STRENGTH.set((double) config.getPickupPulseNameAlphaStrength());
        PICKUP_PULSE_TOTAL_COUNT_SCALE_STRENGTH.set((double) config.getPickupPulseTotalCountScaleStrength());
        PICKUP_PULSE_TOTAL_COUNT_ALPHA_STRENGTH.set((double) config.getPickupPulseTotalCountAlphaStrength());
        PICKUP_PULSE_BODY_SCALE_STRENGTH.set((double) config.getPickupPulseBodyScaleStrength());
        PICKUP_PULSE_BODY_ALPHA_STRENGTH.set((double) config.getPickupPulseBodyAlphaStrength());
        PICKUP_PULSE_ACCENT_SCALE_STRENGTH.set((double) config.getPickupPulseAccentScaleStrength());
        PICKUP_PULSE_ACCENT_ALPHA_STRENGTH.set((double) config.getPickupPulseAccentAlphaStrength());
        PICKUP_PULSE_OVERALL_SCALE_STRENGTH.set((double) config.getPickupPulseOverallScaleStrength());
        PICKUP_PULSE_OVERALL_ALPHA_STRENGTH.set((double) config.getPickupPulseOverallAlphaStrength());
        SHOW_PROGRESS_BAR.set(config.isShowProgressBar());
        PROGRESS_BAR_COLOR.set((long) config.getProgressBarColor() & 0xFFFFFFFFL);
        PROGRESS_BAR_HEIGHT.set(config.getProgressBarHeight());
        ICON_GLOW_ENABLED.set(config.isIconGlowEnabled());
        ICON_GLOW_COLOR.set((long) config.getIconGlowColor() & 0xFFFFFFFFL);
        ICON_GLOW_RADIUS.set(config.getIconGlowRadius());
        ICON_GLOW_SHAPE.set(config.getIconGlowShape().name().toLowerCase());
        ICON_GLOW_SOFTNESS.set((double) config.getIconGlowSoftness());
        ICON_GLOW_PULSE_SPEED.set((double) config.getIconGlowPulseSpeed());
        ICON_GLOW_PULSE_MIN.set((double) config.getIconGlowPulseMin());
        ICON_GLOW_PULSE_MAX.set((double) config.getIconGlowPulseMax());
        ICON_SHADOW_ENABLED.set(config.isIconShadowEnabled());
        ICON_SHADOW_COLOR.set((long) config.getIconShadowColor() & 0xFFFFFFFFL);
        ICON_SHADOW_OFFSET_X.set(config.getIconShadowOffsetX());
        ICON_SHADOW_OFFSET_Y.set(config.getIconShadowOffsetY());
        ICON_SHADOW_RADIUS.set(config.getIconShadowRadius());
        ICON_SHADOW_SHAPE.set(config.getIconShadowShape().name().toLowerCase());
        ICON_SHADOW_SOFTNESS.set((double) config.getIconShadowSoftness());
        SOUND_ENABLED.set(config.isSoundEnabled());
        SOUND_ID.set(config.getSoundId());
        SOUND_VOLUME.set((double) config.getSoundVolume());
        SOUND_PITCH.set((double) config.getSoundPitch());
        ITEM_BLACKLIST.set(filter.getItemBlacklist());
        ITEM_WHITELIST.set(filter.getItemWhitelist());
        MOD_BLACKLIST.set(filter.getModBlacklist());
        MOD_WHITELIST.set(filter.getModWhitelist());

        SPEC.save();
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent event) {
        LootLogConfig config = LootLog.getConfig();

        // General
        config.setDisplayDurationMs(DISPLAY_DURATION_MS.get());
        config.setMaxEntries(MAX_ENTRIES.get());
        config.setCombineMode(COMBINE_MODE.get());
        config.setShowItems(SHOW_ITEMS.get());
        config.setShowXp(SHOW_XP.get());
        // Position
        config.setAnchor(ANCHOR.get());
        config.setXOffset(X_OFFSET.get());
        config.setYOffset(Y_OFFSET.get());
        config.setEntrySpacing(ENTRY_SPACING.get());
        config.setScale(SCALE.get().floatValue());
        config.setClampToScreen(CLAMP_TO_SCREEN.get());
        try { config.setGrowthDirection(GrowthDirection.valueOf(LootLogConfig.migrateGrowthDirection(GROWTH_DIRECTION.get()))); }
        catch (IllegalArgumentException e) { config.setGrowthDirection(GrowthDirection.NORMAL); }

        // Animation
        config.setFadeInMs(FADE_IN_MS.get());
        config.setFadeOutMs(FADE_OUT_MS.get());
        config.setSlideDistance(SLIDE_DISTANCE.get().floatValue());
        config.setFadeOutSlide(FADE_OUT_SLIDE.get());
        config.setVerticalAnimSpeed(VERTICAL_ANIM_SPEED.get().floatValue());
        config.setSlideEasing(SLIDE_EASING.get());
        config.setScaleEntrance(SCALE_ENTRANCE.get());
        config.setEntranceScaleStart(ENTRANCE_SCALE_START.get().floatValue());
        config.setStaggerDelayMs(STAGGER_DELAY_MS.get());

        // Layout
        String layoutPreset = LAYOUT_PRESET.get();
        config.setLayoutPreset(layoutPreset != null && !layoutPreset.isEmpty() ? layoutPreset : null);

        // Appearance
        String decoName = DECORATION.get();
        String migratedStyle = LootLogConfig.migrateStyleName(BACKGROUND_STYLE.get());
        if (decoName != null && !decoName.isEmpty()) {
            config.setDecoration(decoName);
            Decoration deco = Decoration.byName(decoName);
            if (deco != null) {
                config.setBackgroundStyle(deco.getImpliedStyle());
            } else {
                try { config.setBackgroundStyle(BackgroundStyle.valueOf(migratedStyle)); }
                catch (IllegalArgumentException e) { config.setBackgroundStyle(BackgroundStyle.BANNER); }
            }
        } else {
            try { config.setBackgroundStyle(BackgroundStyle.valueOf(migratedStyle)); }
            catch (IllegalArgumentException e) { config.setBackgroundStyle(BackgroundStyle.BANNER); }
        }
        config.setBackgroundColor((int) BACKGROUND_COLOR.get().longValue());
        config.setBackgroundHPadding(BACKGROUND_H_PADDING.get());
        config.setBackgroundVPadding(BACKGROUND_V_PADDING.get());
        config.setMaxNameWidth(MAX_NAME_WIDTH.get());
        config.setCountColor((int) COUNT_COLOR.get().longValue());
        config.setNameColorOverride((int) NAME_COLOR_OVERRIDE.get().longValue());
        config.setShowCount(SHOW_COUNT.get());
        config.setUseRarityColors(USE_RARITY_COLORS.get());
        config.setIconOnRight(ICON_ON_RIGHT.get());
        config.setTextShadow(TEXT_SHADOW.get());
        config.setShowCountRight(SHOW_COUNT_RIGHT.get());
        config.setAnimateXpColor(ANIMATE_XP_COLOR.get());
        config.setAbbreviateCounts(ABBREVIATE_COUNTS.get());

        // Banner / FLAT layout
        config.setBannerElementOrder(BANNER_ELEMENT_ORDER.get());
        config.setDecorativeEdgeInset(DECORATIVE_EDGE_INSET.get());
        config.setIconToNameGap(ICON_TO_NAME_GAP.get());
        config.setNameToCountGap(NAME_TO_COUNT_GAP.get());

        // Banner layer 0 (body)
        config.setBodyAlpha(BODY_ALPHA.get().floatValue());
        config.setBodyTint((int) BODY_TINT.get().longValue());
        config.setBodyAnimSpeed(BODY_ANIM_SPEED.get());

        // Banner layer 1 (accent)
        config.setShowAccent(SHOW_ACCENT.get());
        config.setAccentAlpha(ACCENT_ALPHA.get().floatValue());
        config.setAccentTint((int) ACCENT_TINT.get().longValue());
        config.setAccentAnimSpeed(ACCENT_ANIM_SPEED.get());
        config.setAccentXOffset(ACCENT_X_OFFSET.get());
        config.setAccentYOffset(ACCENT_Y_OFFSET.get());
        config.setAccentAnchor(ACCENT_ANCHOR.get());

        // Effect targeting
        config.setEffectTarget(EFFECT_TARGET.get());

        // Pickup Pulse
        config.setPickupPulseEnabled(PICKUP_PULSE_ENABLED.get());
        config.setPickupPulseDurationMs(PICKUP_PULSE_DURATION_MS.get());
        config.setPickupPulseIconScaleStrength(PICKUP_PULSE_ICON_SCALE_STRENGTH.get().floatValue());
        config.setPickupPulseIconAlphaStrength(PICKUP_PULSE_ICON_ALPHA_STRENGTH.get().floatValue());
        config.setPickupPulseNameScaleStrength(PICKUP_PULSE_NAME_SCALE_STRENGTH.get().floatValue());
        config.setPickupPulseNameAlphaStrength(PICKUP_PULSE_NAME_ALPHA_STRENGTH.get().floatValue());
        config.setPickupPulseTotalCountScaleStrength(PICKUP_PULSE_TOTAL_COUNT_SCALE_STRENGTH.get().floatValue());
        config.setPickupPulseTotalCountAlphaStrength(PICKUP_PULSE_TOTAL_COUNT_ALPHA_STRENGTH.get().floatValue());
        config.setPickupPulseBodyScaleStrength(PICKUP_PULSE_BODY_SCALE_STRENGTH.get().floatValue());
        config.setPickupPulseBodyAlphaStrength(PICKUP_PULSE_BODY_ALPHA_STRENGTH.get().floatValue());
        config.setPickupPulseAccentScaleStrength(PICKUP_PULSE_ACCENT_SCALE_STRENGTH.get().floatValue());
        config.setPickupPulseAccentAlphaStrength(PICKUP_PULSE_ACCENT_ALPHA_STRENGTH.get().floatValue());
        config.setPickupPulseOverallScaleStrength(PICKUP_PULSE_OVERALL_SCALE_STRENGTH.get().floatValue());
        config.setPickupPulseOverallAlphaStrength(PICKUP_PULSE_OVERALL_ALPHA_STRENGTH.get().floatValue());

        // Progress Bar
        config.setShowProgressBar(SHOW_PROGRESS_BAR.get());
        config.setProgressBarColor((int) PROGRESS_BAR_COLOR.get().longValue());
        config.setProgressBarHeight(PROGRESS_BAR_HEIGHT.get());

        // Icon Glow
        config.setIconGlowEnabled(ICON_GLOW_ENABLED.get());
        config.setIconGlowColor((int) ICON_GLOW_COLOR.get().longValue());
        config.setIconGlowRadius(ICON_GLOW_RADIUS.get());
        config.setIconGlowShape(ICON_GLOW_SHAPE.get());
        config.setIconGlowSoftness(ICON_GLOW_SOFTNESS.get().floatValue());
        config.setIconGlowPulseSpeed(ICON_GLOW_PULSE_SPEED.get().floatValue());
        config.setIconGlowPulseMin(ICON_GLOW_PULSE_MIN.get().floatValue());
        config.setIconGlowPulseMax(ICON_GLOW_PULSE_MAX.get().floatValue());

        // Icon Shadow
        config.setIconShadowEnabled(ICON_SHADOW_ENABLED.get());
        config.setIconShadowColor((int) ICON_SHADOW_COLOR.get().longValue());
        config.setIconShadowOffsetX(ICON_SHADOW_OFFSET_X.get());
        config.setIconShadowOffsetY(ICON_SHADOW_OFFSET_Y.get());
        config.setIconShadowRadius(ICON_SHADOW_RADIUS.get());
        config.setIconShadowShape(ICON_SHADOW_SHAPE.get());
        config.setIconShadowSoftness(ICON_SHADOW_SOFTNESS.get().floatValue());

        // Sound
        config.setSoundEnabled(SOUND_ENABLED.get());
        config.setSoundId(SOUND_ID.get());
        config.setSoundVolume(SOUND_VOLUME.get().floatValue());
        config.setSoundPitch(SOUND_PITCH.get().floatValue());

        // Filtering
        ItemFilter filter = LootLog.getFilter();
        filter.setItemBlacklist((List<String>) ITEM_BLACKLIST.get());
        filter.setItemWhitelist((List<String>) ITEM_WHITELIST.get());
        filter.setModBlacklist((List<String>) MOD_BLACKLIST.get());
        filter.setModWhitelist((List<String>) MOD_WHITELIST.get());
    }
}
