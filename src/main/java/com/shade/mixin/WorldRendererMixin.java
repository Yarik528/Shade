package com.shade.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.shade.module.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, float tickDelta, long limitTime,
                          boolean renderBlockOutline, Camera camera,
                          GameRenderer gameRenderer,
                          LightmapTextureManager lm,
                          Matrix4f proj,
                          CallbackInfo ci) {

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        // ESP
        ESP esp = (ESP) ModuleManager.INSTANCE.byName("ESP");
        if (esp != null && esp.isEnabled()) {
            esp.render(matrices, camera, tickDelta);
        }

        // Tracers
        Tracers tracers = (Tracers) ModuleManager.INSTANCE.byName("Tracers");
        if (tracers != null && tracers.isEnabled()) {
            renderTracers(matrices, camera, tracers);
        }
    }

    private void renderTracers(MatrixStack matrices, Camera camera, Tracers tracers) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Vec3d camPos = camera.getPos();

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        GL11.glLineWidth(1.5f);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);

        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player || !e.isAlive()) continue;

            boolean player = e instanceof PlayerEntity;
            boolean hostile = e instanceof HostileEntity;
            boolean passive = e instanceof PassiveEntity;

            if (player && !tracers.isShowPlayers()) continue;
            if (hostile && !tracers.isShowHostile()) continue;
            if (passive && !tracers.isShowPassive()) continue;
            if (!player && !hostile && !passive) continue;

            float r, g, b;
            if (player) { r = 1f; g = 0.2f; b = 0.2f; }
            else if (hostile) { r = 1f; g = 0.6f; b = 0f; }
            else { r = 0.2f; g = 1f; b = 0.2f; }

            Vec3d targetPos = e.getPos().add(0, e.getHeight() * 0.5, 0)
                .subtract(camPos);

            // линия от низа экрана (0, -0.2, 0) к цели
            buf.vertex(0, -0.2, 0).color(r, g, b, 1f).next();
            buf.vertex(targetPos.x, targetPos.y, targetPos.z)
                .color(r, g, b, 1f).next();
        }

        tess.draw();
        GL11.glLineWidth(1f);
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }
  }
