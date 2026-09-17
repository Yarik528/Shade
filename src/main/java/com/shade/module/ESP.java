package com.shade.module;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class ESP extends Module {
    public ESP() { super("ESP"); }

    public void render(MatrixStack matrices, Camera camera, float tickDelta) {
        if (!isEnabled() || mc.world == null) return;

        Vec3d camPos = camera.getPos();
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        GL11.glLineWidth(2.0f);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);

        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player || !e.isAlive()) continue;
            boolean hostile = e instanceof HostileEntity;
            boolean player = e instanceof PlayerEntity;
            if (!hostile && !player) continue;

            Box box = e.getBoundingBox().offset(-camPos.x, -camPos.y, -camPos.z);
            float r = player ? 0f : 1f;
            float g = player ? 1f : 0f;
            float b = 0f, a = 1f;

            double[] xs = {box.minX, box.maxX};
            double[] ys = {box.minY, box.maxY};
            double[] zs = {box.minZ, box.maxZ};

            int[][] edges = {
                {0,0,0, 1,0,0},{0,0,1, 1,0,1},{0,1,0, 1,1,0},{0,1,1, 1,1,1},
                {0,0,0, 0,0,1},{1,0,0, 1,0,1},{0,1,0, 0,1,1},{1,1,0, 1,1,1},
                {0,0,0, 0,1,0},{1,0,0, 1,1,0},{0,0,1, 0,1,1},{1,0,1, 1,1,1}
            };
            for (int[] ed : edges) {
                buf.vertex(xs[ed[0]], ys[ed[1]], zs[ed[2]]).color(r,g,b,a).next();
                buf.vertex(xs[ed[3]], ys[ed[4]], zs[ed[5]]).color(r,g,b,a).next();
            }
        }

        tess.draw();
        GL11.glLineWidth(1.0f);
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }
            }
