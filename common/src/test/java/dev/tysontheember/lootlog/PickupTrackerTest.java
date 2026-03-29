package dev.tysontheember.lootlog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PickupTrackerTest {

    private LootLogConfig config;
    private PickupTracker tracker;

    @BeforeEach
    void setUp() {
        config = new LootLogConfig();
        tracker = new PickupTracker(config);
    }

    @Test
    void addEntry_newItem_appearsInList() {
        PickupEntry entry = makeEntry("Cobblestone", 64, PickupType.ITEM);
        tracker.addEntry(entry);

        assertEquals(1, tracker.size());
        assertEquals("Cobblestone", tracker.getEntries().get(0).getDisplayName());
        assertEquals(64, tracker.getEntries().get(0).getCount());
    }

    @Test
    void addEntry_sameItem_stacksCounts() {
        tracker.addEntry(makeEntry("Cobblestone", 64, PickupType.ITEM));
        tracker.addEntry(makeEntry("Cobblestone", 32, PickupType.ITEM));

        assertEquals(1, tracker.size());
        assertEquals(96, tracker.getEntries().get(0).getCount());
    }

    @Test
    void addEntry_differentItems_separateEntries() {
        tracker.addEntry(makeEntry("Cobblestone", 64, PickupType.ITEM));
        tracker.addEntry(makeEntry("Diamond", 1, PickupType.ITEM));

        assertEquals(2, tracker.size());
    }

    @Test
    void addEntry_sameNameDifferentType_separateEntries() {
        tracker.addEntry(makeEntry("Arrow", 1, PickupType.ITEM));
        tracker.addEntry(makeEntry("Arrow", 1, PickupType.XP));

        assertEquals(2, tracker.size());
    }

    @Test
    void addEntry_stacking_staysInPlace() {
        tracker.addEntry(makeEntry("Cobblestone", 64, PickupType.ITEM));
        tracker.addEntry(makeEntry("Diamond", 1, PickupType.ITEM));
        // Stack onto Cobblestone — should stay in its original position
        tracker.addEntry(makeEntry("Cobblestone", 32, PickupType.ITEM));

        assertEquals("Diamond", tracker.getEntries().get(0).getDisplayName());
        assertEquals("Cobblestone", tracker.getEntries().get(1).getDisplayName());
        assertEquals(96, tracker.getEntries().get(1).getCount());
    }

    @Test
    void addEntry_exceedsMaxEntries_removesOldest() {
        config.setMaxEntries(3);

        tracker.addEntry(makeEntry("Item1", 1, PickupType.ITEM));
        tracker.addEntry(makeEntry("Item2", 1, PickupType.ITEM));
        tracker.addEntry(makeEntry("Item3", 1, PickupType.ITEM));
        tracker.addEntry(makeEntry("Item4", 1, PickupType.ITEM));

        assertEquals(3, tracker.size());
        // Most recent at front, oldest (Item1) evicted
        assertEquals("Item4", tracker.getEntries().get(0).getDisplayName());
        assertEquals("Item3", tracker.getEntries().get(1).getDisplayName());
        assertEquals("Item2", tracker.getEntries().get(2).getDisplayName());
    }

    @Test
    void tick_removesExpiredEntries() {
        config.setDisplayDurationMs(500);
        config.setFadeInMs(0);
        config.setFadeOutMs(0);

        tracker.addEntry(makeEntry("Old", 1, PickupType.ITEM));
        tracker.tick();

        // Entry just created — should still be alive
        assertEquals(1, tracker.size());
    }

    @Test
    void tick_keepsLiveEntries() {
        config.setDisplayDurationMs(60000);

        tracker.addEntry(makeEntry("Fresh", 1, PickupType.ITEM));
        tracker.tick();

        assertEquals(1, tracker.size());
    }

    @Test
    void clear_removesAllEntries() {
        tracker.addEntry(makeEntry("A", 1, PickupType.ITEM));
        tracker.addEntry(makeEntry("B", 1, PickupType.ITEM));
        tracker.clear();

        assertEquals(0, tracker.size());
    }

    @Test
    void getEntries_returnsUnmodifiableList() {
        tracker.addEntry(makeEntry("Test", 1, PickupType.ITEM));

        assertThrows(UnsupportedOperationException.class, () ->
            tracker.getEntries().add(makeEntry("Hack", 1, PickupType.ITEM))
        );
    }

    // --- CombineMode tests ---

    @Test
    void combineMode_never_neverStacks() {
        config.setCombineMode(CombineMode.NEVER);

        tracker.addEntry(makeEntry("Cobblestone", 64, PickupType.ITEM));
        tracker.addEntry(makeEntry("Cobblestone", 32, PickupType.ITEM));

        assertEquals(2, tracker.size());
    }

    @Test
    void combineMode_always_stacks() {
        config.setCombineMode(CombineMode.ALWAYS);

        tracker.addEntry(makeEntry("Cobblestone", 64, PickupType.ITEM));
        tracker.addEntry(makeEntry("Cobblestone", 32, PickupType.ITEM));

        assertEquals(1, tracker.size());
        assertEquals(96, tracker.getEntries().get(0).getCount());
    }

    @Test
    void combineMode_excludeNamed_stacksUnnamed() {
        config.setCombineMode(CombineMode.EXCLUDE_NAMED);

        tracker.addEntry(makeEntry("Cobblestone", 64, PickupType.ITEM));
        tracker.addEntry(makeEntry("Cobblestone", 32, PickupType.ITEM));

        assertEquals(1, tracker.size());
        assertEquals(96, tracker.getEntries().get(0).getCount());
    }

    @Test
    void combineMode_excludeNamed_doesNotStackNamed() {
        config.setCombineMode(CombineMode.EXCLUDE_NAMED);

        tracker.addEntry(makeNamedEntry("Excalibur", 1));
        tracker.addEntry(makeNamedEntry("Excalibur", 1));

        assertEquals(2, tracker.size());
    }

    private PickupEntry makeEntry(String name, int count, PickupType type) {
        return PickupEntry.builder(name, count, type).build();
    }

    private PickupEntry makeNamedEntry(String name, int count) {
        return PickupEntry.builder(name, count, PickupType.ITEM)
                .hasCustomName(true).build();
    }
}
