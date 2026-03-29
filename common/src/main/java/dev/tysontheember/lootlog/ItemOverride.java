package dev.tysontheember.lootlog;

import java.util.List;

/**
 * JSON-mapped POJO for a single override rule.
 * All fields are nullable -- null means "inherit from lower-priority layer or global config".
 * Deserialized by Gson from override JSON files in config/lootlog/overrides/.
 */
public class ItemOverride {

    private MatchRule match;
    private SoundOverride sound;
    private BackgroundOverride background;
    private TextOverride text;
    private DisplayOverride display;
    private VisualOverride visual;
    private BehaviorOverride behavior;
    private LayoutOverride layout;

    public MatchRule getMatch() { return match; }
    public SoundOverride getSound() { return sound; }
    public BackgroundOverride getBackground() { return background; }
    public TextOverride getText() { return text; }
    public DisplayOverride getDisplay() { return display; }
    public VisualOverride getVisual() { return visual; }
    public BehaviorOverride getBehavior() { return behavior; }
    public LayoutOverride getLayout() { return layout; }

    // -- Nested POJOs --

    public static class MatchRule {
        private String type; // "item", "tag", "mod", "rarity", "regex"
        private String id;   // e.g., "minecraft:diamond", "c:ores", "create", "epic", "minecraft:.*_ingot"

        public String getType() { return type; }
        public String getId() { return id; }
    }

    public static class SoundOverride {
        private String soundId;
        private Float volume;
        private Float pitch;

        public String getSoundId() { return soundId; }
        public Float getVolume() { return volume; }
        public Float getPitch() { return pitch; }
    }

    public static class BackgroundOverride {
        private String decoration;
        private String style;
        private String texture;
        private String color;
        private AnimationDef animation;
        private Integer textureWidth;
        private Integer textureHeight;
        private String renderMode;    // "STRETCH" or "NINE_SLICE"
        private Integer sliceBorder;
        private Integer accentY;       // legacy: converted to layers during resolution
        private Integer accentHeight;  // legacy: converted to layers during resolution
        private java.util.List<LayerDef> layers;

        public String getDecoration() { return decoration; }
        public String getStyle() { return style; }
        public String getTexture() { return texture; }
        public String getColor() { return color; }
        public AnimationDef getAnimation() { return animation; }
        public Integer getTextureWidth() { return textureWidth; }
        public Integer getTextureHeight() { return textureHeight; }
        public String getRenderMode() { return renderMode; }
        public Integer getSliceBorder() { return sliceBorder; }
        public Integer getAccentY() { return accentY; }
        public Integer getAccentHeight() { return accentHeight; }
        public java.util.List<LayerDef> getLayers() { return layers; }
    }

    public static class LayerDef {
        private String texture;
        private Integer sourceHeight;
        private Integer frames;
        private String tint;
        private Float alpha;
        private Integer animSpeed;
        private Integer xOffset;
        private Integer yOffset;
        private Boolean visible;
        private String anchor;

        public String getTexture() { return texture; }
        public Integer getSourceHeight() { return sourceHeight; }
        public Integer getFrames() { return frames; }
        public String getTint() { return tint; }
        public Float getAlpha() { return alpha; }
        public Integer getAnimSpeed() { return animSpeed; }
        public String getAnchor() { return anchor; }
        public Integer getXOffset() { return xOffset; }
        public Integer getYOffset() { return yOffset; }
        public Boolean getVisible() { return visible; }
    }

    public static class AnimationDef {
        private String type; // "spritesheet"
        private Integer frames;
        private Integer frameTimeMs;
        private Boolean interpolate;

        public String getType() { return type; }
        public Integer getFrames() { return frames; }
        public Integer getFrameTimeMs() { return frameTimeMs; }
        public Boolean getInterpolate() { return interpolate; }
    }

    public static class TextOverride {
        private String markup;
        private String color;
        private String prefix;
        private String suffix;
        private String fullName;

        public String getMarkup() { return markup; }
        public String getColor() { return color; }
        public String getPrefix() { return prefix; }
        public String getSuffix() { return suffix; }
        public String getFullName() { return fullName; }
    }

    public static class DisplayOverride {
        private Long durationMs;
        private Float scale;
        private String combineMode;

        public Long getDurationMs() { return durationMs; }
        public Float getScale() { return scale; }
        public String getCombineMode() { return combineMode; }
    }

    public static class IconGlowDef {
        private String color;
        private Integer radius;
        private String shape;    // "square", "circle", "diamond", "item"
        private String style;    // "smooth", "dithered"
        private Float softness;  // 1.0+, higher = more gradual fade
        private PulseDef pulse;

        public String getColor() { return color; }
        public Integer getRadius() { return radius; }
        public String getShape() { return shape; }
        public String getStyle() { return style; }
        public Float getSoftness() { return softness; }
        public PulseDef getPulse() { return pulse; }
    }

