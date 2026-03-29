package dev.tysontheember.lootlog;

/**
 * Named layout presets that produce {@link PopupLayout} instances matching
 * the built-in rendering styles. Each preset captures the exact positioning
 * constants from the original rendering styles.
 */
public enum LayoutPreset {

    /**
     * Classic banner-style layout.
     * Right-anchored: elements extend leftward from the screen edge.
     * Left-anchored: elements extend rightward.
     * Accent bar rendered below the main banner.
     */
    CLASSIC {
        @Override
        public PopupLayout createLayout(TextureSpec tex) {
            int texH = tex.getSourceHeight();
            int entryH = Math.max(texH, PopupLayout.ICON_SIZE);
            int coy = PopupLayout.computeContentOffsetY(texH);
            int textY = PopupLayout.computeTextOffsetY(coy);
            int iconY = coy;

            // Classic places icon/text relative to the entry, not with padding.
            // These offsets are base values; PopupRenderer adjusts for dynamic widths.
            return new PopupLayout.Builder()
                    .icon(new PopupLayout.ElementSlot(0, iconY, true))
                    .itemName(new PopupLayout.ElementSlot(0, textY, true))
                    .pickupCount(new PopupLayout.ElementSlot(0, textY, true))
                    .totalCount(new PopupLayout.ElementSlot(0, textY, true))
                    .entryHeight(entryH)
                    .contentOffsetY(coy)
                    .padding(0)
                    .build();
        }
    },

    /**
     * Standard layout with icon on the right side (from StandardRenderer).
     * Layout: [pad | name | gap | icon | gap | total | pad]
     */
    STANDARD_RIGHT {
        @Override
        public PopupLayout createLayout(TextureSpec tex) {
            int texH = tex.getSourceHeight();
            int entryH = Math.max(texH, PopupLayout.ICON_SIZE);
            int coy = PopupLayout.computeContentOffsetY(texH);
            int textY = PopupLayout.computeTextOffsetY(coy);
            int iconY = coy;

            return new PopupLayout.Builder()
                    .icon(new PopupLayout.ElementSlot(0, iconY, true))
                    .itemName(new PopupLayout.ElementSlot(0, textY, true))
                    .pickupCount(new PopupLayout.ElementSlot(0, textY, true))
                    .totalCount(new PopupLayout.ElementSlot(0, textY, true))
                    .entryHeight(entryH)
                    .contentOffsetY(coy)
                    .padding(4) // default bgHPad
                    .build();
        }
    },

    /**
     * Standard layout with icon on the left side.
     * Layout: [pad | icon | gap | name | pad]
     */
    STANDARD_LEFT {
        @Override
        public PopupLayout createLayout(TextureSpec tex) {
            int texH = tex.getSourceHeight();
            int entryH = Math.max(texH, PopupLayout.ICON_SIZE);
            int coy = PopupLayout.computeContentOffsetY(texH);
            int textY = PopupLayout.computeTextOffsetY(coy);
            int iconY = coy;

            return new PopupLayout.Builder()
                    .icon(new PopupLayout.ElementSlot(0, iconY, true))
                    .itemName(new PopupLayout.ElementSlot(0, textY, true))
                    .pickupCount(new PopupLayout.ElementSlot(0, textY, true))
                    .totalCount(new PopupLayout.ElementSlot(0, textY, false))
                    .entryHeight(entryH)
                    .contentOffsetY(coy)
                    .padding(4)
                    .build();
        }
    };

    /**
     * Create a PopupLayout using this preset's positioning rules,
     * adapting to the given texture dimensions.
     */
    public abstract PopupLayout createLayout(TextureSpec tex);

    /** Case-insensitive lookup by name. Returns CLASSIC for unknown names. */
    public static LayoutPreset byName(String name) {
        if (name == null || name.isEmpty()) return CLASSIC;
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CLASSIC;
        }
    }
}
