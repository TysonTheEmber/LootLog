package dev.tysontheember.lootlog.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.tysontheember.lootlog.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * YACL-based config screen builder. Only loaded when YACL is present at runtime.
 * Constructs a config screen that reads from / writes to {@link LootLogConfig}.
 */
public class LootLogConfigScreen {

    public static Screen create(Screen parent, Runnable saveCallback) {
        LootLogConfig cfg = LootLog.getConfig();
        LootLogConfig def = new LootLogConfig();
        ItemFilter filter = LootLog.getFilter();

        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("lootlog.config.title"))
                .category(buildGeneral(cfg, def))
                .category(buildPosition(cfg, def))
                .category(buildAnimation(cfg, def))
                .category(buildAppearance(cfg, def))
                .category(buildBanner(cfg, def))
                .category(buildEffects(cfg, def))
                .category(buildSound(cfg, def))
                .category(buildFiltering(cfg, def, filter))
                .save(saveCallback)
                .build()
                .generateScreen(parent);
    }

    // ==========================================================================
    // General
    // ==========================================================================

    private static ConfigCategory buildGeneral(LootLogConfig cfg, LootLogConfig def) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("lootlog.config.category.general"))
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("lootlog.config.displayDurationMs"))
                        .description(desc("displayDurationMs"))
                        .binding((int) def.getDisplayDurationMs(), () -> (int) cfg.getDisplayDurationMs(), v -> cfg.setDisplayDurationMs(v))
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(500, 30000).step(100))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("lootlog.config.maxEntries"))
                        .description(desc("maxEntries"))
                        .binding(def.getMaxEntries(), cfg::getMaxEntries, cfg::setMaxEntries)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 30).step(1))
                        .build())
                .option(Option.<CombineMode>createBuilder()
                        .name(Component.translatable("lootlog.config.combineMode"))
                        .description(desc("combineMode"))
                        .binding(def.getCombineMode(), cfg::getCombineMode, cfg::setCombineMode)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(CombineMode.class))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("lootlog.config.showItems"))
                        .description(desc("showItems"))
                        .binding(def.isShowItems(), cfg::isShowItems, cfg::setShowItems)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("lootlog.config.showXp"))
                        .description(desc("showXp"))
                        .binding(def.isShowXp(), cfg::isShowXp, cfg::setShowXp)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .build();
    }

    // ==========================================================================
    // Position
    // ==========================================================================

    private static ConfigCategory buildPosition(LootLogConfig cfg, LootLogConfig def) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("lootlog.config.category.position"))
                .option(Option.<HudAnchor>createBuilder()
                        .name(Component.translatable("lootlog.config.anchor"))
                        .description(desc("anchor"))
                        .binding(def.getAnchor(), cfg::getAnchor, cfg::setAnchor)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(HudAnchor.class))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("lootlog.config.xOffset"))
                        .description(desc("xOffset"))
                        .binding(def.getXOffset(), cfg::getXOffset, cfg::setXOffset)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 500).step(1))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("lootlog.config.yOffset"))
                        .description(desc("yOffset"))
                        .binding(def.getYOffset(), cfg::getYOffset, cfg::setYOffset)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 500).step(1))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("lootlog.config.entrySpacing"))
                        .description(desc("entrySpacing"))
                        .binding(def.getEntrySpacing(), cfg::getEntrySpacing, cfg::setEntrySpacing)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 20).step(1))
                        .build())
                .option(Option.<Float>createBuilder()
                        .name(Component.translatable("lootlog.config.scale"))
                        .description(desc("scale"))
                        .binding(def.getScale(), cfg::getScale, cfg::setScale)
                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.25f, 4.0f).step(0.05f))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("lootlog.config.clampToScreen"))
                        .description(desc("clampToScreen"))
                        .binding(def.isClampToScreen(), cfg::isClampToScreen, cfg::setClampToScreen)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<GrowthDirection>createBuilder()
                        .name(Component.translatable("lootlog.config.growthDirection"))
                        .description(desc("growthDirection"))
                        .binding(def.getGrowthDirection(), cfg::getGrowthDirection, cfg::setGrowthDirection)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(GrowthDirection.class))
                        .build())
                .build();
    }

    // ==========================================================================
    // Animation
    // ==========================================================================

    private static ConfigCategory buildAnimation(LootLogConfig cfg, LootLogConfig def) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("lootlog.config.category.animation"))
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("lootlog.config.fadeInMs"))
                        .description(desc("fadeInMs"))
                        .binding((int) def.getFadeInMs(), () -> (int) cfg.getFadeInMs(), v -> cfg.setFadeInMs(v))
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 2000).step(50))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("lootlog.config.fadeOutMs"))
                        .description(desc("fadeOutMs"))
                        .binding((int) def.getFadeOutMs(), () -> (int) cfg.getFadeOutMs(), v -> cfg.setFadeOutMs(v))
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 2000).step(50))
                        .build())
                .option(Option.<Float>createBuilder()
                        .name(Component.translatable("lootlog.config.slideDistance"))
                        .description(desc("slideDistance"))
                        .binding(def.getSlideDistance(), cfg::getSlideDistance, cfg::setSlideDistance)
                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 500f).step(5f))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("lootlog.config.fadeOutSlide"))
                        .description(desc("fadeOutSlide"))
                        .binding(def.isFadeOutSlide(), cfg::isFadeOutSlide, cfg::setFadeOutSlide)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Float>createBuilder()
                        .name(Component.translatable("lootlog.config.verticalAnimSpeed"))
                        .description(desc("verticalAnimSpeed"))
                        .binding(def.getVerticalAnimSpeed(), cfg::getVerticalAnimSpeed, cfg::setVerticalAnimSpeed)
                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0f, 1f).step(0.05f))
                        .build())
                .option(Option.<Easing>createBuilder()
                        .name(Component.translatable("lootlog.config.slideEasing"))
                        .description(desc("slideEasing"))
                        .binding(def.getSlideEasing(), cfg::getSlideEasing, cfg::setSlideEasing)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(Easing.class))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("lootlog.config.scaleEntrance"))
                        .description(desc("scaleEntrance"))
                        .binding(def.isScaleEntrance(), cfg::isScaleEntrance, cfg::setScaleEntrance)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Float>createBuilder()
                        .name(Component.translatable("lootlog.config.entranceScaleStart"))
                        .description(desc("entranceScaleStart"))
                        .binding(def.getEntranceScaleStart(), cfg::getEntranceScaleStart, cfg::setEntranceScaleStart)
                        .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.1f, 1.0f).step(0.05f))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("lootlog.config.staggerDelayMs"))
                        .description(desc("staggerDelayMs"))
                        .binding(def.getStaggerDelayMs(), cfg::getStaggerDelayMs, cfg::setStaggerDelayMs)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 500).step(10))
                        .build())
                .build();
    }

    // ==========================================================================
    // Appearance
    // ==========================================================================

    private static ConfigCategory buildAppearance(LootLogConfig cfg, LootLogConfig def) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("lootlog.config.category.appearance"))
                .option(Option.<BackgroundStyle>createBuilder()
                        .name(Component.translatable("lootlog.config.backgroundStyle"))
                        .description(desc("backgroundStyle"))
                        .binding(def.getBackgroundStyle(), cfg::getBackgroundStyle, cfg::setBackgroundStyle)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(BackgroundStyle.class))
                        .build())
                .option(Option.<String>createBuilder()
                        .name(Component.translatable("lootlog.config.decoration"))
                        .description(desc("decoration"))
                        .binding(def.getDecoration() != null ? def.getDecoration() : "",
                                () -> cfg.getDecoration() != null ? cfg.getDecoration() : "",
                                v -> cfg.setDecoration(v.isEmpty() ? null : v))
                        .controller(StringControllerBuilder::create)
                        .build())
                .option(Option.<String>createBuilder()
                        .name(Component.translatable("lootlog.config.layoutPreset"))
                        .description(desc("layoutPreset"))
                        .binding(def.getLayoutPreset() != null ? def.getLayoutPreset() : "",
                                () -> cfg.getLayoutPreset() != null ? cfg.getLayoutPreset() : "",
                                v -> cfg.setLayoutPreset(v.isEmpty() ? null : v))
                        .controller(StringControllerBuilder::create)
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("lootlog.config.group.colors"))
                        .description(groupDesc("colors"))
                        .option(Option.<Color>createBuilder()
                                .name(Component.translatable("lootlog.config.backgroundColor"))
                                .description(desc("backgroundColor"))
                                .binding(new Color(def.getBackgroundColor(), true),
                                        () -> new Color(cfg.getBackgroundColor(), true),
                                        v -> cfg.setBackgroundColor(v.getRGB()))
                                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Component.translatable("lootlog.config.countColor"))
                                .description(desc("countColor"))
                                .binding(new Color(def.getCountColor(), true),
                                        () -> new Color(cfg.getCountColor(), true),
                                        v -> cfg.setCountColor(v.getRGB()))
                                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Component.translatable("lootlog.config.nameColorOverride"))
                                .description(desc("nameColorOverride"))
                                .binding(new Color(def.getNameColorOverride(), true),
                                        () -> new Color(cfg.getNameColorOverride(), true),
                                        v -> cfg.setNameColorOverride(v.getRGB()))
                                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("lootlog.config.group.text"))
                        .description(groupDesc("text"))
                        .option(boolOpt("showCount", def.isShowCount(), cfg::isShowCount, cfg::setShowCount))
                        .option(boolOpt("useRarityColors", def.isUseRarityColors(), cfg::isUseRarityColors, cfg::setUseRarityColors))
                        .option(boolOpt("iconOnRight", def.isIconOnRight(), cfg::isIconOnRight, cfg::setIconOnRight))
                        .option(boolOpt("textShadow", def.isTextShadow(), cfg::isTextShadow, cfg::setTextShadow))
                        .option(boolOpt("showCountRight", def.isShowCountRight(), cfg::isShowCountRight, cfg::setShowCountRight))
                        .option(boolOpt("animateXpColor", def.isAnimateXpColor(), cfg::isAnimateXpColor, cfg::setAnimateXpColor))
                        .option(boolOpt("abbreviateCounts", def.isAbbreviateCounts(), cfg::isAbbreviateCounts, cfg::setAbbreviateCounts))
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("lootlog.config.group.sizing"))
                        .description(groupDesc("sizing"))
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.maxNameWidth"))
                                .description(desc("maxNameWidth"))
                                .binding(def.getMaxNameWidth(), cfg::getMaxNameWidth, cfg::setMaxNameWidth)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 500).step(5))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.backgroundHPadding"))
                                .description(desc("backgroundHPadding"))
                                .binding(def.getBackgroundHPadding(), cfg::getBackgroundHPadding, cfg::setBackgroundHPadding)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 20).step(1))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.backgroundVPadding"))
                                .description(desc("backgroundVPadding"))
                                .binding(def.getBackgroundVPadding(), cfg::getBackgroundVPadding, cfg::setBackgroundVPadding)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 20).step(1))
                                .build())
                        .build())
                .build();
    }

    // ==========================================================================
    // Banner Layout
    // ==========================================================================

    private static ConfigCategory buildBanner(LootLogConfig cfg, LootLogConfig def) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("lootlog.config.category.banner"))
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("lootlog.config.group.elementOrder"))
                        .description(groupDesc("elementOrder"))
                        .option(Option.<String>createBuilder()
                                .name(Component.translatable("lootlog.config.bannerElementOrder"))
                                .description(desc("bannerElementOrder"))
                                .binding(def.getBannerElementOrder(), cfg::getBannerElementOrder, cfg::setBannerElementOrder)
                                .controller(StringControllerBuilder::create)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.decorativeEdgeInset"))
                                .description(desc("decorativeEdgeInset"))
                                .binding(def.getDecorativeEdgeInset(), cfg::getDecorativeEdgeInset, cfg::setDecorativeEdgeInset)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 50).step(1))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.iconToNameGap"))
                                .description(desc("iconToNameGap"))
                                .binding(def.getIconToNameGap(), cfg::getIconToNameGap, cfg::setIconToNameGap)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 20).step(1))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.nameToCountGap"))
                                .description(desc("nameToCountGap"))
                                .binding(def.getNameToCountGap(), cfg::getNameToCountGap, cfg::setNameToCountGap)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 20).step(1))
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("lootlog.config.group.body"))
                        .description(groupDesc("body"))
                        .option(floatSlider("bodyAlpha", 0f, 1f, 0.05f, def.getBodyAlpha(), cfg::getBodyAlpha, cfg::setBodyAlpha))
                        .option(colorOpt("bodyTint", def.getBodyTint(), cfg::getBodyTint, cfg::setBodyTint))
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.bodyAnimSpeed"))
                                .description(desc("bodyAnimSpeed"))
                                .binding(def.getBodyAnimSpeed(), cfg::getBodyAnimSpeed, cfg::setBodyAnimSpeed)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 100).step(1))
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("lootlog.config.group.accent"))
                        .description(groupDesc("accent"))
                        .option(boolOpt("showAccent", def.isShowAccent(), cfg::isShowAccent, cfg::setShowAccent))
                        .option(floatSlider("accentAlpha", 0f, 1f, 0.05f, def.getAccentAlpha(), cfg::getAccentAlpha, cfg::setAccentAlpha))
                        .option(colorOpt("accentTint", def.getAccentTint(), cfg::getAccentTint, cfg::setAccentTint))
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.accentAnimSpeed"))
                                .description(desc("accentAnimSpeed"))
                                .binding(def.getAccentAnimSpeed(), cfg::getAccentAnimSpeed, cfg::setAccentAnimSpeed)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 100).step(1))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.accentXOffset"))
                                .description(desc("accentXOffset"))
                                .binding(def.getAccentXOffset(), cfg::getAccentXOffset, cfg::setAccentXOffset)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(-50, 50).step(1))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.accentYOffset"))
                                .description(desc("accentYOffset"))
                                .binding(def.getAccentYOffset(), cfg::getAccentYOffset, cfg::setAccentYOffset)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(-50, 50).step(1))
                                .build())
                        .option(Option.<AccentAnchor>createBuilder()
                                .name(Component.translatable("lootlog.config.accentAnchor"))
                                .description(desc("accentAnchor"))
                                .binding(def.getAccentAnchor(), cfg::getAccentAnchor, v -> cfg.setAccentAnchor(v))
                                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(AccentAnchor.class))
                                .build())
                        .build())
                .build();
    }

    // ==========================================================================
    // Effects
    // ==========================================================================

    private static ConfigCategory buildEffects(LootLogConfig cfg, LootLogConfig def) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("lootlog.config.category.effects"))
                .option(Option.<EffectTarget>createBuilder()
                        .name(Component.translatable("lootlog.config.effectTarget"))
                        .description(desc("effectTarget"))
                        .binding(def.getEffectTarget(), cfg::getEffectTarget, v -> cfg.setEffectTarget(v))
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(EffectTarget.class))
                        .build())
                // Pickup Pulse
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("lootlog.config.group.pickupPulse"))
                        .description(groupDesc("pickupPulse"))
                        .option(boolOpt("pickupPulseEnabled", def.isPickupPulseEnabled(), cfg::isPickupPulseEnabled, cfg::setPickupPulseEnabled))
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.pickupPulseDurationMs"))
                                .description(desc("pickupPulseDurationMs"))
                                .binding(def.getPickupPulseDurationMs(), cfg::getPickupPulseDurationMs, cfg::setPickupPulseDurationMs)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(50, 1000).step(10))
                                .build())
                        .option(floatSlider("pickupPulseOverallScaleStrength", 0f, 0.5f, 0.01f, def.getPickupPulseOverallScaleStrength(), cfg::getPickupPulseOverallScaleStrength, cfg::setPickupPulseOverallScaleStrength))
                        .option(floatSlider("pickupPulseOverallAlphaStrength", 0f, 1f, 0.01f, def.getPickupPulseOverallAlphaStrength(), cfg::getPickupPulseOverallAlphaStrength, cfg::setPickupPulseOverallAlphaStrength))
                        .build())
                // Pickup Pulse Advanced
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("lootlog.config.group.pickupPulseAdvanced"))
                        .description(groupDesc("pickupPulseAdvanced"))
                        .collapsed(true)
                        .option(floatSlider("pickupPulseIconScaleStrength", 0f, 0.5f, 0.01f, def.getPickupPulseIconScaleStrength(), cfg::getPickupPulseIconScaleStrength, cfg::setPickupPulseIconScaleStrength))
                        .option(floatSlider("pickupPulseIconAlphaStrength", 0f, 1f, 0.01f, def.getPickupPulseIconAlphaStrength(), cfg::getPickupPulseIconAlphaStrength, cfg::setPickupPulseIconAlphaStrength))
                        .option(floatSlider("pickupPulseNameScaleStrength", 0f, 0.5f, 0.01f, def.getPickupPulseNameScaleStrength(), cfg::getPickupPulseNameScaleStrength, cfg::setPickupPulseNameScaleStrength))
                        .option(floatSlider("pickupPulseNameAlphaStrength", 0f, 1f, 0.01f, def.getPickupPulseNameAlphaStrength(), cfg::getPickupPulseNameAlphaStrength, cfg::setPickupPulseNameAlphaStrength))
                        .option(floatSlider("pickupPulseTotalCountScaleStrength", 0f, 0.5f, 0.01f, def.getPickupPulseTotalCountScaleStrength(), cfg::getPickupPulseTotalCountScaleStrength, cfg::setPickupPulseTotalCountScaleStrength))
                        .option(floatSlider("pickupPulseTotalCountAlphaStrength", 0f, 1f, 0.01f, def.getPickupPulseTotalCountAlphaStrength(), cfg::getPickupPulseTotalCountAlphaStrength, cfg::setPickupPulseTotalCountAlphaStrength))
                        .option(floatSlider("pickupPulseBodyScaleStrength", 0f, 0.5f, 0.01f, def.getPickupPulseBodyScaleStrength(), cfg::getPickupPulseBodyScaleStrength, cfg::setPickupPulseBodyScaleStrength))
                        .option(floatSlider("pickupPulseBodyAlphaStrength", 0f, 1f, 0.01f, def.getPickupPulseBodyAlphaStrength(), cfg::getPickupPulseBodyAlphaStrength, cfg::setPickupPulseBodyAlphaStrength))
                        .option(floatSlider("pickupPulseAccentScaleStrength", 0f, 0.5f, 0.01f, def.getPickupPulseAccentScaleStrength(), cfg::getPickupPulseAccentScaleStrength, cfg::setPickupPulseAccentScaleStrength))
                        .option(floatSlider("pickupPulseAccentAlphaStrength", 0f, 1f, 0.01f, def.getPickupPulseAccentAlphaStrength(), cfg::getPickupPulseAccentAlphaStrength, cfg::setPickupPulseAccentAlphaStrength))
                        .build())
                // Progress Bar
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("lootlog.config.group.progressBar"))
                        .description(groupDesc("progressBar"))
                        .option(boolOpt("showProgressBar", def.isShowProgressBar(), cfg::isShowProgressBar, cfg::setShowProgressBar))
                        .option(colorOpt("progressBarColor", def.getProgressBarColor(), cfg::getProgressBarColor, cfg::setProgressBarColor))
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.progressBarHeight"))
                                .description(desc("progressBarHeight"))
                                .binding(def.getProgressBarHeight(), cfg::getProgressBarHeight, cfg::setProgressBarHeight)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 3).step(1))
                                .build())
                        .build())
                // Icon Glow
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("lootlog.config.group.iconGlow"))
                        .description(groupDesc("iconGlow"))
                        .option(boolOpt("iconGlowEnabled", def.isIconGlowEnabled(), cfg::isIconGlowEnabled, cfg::setIconGlowEnabled))
                        .option(colorOpt("iconGlowColor", def.getIconGlowColor(), cfg::getIconGlowColor, cfg::setIconGlowColor))
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.iconGlowRadius"))
                                .description(desc("iconGlowRadius"))
                                .binding(def.getIconGlowRadius(), cfg::getIconGlowRadius, cfg::setIconGlowRadius)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 8).step(1))
                                .build())
                        .option(Option.<IconShape>createBuilder()
                                .name(Component.translatable("lootlog.config.iconGlowShape"))
                                .description(desc("iconGlowShape"))
                                .binding(def.getIconGlowShape(), cfg::getIconGlowShape, v -> cfg.setIconGlowShape(v))
                                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(IconShape.class))
                                .build())
                        .option(floatSlider("iconGlowSoftness", 0.5f, 5f, 0.1f, def.getIconGlowSoftness(), cfg::getIconGlowSoftness, cfg::setIconGlowSoftness))
                        .option(floatSlider("iconGlowPulseSpeed", 0f, 10f, 0.1f, def.getIconGlowPulseSpeed(), cfg::getIconGlowPulseSpeed, cfg::setIconGlowPulseSpeed))
                        .option(floatSlider("iconGlowPulseMin", 0f, 1f, 0.05f, def.getIconGlowPulseMin(), cfg::getIconGlowPulseMin, cfg::setIconGlowPulseMin))
                        .option(floatSlider("iconGlowPulseMax", 0f, 1f, 0.05f, def.getIconGlowPulseMax(), cfg::getIconGlowPulseMax, cfg::setIconGlowPulseMax))
                        .build())
                // Icon Shadow
                .group(OptionGroup.createBuilder()
                        .name(Component.translatable("lootlog.config.group.iconShadow"))
                        .description(groupDesc("iconShadow"))
                        .option(boolOpt("iconShadowEnabled", def.isIconShadowEnabled(), cfg::isIconShadowEnabled, cfg::setIconShadowEnabled))
                        .option(colorOpt("iconShadowColor", def.getIconShadowColor(), cfg::getIconShadowColor, cfg::setIconShadowColor))
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.iconShadowOffsetX"))
                                .description(desc("iconShadowOffsetX"))
                                .binding(def.getIconShadowOffsetX(), cfg::getIconShadowOffsetX, cfg::setIconShadowOffsetX)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 4).step(1))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.iconShadowOffsetY"))
                                .description(desc("iconShadowOffsetY"))
                                .binding(def.getIconShadowOffsetY(), cfg::getIconShadowOffsetY, cfg::setIconShadowOffsetY)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 4).step(1))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("lootlog.config.iconShadowRadius"))
                                .description(desc("iconShadowRadius"))
                                .binding(def.getIconShadowRadius(), cfg::getIconShadowRadius, cfg::setIconShadowRadius)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 4).step(1))
                                .build())
                        .option(Option.<IconShape>createBuilder()
                                .name(Component.translatable("lootlog.config.iconShadowShape"))
                                .description(desc("iconShadowShape"))
                                .binding(def.getIconShadowShape(), cfg::getIconShadowShape, v -> cfg.setIconShadowShape(v))
                                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(IconShape.class))
                                .build())
                        .option(floatSlider("iconShadowSoftness", 0.5f, 5f, 0.1f, def.getIconShadowSoftness(), cfg::getIconShadowSoftness, cfg::setIconShadowSoftness))
                        .build())
                .build();
    }

    // ==========================================================================
    // Sound
    // ==========================================================================

    private static ConfigCategory buildSound(LootLogConfig cfg, LootLogConfig def) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("lootlog.config.category.sound"))
                .option(boolOpt("soundEnabled", def.isSoundEnabled(), cfg::isSoundEnabled, cfg::setSoundEnabled))
                .option(Option.<String>createBuilder()
                        .name(Component.translatable("lootlog.config.soundId"))
                        .description(desc("soundId"))
                        .binding(def.getSoundId(), cfg::getSoundId, cfg::setSoundId)
                        .controller(StringControllerBuilder::create)
                        .build())
                .option(floatSlider("soundVolume", 0f, 1f, 0.05f, def.getSoundVolume(), cfg::getSoundVolume, cfg::setSoundVolume))
                .option(floatSlider("soundPitch", 0.5f, 2f, 0.05f, def.getSoundPitch(), cfg::getSoundPitch, cfg::setSoundPitch))
                .build();
    }

    // ==========================================================================
    // Filtering
    // ==========================================================================

    private static ConfigCategory buildFiltering(LootLogConfig cfg, LootLogConfig def, ItemFilter filter) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("lootlog.config.category.filtering"))
                .group(ListOption.<String>createBuilder()
                        .name(Component.translatable("lootlog.config.itemBlacklist"))
                        .description(desc("itemBlacklist"))
                        .binding(new ArrayList<>(),
                                () -> new ArrayList<>(filter.getItemBlacklist()),
                                filter::setItemBlacklist)
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .build())
                .group(ListOption.<String>createBuilder()
                        .name(Component.translatable("lootlog.config.itemWhitelist"))
                        .description(desc("itemWhitelist"))
                        .binding(new ArrayList<>(),
                                () -> new ArrayList<>(filter.getItemWhitelist()),
                                filter::setItemWhitelist)
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .build())
                .group(ListOption.<String>createBuilder()
                        .name(Component.translatable("lootlog.config.modBlacklist"))
                        .description(desc("modBlacklist"))
                        .binding(new ArrayList<>(),
                                () -> new ArrayList<>(filter.getModBlacklist()),
                                filter::setModBlacklist)
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .build())
                .group(ListOption.<String>createBuilder()
                        .name(Component.translatable("lootlog.config.modWhitelist"))
                        .description(desc("modWhitelist"))
                        .binding(new ArrayList<>(),
                                () -> new ArrayList<>(filter.getModWhitelist()),
                                filter::setModWhitelist)
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .build())
                .build();
    }

    // ==========================================================================
    // Helper factories
    // ==========================================================================

    private static Option<Boolean> boolOpt(String key, boolean def,
                                           java.util.function.Supplier<Boolean> getter,
                                           java.util.function.Consumer<Boolean> setter) {
        return Option.<Boolean>createBuilder()
                .name(Component.translatable("lootlog.config." + key))
                .description(desc(key))
                .binding(def, getter, setter)
                .controller(TickBoxControllerBuilder::create)
                .build();
    }

    private static Option<Float> floatSlider(String key, float min, float max, float step,
                                             float def,
                                             java.util.function.Supplier<Float> getter,
                                             java.util.function.Consumer<Float> setter) {
        return Option.<Float>createBuilder()
                .name(Component.translatable("lootlog.config." + key))
                .description(desc(key))
                .binding(def, getter, setter)
                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(min, max).step(step))
                .build();
    }

    private static Option<Color> colorOpt(String key, int def,
                                           java.util.function.Supplier<Integer> getter,
                                           java.util.function.Consumer<Integer> setter) {
        return Option.<Color>createBuilder()
                .name(Component.translatable("lootlog.config." + key))
                .description(desc(key))
                .binding(new Color(def, true), () -> new Color(getter.get(), true), v -> setter.accept(v.getRGB()))
                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                .build();
    }

    private static OptionDescription desc(String key) {
        OptionDescription.Builder builder = OptionDescription.createBuilder()
                .text(Component.translatable("lootlog.config." + key + ".desc"));
        // Add detail lines (.detail, .detail2, .detail3, ...)
        String firstDetail = "lootlog.config." + key + ".detail";
        Component firstDetailComp = Component.translatable(firstDetail);
        if (!firstDetailComp.getString().equals(firstDetail)) {
            builder.text(Component.translatable(firstDetail).withStyle(ChatFormatting.GRAY));
            for (int i = 2; i <= 8; i++) {
                String nthKey = "lootlog.config." + key + ".detail" + i;
                Component nth = Component.translatable(nthKey);
                if (nth.getString().equals(nthKey)) break;
                builder.text(Component.translatable(nthKey).withStyle(ChatFormatting.GRAY));
            }
        }
        // Add range line
        String rangeKey = "lootlog.config." + key + ".range";
        Component range = Component.translatable(rangeKey);
        if (!range.getString().equals(rangeKey)) {
            builder.text(Component.translatable(rangeKey).withStyle(ChatFormatting.DARK_GRAY));
        }
        return builder.build();
    }

    private static OptionDescription groupDesc(String groupKey) {
        String descKey = "lootlog.config.group." + groupKey + ".desc";
        Component descComp = Component.translatable(descKey);
        if (descComp.getString().equals(descKey)) {
            return OptionDescription.EMPTY;
        }
        return OptionDescription.of(Component.translatable(descKey));
    }
}
