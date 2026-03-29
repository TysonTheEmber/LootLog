package dev.tysontheember.lootlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Maintains an ordered list of recent pickup entries.
 * Handles stacking (same item updates count), expiry, and max entry cap.
 *
 * Thread safety: both packet handling and rendering run on the client main
 * thread, so no synchronization is needed.
 */
public class PickupTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(PickupTracker.class);

    private final List<PickupEntry> entries = new ArrayList<>();
    private LootLogConfig config;
    private ItemFilter filter;

    public PickupTracker(LootLogConfig config) {
        this(config, new ItemFilter());
    }

    public PickupTracker(LootLogConfig config, ItemFilter filter) {
        this.config = config;
        this.filter = filter;
    }

    public void setConfig(LootLogConfig config) {
        this.config = config;
    }

    public void setFilter(ItemFilter filter) {
        this.filter = filter;
    }

    /**
     * Add a pickup to the tracker. If an existing entry matches (same name + type),
     * its count is incremented and timestamp refreshed instead of adding a duplicate.
     */
    public void addEntry(PickupEntry entry) {
        // Per-type toggle check
        PickupType type = entry.getType();
        if (type == PickupType.ITEM && !config.isShowItems()) return;
        if (type == PickupType.XP && !config.isShowXp()) return;

        // Check for existing entry to stack with (respecting combine mode)
        if (shouldCombine(entry)) {
            for (PickupEntry existing : entries) {
                if (existing.matches(entry.getDisplayName(), entry.getType())) {
                    LOGGER.debug("Stacking '{}': count {} + {} = {}", entry.getDisplayName(),
                            existing.getCount(), entry.getCount(),
                            existing.getCount() + entry.getCount());
                    existing.addCount(entry.getCount());
                    existing.setTotalCount(entry.getTotalCount());
                    existing.refreshTimestamp(config.getFadeInMs());
                    return;
                }
            }
            LOGGER.debug("No match for '{}' (type={}) among {} entries",
                    entry.getDisplayName(), entry.getType(), entries.size());
        } else {
            LOGGER.debug("Combine disabled for '{}' (mode={})",
                    entry.getDisplayName(), config.getCombineMode());
        }

        // New entry — add to front
        entries.add(0, entry);

        // Enforce max entries by removing oldest
        while (entries.size() > config.getMaxEntries()) {
            entries.remove(entries.size() - 1);
        }
    }

    /** Remove entries that have exceeded their total lifetime. */
    public void tick() {
        long now = System.currentTimeMillis();
        long defaultLifetime = config.getTotalLifetimeMs();
        Iterator<PickupEntry> it = entries.iterator();
        while (it.hasNext()) {
            PickupEntry entry = it.next();
            long lifetime = getEntryLifetime(entry, defaultLifetime);
            if (now - entry.getCreatedAtMs() >= lifetime) {
                it.remove();
            }
        }
    }

    /**
     * Get the total lifetime for an entry, considering override duration.
     * If the override specifies a custom displayDurationMs, the total lifetime
     * is fadeIn + overrideDuration + fadeOut. Otherwise, uses the global default.
     */
    long getEntryLifetime(PickupEntry entry, long defaultLifetime) {
        ResolvedOverride override = entry.getOverride();
        if (override != null && override.getDisplayDurationMs() != null) {
            return config.getFadeInMs() + override.getDisplayDurationMs() + config.getFadeOutMs();
        }
        return defaultLifetime;
    }

    public List<PickupEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public void clear() {
        entries.clear();
    }

    public int size() {
        return entries.size();
    }

    private boolean shouldCombine(PickupEntry entry) {
        // Per-entry override takes priority over global config
        CombineMode mode = config.getCombineMode();
        ResolvedOverride override = entry.getOverride();
        if (override != null && override.getCombineMode() != null) {
            mode = override.getCombineMode();
        }
        if (mode == CombineMode.NEVER) return false;
        if (mode == CombineMode.EXCLUDE_NAMED && entry.hasCustomName()) return false;
        return true;
    }
}
