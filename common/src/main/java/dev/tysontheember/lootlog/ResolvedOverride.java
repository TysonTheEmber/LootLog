package dev.tysontheember.lootlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Flat, fully-resolved override after merge/layer resolution.
 * All fields nullable -- null means "use global config default".
 * Immutable after construction via {@link Builder}.
 */
public class ResolvedOverride {

    // Sound
    private final String soundId;
    private final Float soundVolume;
    private final Float soundPitch;

    // Background
    private final BackgroundStyle backgroundStyle;
    private final String backgroundTexture;
    private final String backgroundDecoration;
    private final Integer backgroundColor;
    private final FrameAnimation frameAnimation;
    private final List<BgEffect> backgroundEffects;
    private final Integer textureWidth;
    private final Integer textureHeight;
    private final String renderMode;
    private final Integer sliceBorder;
    private final Integer accentY;
    private final Integer accentHeight;
    private final List<BannerLayer> layers;

    // Layout
    private final String layoutPreset;
    private final Boolean iconEnabled;
    private final Integer iconOffsetX;
    private final Integer iconOffsetY;
    private final Boolean nameEnabled;
    private final Boolean pickupCountEnabled;
    private final Boolean totalCountEnabled;

    // Text
    private final String nameMarkup;
    private final Integer textColor;
    private final String namePrefix;
    private final String nameSuffix;
    private final String fullName;

    // Display
    private final Long displayDurationMs;
    private final Float displayScale;
    private final CombineMode combineMode;

    // Visual - icon glow
    private final Integer iconGlowColor;
    private final Integer iconGlowRadius;
    private final String iconGlowShape;     // "square", "circle", "diamond", "item"
    private final String iconGlowStyle;     // "smooth", "dithered"
    private final Float iconGlowSoftness;   // 1.0+, higher = more gradual
    private final Float iconGlowPulseSpeed;
    private final Float iconGlowPulseMin;
    private final Float iconGlowPulseMax;

    // Visual - icon shadow
    private final Integer iconShadowColor;
    private final Integer iconShadowOffsetX;
    private final Integer iconShadowOffsetY;
    private final Integer iconShadowRadius;
    private final String iconShadowShape;
    private final Float iconShadowSoftness;

    // Visual - animations
    private final Float iconSpinSpeed;
    private final Float scalePulseSpeed;
    private final Float scalePulseMin;
    private final Float scalePulseMax;

    // Visual - pickup pulse
    private final Boolean pickupPulseEnabled;
    private final Integer pickupPulseDurationMs;
    private final Float pickupPulseIconScaleStrength;
    private final Float pickupPulseIconAlphaStrength;
    private final Float pickupPulseNameScaleStrength;
    private final Float pickupPulseNameAlphaStrength;
    private final Float pickupPulseTotalCountScaleStrength;
    private final Float pickupPulseTotalCountAlphaStrength;
    private final Float pickupPulseBodyScaleStrength;
    private final Float pickupPulseBodyAlphaStrength;
    private final Float pickupPulseAccentScaleStrength;
    private final Float pickupPulseAccentAlphaStrength;
    private final Float pickupPulseOverallScaleStrength;
    private final Float pickupPulseOverallAlphaStrength;

    // Behavior
    private final int priority;
    private final boolean forceShow;

