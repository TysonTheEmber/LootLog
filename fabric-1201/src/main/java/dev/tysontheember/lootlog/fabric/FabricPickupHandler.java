package dev.tysontheember.lootlog.fabric;

import dev.tysontheember.lootlog.LootLog;
import dev.tysontheember.lootlog.PickupHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;

public final class FabricPickupHandler {

    private FabricPickupHandler() {}

    public static void onItemPickup(ItemStack stack, int count) {
        String name = resolveDisplayName(stack);
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        int color = rarityToArgb(stack.getRarity());
        String rarityName = stack.getRarity().name().toLowerCase(Locale.ROOT);
        int total = countInInventory(stack) + count;
        Set<String> tags = LootLog.getTagBridge() != null
                ? LootLog.getTagBridge().getItemTags(stack)
                : Collections.emptySet();
        boolean hasCustomName = stack.hasCustomHoverName();
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
            if (!invStack.isEmpty() && ItemStack.isSameItemSameTags(invStack, target)) {
                total += invStack.getCount();
            }
        }
        return total;
    }

    private static String resolveDisplayName(ItemStack stack) {
        if (stack.is(Items.ENCHANTED_BOOK)) {
            ListTag enchTags = EnchantedBookItem.getEnchantments(stack);
            if (!enchTags.isEmpty()) {
                StringJoiner joiner = new StringJoiner(", ");
                for (int i = 0; i < enchTags.size(); i++) {
                    CompoundTag tag = enchTags.getCompound(i);
                    ResourceLocation id = ResourceLocation.tryParse(tag.getString("id"));
                    int lvl = tag.getInt("lvl");
                    if (id != null) {
                        Enchantment ench = BuiltInRegistries.ENCHANTMENT.get(id);
                        if (ench != null) {
                            joiner.add(ench.getFullname(lvl).getString());
                        }
                    }
                }
                String result = joiner.toString();
                if (!result.isEmpty()) return result;
            }
        }
        return stack.getHoverName().getString();
    }

    private static int rarityToArgb(Rarity rarity) {
        ChatFormatting formatting = rarity.color;
        Integer rgb = formatting.getColor();
        if (rgb == null) return 0xFFFFFFFF;
        return rgb | 0xFF000000;
    }
}
