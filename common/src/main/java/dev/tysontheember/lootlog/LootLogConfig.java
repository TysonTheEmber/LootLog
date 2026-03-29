package dev.tysontheember.lootlog;

/**
 * Configuration POJO with sensible defaults and clamped validation.
 * Platform-specific config systems (ForgeConfigSpec, JSON) read/write
 * to an instance of this class.
 */
public class LootLogConfig {

    // --- General ---
    private long displayDurationMs = 5000;
    private int maxEntries = 10;
    private CombineMode combineMode = CombineMode.ALWAYS;
    private boolean showItems = true;
    private boolean showXp = true;

    // --- Position ---
    private HudAnchor anchor = HudAnchor.BOTTOM_RIGHT;
    private int xOffset = 5;
    private int yOffset = 5;
    private int entrySpacing = 2;
    private float scale = 1.0f;
    private boolean clampToScreen = true;
    private GrowthDirection growthDirection = GrowthDirection.NORMAL;

    // --- Layout ---
    private String layoutPreset = null; // null = auto-detect from backgroundStyle

    // --- Animation ---
    private long fadeInMs = 300;
    private long fadeOutMs = 500;
    private float slideDistance = 220.0f;
    private boolean fadeOutSlide = true;
    private float verticalAnimSpeed = 0.3f;
    private Easing slideEasing = Easing.QUAD_OUT;
    private boolean scaleEntrance = false;
    private float entranceScaleStart = 0.8f;
    private int staggerDelayMs = 0;

    // --- Appearance ---
    private String decoration = null;
    private BackgroundStyle backgroundStyle = BackgroundStyle.BANNER;
    private int backgroundColor = 0xAA000000;
    private int backgroundHPadding = 4;
    private int backgroundVPadding = 2;
    private int maxNameWidth = 150;
    private int countColor = 0xFFAAAAAA;
    private int nameColorOverride = 0xFFFFFFFF;
    private boolean showCount = true;
    private boolean useRarityColors = true;
    private boolean iconOnRight = true;
    private boolean textShadow = true;
    private boolean showCountRight = true;
    private boolean animateXpColor = true;
    private boolean abbreviateCounts = true;

    // --- Banner / FLAT layout ---
    private String bannerElementOrder = "PICKUP_COUNT,NAME,ICON,TOTAL_COUNT";
    private int decorativeEdgeInset = 6;
    private int iconToNameGap = 4;
    private int nameToCountGap = 4;

    // --- Banner layer 0 (body) ---
    private float bodyAlpha = 1.0f;
    private int bodyTint = 0xFFFFFFFF;
    private int bodyAnimSpeed = 4;

    // --- Banner layer 1 (accent) ---
    private boolean showAccent = true;
    private float accentAlpha = 1.0f;
    private int accentTint = 0xFFFFFFFF;
    private int accentAnimSpeed = 4;
    private int accentXOffset = 0;
    private int accentYOffset = 0;
    private AccentAnchor accentAnchor = AccentAnchor.ICON;

    // --- Effect targeting ---
    private EffectTarget effectTarget = EffectTarget.ALL;

    // --- Pickup Pulse ---
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

    // --- Progress Bar ---
    private boolean showProgressBar = false;
    private int progressBarColor = 0x80FFFFFF;
    private int progressBarHeight = 1;

    // --- Icon Glow ---
    private boolean iconGlowEnabled = false;
    private int iconGlowColor = 0xAAFFFFFF;
    private int iconGlowRadius = 3;
    private IconShape iconGlowShape = IconShape.CIRCLE;
    private float iconGlowSoftness = 1.5f;
    private float iconGlowPulseSpeed = 0.0f;
    private float iconGlowPulseMin = 0.5f;
    private float iconGlowPulseMax = 1.0f;

