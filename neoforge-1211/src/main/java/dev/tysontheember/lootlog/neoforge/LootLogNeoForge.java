package dev.tysontheember.lootlog.neoforge;

import dev.tysontheember.lootlog.LootLog;
import dev.tysontheember.lootlog.OverrideFileLoader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Mod(LootLog.MOD_ID)
public class LootLogNeoForge {

    public LootLogNeoForge(IEventBus modBus, ModContainer container) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            container.registerConfig(ModConfig.Type.CLIENT, NeoForgeConfig.SPEC);
            modBus.register(NeoForgeConfig.class);
            modBus.addListener(NeoForgeHudRenderer::registerLayer);
            LootLog.setRenderBridge(new NeoForgeHudRenderer());
            LootLog.setSoundBridge(new NeoForgeSoundBridge());
            LootLog.setTagBridge(new NeoForgeTagBridge());
            LootLog.setTextEffectBridge(detectTextEffectBridge());

            loadOverrides();
            NeoForgeClientCommands.register();

            // Config screen (YACL, optional)
            container.registerExtensionPoint(IConfigScreenFactory.class,
                    (modContainer, parent) -> {
                        try {
                            Class.forName("dev.isxander.yacl3.api.YetAnotherConfigLib");
                            return dev.tysontheember.lootlog.config.LootLogConfigScreen.create(
                                    parent, NeoForgeConfig::saveFromPojo);
                        } catch (ClassNotFoundException e) {
                            return parent;
                        }
                    });

            // Reload overrides and evict texture cache on F3+T
            modBus.addListener((RegisterClientReloadListenersEvent event) -> {
                event.registerReloadListener((PreparableReloadListener) (preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2) ->
                        preparationBarrier.wait(null).thenRunAsync(() -> {
                            LootLog.syncDevResources();
                            releaseTextures();
                            loadOverrides();
                        }, executor2));
            });
        }
    }

    private static void releaseTextures() {
        var textureManager = Minecraft.getInstance().getTextureManager();
        for (String path : LootLog.getTextureResourcePaths()) {
            String[] parts = path.split(":", 2);
            if (parts.length == 2) {
                textureManager.release(ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]));
            }
        }
    }

    public static void loadOverrides() {
        Path overridesDir = FMLPaths.CONFIGDIR.get()
                .resolve("lootlog").resolve("overrides");
        LootLog.getOverrideRegistry().load(OverrideFileLoader.loadAll(overridesDir));
    }

    private static dev.tysontheember.lootlog.TextEffectBridge detectTextEffectBridge() {
        try {
            Class.forName("net.tysontheember.emberstextapi.immersivemessages.effects.EffectRegistry");
            return new EmbersTextEffectBridge();
        } catch (ClassNotFoundException e) {
            return new dev.tysontheember.lootlog.DefaultTextEffectBridge();
        }
    }
}
