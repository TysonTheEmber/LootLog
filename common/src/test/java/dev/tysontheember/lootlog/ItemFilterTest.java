package dev.tysontheember.lootlog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ItemFilterTest {

    private ItemFilter filter;

    @BeforeEach
    void setUp() {
        filter = new ItemFilter();
    }

    // --- No filters = everything allowed ---

    @Test
    void noFilters_allowsEverything() {
        assertTrue(filter.isAllowed("minecraft:diamond"));
        assertTrue(filter.isAllowed("modname:custom_item"));
    }

    @Test
    void nullOrEmpty_allowed() {
        assertTrue(filter.isAllowed(null));
        assertTrue(filter.isAllowed(""));
    }

    // --- Item blacklist ---

    @Test
    void itemBlacklist_blocksSpecificItem() {
        filter.setItemBlacklist(Arrays.asList("minecraft:cobblestone", "minecraft:dirt"));

        assertFalse(filter.isAllowed("minecraft:cobblestone"));
        assertFalse(filter.isAllowed("minecraft:dirt"));
        assertTrue(filter.isAllowed("minecraft:diamond"));
    }

    // --- Mod blacklist ---

    @Test
    void modBlacklist_blocksEntireMod() {
        filter.setModBlacklist(Collections.singletonList("create"));

        assertFalse(filter.isAllowed("create:brass_ingot"));
        assertFalse(filter.isAllowed("create:andesite_alloy"));
        assertTrue(filter.isAllowed("minecraft:diamond"));
    }

    // --- Item whitelist ---

    @Test
    void itemWhitelist_onlyAllowsListed() {
        filter.setItemWhitelist(Arrays.asList("minecraft:diamond", "minecraft:emerald"));

        assertTrue(filter.isAllowed("minecraft:diamond"));
        assertTrue(filter.isAllowed("minecraft:emerald"));
        assertFalse(filter.isAllowed("minecraft:cobblestone"));
    }

    // --- Mod whitelist ---

    @Test
    void modWhitelist_onlyAllowsModItems() {
        filter.setModWhitelist(Collections.singletonList("minecraft"));

        assertTrue(filter.isAllowed("minecraft:diamond"));
        assertFalse(filter.isAllowed("create:brass_ingot"));
    }

    // --- Blacklist overrides whitelist ---

    @Test
    void blacklistOverridesWhitelist() {
        filter.setItemWhitelist(Arrays.asList("minecraft:diamond", "minecraft:cobblestone"));
        filter.setItemBlacklist(Collections.singletonList("minecraft:cobblestone"));

        assertTrue(filter.isAllowed("minecraft:diamond"));
        assertFalse(filter.isAllowed("minecraft:cobblestone")); // blacklisted despite whitelist
    }

    @Test
    void modBlacklistOverridesModWhitelist() {
        filter.setModWhitelist(Arrays.asList("minecraft", "create"));
        filter.setModBlacklist(Collections.singletonList("create"));

        assertTrue(filter.isAllowed("minecraft:diamond"));
        assertFalse(filter.isAllowed("create:brass_ingot"));
    }

    // --- extractNamespace ---

    @Test
    void extractNamespace_standard() {
        assertEquals("minecraft", ItemFilter.extractNamespace("minecraft:diamond"));
        assertEquals("create", ItemFilter.extractNamespace("create:brass_ingot"));
    }

    @Test
    void extractNamespace_noColon_defaultsToMinecraft() {
        assertEquals("minecraft", ItemFilter.extractNamespace("diamond"));
    }

    // --- Integration with PickupTracker ---
    // Note: filtering is handled by PickupHandler before entries reach the tracker.
    // PickupTracker.addEntry() no longer performs filter checks.

    @Test
    void tracker_acceptsAllEntries_filteringIsUpstream() {
        LootLogConfig config = new LootLogConfig();
        ItemFilter trackerFilter = new ItemFilter();
        trackerFilter.setItemBlacklist(Collections.singletonList("minecraft:cobblestone"));
        PickupTracker tracker = new PickupTracker(config, trackerFilter);

        // Tracker does not filter — that's PickupHandler's job
        tracker.addEntry(PickupEntry.builder("Cobblestone", 64, PickupType.ITEM)
                .itemId("minecraft:cobblestone").build());
        assertEquals(1, tracker.size());

        tracker.addEntry(PickupEntry.builder("Diamond", 1, PickupType.ITEM)
                .itemId("minecraft:diamond").build());
        assertEquals(2, tracker.size());
    }
}