    // --- Icon Shadow ---
    private boolean iconShadowEnabled = false;
    private int iconShadowColor = 0x80000000;
    private int iconShadowOffsetX = 1;
    private int iconShadowOffsetY = 1;
    private int iconShadowRadius = 1;
    private IconShape iconShadowShape = IconShape.ITEM;
    private float iconShadowSoftness = 1.5f;

    // --- Sound ---
    private boolean soundEnabled = false;
    private String soundId = "minecraft:entity.item.pickup";
    private float soundVolume = 0.5f;
    private float soundPitch = 1.0f;

    // --- Filtering ---
    private java.util.List<String> itemBlacklist = java.util.Collections.emptyList();
    private java.util.List<String> itemWhitelist = java.util.Collections.emptyList();
    private java.util.List<String> modBlacklist = java.util.Collections.emptyList();
    private java.util.List<String> modWhitelist = java.util.Collections.emptyList();

    // =====================================================================
    // General
    // =====================================================================

    public long getDisplayDurationMs() { return displayDurationMs; }
    public void setDisplayDurationMs(long v) { this.displayDurationMs = clamp(v, 500, 30000); }

    public int getMaxEntries() { return maxEntries; }
    public void setMaxEntries(int v) { this.maxEntries = clamp(v, 1, 30); }

    public CombineMode getCombineMode() { return combineMode; }
    public void setCombineMode(CombineMode v) { this.combineMode = v; }

    public boolean isShowItems() { return showItems; }
    public void setShowItems(boolean v) { this.showItems = v; }

    public boolean isShowXp() { return showXp; }
    public void setShowXp(boolean v) { this.showXp = v; }

    // =====================================================================
    // Position
    // =====================================================================

    public HudAnchor getAnchor() { return anchor; }
    public void setAnchor(HudAnchor v) { this.anchor = v; }

    public int getXOffset() { return xOffset; }
    public void setXOffset(int v) { this.xOffset = clamp(v, 0, 500); }

    public int getYOffset() { return yOffset; }
    public void setYOffset(int v) { this.yOffset = clamp(v, 0, 500); }

    public int getEntrySpacing() { return entrySpacing; }
    public void setEntrySpacing(int v) { this.entrySpacing = clamp(v, 0, 20); }

    public float getScale() { return scale; }
    public void setScale(float v) { this.scale = clamp(v, 0.25f, 4.0f); }

    public boolean isClampToScreen() { return clampToScreen; }
    public void setClampToScreen(boolean v) { this.clampToScreen = v; }

    public GrowthDirection getGrowthDirection() { return growthDirection; }
    public void setGrowthDirection(GrowthDirection v) { this.growthDirection = v; }

    // =====================================================================
    // Layout
    // =====================================================================

    public String getLayoutPreset() { return layoutPreset; }
    public void setLayoutPreset(String v) { this.layoutPreset = v; }

    // =====================================================================
    // Animation
    // =====================================================================

    public long getFadeInMs() { return fadeInMs; }
    public void setFadeInMs(long v) { this.fadeInMs = clamp(v, 0, 2000); }

    public long getFadeOutMs() { return fadeOutMs; }
    public void setFadeOutMs(long v) { this.fadeOutMs = clamp(v, 0, 2000); }

    public float getSlideDistance() { return slideDistance; }
    public void setSlideDistance(float v) { this.slideDistance = clamp(v, 0f, 500f); }

    public boolean isFadeOutSlide() { return fadeOutSlide; }
    public void setFadeOutSlide(boolean v) { this.fadeOutSlide = v; }

    public float getVerticalAnimSpeed() { return verticalAnimSpeed; }
    public void setVerticalAnimSpeed(float v) { this.verticalAnimSpeed = clamp(v, 0f, 1f); }

    public Easing getSlideEasing() { return slideEasing; }
    public void setSlideEasing(Easing v) { this.slideEasing = v != null ? v : Easing.QUAD_OUT; }
    public void setSlideEasingByName(String name) { this.slideEasing = Easing.byName(name); }

