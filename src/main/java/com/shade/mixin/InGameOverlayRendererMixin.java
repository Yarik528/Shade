package com.shade.mixin;

import com.shade.module.ModuleManager;
import com.shade.module.NoRender;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {

    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void onFire(MatrixStack matrices, CallbackInfo ci) {
        NoRender nr = (NoRender) ModuleManager.INSTANCE.byName("NoRender");
        if (nr != null && nr.isEnabled() && nr.isFire()) ci.cancel();
    }

    @Inject(method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true)
    private static void onWater(MatrixStack matrices, CallbackInfo ci) {
        NoRender nr = (NoRender) ModuleManager.INSTANCE.byName("NoRender");
        if (nr != null && nr.isEnabled() && nr.isWater()) ci.cancel();
    }
          }
