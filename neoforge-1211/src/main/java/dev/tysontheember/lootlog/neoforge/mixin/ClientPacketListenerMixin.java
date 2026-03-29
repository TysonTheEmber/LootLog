package dev.tysontheember.lootlog.neoforge.mixin;

import dev.tysontheember.lootlog.neoforge.NeoForgePickupHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger("lootlog/mixin");

    @Inject(method = "handleTakeItemEntity", at = @At("HEAD"))
    private void lootlog$onTakeItem(ClientboundTakeItemEntityPacket packet, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.isSameThread()) return;
        if (mc.player == null || packet.getPlayerId() != mc.player.getId()) return;
        Level level = mc.level;
        if (level == null) return;

        Entity entity = level.getEntity(packet.getItemId());
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem().copy();
            LOGGER.debug("Pickup: packet.amount={}, stack.count={}, item={}",
                    packet.getAmount(), stack.getCount(), stack.getHoverName().getString());
            if (!stack.isEmpty()) {
                NeoForgePickupHandler.onItemPickup(stack, stack.getCount());
            }
        } else if (entity instanceof ExperienceOrb orb) {
            NeoForgePickupHandler.onXpPickup(orb.getValue());
        }
    }
}