    public boolean isScaleEntrance() { return scaleEntrance; }
    public void setScaleEntrance(boolean v) { this.scaleEntrance = v; }

    public float getEntranceScaleStart() { return entranceScaleStart; }
    public void setEntranceScaleStart(float v) { this.entranceScaleStart = clamp(v, 0.1f, 1.0f); }

    public int getStaggerDelayMs() { return staggerDelayMs; }
    public void setStaggerDelayMs(int v) { this.staggerDelayMs = clamp(v, 0, 500); }

    // =====================================================================
    // Appearance
    // =====================================================================

    public String getDecoration() { return decoration; }
    public void setDecoration(String v) { this.decoration = v; }

    /** Returns the texture path from the resolved global decoration, or null if none set. */
    public String getDecorationTexture() {
        if (decoration == null) return null;
        Decoration deco = Decoration.byName(decoration);
        return deco != null ? deco.getTexture() : null;
    }

    public BackgroundStyle getBackgroundStyle() { return backgroundStyle; }
    public void setBackgroundStyle(BackgroundStyle v) { this.backgroundStyle = v; }

    public boolean isBackgroundEnabled() { return backgroundStyle != BackgroundStyle.NONE; }

    public int getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(int v) { this.backgroundColor = v; }

    public int getBackgroundHPadding() { return backgroundHPadding; }
    public void setBackgroundHPadding(int v) { this.backgroundHPadding = clamp(v, 0, 20); }

    public int getBackgroundVPadding() { return backgroundVPadding; }
    public void setBackgroundVPadding(int v) { this.backgroundVPadding = clamp(v, 0, 20); }

    public int getMaxNameWidth() { return maxNameWidth; }
    public void setMaxNameWidth(int v) { this.maxNameWidth = clamp(v, 0, 500); }

    public int getCountColor() { return countColor; }
    public void setCountColor(int v) { this.countColor = v; }

    public int getNameColorOverride() { return nameColorOverride; }
    public void setNameColorOverride(int v) { this.nameColorOverride = v; }

    public boolean isShowCount() { return showCount; }
    public void setShowCount(boolean v) { this.showCount = v; }

    public boolean isUseRarityColors() { return useRarityColors; }
    public void setUseRarityColors(boolean v) { this.useRarityColors = v; }

    public boolean isIconOnRight() { return iconOnRight; }
    public void setIconOnRight(boolean v) { this.iconOnRight = v; }

    public boolean isTextShadow() { return textShadow; }
    public void setTextShadow(boolean v) { this.textShadow = v; }

    public boolean isShowCountRight() { return showCountRight; }
    public void setShowCountRight(boolean v) { this.showCountRight = v; }

    public boolean isAnimateXpColor() { return animateXpColor; }
    public void setAnimateXpColor(boolean v) { this.animateXpColor = v; }

    public boolean isAbbreviateCounts() { return abbreviateCounts; }
    public void setAbbreviateCounts(boolean v) { this.abbreviateCounts = v; }

    // =====================================================================
    // Banner / FLAT layout
    // =====================================================================

    public int getDecorativeEdgeInset() { return decorativeEdgeInset; }
    public void setDecorativeEdgeInset(int v) { this.decorativeEdgeInset = clamp(v, 0, 50); }

    public String getBannerElementOrder() { return bannerElementOrder; }
    public void setBannerElementOrder(String v) {
        this.bannerElementOrder = v != null ? v : "PICKUP_COUNT,NAME,ICON,TOTAL_COUNT";
    }

    public int getIconToNameGap() { return iconToNameGap; }
    public void setIconToNameGap(int v) { this.iconToNameGap = clamp(v, 0, 20); }

    public int getNameToCountGap() { return nameToCountGap; }
    public void setNameToCountGap(int v) { this.nameToCountGap = clamp(v, 0, 20); }

    // =====================================================================
    // Banner layer 0 (body)
    // =====================================================================

    public float getBodyAlpha() { return bodyAlpha; }
    public void setBodyAlpha(float v) { this.bodyAlpha = clamp(v, 0.0f, 1.0f); }

