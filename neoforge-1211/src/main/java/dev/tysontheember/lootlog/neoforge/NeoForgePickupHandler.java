package dev.tysontheember.lootlog.neoforge;

import dev.tysontheember.lootlog.LootLog;
import dev.tysontheember.lootlog.PickupHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;

public final class NeoForgePickupHandler {

    private NeoForgePickupHandler() {}

    public static void onItemPickup(ItemStack stack, int count) {
        String name = resolveDisplayName(stack);
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        int color = rarityToArgb(stack.getRarity());
        String rarityName = stack.getRarity().name().toLowerCase(Locale.ROOT);
        int total = countInInventory(stack) + count;
        Set<String> tags = LootLog.getTagBridge() != null
                ? LootLog.getTagBridge().getItemTags(stack)
                : Collections.emptySet();
        boolean hasCustomName = stack.has(DataComponents.CUSTOM_NAME);
        PickupHandler.onItemPickup(stack, name, itemId, color, rarityName, count, total, tags, hasCustomName);
    }

    public static void onXpPickup(int xpValue) {
        Minecraft mc = Minecraft.getInstance();
        int totalXp = mc.player != null ? mc.player.totalExperience + xpValue : xpValue;
        PickupHandler.onXpPickup(xpValue, totalXp);
    }

    private static int countInInventory(ItemStack target) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return 0;
        int total = 0;
        for (ItemStack invStack : mc.player.getInventory().items) {
            if (!invStack.isEmpty() && ItemStack.isSameItemSameComponents(invStack, target)) {
                total += invStack.getCount();
            }
        }
        return total;
    }

    private static String resolveDisplayName(ItemStack stack) {
        if (stack.is(Items.ENCHANTED_BOOK)) {
            ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
            if (stored != null && stored.size() > 0) {
                StringJoiner joiner = new StringJoiner(", ");
                stored.entrySet().forEach(entry ->
                        joiner.add(Enchantment.getFullname(entry.getKey(), entry.getIntValue()).getString()));
                return joiner.toString();
            }
        }
        return stack.getHoverName().getString();
    }

    private static int rarityToArgb(Rarity rarity) {
        ChatFormatting formatting = rarity.color();
        Integer rgb = formatting.getColor();
        if (rgb == null) return 0xFFFFFFFF;
        return rgb | 0xFF000000;
    }
}
