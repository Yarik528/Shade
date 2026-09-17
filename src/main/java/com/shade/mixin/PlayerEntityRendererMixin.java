package com.shade.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.shade.module.Halo;
import com.shade.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(PlayerEntity entity, float yaw, float tickDelta,
                          MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                          int light, CallbackInfo ci) {

        Halo halo = (Halo) ModuleManager.INSTANCE.byName("Halo");
        if (halo == null || !halo.isEnabled()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        // Рисуем только над собой
        if (entity.getUuid() == null || !entity.getUuid().equals(mc.player.getUuid())) return;

        matrices.push();
        matrices.translate(0, entity.getHeight() + 0.3, 0);

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrices.peek().getModel());
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        GL11.glLineWidth(3.0f);

        float r = halo.getRed();
        float g = halo.getGreen();
        float b = halo.getBlue();
        float size = halo.getSize() * 0.4f;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_LINE_LOOP, VertexFormats.POSITION_COLOR);

        int segments = 32;
        for (int i = 0; i < segments; i++) {
            double angle = (i / (double) segments) * Math.PI * 2;
            float x = (float)(Math.cos(angle) * size);
            float z = (float)(Math.sin(angle) * size);
            buf.vertex(x, 0, z).color(r, g, b, 1f).next();
        }

        tess.draw();
        GL11.glLineWidth(1f);
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        matrices.pop();
    }
          }