    public int getBodyTint() { return bodyTint; }
    public void setBodyTint(int v) { this.bodyTint = v; }

    public int getBodyAnimSpeed() { return bodyAnimSpeed; }
    public void setBodyAnimSpeed(int v) { this.bodyAnimSpeed = clamp(v, 0, 100); }

    // =====================================================================
    // Banner layer 1 (accent)
    // =====================================================================

    public boolean isShowAccent() { return showAccent; }
    public void setShowAccent(boolean v) { this.showAccent = v; }

    public float getAccentAlpha() { return accentAlpha; }
    public void setAccentAlpha(float v) { this.accentAlpha = clamp(v, 0.0f, 1.0f); }

    public int getAccentTint() { return accentTint; }
    public void setAccentTint(int v) { this.accentTint = v; }

    public int getAccentAnimSpeed() { return accentAnimSpeed; }
    public void setAccentAnimSpeed(int v) { this.accentAnimSpeed = clamp(v, 0, 100); }

    public int getAccentXOffset() { return accentXOffset; }
    public void setAccentXOffset(int v) { this.accentXOffset = clamp(v, -50, 50); }

    public int getAccentYOffset() { return accentYOffset; }
    public void setAccentYOffset(int v) { this.accentYOffset = clamp(v, -50, 50); }

    public AccentAnchor getAccentAnchor() { return accentAnchor; }
    public void setAccentAnchor(AccentAnchor v) { this.accentAnchor = v != null ? v : AccentAnchor.ICON; }
    public void setAccentAnchor(String v) { this.accentAnchor = AccentAnchor.fromString(v); }

    // =====================================================================
    // Effect targeting
    // =====================================================================

    public EffectTarget getEffectTarget() { return effectTarget; }
    public void setEffectTarget(EffectTarget v) { this.effectTarget = v != null ? v : EffectTarget.ALL; }
    public void setEffectTarget(String v) { this.effectTarget = EffectTarget.fromString(v); }

    // =====================================================================
    // Pickup Pulse
    // =====================================================================

    public boolean isPickupPulseEnabled() { return pickupPulseEnabled; }
    public void setPickupPulseEnabled(boolean v) { this.pickupPulseEnabled = v; }

    public int getPickupPulseDurationMs() { return pickupPulseDurationMs; }
    public void setPickupPulseDurationMs(int v) { this.pickupPulseDurationMs = clamp(v, 50, 1000); }

    public float getPickupPulseIconScaleStrength() { return pickupPulseIconScaleStrength; }
    public void setPickupPulseIconScaleStrength(float v) { this.pickupPulseIconScaleStrength = clamp(v, 0.0f, 0.5f); }

    public float getPickupPulseIconAlphaStrength() { return pickupPulseIconAlphaStrength; }
    public void setPickupPulseIconAlphaStrength(float v) { this.pickupPulseIconAlphaStrength = clamp(v, 0.0f, 1.0f); }

    public float getPickupPulseNameScaleStrength() { return pickupPulseNameScaleStrength; }
    public void setPickupPulseNameScaleStrength(float v) { this.pickupPulseNameScaleStrength = clamp(v, 0.0f, 0.5f); }

    public float getPickupPulseNameAlphaStrength() { return pickupPulseNameAlphaStrength; }
    public void setPickupPulseNameAlphaStrength(float v) { this.pickupPulseNameAlphaStrength = clamp(v, 0.0f, 1.0f); }

    public float getPickupPulseTotalCountScaleStrength() { return pickupPulseTotalCountScaleStrength; }
    public void setPickupPulseTotalCountScaleStrength(float v) { this.pickupPulseTotalCountScaleStrength = clamp(v, 0.0f, 0.5f); }

    public float getPickupPulseTotalCountAlphaStrength() { return pickupPulseTotalCountAlphaStrength; }
    public void setPickupPulseTotalCountAlphaStrength(float v) { this.pickupPulseTotalCountAlphaStrength = clamp(v, 0.0f, 1.0f); }

