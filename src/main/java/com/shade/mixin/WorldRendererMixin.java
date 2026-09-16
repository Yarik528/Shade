// language: Java, file: src/main/java/com/shade/mixin/WorldRendererMixin.java
package com.shade.mixin;

import com.shade.module.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, float tickDelta, long limitTime,
                          boolean renderBlockOutline, Camera camera,
                          net.minecraft.client.render.GameRenderer gameRenderer,
                          net.minecraft.client.render.LightmapTextureManager lm,
                          net.minecraft.client.render.Matrix4f proj, CallbackInfo ci) {
        ESP esp = (ESP) ModuleManager.INSTANCE.byName("ESP");
        if (esp != null && esp.isEnabled()) esp.render(matrices, camera, tickDelta);
    }
                          }
