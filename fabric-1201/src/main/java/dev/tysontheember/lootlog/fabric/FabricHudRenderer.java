package dev.tysontheember.lootlog.fabric;

import dev.tysontheember.lootlog.HudLayout;
import dev.tysontheember.lootlog.LootLog;
import dev.tysontheember.lootlog.RenderBridge;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

/**
 * Fabric-specific HUD renderer. Registered via HudRenderCallback.
 * In 1.20.1, the callback provides (GuiGraphics, float tickDelta).
 */
public class FabricHudRenderer implements RenderBridge {

    /**
     * Custom render type: uses the entity_cutout shader (correct per-face diffuse
     * lighting with separate lightmap/overlay) but with translucent blending enabled
     * so that setShaderColor alpha produces smooth fade-in/fade-out.
     */
    private static final RenderType ENTITY_CUTOUT_BLEND = Shards.TYPE;

    /** Extends RenderStateShard to access protected shard constants. */
    private static final class Shards extends RenderStateShard {
        private Shards() { super("", () -> {}, () -> {}); }
        private static final RenderType ENTITY_CUTOUT = RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS);
        static final RenderType TYPE = new RenderType(
                "lootlog_entity_cutout_blend",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536, false, false,
                () -> {
                    ENTITY_CUTOUT.setupRenderState();
                    TRANSLUCENT_TRANSPARENCY.setupRenderState();
                },
                () -> {
                    TRANSLUCENT_TRANSPARENCY.clearRenderState();
                    ENTITY_CUTOUT.clearRenderState();
                }
        ) {};
    }

    private final Minecraft mc = Minecraft.getInstance();
    private GuiGraphics graphics;

    public void onHudRender(GuiGraphics guiGraphics, float tickDelta) {
        this.graphics = guiGraphics;
        LootLog.getTracker().tick();
        LootLog.getHudLayout().render(LootLog.getTracker().getEntries(), LootLog.getConfig(), this);
        this.graphics = null;
    }

    @Override
    public void renderRect(int x, int y, int width, int height, int argbColor) {
        graphics.fill(x, y, x + width, y + height, argbColor);
    }

    @Override
    public void renderGradientRect(int x, int y, int width, int height, int colorTop, int colorBottom) {
        graphics.fillGradient(x, y, x + width, y + height, colorTop, colorBottom);
    }

    @Override
    public void renderTexture(String resourcePath, int x, int y, int width, int height,
                              float u, float v, float regionWidth, float regionHeight,
                              int textureWidth, int textureHeight, float alpha) {
        String[] parts = resourcePath.split(":", 2);
        ResourceLocation location = new ResourceLocation(parts[0], parts[1]);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        RenderSystem.enableBlend();
        graphics.blit(location, x, y, width, height, u, v,
                (int) regionWidth, (int) regionHeight, textureWidth, textureHeight);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void renderTintedTexture(String resourcePath, int x, int y, int width, int height,
                                     float u, float v, float regionWidth, float regionHeight,
                                     int textureWidth, int textureHeight, float alpha,
                                     float red, float green, float blue) {
        String[] parts = resourcePath.split(":", 2);
        ResourceLocation location = new ResourceLocation(parts[0], parts[1]);
        RenderSystem.setShaderColor(red, green, blue, alpha);
        RenderSystem.enableBlend();
        graphics.blit(location, x, y, width, height, u, v,
                (int) regionWidth, (int) regionHeight, textureWidth, textureHeight);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void renderItemIcon(Object itemStack, int x, int y, float alpha) {
        if (!(itemStack instanceof ItemStack stack) || stack.isEmpty()) return;

        graphics.flush();

        BakedModel model = mc.getItemRenderer().getModel(stack, mc.level, mc.player, 0);

        graphics.pose().pushPose();
        graphics.pose().translate(x + 8.0f, y + 8.0f, 150.0f);
        // Use mulPoseMatrix for Y-flip + uniform scale to preserve correct normals.
        // In 1.20.1, PoseStack.scale(16,-16,16) corrupts the normal matrix to (-1,1,-1)
        // because it treats non-uniform signs differently from 1.21.1+.
        graphics.pose().mulPoseMatrix(new Matrix4f().scaling(1.0f, -1.0f, 1.0f));
        graphics.pose().scale(16.0f, 16.0f, 16.0f);

        boolean flatLight = !model.usesBlockLight();
        if (flatLight) {
            Lighting.setupForFlatItems();
        } else {
            Lighting.setupFor3DItems();
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        try {
            mc.getItemRenderer().render(stack, ItemDisplayContext.GUI, false, graphics.pose(),
                    renderType -> graphics.bufferSource().getBuffer(
                            renderType == RenderType.glintDirect() ? renderType : ENTITY_CUTOUT_BLEND),
                    15728880, OverlayTexture.NO_OVERLAY, model);
        } catch (Exception e) {
            // Third-party item renderers may use incompatible vertex formats (e.g. Sodium + custom text rendering)
        }
        graphics.flush();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        if (flatLight) {
            Lighting.setupFor3DItems();
        }

        graphics.pose().popPose();
    }

    @Override
    public void renderTintedItemIcon(Object itemStack, int x, int y, float alpha,
                                      float red, float green, float blue) {
        if (!(itemStack instanceof ItemStack stack) || stack.isEmpty()) return;

        graphics.flush();
        BakedModel model = mc.getItemRenderer().getModel(stack, mc.level, mc.player, 0);

        graphics.pose().pushPose();
        graphics.pose().translate(x + 8.0f, y + 8.0f, 150.0f);
        graphics.pose().mulPoseMatrix(new Matrix4f().scaling(1.0f, -1.0f, 1.0f));
        graphics.pose().scale(16.0f, 16.0f, 16.0f);

        boolean flatLight = !model.usesBlockLight();
        if (flatLight) Lighting.setupForFlatItems();
        else Lighting.setupFor3DItems();

        RenderSystem.setShaderColor(red, green, blue, alpha);

        try {
            mc.getItemRenderer().render(stack, ItemDisplayContext.GUI, false, graphics.pose(),
                    renderType -> graphics.bufferSource().getBuffer(
                            renderType == RenderType.glintDirect() ? renderType : ENTITY_CUTOUT_BLEND),
                    15728880, OverlayTexture.NO_OVERLAY, model);
        } catch (Exception e) {
            // Third-party item renderers may use incompatible vertex formats (e.g. Sodium + custom text rendering)
        }
        graphics.flush();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (flatLight) Lighting.setupFor3DItems();
        graphics.pose().popPose();
    }

    @Override
    public void renderText(String text, int x, int y, int argbColor, boolean shadow) {
        graphics.drawString(mc.font, text, x, y, argbColor, shadow);
    }

    @Override
    public int getTextWidth(String text) {
        return mc.font.width(text);
    }

    @Override
    public int getScreenWidth() {
        return mc.getWindow().getGuiScaledWidth();
    }

    @Override
    public int getScreenHeight() {
        return mc.getWindow().getGuiScaledHeight();
    }

    @Override
    public void pushPose() {
        graphics.pose().pushPose();
    }

    @Override
    public void popPose() {
        graphics.pose().popPose();
    }

    @Override
    public void translate(float x, float y, float z) {
        graphics.pose().translate(x, y, z);
    }

    @Override
    public void scale(float x, float y, float z) {
        graphics.pose().scale(x, y, z);
    }

    @Override
    public void rotateZ(float degrees) {
        graphics.pose().mulPose(new org.joml.Quaternionf().rotateZ((float) Math.toRadians(degrees)));
    }
}