    public float getPickupPulseBodyScaleStrength() { return pickupPulseBodyScaleStrength; }
    public void setPickupPulseBodyScaleStrength(float v) { this.pickupPulseBodyScaleStrength = clamp(v, 0.0f, 0.5f); }

    public float getPickupPulseBodyAlphaStrength() { return pickupPulseBodyAlphaStrength; }
    public void setPickupPulseBodyAlphaStrength(float v) { this.pickupPulseBodyAlphaStrength = clamp(v, 0.0f, 1.0f); }

    public float getPickupPulseAccentScaleStrength() { return pickupPulseAccentScaleStrength; }
    public void setPickupPulseAccentScaleStrength(float v) { this.pickupPulseAccentScaleStrength = clamp(v, 0.0f, 0.5f); }

    public float getPickupPulseAccentAlphaStrength() { return pickupPulseAccentAlphaStrength; }
    public void setPickupPulseAccentAlphaStrength(float v) { this.pickupPulseAccentAlphaStrength = clamp(v, 0.0f, 1.0f); }

    public float getPickupPulseOverallScaleStrength() { return pickupPulseOverallScaleStrength; }
    public void setPickupPulseOverallScaleStrength(float v) { this.pickupPulseOverallScaleStrength = clamp(v, 0.0f, 0.5f); }

    public float getPickupPulseOverallAlphaStrength() { return pickupPulseOverallAlphaStrength; }
    public void setPickupPulseOverallAlphaStrength(float v) { this.pickupPulseOverallAlphaStrength = clamp(v, 0.0f, 1.0f); }

    // =====================================================================
    // Progress Bar
    // =====================================================================

    public boolean isShowProgressBar() { return showProgressBar; }
    public void setShowProgressBar(boolean v) { this.showProgressBar = v; }

    public int getProgressBarColor() { return progressBarColor; }
    public void setProgressBarColor(int v) { this.progressBarColor = v; }

    public int getProgressBarHeight() { return progressBarHeight; }
    public void setProgressBarHeight(int v) { this.progressBarHeight = clamp(v, 1, 3); }

    // =====================================================================
    // Icon Glow
    // =====================================================================

    public boolean isIconGlowEnabled() { return iconGlowEnabled; }
    public void setIconGlowEnabled(boolean v) { this.iconGlowEnabled = v; }

    public int getIconGlowColor() { return iconGlowColor; }
    public void setIconGlowColor(int v) { this.iconGlowColor = v; }

    public int getIconGlowRadius() { return iconGlowRadius; }
    public void setIconGlowRadius(int v) { this.iconGlowRadius = clamp(v, 0, 8); }

    public IconShape getIconGlowShape() { return iconGlowShape; }
    public void setIconGlowShape(IconShape v) { this.iconGlowShape = v != null ? v : IconShape.CIRCLE; }
    public void setIconGlowShape(String v) { this.iconGlowShape = IconShape.fromString(v); }

    public float getIconGlowSoftness() { return iconGlowSoftness; }
    public void setIconGlowSoftness(float v) { this.iconGlowSoftness = clamp(v, 0.5f, 5.0f); }

    public float getIconGlowPulseSpeed() { return iconGlowPulseSpeed; }
    public void setIconGlowPulseSpeed(float v) { this.iconGlowPulseSpeed = clamp(v, 0.0f, 10.0f); }

    public float getIconGlowPulseMin() { return iconGlowPulseMin; }
    public void setIconGlowPulseMin(float v) { this.iconGlowPulseMin = clamp(v, 0.0f, 1.0f); }

    public float getIconGlowPulseMax() { return iconGlowPulseMax; }
    public void setIconGlowPulseMax(float v) { this.iconGlowPulseMax = clamp(v, 0.0f, 1.0f); }

