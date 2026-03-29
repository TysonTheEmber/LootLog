package dev.tysontheember.lootlog.fabric;

import dev.tysontheember.lootlog.LootLog;
import dev.tysontheember.lootlog.OverrideFileLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

import java.nio.file.Path;

public class LootLogFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricConfig.load();

        FabricHudRenderer renderer = new FabricHudRenderer();
        LootLog.setRenderBridge(renderer);
        LootLog.setSoundBridge(new FabricSoundBridge());
        LootLog.setTagBridge(new FabricTagBridge());
        LootLog.setTextEffectBridge(detectTextEffectBridge());

        loadOverrides();

        FabricClientCommands.register();
        HudRenderCallback.EVENT.register(renderer::onHudRender);

        // Reload overrides on F3+T
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public ResourceLocation getFabricId() {
                        return new ResourceLocation(LootLog.MOD_ID, "override_reloader");
                    }

                    @Override
                    public void onResourceManagerReload(ResourceManager manager) {
                        LootLog.syncDevResources();
                        releaseTextures();
                        loadOverrides();
                    }
                });
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
        Path overridesDir = FabricLoader.getInstance().getConfigDir()
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
