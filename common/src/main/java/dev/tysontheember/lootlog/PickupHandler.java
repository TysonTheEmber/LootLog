package dev.tysontheember.lootlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Common pickup handler logic. Platform-specific handlers extract
 * MC-version-dependent data and delegate here.
 */
public final class PickupHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PickupHandler.class);
    private static final Set<String> seenRarities = ConcurrentHashMap.newKeySet();

    private PickupHandler() {}

    /**
     * Handle an item pickup event.
     *
     * @param itemStack        the platform ItemStack (opaque Object for common module)
     * @param name             the item's display name
     * @param itemId           the namespaced item ID (e.g., "minecraft:diamond")
     * @param rarityArgb       the rarity color as ARGB
     * @param rarityName       the lowercased rarity name (e.g., "rare")
     * @param count            number of items picked up
     * @param totalInInventory total count of this item in the player's inventory
     * @param tags             set of tag strings for this item (from TagBridge)
     * @param hasCustomName    true if the item has a custom display name (e.g., renamed on anvil)
     */
    public static void onItemPickup(Object itemStack, String name, String itemId,
                                     int rarityArgb, String rarityName,
                                     int count, int totalInInventory,
                                     Set<String> tags, boolean hasCustomName) {
        if (seenRarities.add(rarityName)) {
            LOGGER.debug("New rarity encountered: '{}' (from item {})", rarityName, itemId);
        }

        // Resolve per-item override
        ResolvedOverride override = OverrideResolver.resolve(
                itemId, tags, rarityName, LootLog.getOverrideRegistry());

        // Apply forceShow bypass before filter check
        boolean forceShow = override != null && override.isForceShow();
        if (!forceShow && !LootLog.getFilter().isAllowed(itemId)) {
            return;
        }

        // Apply name overrides
        String displayName = name;
        if (override != null) {
            if (override.getFullName() != null) {
                displayName = override.getFullName();
            } else {
                if (override.getNamePrefix() != null) {
                    displayName = override.getNamePrefix() + displayName;
                }
                if (override.getNameSuffix() != null) {
                    displayName = displayName + override.getNameSuffix();
                }
            }
        }

        PickupEntry entry = PickupEntry.builder(displayName, count, PickupType.ITEM)
                .itemStack(itemStack)
                .itemId(itemId)
                .totalCount(totalInInventory)
                .rarityColor(rarityArgb)
                .hasCustomName(hasCustomName)
                .override(override)
                .build();
        LootLog.getTracker().addEntry(entry);
        playSound(PickupType.ITEM, override);
    }

    /**
     * Handle an XP orb pickup event.
     *
     * @param xpValue   the XP value of the orb picked up
     * @param totalXp   the player's total XP points after pickup
     */
    public static void onXpPickup(int xpValue, int totalXp) {
        PickupEntry entry = PickupEntry.builder("Experience", xpValue, PickupType.XP)
                .totalCount(totalXp)
                .rarityColor(0xFF55FF55)
                .build();
        LootLog.getTracker().addEntry(entry);
        playSound(PickupType.XP, null);
    }

    private static void playSound(PickupType type, ResolvedOverride override) {
        LootLogConfig config = LootLog.getConfig();
        SoundBridge bridge = LootLog.getSoundBridge();
        if (bridge == null) return;

        // Override sound takes priority
        if (override != null && override.getSoundId() != null) {
            float vol = override.getSoundVolume() != null
                    ? override.getSoundVolume() : config.getSoundVolume();
            float pitch = override.getSoundPitch() != null
                    ? override.getSoundPitch() : config.getSoundPitch();
            bridge.playSound(override.getSoundId(), vol, pitch);
            return;
        }

        // Fall back to global config
        if (config.isSoundEnabled()) {
            bridge.playSound(config.getSoundId(), config.getSoundVolume(), config.getSoundPitch());
        }
    }
}
