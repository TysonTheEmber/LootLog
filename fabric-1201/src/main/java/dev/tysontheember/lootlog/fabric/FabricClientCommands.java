package dev.tysontheember.lootlog.fabric;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.network.chat.Component;

/**
 * Registers client-side commands for the mod.
 */
public final class FabricClientCommands {

    private FabricClientCommands() {}

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("lootlog")
                        .then(ClientCommandManager.literal("reload")
                                .executes(ctx -> {
                                    LootLogFabric.loadOverrides();
                                    ctx.getSource().sendFeedback(
                                            Component.literal("Loot Log overrides reloaded."));
                                    return 1;
                                }))));
    }
}
