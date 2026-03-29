package dev.tysontheember.lootlog;

/**
 * Immutable set of modifier values computed from background effects.
 * BackgroundRenderer applies these modifiers when making its render calls,
 * so effects are integrated into the background rather than overlaid.
 */
public final class EffectModifiers {

    /** Identity instance -- no modifications applied. */
    public static final EffectModifiers IDENTITY = new EffectModifiers();

    // Body modifiers (fill/center of background)
    public final float bodyTintR, bodyTintG, bodyTintB;
    public final float bodyAlphaMultiplier;
    public final int bodyBrighten;

    // Border modifiers (edges/borders, independent of body)
    public final float borderTintR, borderTintG, borderTintB;
    public final float borderAlphaMultiplier;
    public final int borderBrighten;

    // Texture tint (passed to renderTintedTexture)
    public final float texTintR, texTintG, texTintB;

    // Sweep data (horizontal shine band)
    public final float sweepPosition;   // -1 = no sweep; 0-1 = position as fraction of width
    public final float sweepWidth;      // fraction of entry width
    public final float sweepIntensity;  // 0-1 brightness boost in sweep band

    /** Identity constructor. */
    private EffectModifiers() {
        this.bodyTintR = 1f; this.bodyTintG = 1f; this.bodyTintB = 1f;
        this.bodyAlphaMultiplier = 1f;
        this.bodyBrighten = 0;
        this.borderTintR = 1f; this.borderTintG = 1f; this.borderTintB = 1f;
        this.borderAlphaMultiplier = 1f;
        this.borderBrighten = 0;
        this.texTintR = 1f; this.texTintG = 1f; this.texTintB = 1f;
        this.sweepPosition = -1f;
        this.sweepWidth = 0f;
        this.sweepIntensity = 0f;
    }

    private EffectModifiers(Builder b) {
        this.bodyTintR = b.bodyTintR; this.bodyTintG = b.bodyTintG; this.bodyTintB = b.bodyTintB;
        this.bodyAlphaMultiplier = b.bodyAlphaMultiplier;
        this.bodyBrighten = b.bodyBrighten;
        this.borderTintR = b.borderTintR; this.borderTintG = b.borderTintG; this.borderTintB = b.borderTintB;
        this.borderAlphaMultiplier = b.borderAlphaMultiplier;
        this.borderBrighten = b.borderBrighten;
        this.texTintR = b.texTintR; this.texTintG = b.texTintG; this.texTintB = b.texTintB;
        this.sweepPosition = b.sweepPosition;
        this.sweepWidth = b.sweepWidth;
        this.sweepIntensity = b.sweepIntensity;
    }

    public boolean isIdentity() {
        return this == IDENTITY;
    }

    /** Apply body modifiers (tint + brighten) to a color. */
    public int applyBody(int argb) {
        int result = ColorUtil.tintRgb(argb, bodyTintR, bodyTintG, bodyTintB);
        if (bodyBrighten > 0) result = ColorUtil.brighten(result, bodyBrighten);
        if (bodyAlphaMultiplier != 1f) result = ColorUtil.multiplyAlpha(result, bodyAlphaMultiplier);
        return result;
    }

    /** Apply border modifiers (tint + brighten) to a color. */
    public int applyBorder(int argb) {
        int result = ColorUtil.tintRgb(argb, borderTintR, borderTintG, borderTintB);
        if (borderBrighten > 0) result = ColorUtil.brighten(result, borderBrighten);
        if (borderAlphaMultiplier != 1f) result = ColorUtil.multiplyAlpha(result, borderAlphaMultiplier);
        return result;
    }

    /**
     * Compose two modifier sets. Tints multiply, brightness adds, first sweep wins.
     */
    public static EffectModifiers compose(EffectModifiers a, EffectModifiers b) {
        if (a.isIdentity()) return b;
        if (b.isIdentity()) return a;

        Builder builder = new Builder();
        builder.bodyTintR = a.bodyTintR * b.bodyTintR;
        builder.bodyTintG = a.bodyTintG * b.bodyTintG;
        builder.bodyTintB = a.bodyTintB * b.bodyTintB;
        builder.bodyAlphaMultiplier = a.bodyAlphaMultiplier * b.bodyAlphaMultiplier;
        builder.bodyBrighten = Math.min(255, a.bodyBrighten + b.bodyBrighten);

        builder.borderTintR = a.borderTintR * b.borderTintR;
        builder.borderTintG = a.borderTintG * b.borderTintG;
        builder.borderTintB = a.borderTintB * b.borderTintB;
        builder.borderAlphaMultiplier = a.borderAlphaMultiplier * b.borderAlphaMultiplier;
        builder.borderBrighten = Math.min(255, a.borderBrighten + b.borderBrighten);

        builder.texTintR = a.texTintR * b.texTintR;
        builder.texTintG = a.texTintG * b.texTintG;
        builder.texTintB = a.texTintB * b.texTintB;

        // First sweep wins
        if (a.sweepPosition >= 0) {
            builder.sweepPosition = a.sweepPosition;
            builder.sweepWidth = a.sweepWidth;
            builder.sweepIntensity = a.sweepIntensity;
        } else {
            builder.sweepPosition = b.sweepPosition;
            builder.sweepWidth = b.sweepWidth;
            builder.sweepIntensity = b.sweepIntensity;
        }

        return builder.build();
    }

    public static class Builder {
        float bodyTintR = 1f, bodyTintG = 1f, bodyTintB = 1f;
        float bodyAlphaMultiplier = 1f;
        int bodyBrighten = 0;
        float borderTintR = 1f, borderTintG = 1f, borderTintB = 1f;
        float borderAlphaMultiplier = 1f;
        int borderBrighten = 0;
        float texTintR = 1f, texTintG = 1f, texTintB = 1f;
        float sweepPosition = -1f;
        float sweepWidth = 0f;
        float sweepIntensity = 0f;

        public Builder bodyTint(float r, float g, float b) {
            this.bodyTintR = r; this.bodyTintG = g; this.bodyTintB = b; return this;
        }

        public Builder bodyAlpha(float multiplier) {
            this.bodyAlphaMultiplier = multiplier; return this;
        }

        public Builder bodyBrighten(int amount) {
            this.bodyBrighten = Math.max(0, Math.min(255, amount)); return this;
        }

        public Builder borderTint(float r, float g, float b) {
            this.borderTintR = r; this.borderTintG = g; this.borderTintB = b; return this;
        }

        public Builder borderAlpha(float multiplier) {
            this.borderAlphaMultiplier = multiplier; return this;
        }

        public Builder borderBrighten(int amount) {
            this.borderBrighten = Math.max(0, Math.min(255, amount)); return this;
        }

        public Builder texTint(float r, float g, float b) {
            this.texTintR = r; this.texTintG = g; this.texTintB = b; return this;
        }

        public Builder sweep(float position, float width, float intensity) {
            this.sweepPosition = position;
            this.sweepWidth = width;
            this.sweepIntensity = intensity;
            return this;
        }

        public EffectModifiers build() {
            return new EffectModifiers(this);
        }
    }
}
