package dev.tysontheember.lootlog.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.tysontheember.lootlog.*;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * JSON-based config for Fabric. Reads/writes config/lootlog.json.
 */
public class FabricConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(LootLog.MOD_ID);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // General
    private long displayDurationMs = 5000;
    private int maxEntries = 10;
    private String combineMode = "ALWAYS";
    private boolean showItems = true;
    private boolean showXp = true;
    // Position
    private String anchor = "BOTTOM_RIGHT";
    private int xOffset = 5;
    private int yOffset = 5;
    private int entrySpacing = 2;
    private float scale = 1.0f;
    private boolean clampToScreen = true;
    private String growthDirection = "NORMAL";

    // Animation
    private long fadeInMs = 300;
    private long fadeOutMs = 500;
    private float slideDistance = 220.0f;
    private boolean fadeOutSlide = true;
    private float verticalAnimSpeed = 0.3f;
    private String slideEasing = "QUAD_OUT";
    private boolean scaleEntrance = false;
    private float entranceScaleStart = 0.8f;
    private int staggerDelayMs = 0;

    // Layout
    private String layoutPreset = null;

    // Appearance
    private String decoration = null;
    private String backgroundStyle = "BANNER";
    private long backgroundColor = 0xAA000000L;
    private int backgroundHPadding = 4;
    private int backgroundVPadding = 2;
    private int maxNameWidth = 150;
    private long countColor = 0xFFAAAAAAL;
    private long nameColorOverride = 0xFFFFFFFFL;
    private boolean showCount = true;
    private boolean useRarityColors = true;
    private boolean iconOnRight = true;
    private boolean textShadow = true;
    private boolean showCountRight = true;
    private boolean animateXpColor = true;
    private boolean abbreviateCounts = true;

    // Banner / FLAT layout
    private String bannerElementOrder = "PICKUP_COUNT,NAME,ICON,TOTAL_COUNT";
    private int decorativeEdgeInset = 6;
    private int iconToNameGap = 4;
    private int nameToCountGap = 4;

    // Banner layer 0 (body)
    private float bodyAlpha = 1.0f;
    private long bodyTint = 0xFFFFFFFFL;
    private int bodyAnimSpeed = 4;

    // Banner layer 1 (accent)
    private boolean showAccent = true;
    private float accentAlpha = 1.0f;
    private long accentTint = 0xFFFFFFFFL;
    private int accentAnimSpeed = 4;
    private int accentXOffset = 0;
    private int accentYOffset = 0;
    private String accentAnchor = "ICON";

    // Effect targeting
    private String effectTarget = "ALL";

    // Filtering
    private List<String> itemBlacklist = Collections.emptyList();
    private List<String> itemWhitelist = Collections.emptyList();
    private List<String> modBlacklist = Collections.emptyList();
    private List<String> modWhitelist = Collections.emptyList();

    // Pickup Pulse
    private boolean pickupPulseEnabled = true;
    private int pickupPulseDurationMs = 200;
    private float pickupPulseIconScaleStrength = 0.05f;
    private float pickupPulseIconAlphaStrength = 0.05f;
    private float pickupPulseNameScaleStrength = 0.05f;
    private float pickupPulseNameAlphaStrength = 0.0f;
    private float pickupPulseTotalCountScaleStrength = 0.05f;
    private float pickupPulseTotalCountAlphaStrength = 0.0f;
    private float pickupPulseBodyScaleStrength = 0.0f;
    private float pickupPulseBodyAlphaStrength = 0.0f;
    private float pickupPulseAccentScaleStrength = 0.0f;
    private float pickupPulseAccentAlphaStrength = 0.0f;
    private float pickupPulseOverallScaleStrength = 0.05f;
    private float pickupPulseOverallAlphaStrength = 0.0f;

    // Progress Bar
    private boolean showProgressBar = false;
    private long progressBarColor = 0x80FFFFFFL;
    private int progressBarHeight = 1;

    // Icon Glow
    private boolean iconGlowEnabled = false;
    private long iconGlowColor = 0xAAFFFFFFL;
    private int iconGlowRadius = 3;
    private String iconGlowShape = "circle";
    private float iconGlowSoftness = 1.5f;
    private float iconGlowPulseSpeed = 0.0f;
    private float iconGlowPulseMin = 0.5f;
    private float iconGlowPulseMax = 1.0f;

    // Icon Shadow
    private boolean iconShadowEnabled = false;
    private long iconShadowColor = 0x80000000L;
    private int iconShadowOffsetX = 1;
    private int iconShadowOffsetY = 1;
    private int iconShadowRadius = 1;
    private String iconShadowShape = "item";
    private float iconShadowSoftness = 1.5f;

    // Sound
    private boolean soundEnabled = false;
    private String soundId = "minecraft:entity.item.pickup";
    private float soundVolume = 0.5f;
    private float soundPitch = 1.0f;

    public static void load() {
        Path configPath = getConfigPath();
        FabricConfig fileConfig;

        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                fileConfig = GSON.fromJson(reader, FabricConfig.class);
            } catch (IOException e) {
                LOGGER.error("Failed to read config, using defaults", e);
                fileConfig = new FabricConfig();
            }
        } else {
            fileConfig = new FabricConfig();
            save(fileConfig);
        }

        fileConfig.applyTo(LootLog.getConfig());
    }

    public static void saveFromPojo() {
        LootLogConfig config = LootLog.getConfig();
        ItemFilter filter = LootLog.getFilter();
        FabricConfig fc = new FabricConfig();

        fc.displayDurationMs = config.getDisplayDurationMs();
        fc.maxEntries = config.getMaxEntries();
        fc.combineMode = config.getCombineMode().name();
        fc.showItems = config.isShowItems();
        fc.showXp = config.isShowXp();
        fc.anchor = config.getAnchor().name();
        fc.xOffset = config.getXOffset();
        fc.yOffset = config.getYOffset();
        fc.entrySpacing = config.getEntrySpacing();
        fc.scale = config.getScale();
        fc.clampToScreen = config.isClampToScreen();
        fc.growthDirection = config.getGrowthDirection().name();
        fc.fadeInMs = config.getFadeInMs();
        fc.fadeOutMs = config.getFadeOutMs();
        fc.slideDistance = config.getSlideDistance();
        fc.fadeOutSlide = config.isFadeOutSlide();
        fc.verticalAnimSpeed = config.getVerticalAnimSpeed();
        fc.slideEasing = config.getSlideEasing().name();
        fc.scaleEntrance = config.isScaleEntrance();
        fc.entranceScaleStart = config.getEntranceScaleStart();
        fc.staggerDelayMs = config.getStaggerDelayMs();
        fc.layoutPreset = config.getLayoutPreset();
        fc.decoration = config.getDecoration();
        fc.backgroundStyle = config.getBackgroundStyle().name();
        fc.backgroundColor = (long) config.getBackgroundColor() & 0xFFFFFFFFL;
        fc.backgroundHPadding = config.getBackgroundHPadding();
        fc.backgroundVPadding = config.getBackgroundVPadding();
        fc.maxNameWidth = config.getMaxNameWidth();
        fc.countColor = (long) config.getCountColor() & 0xFFFFFFFFL;
        fc.nameColorOverride = (long) config.getNameColorOverride() & 0xFFFFFFFFL;
        fc.showCount = config.isShowCount();
        fc.useRarityColors = config.isUseRarityColors();
        fc.iconOnRight = config.isIconOnRight();
        fc.textShadow = config.isTextShadow();
        fc.showCountRight = config.isShowCountRight();
        fc.animateXpColor = config.isAnimateXpColor();
        fc.abbreviateCounts = config.isAbbreviateCounts();
        fc.bannerElementOrder = config.getBannerElementOrder();
        fc.decorativeEdgeInset = config.getDecorativeEdgeInset();
        fc.iconToNameGap = config.getIconToNameGap();
        fc.nameToCountGap = config.getNameToCountGap();
        fc.bodyAlpha = config.getBodyAlpha();
        fc.bodyTint = (long) config.getBodyTint() & 0xFFFFFFFFL;
        fc.bodyAnimSpeed = config.getBodyAnimSpeed();
        fc.showAccent = config.isShowAccent();
        fc.accentAlpha = config.getAccentAlpha();
        fc.accentTint = (long) config.getAccentTint() & 0xFFFFFFFFL;
        fc.accentAnimSpeed = config.getAccentAnimSpeed();
        fc.accentXOffset = config.getAccentXOffset();
        fc.accentYOffset = config.getAccentYOffset();
        fc.accentAnchor = config.getAccentAnchor().name();
        fc.effectTarget = config.getEffectTarget().name();
        fc.pickupPulseEnabled = config.isPickupPulseEnabled();
        fc.pickupPulseDurationMs = config.getPickupPulseDurationMs();
        fc.pickupPulseIconScaleStrength = config.getPickupPulseIconScaleStrength();
        fc.pickupPulseIconAlphaStrength = config.getPickupPulseIconAlphaStrength();
        fc.pickupPulseNameScaleStrength = config.getPickupPulseNameScaleStrength();
        fc.pickupPulseNameAlphaStrength = config.getPickupPulseNameAlphaStrength();
        fc.pickupPulseTotalCountScaleStrength = config.getPickupPulseTotalCountScaleStrength();
        fc.pickupPulseTotalCountAlphaStrength = config.getPickupPulseTotalCountAlphaStrength();
        fc.pickupPulseBodyScaleStrength = config.getPickupPulseBodyScaleStrength();
        fc.pickupPulseBodyAlphaStrength = config.getPickupPulseBodyAlphaStrength();
        fc.pickupPulseAccentScaleStrength = config.getPickupPulseAccentScaleStrength();
        fc.pickupPulseAccentAlphaStrength = config.getPickupPulseAccentAlphaStrength();
        fc.pickupPulseOverallScaleStrength = config.getPickupPulseOverallScaleStrength();
        fc.pickupPulseOverallAlphaStrength = config.getPickupPulseOverallAlphaStrength();
        fc.showProgressBar = config.isShowProgressBar();
        fc.progressBarColor = (long) config.getProgressBarColor() & 0xFFFFFFFFL;
        fc.progressBarHeight = config.getProgressBarHeight();
        fc.iconGlowEnabled = config.isIconGlowEnabled();
        fc.iconGlowColor = (long) config.getIconGlowColor() & 0xFFFFFFFFL;
        fc.iconGlowRadius = config.getIconGlowRadius();
        fc.iconGlowShape = config.getIconGlowShape().name().toLowerCase();
        fc.iconGlowSoftness = config.getIconGlowSoftness();
        fc.iconGlowPulseSpeed = config.getIconGlowPulseSpeed();
        fc.iconGlowPulseMin = config.getIconGlowPulseMin();
        fc.iconGlowPulseMax = config.getIconGlowPulseMax();
        fc.iconShadowEnabled = config.isIconShadowEnabled();
        fc.iconShadowColor = (long) config.getIconShadowColor() & 0xFFFFFFFFL;
        fc.iconShadowOffsetX = config.getIconShadowOffsetX();
        fc.iconShadowOffsetY = config.getIconShadowOffsetY();
        fc.iconShadowRadius = config.getIconShadowRadius();
        fc.iconShadowShape = config.getIconShadowShape().name().toLowerCase();
        fc.iconShadowSoftness = config.getIconShadowSoftness();
        fc.soundEnabled = config.isSoundEnabled();
        fc.soundId = config.getSoundId();
        fc.soundVolume = config.getSoundVolume();
        fc.soundPitch = config.getSoundPitch();
        fc.itemBlacklist = filter.getItemBlacklist();
        fc.itemWhitelist = filter.getItemWhitelist();
        fc.modBlacklist = filter.getModBlacklist();
        fc.modWhitelist = filter.getModWhitelist();

        save(fc);
    }

    private static void save(FabricConfig config) {
        Path configPath = getConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to write config", e);
        }
    }

    private static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("lootlog.json");
    }

    private void applyTo(LootLogConfig config) {
        // General
        config.setDisplayDurationMs(displayDurationMs);
        config.setMaxEntries(maxEntries);
        try { config.setCombineMode(CombineMode.valueOf(combineMode)); }
        catch (IllegalArgumentException e) { config.setCombineMode(CombineMode.ALWAYS); }
        config.setShowItems(showItems);
        config.setShowXp(showXp);
        // Position
        try { config.setAnchor(HudAnchor.valueOf(anchor)); }
        catch (IllegalArgumentException e) { config.setAnchor(HudAnchor.BOTTOM_RIGHT); }
        config.setXOffset(xOffset);
        config.setYOffset(yOffset);
        config.setEntrySpacing(entrySpacing);
        config.setScale(scale);
        config.setClampToScreen(clampToScreen);
        try { config.setGrowthDirection(GrowthDirection.valueOf(LootLogConfig.migrateGrowthDirection(growthDirection))); }
        catch (IllegalArgumentException e) { config.setGrowthDirection(GrowthDirection.NORMAL); }

        // Animation
        config.setFadeInMs(fadeInMs);
        config.setFadeOutMs(fadeOutMs);
        config.setSlideDistance(slideDistance);
        config.setFadeOutSlide(fadeOutSlide);
        config.setVerticalAnimSpeed(verticalAnimSpeed);
        config.setSlideEasingByName(slideEasing != null ? slideEasing : "QUAD_OUT");
        config.setScaleEntrance(scaleEntrance);
        config.setEntranceScaleStart(entranceScaleStart);
        config.setStaggerDelayMs(staggerDelayMs);

        // Layout
        config.setLayoutPreset(layoutPreset);

        // Appearance
        config.setDecoration(decoration);
        if (decoration != null) {
            Decoration deco = Decoration.byName(decoration);
            if (deco != null) {
                config.setBackgroundStyle(deco.getImpliedStyle());
            } else {
                try { config.setBackgroundStyle(BackgroundStyle.valueOf(LootLogConfig.migrateStyleName(backgroundStyle))); }
                catch (IllegalArgumentException e) { config.setBackgroundStyle(BackgroundStyle.BANNER); }
            }
        } else {
            try { config.setBackgroundStyle(BackgroundStyle.valueOf(LootLogConfig.migrateStyleName(backgroundStyle))); }
            catch (IllegalArgumentException e) { config.setBackgroundStyle(BackgroundStyle.BANNER); }
        }
        config.setBackgroundColor((int) backgroundColor);
        config.setBackgroundHPadding(backgroundHPadding);
        config.setBackgroundVPadding(backgroundVPadding);
        config.setMaxNameWidth(maxNameWidth);
        config.setCountColor((int) countColor);
        config.setNameColorOverride((int) nameColorOverride);
        config.setShowCount(showCount);
        config.setUseRarityColors(useRarityColors);
        config.setIconOnRight(iconOnRight);
        config.setTextShadow(textShadow);
        config.setShowCountRight(showCountRight);
        config.setAnimateXpColor(animateXpColor);
        config.setAbbreviateCounts(abbreviateCounts);

        // Banner / FLAT layout
        config.setBannerElementOrder(bannerElementOrder);
        config.setDecorativeEdgeInset(decorativeEdgeInset);
        config.setIconToNameGap(iconToNameGap);
        config.setNameToCountGap(nameToCountGap);

        // Banner layer 0 (body)
        config.setBodyAlpha(bodyAlpha);
        config.setBodyTint((int) bodyTint);
        config.setBodyAnimSpeed(bodyAnimSpeed);

        // Banner layer 1 (accent)
        config.setShowAccent(showAccent);
        config.setAccentAlpha(accentAlpha);
        config.setAccentTint((int) accentTint);
        config.setAccentAnimSpeed(accentAnimSpeed);
        config.setAccentXOffset(accentXOffset);
        config.setAccentYOffset(accentYOffset);
        config.setAccentAnchor(accentAnchor);

        // Effect targeting
        config.setEffectTarget(effectTarget);

        // Pickup Pulse
        config.setPickupPulseEnabled(pickupPulseEnabled);
        config.setPickupPulseDurationMs(pickupPulseDurationMs);
        config.setPickupPulseIconScaleStrength(pickupPulseIconScaleStrength);
        config.setPickupPulseIconAlphaStrength(pickupPulseIconAlphaStrength);
        config.setPickupPulseNameScaleStrength(pickupPulseNameScaleStrength);
        config.setPickupPulseNameAlphaStrength(pickupPulseNameAlphaStrength);
        config.setPickupPulseTotalCountScaleStrength(pickupPulseTotalCountScaleStrength);
        config.setPickupPulseTotalCountAlphaStrength(pickupPulseTotalCountAlphaStrength);
        config.setPickupPulseBodyScaleStrength(pickupPulseBodyScaleStrength);
        config.setPickupPulseBodyAlphaStrength(pickupPulseBodyAlphaStrength);
        config.setPickupPulseAccentScaleStrength(pickupPulseAccentScaleStrength);
        config.setPickupPulseAccentAlphaStrength(pickupPulseAccentAlphaStrength);
        config.setPickupPulseOverallScaleStrength(pickupPulseOverallScaleStrength);
        config.setPickupPulseOverallAlphaStrength(pickupPulseOverallAlphaStrength);

        // Progress Bar
        config.setShowProgressBar(showProgressBar);
        config.setProgressBarColor((int) progressBarColor);
        config.setProgressBarHeight(progressBarHeight);

        // Icon Glow
        config.setIconGlowEnabled(iconGlowEnabled);
        config.setIconGlowColor((int) iconGlowColor);
        config.setIconGlowRadius(iconGlowRadius);
        config.setIconGlowShape(iconGlowShape);
        config.setIconGlowSoftness(iconGlowSoftness);
        config.setIconGlowPulseSpeed(iconGlowPulseSpeed);
        config.setIconGlowPulseMin(iconGlowPulseMin);
        config.setIconGlowPulseMax(iconGlowPulseMax);

        // Icon Shadow
        config.setIconShadowEnabled(iconShadowEnabled);
        config.setIconShadowColor((int) iconShadowColor);
        config.setIconShadowOffsetX(iconShadowOffsetX);
        config.setIconShadowOffsetY(iconShadowOffsetY);
        config.setIconShadowRadius(iconShadowRadius);
        config.setIconShadowShape(iconShadowShape);
        config.setIconShadowSoftness(iconShadowSoftness);

        // Sound
        config.setSoundEnabled(soundEnabled);
        config.setSoundId(soundId);
        config.setSoundVolume(soundVolume);
        config.setSoundPitch(soundPitch);

        // Filtering
        ItemFilter filter = LootLog.getFilter();
        filter.setItemBlacklist(itemBlacklist != null ? itemBlacklist : Collections.emptyList());
        filter.setItemWhitelist(itemWhitelist != null ? itemWhitelist : Collections.emptyList());
        filter.setModBlacklist(modBlacklist != null ? modBlacklist : Collections.emptyList());
        filter.setModWhitelist(modWhitelist != null ? modWhitelist : Collections.emptyList());
    }
}
