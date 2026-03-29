package dev.tysontheember.lootlog;

import java.util.Set;

/**
 * Abstraction for querying item tags.
 * Platform implementations use Minecraft's tag registry.
 */
public interface TagBridge {

    /**
     * Get all tags that apply to the given item.
     *
     * @param itemStack opaque platform ItemStack
     * @return set of tag strings (e.g., {"minecraft:planks", "c:ores"})
     */
    Set<String> getItemTags(Object itemStack);
}
