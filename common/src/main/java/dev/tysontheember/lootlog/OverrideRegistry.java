package dev.tysontheember.lootlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Stores loaded override rules indexed for efficient lookup.
 * Thread-safe: reads happen on render thread, writes on reload via volatile swap.
 */
public class OverrideRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(OverrideRegistry.class);

    private volatile Map<String, ItemOverride> itemOverrides = Collections.emptyMap();
    private volatile List<ItemOverride> tagOverrides = Collections.emptyList();
    private volatile Map<String, ItemOverride> modOverrides = Collections.emptyMap();
    private volatile Map<String, ItemOverride> rarityOverrides = Collections.emptyMap();
    private volatile List<RegexEntry> regexOverrides = Collections.emptyList();

    private static class RegexEntry {
        final Pattern pattern;
        final ItemOverride override;

        RegexEntry(Pattern pattern, ItemOverride override) {
            this.pattern = pattern;
            this.override = override;
        }
    }

    /**
     * Replace all overrides atomically from a flat list of parsed rules.
     * Tag and regex overrides are sorted by priority ascending so higher-priority
     * rules are applied last (and win per-field in the merge).
     */
    public void load(List<ItemOverride> allOverrides) {
        Map<String, ItemOverride> items = new HashMap<>();
        List<ItemOverride> tags = new ArrayList<>();
        Map<String, ItemOverride> mods = new HashMap<>();
        Map<String, ItemOverride> rarities = new HashMap<>();
        List<RegexEntry> regexes = new ArrayList<>();

        for (ItemOverride o : allOverrides) {
            if (o.getMatch() == null || o.getMatch().getType() == null
                    || o.getMatch().getId() == null) {
                continue;
            }
            String id = o.getMatch().getId();
            switch (o.getMatch().getType()) {
                case "item":
                    if (items.containsKey(id)) {
                        LOGGER.warn("Duplicate item override for '{}', last one wins", id);
                    }
                    items.put(id, o);
                    break;
                case "tag":
                    tags.add(o);
                    break;
                case "mod":
                    if (mods.containsKey(id)) {
                        LOGGER.warn("Duplicate mod override for '{}', last one wins", id);
                    }
                    mods.put(id, o);
                    break;
                case "rarity":
                    if (rarities.containsKey(id)) {
                        LOGGER.warn("Duplicate rarity override for '{}', last one wins", id);
                    }
                    rarities.put(id, o);
                    break;
                case "regex":
                    try {
                        Pattern p = Pattern.compile(id);
                        regexes.add(new RegexEntry(p, o));
                    } catch (PatternSyntaxException e) {
                        LOGGER.warn("Invalid regex pattern '{}', skipping override: {}", id, e.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }

        Comparator<ItemOverride> byPriority = Comparator.comparingInt(o -> {
            if (o.getBehavior() != null && o.getBehavior().getPriority() != null) {
                return o.getBehavior().getPriority();
            }
            return 0;
        });

        tags.sort(byPriority);
        regexes.sort(Comparator.comparingInt(e -> {
            ItemOverride o = e.override;
            if (o.getBehavior() != null && o.getBehavior().getPriority() != null) {
                return o.getBehavior().getPriority();
            }
            return 0;
        }));

        if (!rarities.isEmpty()) {
            LOGGER.debug("Rarity overrides registered for: {}", rarities.keySet());
        }

        this.itemOverrides = Collections.unmodifiableMap(items);
        this.tagOverrides = Collections.unmodifiableList(tags);
        this.modOverrides = Collections.unmodifiableMap(mods);
        this.rarityOverrides = Collections.unmodifiableMap(rarities);
        this.regexOverrides = Collections.unmodifiableList(regexes);
    }

    public void clear() {
        this.itemOverrides = Collections.emptyMap();
        this.tagOverrides = Collections.emptyList();
        this.modOverrides = Collections.emptyMap();
        this.rarityOverrides = Collections.emptyMap();
        this.regexOverrides = Collections.emptyList();
    }

    public ItemOverride getItemOverride(String itemId) {
        return itemOverrides.get(itemId);
    }

    /**
     * Return all tag overrides whose tag ID is present in the given set.
     * Returned in priority-sorted order (ascending).
     */
    public List<ItemOverride> getMatchingTagOverrides(Set<String> tags) {
        if (tags == null || tags.isEmpty() || tagOverrides.isEmpty()) {
            return Collections.emptyList();
        }
        List<ItemOverride> result = new ArrayList<>();
        for (ItemOverride o : tagOverrides) {
            if (tags.contains(o.getMatch().getId())) {
                result.add(o);
            }
        }
        return result;
    }

    public ItemOverride getModOverride(String namespace) {
        return modOverrides.get(namespace);
    }

    public ItemOverride getRarityOverride(String rarityName) {
        return rarityOverrides.get(rarityName);
    }

    /**
     * Return all regex overrides whose pattern matches the given item ID.
     * Returned in priority-sorted order (ascending).
     */
    public List<ItemOverride> getMatchingRegexOverrides(String itemId) {
        if (itemId == null || regexOverrides.isEmpty()) {
            return Collections.emptyList();
        }
        List<ItemOverride> result = new ArrayList<>();
        for (RegexEntry entry : regexOverrides) {
            if (entry.pattern.matcher(itemId).matches()) {
                result.add(entry.override);
            }
        }
        return result;
    }

    public int size() {
        return itemOverrides.size() + tagOverrides.size() + modOverrides.size()
                + rarityOverrides.size() + regexOverrides.size();
    }

    public boolean isEmpty() {
        return itemOverrides.isEmpty() && tagOverrides.isEmpty() && modOverrides.isEmpty()
                && rarityOverrides.isEmpty() && regexOverrides.isEmpty();
    }
}
