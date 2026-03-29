package dev.tysontheember.lootlog;

/**
 * Represents a single pickup event displayed on the HUD.
 * The itemStack field is typed as Object to avoid Minecraft class dependencies
 * in the common module — platform code stores real ItemStack instances and
 * RenderBridge implementations cast back when rendering.
 *
 * Use {@link #builder(String, int, PickupType)} to construct instances.
 */
public class PickupEntry {

    private final Object itemStack;
    private final String displayName;
    private final String itemId;
    private final PickupType type;
    private final int rarityColor;
    private final boolean hasCustomName;
    private final ResolvedOverride override;
    private int count;
    private int totalCount;
    private long createdAtMs;

    // Per-entry render state for smooth vertical transitions
    private float verticalOffset;
    private int lastRenderedIndex = -1;
    private long bounceStartMs;

    PickupEntry(Object itemStack, String displayName, String itemId, int count, int totalCount,
                PickupType type, int rarityColor, boolean hasCustomName, ResolvedOverride override) {
        this.itemStack = itemStack;
        this.displayName = displayName;
        this.itemId = itemId;
        this.count = count;
        this.totalCount = totalCount;
        this.type = type;
        this.rarityColor = rarityColor;
        this.hasCustomName = hasCustomName;
        this.override = override;
        this.createdAtMs = System.currentTimeMillis();
        this.bounceStartMs = this.createdAtMs;
    }

    public static Builder builder(String displayName, int count, PickupType type) {
        return new Builder(displayName, count, type);
    }

    public Object getItemStack() {
        return itemStack;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getCount() {
        return count;
    }

    public void addCount(int amount) {
        this.count += amount;
        this.bounceStartMs = System.currentTimeMillis();
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getItemId() {
        return itemId;
    }

    public PickupType getType() {
        return type;
    }

    public int getRarityColor() {
        return rarityColor;
    }

    public boolean hasCustomName() {
        return hasCustomName;
    }

    public ResolvedOverride getOverride() {
        return override;
    }

    public long getCreatedAtMs() {
        return createdAtMs;
    }

    /**
     * Refresh the timestamp so the entry restarts at the hold phase,
     * skipping fade-in to avoid flicker when stacking.
     */
    public void refreshTimestamp(long fadeInMs) {
        this.createdAtMs = System.currentTimeMillis() - fadeInMs;
    }

    /**
     * Returns true if this entry represents the same logical pickup
     * (same display name and type), meaning counts should be stacked.
     */
    public boolean matches(String otherName, PickupType otherType) {
        return this.type == otherType && this.displayName.equals(otherName);
    }

    public long getBounceStartMs() {
        return bounceStartMs;
    }

    public float getVerticalOffset() {
        return verticalOffset;
    }

    public void setVerticalOffset(float verticalOffset) {
        this.verticalOffset = verticalOffset;
    }

    public int getLastRenderedIndex() {
        return lastRenderedIndex;
    }

    public void setLastRenderedIndex(int lastRenderedIndex) {
        this.lastRenderedIndex = lastRenderedIndex;
    }

    public static class Builder {
        private final String displayName;
        private final int count;
        private final PickupType type;
        private Object itemStack;
        private String itemId = "";
        private int totalCount;
        private int rarityColor = 0xFFFFFFFF;
        private boolean hasCustomName;
        private ResolvedOverride override;

        Builder(String displayName, int count, PickupType type) {
            this.displayName = displayName;
            this.count = count;
            this.type = type;
            this.totalCount = count;
        }

        public Builder itemStack(Object itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        public Builder itemId(String itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder totalCount(int totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public Builder rarityColor(int rarityColor) {
            this.rarityColor = rarityColor;
            return this;
        }

        public Builder hasCustomName(boolean hasCustomName) {
            this.hasCustomName = hasCustomName;
            return this;
        }

        public Builder override(ResolvedOverride override) {
            this.override = override;
            return this;
        }

        public PickupEntry build() {
            return new PickupEntry(itemStack, displayName, itemId, count, totalCount,
                    type, rarityColor, hasCustomName, override);
        }
    }
}
