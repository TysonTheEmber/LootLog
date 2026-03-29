package dev.tysontheember.lootlog;

/** Text formatting and truncation for HUD entries. */
public final class TextFormatter {

    private TextFormatter() {}

    /** Format the left text portion: "[Nx] ItemName" or just "ItemName". */
    public static String formatLeftText(PickupEntry entry, LootLogConfig config) {
        if (config.isShowCount() && entry.getCount() > 1) {
            String count = config.isAbbreviateCounts()
                    ? abbreviateCount(entry.getCount()) : String.valueOf(entry.getCount());
            return count + "x " + entry.getDisplayName();
        }
        return entry.getDisplayName();
    }

    /** Format the right count text showing total inventory amount (e.g., "x64"). */
    public static String formatRightText(PickupEntry entry, LootLogConfig config) {
        String count = config.isAbbreviateCounts()
                ? abbreviateCount(entry.getTotalCount()) : String.valueOf(entry.getTotalCount());
        return "x" + count;
    }

    /** Format just the pickup count (e.g., "x10"). Empty if count is 1 or showCount is off. */
    public static String formatPickupCount(PickupEntry entry, LootLogConfig config) {
        if (!config.isShowCount() || entry.getCount() <= 1) return "";
        String count = config.isAbbreviateCounts()
                ? abbreviateCount(entry.getCount()) : String.valueOf(entry.getCount());
        return "x" + count;
    }

    /** Format just the item name without any count prefix. */
    public static String formatItemName(PickupEntry entry) {
        return entry.getDisplayName();
    }

    /** Format total inventory count without "x" prefix (e.g., "1.5K"). */
    public static String formatTotalCount(PickupEntry entry, LootLogConfig config) {
        return config.isAbbreviateCounts()
                ? abbreviateCount(entry.getTotalCount())
                : String.valueOf(entry.getTotalCount());
    }

    /** Abbreviate large counts: 1500 -> "1.5K", 2500000 -> "2.5M", etc. */
    public static String abbreviateCount(long count) {
        if (count >= 1_000_000_000L) return String.format("%.1fB", count / 1_000_000_000.0);
        if (count >= 1_000_000L) return String.format("%.1fM", count / 1_000_000.0);
        if (count >= 1_000L) return String.format("%.1fK", count / 1_000.0);
        return String.valueOf(count);
    }

    /**
     * Truncate a display name to fit within maxWidth pixels, appending "..." if needed.
     * Returns the name unchanged if maxWidth is 0 (unlimited) or it already fits.
     */
    public static String truncateName(String name, int maxWidth, RenderBridge bridge) {
        if (maxWidth <= 0 || bridge.getTextWidth(name) <= maxWidth) return name;
        String ellipsis = "...";
        int ellipsisWidth = bridge.getTextWidth(ellipsis);
        for (int i = name.length() - 1; i > 0; i--) {
            if (bridge.getTextWidth(name.substring(0, i)) + ellipsisWidth <= maxWidth) {
                return name.substring(0, i) + ellipsis;
            }
        }
        return ellipsis;
    }
}
