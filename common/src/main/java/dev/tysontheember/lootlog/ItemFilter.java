package dev.tysontheember.lootlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Filters items from appearing on the HUD based on blacklist/whitelist rules.
 *
 * Rules:
 *   - If both lists are empty, everything is allowed.
 *   - If whitelist is non-empty, only whitelisted items pass.
 *   - Blacklist always takes priority over whitelist.
 *   - Mod filtering uses the namespace portion of the item ID (e.g., "minecraft" from "minecraft:diamond").
 */
public class ItemFilter {

    private Set<String> itemBlacklist = Collections.emptySet();
    private Set<String> itemWhitelist = Collections.emptySet();
    private Set<String> modBlacklist = Collections.emptySet();
    private Set<String> modWhitelist = Collections.emptySet();

    public void setItemBlacklist(List<String> items) {
        this.itemBlacklist = items == null || items.isEmpty()
                ? Collections.emptySet() : new HashSet<>(items);
    }

    public void setItemWhitelist(List<String> items) {
        this.itemWhitelist = items == null || items.isEmpty()
                ? Collections.emptySet() : new HashSet<>(items);
    }

    public void setModBlacklist(List<String> mods) {
        this.modBlacklist = mods == null || mods.isEmpty()
                ? Collections.emptySet() : new HashSet<>(mods);
    }

    public void setModWhitelist(List<String> mods) {
        this.modWhitelist = mods == null || mods.isEmpty()
                ? Collections.emptySet() : new HashSet<>(mods);
    }

    public List<String> getItemBlacklist() { return new ArrayList<>(itemBlacklist); }
    public List<String> getItemWhitelist() { return new ArrayList<>(itemWhitelist); }
    public List<String> getModBlacklist() { return new ArrayList<>(modBlacklist); }
    public List<String> getModWhitelist() { return new ArrayList<>(modWhitelist); }

    /**
     * Check if an item should appear on the HUD.
     * @param itemId the full registry name, e.g. "minecraft:diamond"
     * @return true if the item is allowed
     */
    public boolean isAllowed(String itemId) {
        if (itemId == null || itemId.isEmpty()) return true;

        // Extract mod namespace
        String modId = extractNamespace(itemId);

        // Blacklist always wins
        if (itemBlacklist.contains(itemId)) return false;
        if (modBlacklist.contains(modId)) return false;

        // If whitelists are active, item must be in at least one
        boolean hasItemWhitelist = !itemWhitelist.isEmpty();
        boolean hasModWhitelist = !modWhitelist.isEmpty();

        if (hasItemWhitelist || hasModWhitelist) {
            if (hasItemWhitelist && itemWhitelist.contains(itemId)) return true;
            if (hasModWhitelist && modWhitelist.contains(modId)) return true;
            return false; // not in any whitelist
        }

        return true;
    }

    static String extractNamespace(String itemId) {
        int colonIndex = itemId.indexOf(':');
        return colonIndex > 0 ? itemId.substring(0, colonIndex) : "minecraft";
    }
}
