package dev.tysontheember.lootlog.neoforge;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;

/**
 * Registers client-side commands for the mod.
 */
public final class NeoForgeClientCommands {

    private NeoForgeClientCommands() {}

    public static void register() {
        NeoForge.EVENT_BUS.register(NeoForgeClientCommands.class);
    }

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("lootlog")
                .then(Commands.literal("reload")
                        .executes(ctx -> {
                            LootLogNeoForge.loadOverrides();
                            ctx.getSource().sendSuccess(
                                    () -> Component.literal("Loot Log overrides reloaded."), false);
                            return 1;
                        })));
    }
}
