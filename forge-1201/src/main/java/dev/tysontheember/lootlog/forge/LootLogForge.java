package dev.tysontheember.lootlog.forge;

import dev.tysontheember.lootlog.LootLog;
import dev.tysontheember.lootlog.OverrideFileLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mod(LootLog.MOD_ID)
public class LootLogForge {

    public LootLogForge() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

            // Register config
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeConfig.SPEC);
            modBus.register(ForgeConfig.class);

            // Register HUD overlay
            modBus.addListener(ForgeHudRenderer::registerOverlay);

            // Set bridges
            LootLog.setRenderBridge(new ForgeHudRenderer());
            LootLog.setSoundBridge(new ForgeSoundBridge());
            LootLog.setTagBridge(new ForgeTagBridge());
            LootLog.setTextEffectBridge(detectTextEffectBridge());

            loadOverrides();
            ForgeClientCommands.register();

            // Config screen (YACL, optional)
            ModLoadingContext.get().registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> {
                        try {
                            Class.forName("dev.isxander.yacl3.api.YetAnotherConfigLib");
                            return dev.tysontheember.lootlog.config.LootLogConfigScreen.create(
                                    parent, ForgeConfigSaver::save);
                        } catch (ClassNotFoundException e) {
                            return null;
                        }
                    })
            );

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
                textureManager.release(new ResourceLocation(parts[0], parts[1]));
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
