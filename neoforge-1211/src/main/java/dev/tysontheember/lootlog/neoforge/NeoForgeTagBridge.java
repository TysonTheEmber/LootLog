package dev.tysontheember.lootlog.neoforge;

import dev.tysontheember.lootlog.TagBridge;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NeoForgeTagBridge implements TagBridge {

    @Override
    public Set<String> getItemTags(Object itemStack) {
        if (!(itemStack instanceof ItemStack stack)) return Collections.emptySet();
        Set<String> tags = new HashSet<>();
        stack.getTags().forEach(tagKey -> tags.add(tagKey.location().toString()));
        return tags;
    }
}
