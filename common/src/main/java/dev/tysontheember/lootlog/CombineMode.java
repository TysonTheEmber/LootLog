package dev.tysontheember.lootlog;

/**
 * Controls how duplicate item pickups are stacked in the HUD.
 */
public enum CombineMode {
    /** Always merge identical items into a single entry. */
    ALWAYS,
    /** Never merge — each pickup creates a separate entry. */
    NEVER,
    /** Merge unnamed items, but keep custom-named items as separate entries. */
    EXCLUDE_NAMED
}
