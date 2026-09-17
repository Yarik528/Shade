package com.shade.mixin;

import com.shade.module.XRay;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockRenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRenderManager.class)
public class BlockRenderManagerMixin {
    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private static void onShouldDrawSide(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!XRay.shouldRender(state.getBlock())) {
            cir.setReturnValue(false);
        }
    }
}