    public static class PulseDef {
        private Float speed; // oscillations per second
        private Float min;   // minimum brightness multiplier
        private Float max;   // maximum brightness multiplier

        public Float getSpeed() { return speed; }
        public Float getMin() { return min; }
        public Float getMax() { return max; }
    }

    public static class PickupPulseDef {
        private Boolean enabled;
        private Integer durationMs;
        private Float iconScaleStrength;
        private Float iconAlphaStrength;
        private Float nameScaleStrength;
        private Float nameAlphaStrength;
        private Float totalCountScaleStrength;
        private Float totalCountAlphaStrength;
        private Float bodyScaleStrength;
        private Float bodyAlphaStrength;
        private Float accentScaleStrength;
        private Float accentAlphaStrength;
        private Float overallScaleStrength;
        private Float overallAlphaStrength;

        public Boolean getEnabled() { return enabled; }
        public Integer getDurationMs() { return durationMs; }
        public Float getIconScaleStrength() { return iconScaleStrength; }
        public Float getIconAlphaStrength() { return iconAlphaStrength; }
        public Float getNameScaleStrength() { return nameScaleStrength; }
        public Float getNameAlphaStrength() { return nameAlphaStrength; }
        public Float getTotalCountScaleStrength() { return totalCountScaleStrength; }
        public Float getTotalCountAlphaStrength() { return totalCountAlphaStrength; }
        public Float getBodyScaleStrength() { return bodyScaleStrength; }
        public Float getBodyAlphaStrength() { return bodyAlphaStrength; }
        public Float getAccentScaleStrength() { return accentScaleStrength; }
        public Float getAccentAlphaStrength() { return accentAlphaStrength; }
        public Float getOverallScaleStrength() { return overallScaleStrength; }
        public Float getOverallAlphaStrength() { return overallAlphaStrength; }
    }

    public static class BgEffectDef {
        private String type;
        private String color;
        private Float speed;
        private Float minAlpha;
        private Float maxAlpha;
        private Float width;
        private Float intensity;
        private Integer durationMs;
        private Integer pauseMs;
        private java.util.List<String> colors; // for color_shift

        public String getType() { return type; }
        public String getColor() { return color; }
        public Float getSpeed() { return speed; }
        public Float getMinAlpha() { return minAlpha; }
        public Float getMaxAlpha() { return maxAlpha; }
        public Float getWidth() { return width; }
        public Float getIntensity() { return intensity; }
        public Integer getDurationMs() { return durationMs; }
        public Integer getPauseMs() { return pauseMs; }
        public java.util.List<String> getColors() { return colors; }
    }

    public static class IconShadowDef {
        private String color;     // hex ARGB, default semi-transparent black
        private Integer offsetX;  // pixel offset right
        private Integer offsetY;  // pixel offset down
        private Integer radius;   // blur radius
        private String shape;     // "item", "circle", "square", "diamond"
        private Float softness;   // gradient falloff

        public String getColor() { return color; }
        public Integer getOffsetX() { return offsetX; }
        public Integer getOffsetY() { return offsetY; }
        public Integer getRadius() { return radius; }
        public String getShape() { return shape; }
        public Float getSoftness() { return softness; }
    }

    public static class VisualOverride {
        private IconGlowDef iconGlow;
        private IconShadowDef iconShadow;
        private List<BgEffectDef> effects;
        private Float iconSpinSpeed;       // degrees per second
        private Float scalePulseSpeed;     // cycles per second
        private Float scalePulseMin;
        private Float scalePulseMax;
        private PickupPulseDef pickupPulse;

        public IconGlowDef getIconGlow() { return iconGlow; }
        public IconShadowDef getIconShadow() { return iconShadow; }
        public List<BgEffectDef> getEffects() { return effects; }
        public Float getIconSpinSpeed() { return iconSpinSpeed; }
        public Float getScalePulseSpeed() { return scalePulseSpeed; }
        public Float getScalePulseMin() { return scalePulseMin; }
        public Float getScalePulseMax() { return scalePulseMax; }
        public PickupPulseDef getPickupPulse() { return pickupPulse; }
    }

    public static class BehaviorOverride {
        private Integer priority;
        private Boolean forceShow;

        public Integer getPriority() { return priority; }
        public Boolean getForceShow() { return forceShow; }
    }

    public static class LayoutOverride {
        private String preset;
        private Boolean iconEnabled;
        private Integer iconOffsetX;
        private Integer iconOffsetY;
        private Boolean nameEnabled;
        private Boolean pickupCountEnabled;
        private Boolean totalCountEnabled;

        public String getPreset() { return preset; }
        public Boolean getIconEnabled() { return iconEnabled; }
        public Integer getIconOffsetX() { return iconOffsetX; }
        public Integer getIconOffsetY() { return iconOffsetY; }
        public Boolean getNameEnabled() { return nameEnabled; }
        public Boolean getPickupCountEnabled() { return pickupCountEnabled; }
        public Boolean getTotalCountEnabled() { return totalCountEnabled; }
    }
}
