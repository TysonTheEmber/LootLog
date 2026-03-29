package dev.tysontheember.lootlog.forge;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Registers client-side commands for the mod.
 */
public final class ForgeClientCommands {

    private ForgeClientCommands() {}

    public static void register() {
        MinecraftForge.EVENT_BUS.register(ForgeClientCommands.class);
    }

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("lootlog")
                .then(Commands.literal("reload")
                        .executes(ctx -> {
                            LootLogForge.loadOverrides();
                            ctx.getSource().sendSuccess(
                                    () -> Component.literal("Loot Log overrides reloaded."), false);
                            return 1;
                        })));
    }
}