    private ResolvedOverride(Builder b) {
        this.soundId = b.soundId;
        this.soundVolume = b.soundVolume;
        this.soundPitch = b.soundPitch;
        this.backgroundStyle = b.backgroundStyle;
        this.backgroundTexture = b.backgroundTexture;
        this.backgroundDecoration = b.backgroundDecoration;
        this.backgroundColor = b.backgroundColor;
        this.frameAnimation = b.frameAnimation;
        this.backgroundEffects = b.backgroundEffects.isEmpty()
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(b.backgroundEffects));
        this.textureWidth = b.textureWidth;
        this.textureHeight = b.textureHeight;
        this.renderMode = b.renderMode;
        this.sliceBorder = b.sliceBorder;
        this.accentY = b.accentY;
        this.accentHeight = b.accentHeight;
        this.layers = b.layers != null && !b.layers.isEmpty()
                ? Collections.unmodifiableList(new ArrayList<>(b.layers))
                : null;
        this.layoutPreset = b.layoutPreset;
        this.iconEnabled = b.iconEnabled;
        this.iconOffsetX = b.iconOffsetX;
        this.iconOffsetY = b.iconOffsetY;
        this.nameEnabled = b.nameEnabled;
        this.pickupCountEnabled = b.pickupCountEnabled;
        this.totalCountEnabled = b.totalCountEnabled;
        this.nameMarkup = b.nameMarkup;
        this.textColor = b.textColor;
        this.namePrefix = b.namePrefix;
        this.nameSuffix = b.nameSuffix;
        this.fullName = b.fullName;
        this.displayDurationMs = b.displayDurationMs;
        this.displayScale = b.displayScale;
        this.combineMode = b.combineMode;
        this.iconGlowColor = b.iconGlowColor;
        this.iconGlowRadius = b.iconGlowRadius;
        this.iconGlowShape = b.iconGlowShape;
        this.iconGlowStyle = b.iconGlowStyle;
        this.iconGlowSoftness = b.iconGlowSoftness;
        this.iconGlowPulseSpeed = b.iconGlowPulseSpeed;
        this.iconGlowPulseMin = b.iconGlowPulseMin;
        this.iconGlowPulseMax = b.iconGlowPulseMax;
        this.iconShadowColor = b.iconShadowColor;
        this.iconShadowOffsetX = b.iconShadowOffsetX;
        this.iconShadowOffsetY = b.iconShadowOffsetY;
        this.iconShadowRadius = b.iconShadowRadius;
        this.iconShadowShape = b.iconShadowShape;
        this.iconShadowSoftness = b.iconShadowSoftness;
        this.iconSpinSpeed = b.iconSpinSpeed;
        this.scalePulseSpeed = b.scalePulseSpeed;
        this.scalePulseMin = b.scalePulseMin;
        this.scalePulseMax = b.scalePulseMax;
        this.pickupPulseEnabled = b.pickupPulseEnabled;
        this.pickupPulseDurationMs = b.pickupPulseDurationMs;
        this.pickupPulseIconScaleStrength = b.pickupPulseIconScaleStrength;
        this.pickupPulseIconAlphaStrength = b.pickupPulseIconAlphaStrength;
        this.pickupPulseNameScaleStrength = b.pickupPulseNameScaleStrength;
        this.pickupPulseNameAlphaStrength = b.pickupPulseNameAlphaStrength;
        this.pickupPulseTotalCountScaleStrength = b.pickupPulseTotalCountScaleStrength;
        this.pickupPulseTotalCountAlphaStrength = b.pickupPulseTotalCountAlphaStrength;
        this.pickupPulseBodyScaleStrength = b.pickupPulseBodyScaleStrength;
        this.pickupPulseBodyAlphaStrength = b.pickupPulseBodyAlphaStrength;
        this.pickupPulseAccentScaleStrength = b.pickupPulseAccentScaleStrength;
        this.pickupPulseAccentAlphaStrength = b.pickupPulseAccentAlphaStrength;
        this.pickupPulseOverallScaleStrength = b.pickupPulseOverallScaleStrength;
        this.pickupPulseOverallAlphaStrength = b.pickupPulseOverallAlphaStrength;
        this.priority = b.priority;
        this.forceShow = b.forceShow;
    }

    // -- Getters --

    public String getSoundId() { return soundId; }
    public Float getSoundVolume() { return soundVolume; }
    public Float getSoundPitch() { return soundPitch; }

    public BackgroundStyle getBackgroundStyle() { return backgroundStyle; }
    public String getBackgroundTexture() { return backgroundTexture; }
    public String getBackgroundDecoration() { return backgroundDecoration; }
    public Integer getBackgroundColor() { return backgroundColor; }
    public FrameAnimation getFrameAnimation() { return frameAnimation; }
    public List<BgEffect> getBackgroundEffects() { return backgroundEffects; }
    public Integer getTextureWidth() { return textureWidth; }
    public Integer getTextureHeight() { return textureHeight; }
    public String getRenderMode() { return renderMode; }
    public Integer getSliceBorder() { return sliceBorder; }
    public Integer getAccentY() { return accentY; }
    public Integer getAccentHeight() { return accentHeight; }

    /** Per-item banner layers, or null to use config/decoration default. */
    public List<BannerLayer> getLayers() { return layers; }

    public String getLayoutPreset() { return layoutPreset; }
    public Boolean getIconEnabled() { return iconEnabled; }
    public Integer getIconOffsetX() { return iconOffsetX; }
    public Integer getIconOffsetY() { return iconOffsetY; }
    public Boolean getNameEnabled() { return nameEnabled; }
    public Boolean getPickupCountEnabled() { return pickupCountEnabled; }
    public Boolean getTotalCountEnabled() { return totalCountEnabled; }

    public String getNameMarkup() { return nameMarkup; }
    public Integer getTextColor() { return textColor; }
    public String getNamePrefix() { return namePrefix; }
    public String getNameSuffix() { return nameSuffix; }
    public String getFullName() { return fullName; }

    public Long getDisplayDurationMs() { return displayDurationMs; }
    public Float getDisplayScale() { return displayScale; }
    public CombineMode getCombineMode() { return combineMode; }

    public Integer getIconGlowColor() { return iconGlowColor; }
    public Integer getIconGlowRadius() { return iconGlowRadius; }
    public String getIconGlowShape() { return iconGlowShape; }
    public String getIconGlowStyle() { return iconGlowStyle; }
    public Float getIconGlowSoftness() { return iconGlowSoftness; }
    public Float getIconGlowPulseSpeed() { return iconGlowPulseSpeed; }
    public Float getIconGlowPulseMin() { return iconGlowPulseMin; }
    public Float getIconGlowPulseMax() { return iconGlowPulseMax; }

    public Integer getIconShadowColor() { return iconShadowColor; }
    public Integer getIconShadowOffsetX() { return iconShadowOffsetX; }
    public Integer getIconShadowOffsetY() { return iconShadowOffsetY; }
    public Integer getIconShadowRadius() { return iconShadowRadius; }
    public String getIconShadowShape() { return iconShadowShape; }
    public Float getIconShadowSoftness() { return iconShadowSoftness; }

    public Float getIconSpinSpeed() { return iconSpinSpeed; }
    public Float getScalePulseSpeed() { return scalePulseSpeed; }
    public Float getScalePulseMin() { return scalePulseMin; }
    public Float getScalePulseMax() { return scalePulseMax; }

    public Boolean getPickupPulseEnabled() { return pickupPulseEnabled; }
    public Integer getPickupPulseDurationMs() { return pickupPulseDurationMs; }
    public Float getPickupPulseIconScaleStrength() { return pickupPulseIconScaleStrength; }
    public Float getPickupPulseIconAlphaStrength() { return pickupPulseIconAlphaStrength; }
    public Float getPickupPulseNameScaleStrength() { return pickupPulseNameScaleStrength; }
    public Float getPickupPulseNameAlphaStrength() { return pickupPulseNameAlphaStrength; }
    public Float getPickupPulseTotalCountScaleStrength() { return pickupPulseTotalCountScaleStrength; }
    public Float getPickupPulseTotalCountAlphaStrength() { return pickupPulseTotalCountAlphaStrength; }
    public Float getPickupPulseBodyScaleStrength() { return pickupPulseBodyScaleStrength; }
    public Float getPickupPulseBodyAlphaStrength() { return pickupPulseBodyAlphaStrength; }
    public Float getPickupPulseAccentScaleStrength() { return pickupPulseAccentScaleStrength; }
    public Float getPickupPulseAccentAlphaStrength() { return pickupPulseAccentAlphaStrength; }
    public Float getPickupPulseOverallScaleStrength() { return pickupPulseOverallScaleStrength; }
    public Float getPickupPulseOverallAlphaStrength() { return pickupPulseOverallAlphaStrength; }

    public int getPriority() { return priority; }
    public boolean isForceShow() { return forceShow; }

    // -- Inner data classes --

    /** Immutable spritesheet animation definition. */
    public static class FrameAnimation {
        private final int frames;
        private final int frameTimeMs;
        private final boolean interpolate;

        public FrameAnimation(int frames, int frameTimeMs, boolean interpolate) {
            this.frames = Math.max(1, frames);
            this.frameTimeMs = Math.max(1, frameTimeMs);
            this.interpolate = interpolate;
        }

        public int getFrames() { return frames; }
        public int getFrameTimeMs() { return frameTimeMs; }
        public boolean isInterpolate() { return interpolate; }
    }

    /** Immutable programmatic background effect. */
    public static class BgEffect {
        private final String type;
        private final int color;
        private final float speed;
        private final float minAlpha;
        private final float maxAlpha;
        private final float width;
        private final float intensity;
        private final int durationMs;
        private final int pauseMs;
        private final int[] colors; // for color_shift

        public BgEffect(String type, int color, float speed,
                         float minAlpha, float maxAlpha, float width,
                         float intensity, int durationMs, int pauseMs, int[] colors) {
            this.type = type;
            this.color = color;
            this.speed = speed;
            this.minAlpha = minAlpha;
            this.maxAlpha = maxAlpha;
            this.width = width;
            this.intensity = intensity;
            this.durationMs = durationMs;
            this.pauseMs = pauseMs;
            this.colors = colors;
        }

        public String getType() { return type; }
        public int getColor() { return color; }
        public float getSpeed() { return speed; }
        public float getMinAlpha() { return minAlpha; }
        public float getMaxAlpha() { return maxAlpha; }
        public float getWidth() { return width; }
        public float getIntensity() { return intensity; }
        public int getDurationMs() { return durationMs; }
        public int getPauseMs() { return pauseMs; }
        public int[] getColors() { return colors != null ? colors.clone() : null; }
    }

    // -- Builder with merge support --

    public static class Builder {
        private String soundId;
        private Float soundVolume;
        private Float soundPitch;

        private BackgroundStyle backgroundStyle;
        private String backgroundTexture;
        private String backgroundDecoration;
        private Integer backgroundColor;
        private FrameAnimation frameAnimation;
        private List<BgEffect> backgroundEffects = new ArrayList<>();
        private Integer textureWidth;
        private Integer textureHeight;
        private String renderMode;
        private Integer sliceBorder;
        private Integer accentY;
        private Integer accentHeight;
        private List<BannerLayer> layers;

        private String layoutPreset;
        private Boolean iconEnabled;
        private Integer iconOffsetX;
        private Integer iconOffsetY;
        private Boolean nameEnabled;
        private Boolean pickupCountEnabled;
        private Boolean totalCountEnabled;

        private String nameMarkup;
        private Integer textColor;
        private String namePrefix;
        private String nameSuffix;
        private String fullName;

        private Long displayDurationMs;
        private Float displayScale;
        private CombineMode combineMode;

        private Integer iconGlowColor;
        private Integer iconGlowRadius;
        private String iconGlowShape;
        private String iconGlowStyle;
        private Float iconGlowSoftness;
        private Float iconGlowPulseSpeed;
        private Float iconGlowPulseMin;
        private Float iconGlowPulseMax;

        private Integer iconShadowColor;
        private Integer iconShadowOffsetX;
        private Integer iconShadowOffsetY;
        private Integer iconShadowRadius;
        private String iconShadowShape;
        private Float iconShadowSoftness;

        private Float iconSpinSpeed;
        private Float scalePulseSpeed;
        private Float scalePulseMin;
        private Float scalePulseMax;

        private Boolean pickupPulseEnabled;
        private Integer pickupPulseDurationMs;
        private Float pickupPulseIconScaleStrength;
        private Float pickupPulseIconAlphaStrength;
        private Float pickupPulseNameScaleStrength;
        private Float pickupPulseNameAlphaStrength;
        private Float pickupPulseTotalCountScaleStrength;
        private Float pickupPulseTotalCountAlphaStrength;
        private Float pickupPulseBodyScaleStrength;
        private Float pickupPulseBodyAlphaStrength;
        private Float pickupPulseAccentScaleStrength;
        private Float pickupPulseAccentAlphaStrength;
        private Float pickupPulseOverallScaleStrength;
        private Float pickupPulseOverallAlphaStrength;

        private int priority;
        private boolean forceShow;

        /**
         * Merge non-null fields from the given override into this builder.
         * Each leaf field is independent -- only non-null source fields overwrite.
         */
        public Builder mergeFrom(ItemOverride source) {
            if (source.getSound() != null) {
                ItemOverride.SoundOverride s = source.getSound();
                if (s.getSoundId() != null) this.soundId = s.getSoundId();
                if (s.getVolume() != null) this.soundVolume = s.getVolume();
                if (s.getPitch() != null) this.soundPitch = s.getPitch();
            }

            if (source.getBackground() != null) {
                ItemOverride.BackgroundOverride bg = source.getBackground();
                // Decoration provides base values; explicit fields below override them
                if (bg.getDecoration() != null) {
                    this.backgroundDecoration = bg.getDecoration();
                    Decoration deco = Decoration.byName(bg.getDecoration());
                    if (deco != null) {
                        this.backgroundStyle = deco.getImpliedStyle();
                        this.backgroundTexture = deco.getTexture();
                    }
                }
                if (bg.getStyle() != null) {
                    String migrated = LootLogConfig.migrateStyleName(bg.getStyle());
                    try {
                        this.backgroundStyle = BackgroundStyle.valueOf(migrated.toUpperCase());
                    } catch (IllegalArgumentException ignored) {}
                }
                if (bg.getTexture() != null) this.backgroundTexture = bg.getTexture();
                if (bg.getColor() != null) this.backgroundColor = parseColor(bg.getColor());
                if (bg.getAnimation() != null) {
                    ItemOverride.AnimationDef a = bg.getAnimation();
                    if (a.getFrames() != null && a.getFrameTimeMs() != null) {
                        this.frameAnimation = new FrameAnimation(
                                a.getFrames(), a.getFrameTimeMs(),
                                a.getInterpolate() != null && a.getInterpolate());
                    }
                }
                if (bg.getTextureWidth() != null) this.textureWidth = bg.getTextureWidth();
                if (bg.getTextureHeight() != null) this.textureHeight = bg.getTextureHeight();
                if (bg.getRenderMode() != null) this.renderMode = bg.getRenderMode();
                if (bg.getSliceBorder() != null) this.sliceBorder = bg.getSliceBorder();
                if (bg.getAccentY() != null) this.accentY = bg.getAccentY();
                if (bg.getAccentHeight() != null) this.accentHeight = bg.getAccentHeight();
                // Layers: last writer wins (higher priority replaces entire list)
                if (bg.getLayers() != null && !bg.getLayers().isEmpty()) {
                    this.layers = convertLayers(bg.getLayers());
                }
            }

            if (source.getText() != null) {
                ItemOverride.TextOverride t = source.getText();
                if (t.getMarkup() != null) this.nameMarkup = t.getMarkup();
                if (t.getColor() != null) this.textColor = parseColor(t.getColor());
                if (t.getPrefix() != null) this.namePrefix = t.getPrefix();
                if (t.getSuffix() != null) this.nameSuffix = t.getSuffix();
                if (t.getFullName() != null) this.fullName = t.getFullName();
            }

            if (source.getDisplay() != null) {
                ItemOverride.DisplayOverride d = source.getDisplay();
                if (d.getDurationMs() != null) this.displayDurationMs = d.getDurationMs();
                if (d.getScale() != null) this.displayScale = d.getScale();
                if (d.getCombineMode() != null) {
                    try {
                        this.combineMode = CombineMode.valueOf(d.getCombineMode());
                    } catch (IllegalArgumentException ignored) {}
                }
            }

            if (source.getVisual() != null) {
                ItemOverride.VisualOverride v = source.getVisual();
                if (v.getIconGlow() != null) {
                    ItemOverride.IconGlowDef g = v.getIconGlow();
                    if (g.getColor() != null) this.iconGlowColor = parseColor(g.getColor());
                    if (g.getRadius() != null) this.iconGlowRadius = g.getRadius();
                    if (g.getShape() != null) this.iconGlowShape = g.getShape();
                    if (g.getStyle() != null) this.iconGlowStyle = g.getStyle();
                    if (g.getSoftness() != null) this.iconGlowSoftness = g.getSoftness();
                    if (g.getPulse() != null) {
                        ItemOverride.PulseDef p = g.getPulse();
                        if (p.getSpeed() != null) this.iconGlowPulseSpeed = p.getSpeed();
                        if (p.getMin() != null) this.iconGlowPulseMin = p.getMin();
                        if (p.getMax() != null) this.iconGlowPulseMax = p.getMax();
                    }
                }
                if (v.getIconShadow() != null) {
                    ItemOverride.IconShadowDef s = v.getIconShadow();
                    if (s.getColor() != null) this.iconShadowColor = parseColor(s.getColor());
                    if (s.getOffsetX() != null) this.iconShadowOffsetX = s.getOffsetX();
                    if (s.getOffsetY() != null) this.iconShadowOffsetY = s.getOffsetY();
                    if (s.getRadius() != null) this.iconShadowRadius = s.getRadius();
                    if (s.getShape() != null) this.iconShadowShape = s.getShape();
                    if (s.getSoftness() != null) this.iconShadowSoftness = s.getSoftness();
                }
                if (v.getIconSpinSpeed() != null) this.iconSpinSpeed = v.getIconSpinSpeed();
                if (v.getScalePulseSpeed() != null) this.scalePulseSpeed = v.getScalePulseSpeed();
                if (v.getScalePulseMin() != null) this.scalePulseMin = v.getScalePulseMin();
                if (v.getScalePulseMax() != null) this.scalePulseMax = v.getScalePulseMax();
                if (v.getPickupPulse() != null) {
                    ItemOverride.PickupPulseDef pp = v.getPickupPulse();
                    if (pp.getEnabled() != null) this.pickupPulseEnabled = pp.getEnabled();
                    if (pp.getDurationMs() != null) this.pickupPulseDurationMs = pp.getDurationMs();
                    if (pp.getIconScaleStrength() != null) this.pickupPulseIconScaleStrength = pp.getIconScaleStrength();
                    if (pp.getIconAlphaStrength() != null) this.pickupPulseIconAlphaStrength = pp.getIconAlphaStrength();
                    if (pp.getNameScaleStrength() != null) this.pickupPulseNameScaleStrength = pp.getNameScaleStrength();
                    if (pp.getNameAlphaStrength() != null) this.pickupPulseNameAlphaStrength = pp.getNameAlphaStrength();
                    if (pp.getTotalCountScaleStrength() != null) this.pickupPulseTotalCountScaleStrength = pp.getTotalCountScaleStrength();
                    if (pp.getTotalCountAlphaStrength() != null) this.pickupPulseTotalCountAlphaStrength = pp.getTotalCountAlphaStrength();
                    if (pp.getBodyScaleStrength() != null) this.pickupPulseBodyScaleStrength = pp.getBodyScaleStrength();
                    if (pp.getBodyAlphaStrength() != null) this.pickupPulseBodyAlphaStrength = pp.getBodyAlphaStrength();
                    if (pp.getAccentScaleStrength() != null) this.pickupPulseAccentScaleStrength = pp.getAccentScaleStrength();
                    if (pp.getAccentAlphaStrength() != null) this.pickupPulseAccentAlphaStrength = pp.getAccentAlphaStrength();
                    if (pp.getOverallScaleStrength() != null) this.pickupPulseOverallScaleStrength = pp.getOverallScaleStrength();
                    if (pp.getOverallAlphaStrength() != null) this.pickupPulseOverallAlphaStrength = pp.getOverallAlphaStrength();
                }
                if (v.getEffects() != null && !v.getEffects().isEmpty()) {
                    this.backgroundEffects = new ArrayList<>();
                    for (ItemOverride.BgEffectDef def : v.getEffects()) {
                        // Parse color_shift color list
                        int[] colorStops = null;
                        if (def.getColors() != null && !def.getColors().isEmpty()) {
                            colorStops = new int[def.getColors().size()];
                            for (int ci = 0; ci < def.getColors().size(); ci++) {
                                Integer parsed = parseColor(def.getColors().get(ci));
                                colorStops[ci] = parsed != null ? parsed : 0xFFFFFFFF;
                            }
                        }

                        this.backgroundEffects.add(new BgEffect(
                                def.getType() != null ? def.getType() : "pulse",
                                def.getColor() != null ? parseColor(def.getColor()) : 0x44FFFFFF,
                                def.getSpeed() != null ? def.getSpeed() : 1.0f,
                                def.getMinAlpha() != null ? def.getMinAlpha() : 0.0f,
                                def.getMaxAlpha() != null ? def.getMaxAlpha() : 1.0f,
                                def.getWidth() != null ? def.getWidth() : 0.2f,
                                def.getIntensity() != null ? def.getIntensity() : 0.5f,
                                def.getDurationMs() != null ? def.getDurationMs() : 400,
                                def.getPauseMs() != null ? def.getPauseMs() : 0,
                                colorStops));
                    }
                }
            }

            if (source.getLayout() != null) {
                ItemOverride.LayoutOverride lo = source.getLayout();
                if (lo.getPreset() != null) this.layoutPreset = lo.getPreset();
                if (lo.getIconEnabled() != null) this.iconEnabled = lo.getIconEnabled();
                if (lo.getIconOffsetX() != null) this.iconOffsetX = lo.getIconOffsetX();
                if (lo.getIconOffsetY() != null) this.iconOffsetY = lo.getIconOffsetY();
                if (lo.getNameEnabled() != null) this.nameEnabled = lo.getNameEnabled();
                if (lo.getPickupCountEnabled() != null) this.pickupCountEnabled = lo.getPickupCountEnabled();
                if (lo.getTotalCountEnabled() != null) this.totalCountEnabled = lo.getTotalCountEnabled();
            }

            if (source.getBehavior() != null) {
                ItemOverride.BehaviorOverride bh = source.getBehavior();
                if (bh.getPriority() != null) this.priority = bh.getPriority();
                if (bh.getForceShow() != null) this.forceShow = bh.getForceShow();
            }

            return this;
        }

        public ResolvedOverride build() {
            return new ResolvedOverride(this);
        }

        private static List<BannerLayer> convertLayers(java.util.List<ItemOverride.LayerDef> defs) {
            List<BannerLayer> result = new ArrayList<>();
            int max = Math.min(defs.size(), 10); // max 10 layers
            for (int i = 0; i < max; i++) {
                ItemOverride.LayerDef def = defs.get(i);
                if (def.getTexture() == null || def.getSourceHeight() == null) continue;
                int srcH = def.getSourceHeight();
                int frames = (def.getFrames() != null && def.getFrames() > 1) ? def.getFrames() : 1;
                TextureSpec tex = TextureSpec.banner(def.getTexture(), srcH)
                        .setPngDimensions(256, srcH * frames);
                BannerLayer.Builder lb = BannerLayer.builder(tex);
                if (def.getAlpha() != null) lb.alpha(def.getAlpha());
                if (def.getTint() != null) {
                    Integer c = parseColor(def.getTint());
                    if (c != null) lb.tint(c);
                }
                if (def.getAnimSpeed() != null) lb.animSpeed(def.getAnimSpeed());
                if (def.getXOffset() != null) lb.xOffset(def.getXOffset());
                if (def.getYOffset() != null) lb.yOffset(def.getYOffset());
                if (def.getVisible() != null) lb.visible(def.getVisible());
                if (def.getAnchor() != null) {
                    try {
                        lb.anchor(BannerLayer.Anchor.valueOf(def.getAnchor().toUpperCase()));
                    } catch (IllegalArgumentException ignored) {}
                }
                result.add(lb.build());
            }
            return result.isEmpty() ? null : result;
        }

        static Integer parseColor(String hex) {
            if (hex == null || hex.isEmpty()) return null;
            if (hex.startsWith("#")) hex = hex.substring(1);
            try {
                long value = Long.parseUnsignedLong(hex, 16);
                // If 6 hex chars (RGB), assume full alpha
                if (hex.length() <= 6) {
                    value |= 0xFF000000L;
                }
                return (int) value;
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