    // =====================================================================
    // Icon Shadow
    // =====================================================================

    public boolean isIconShadowEnabled() { return iconShadowEnabled; }
    public void setIconShadowEnabled(boolean v) { this.iconShadowEnabled = v; }

    public int getIconShadowColor() { return iconShadowColor; }
    public void setIconShadowColor(int v) { this.iconShadowColor = v; }

    public int getIconShadowOffsetX() { return iconShadowOffsetX; }
    public void setIconShadowOffsetX(int v) { this.iconShadowOffsetX = clamp(v, 0, 4); }

    public int getIconShadowOffsetY() { return iconShadowOffsetY; }
    public void setIconShadowOffsetY(int v) { this.iconShadowOffsetY = clamp(v, 0, 4); }

    public int getIconShadowRadius() { return iconShadowRadius; }
    public void setIconShadowRadius(int v) { this.iconShadowRadius = clamp(v, 0, 4); }

    public IconShape getIconShadowShape() { return iconShadowShape; }
    public void setIconShadowShape(IconShape v) { this.iconShadowShape = v != null ? v : IconShape.ITEM; }
    public void setIconShadowShape(String v) { this.iconShadowShape = IconShape.fromString(v); }

    public float getIconShadowSoftness() { return iconShadowSoftness; }
    public void setIconShadowSoftness(float v) { this.iconShadowSoftness = clamp(v, 0.5f, 5.0f); }

    // =====================================================================
    // Sound
    // =====================================================================

    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean v) { this.soundEnabled = v; }

    public String getSoundId() { return soundId; }
    public void setSoundId(String v) { this.soundId = v != null ? v : "minecraft:entity.item.pickup"; }

    public float getSoundVolume() { return soundVolume; }
    public void setSoundVolume(float v) { this.soundVolume = clamp(v, 0f, 1f); }

    public float getSoundPitch() { return soundPitch; }
    public void setSoundPitch(float v) { this.soundPitch = clamp(v, 0.5f, 2f); }

    // =====================================================================
    // Filtering
    // =====================================================================

    public java.util.List<String> getItemBlacklist() { return itemBlacklist; }
    public void setItemBlacklist(java.util.List<String> v) { this.itemBlacklist = v; }

    public java.util.List<String> getItemWhitelist() { return itemWhitelist; }
    public void setItemWhitelist(java.util.List<String> v) { this.itemWhitelist = v; }

    public java.util.List<String> getModBlacklist() { return modBlacklist; }
    public void setModBlacklist(java.util.List<String> v) { this.modBlacklist = v; }

    public java.util.List<String> getModWhitelist() { return modWhitelist; }
    public void setModWhitelist(java.util.List<String> v) { this.modWhitelist = v; }

    // =====================================================================
    // Computed
    // =====================================================================

    /** Total lifetime of an entry from creation to full disappearance. */
    public long getTotalLifetimeMs() {
        return fadeInMs + displayDurationMs + fadeOutMs;
    }

    // =====================================================================
    // Validation helpers
    // =====================================================================

    private static long clamp(long v, long min, long max) {
        return Math.max(min, Math.min(v, max));
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(v, max));
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(v, max));
    }

    /**
     * Migrate legacy background style names to their current equivalents.
     * Handles TEXTURED→TEXTURE and CLASSIC→BANNER renames. Returns the input
     * unchanged for all other values.
     */
    public static String migrateStyleName(String name) {
        if (name == null) return null;
        switch (name.toUpperCase()) {
            case "TEXTURED": return "TEXTURE";
            case "CLASSIC":  return "BANNER";
            default:         return name;
        }
    }

    /**
     * Migrate legacy growth direction names. UP→NORMAL, DOWN→INVERSE.
     */
    public static String migrateGrowthDirection(String name) {
        if (name == null) return null;
        switch (name.toUpperCase()) {
            case "UP":   return "NORMAL";
            case "DOWN": return "INVERSE";
            default:     return name;
        }
    }
}
