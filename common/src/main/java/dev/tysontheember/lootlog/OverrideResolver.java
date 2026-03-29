package dev.tysontheember.lootlog;

import java.util.List;
import java.util.Set;

/**
 * Resolves a {@link ResolvedOverride} for a given item by merging matching rules
 * from the {@link OverrideRegistry} using the priority hierarchy:
 * <p>
 * mod namespace (lowest) &lt; rarity &lt; regex &lt; tag &lt; exact item ID (highest)
 */
public final class OverrideResolver {

    private OverrideResolver() {}

    /**
     * Resolve overrides for an item (without rarity context).
     */
    public static ResolvedOverride resolve(String itemId, Set<String> tags,
                                            OverrideRegistry registry) {
        return resolve(itemId, tags, null, registry);
    }

    /**
     * Resolve overrides for an item.
     *
     * @param itemId     full registry name (e.g., "minecraft:diamond")
     * @param tags       set of tag strings this item belongs to (e.g., {"c:ores"})
     * @param rarityName lowercased rarity name (e.g., "rare"), or null if unavailable
     * @param registry   the loaded override registry
     * @return resolved override, or null if no overrides matched
     */
    public static ResolvedOverride resolve(String itemId, Set<String> tags,
                                            String rarityName, OverrideRegistry registry) {
        if (registry == null || registry.isEmpty()) {
            return null;
        }

        String namespace = ItemFilter.extractNamespace(itemId);

        ItemOverride modMatch = registry.getModOverride(namespace);
        ItemOverride rarityMatch = (rarityName != null) ? registry.getRarityOverride(rarityName) : null;
        List<ItemOverride> regexMatches = registry.getMatchingRegexOverrides(itemId);
        List<ItemOverride> tagMatches = registry.getMatchingTagOverrides(tags);
        ItemOverride itemMatch = registry.getItemOverride(itemId);

        if (modMatch == null && rarityMatch == null && regexMatches.isEmpty()
                && tagMatches.isEmpty() && itemMatch == null) {
            return null;
        }

        ResolvedOverride.Builder builder = new ResolvedOverride.Builder();

        // Layer 1: mod namespace (lowest priority)
        if (modMatch != null) {
            builder.mergeFrom(modMatch);
        }

        // Layer 2: rarity name
        if (rarityMatch != null) {
            builder.mergeFrom(rarityMatch);
        }

        // Layer 3: regex patterns (sorted by priority ascending -- last wins per field)
        for (ItemOverride regexOverride : regexMatches) {
            builder.mergeFrom(regexOverride);
        }

        // Layer 4: tags (sorted by priority ascending -- last wins per field)
        for (ItemOverride tagOverride : tagMatches) {
            builder.mergeFrom(tagOverride);
        }

        // Layer 5: exact item ID (highest priority)
        if (itemMatch != null) {
            builder.mergeFrom(itemMatch);
        }

        return builder.build();
    }
}
