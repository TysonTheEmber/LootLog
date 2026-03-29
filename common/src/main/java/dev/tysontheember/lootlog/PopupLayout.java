package dev.tysontheember.lootlog;

/**
 * Immutable layout descriptor defining how elements are positioned within a popup entry.
 * Elements use offsets from the popup's content origin, which is vertically centered
 * within the background texture height.
 *
 * <p>Element flow for dynamic positioning:
 * <ul>
 *   <li>CLASSIC reversed (right-anchored): origin at right edge, elements extend left:
 *       [pad | name | gap | icon | gap | count | origin]</li>
 *   <li>CLASSIC normal (left-anchored): origin at left edge, elements extend right:
 *       [origin | count | gap | icon | gap | name | pad]</li>
 *   <li>STANDARD icon-right: [pad | name | gap | icon | gap | total | pad]</li>
 *   <li>STANDARD icon-left: [pad | icon | gap | name | pad]</li>
 * </ul>
 */
public final class PopupLayout {

    /** Standard icon size in pixels. */
    public static final int ICON_SIZE = 16;
    /** Gap between icon and adjacent text. */
    public static final int ICON_TEXT_GAP = 4;
    /** Standard font height in pixels. */
    public static final int FONT_HEIGHT = 9;

    private final ElementSlot icon;
    private final ElementSlot itemName;
    private final ElementSlot pickupCount;
    private final ElementSlot totalCount;
    private final int entryHeight;
    private final int contentOffsetY;
    private final int padding;

    private PopupLayout(Builder b) {
        this.icon = b.icon;
        this.itemName = b.itemName;
        this.pickupCount = b.pickupCount;
        this.totalCount = b.totalCount;
        this.entryHeight = b.entryHeight;
        this.contentOffsetY = b.contentOffsetY;
        this.padding = b.padding;
    }

    // --- Getters ---

    public ElementSlot getIcon() { return icon; }
    public ElementSlot getItemName() { return itemName; }
    public ElementSlot getPickupCount() { return pickupCount; }
    public ElementSlot getTotalCount() { return totalCount; }
    public int getEntryHeight() { return entryHeight; }
    public int getContentOffsetY() { return contentOffsetY; }
    public int getPadding() { return padding; }

    /**
     * Compute vertical centering offset for content within a given texture height.
     * Content height is ICON_SIZE (16px). If the texture is shorter, content overflows
     * symmetrically (matching banner behavior with 12px banners).
     */
    public static int computeContentOffsetY(int textureHeight) {
        return Math.max(0, (textureHeight - ICON_SIZE) / 2);
    }

    /**
     * Compute vertical text offset within the content area.
     * Centers 9px font within 16px icon height.
     */
    public static int computeTextOffsetY(int contentOffsetY) {
        return contentOffsetY + (ICON_SIZE - FONT_HEIGHT) / 2;
    }

    /**
     * Create a copy of this layout with element visibility overridden.
     */
    public PopupLayout withVisibility(Boolean iconEnabled, Boolean nameEnabled,
                                      Boolean pickupCountEnabled, Boolean totalCountEnabled) {
        Builder b = new Builder();
        b.icon = iconEnabled != null ? icon.withEnabled(iconEnabled) : icon;
        b.itemName = nameEnabled != null ? itemName.withEnabled(nameEnabled) : itemName;
        b.pickupCount = pickupCountEnabled != null ? pickupCount.withEnabled(pickupCountEnabled) : pickupCount;
        b.totalCount = totalCountEnabled != null ? totalCount.withEnabled(totalCountEnabled) : totalCount;
        b.entryHeight = entryHeight;
        b.contentOffsetY = contentOffsetY;
        b.padding = padding;
        return new PopupLayout(b);
    }

    // --- Element Slot ---

    /**
     * Describes one element's position and visibility within the popup.
     */
    public static final class ElementSlot {
        private final int offsetX;
        private final int offsetY;
        private final boolean enabled;

        public ElementSlot(int offsetX, int offsetY, boolean enabled) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.enabled = enabled;
        }

        public int getOffsetX() { return offsetX; }
        public int getOffsetY() { return offsetY; }
        public boolean isEnabled() { return enabled; }

        public ElementSlot withEnabled(boolean enabled) {
            return new ElementSlot(offsetX, offsetY, enabled);
        }

        public ElementSlot withOffset(int x, int y) {
            return new ElementSlot(x, y, enabled);
        }
    }

    // --- Builder ---

    public static final class Builder {
        private ElementSlot icon = new ElementSlot(0, 0, true);
        private ElementSlot itemName = new ElementSlot(0, 0, true);
        private ElementSlot pickupCount = new ElementSlot(0, 0, true);
        private ElementSlot totalCount = new ElementSlot(0, 0, true);
        private int entryHeight = ICON_SIZE;
        private int contentOffsetY = 0;
        private int padding = 0;

        public Builder icon(ElementSlot s) { this.icon = s; return this; }
        public Builder itemName(ElementSlot s) { this.itemName = s; return this; }
        public Builder pickupCount(ElementSlot s) { this.pickupCount = s; return this; }
        public Builder totalCount(ElementSlot s) { this.totalCount = s; return this; }
        public Builder entryHeight(int h) { this.entryHeight = h; return this; }
        public Builder contentOffsetY(int y) { this.contentOffsetY = y; return this; }
        public Builder padding(int p) { this.padding = p; return this; }

        public PopupLayout build() {
            return new PopupLayout(this);
        }
    }
}
