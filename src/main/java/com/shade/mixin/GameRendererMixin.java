package com.shade.mixin;

import com.shade.module.ModuleManager;
import com.shade.module.Reach;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Redirect(method = "updateTargetedEntity",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/network/ClientPlayerEntity;getEntityInteractionRange()D"))
    private double reachEntity(ClientPlayerEntity player) {
        Reach r = (Reach) ModuleManager.INSTANCE.byName("Reach");
        return r != null ? r.getEntityReach() : player.getEntityInteractionRange();
    }

    @Redirect(method = "updateTargetedEntity",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/network/ClientPlayerEntity;getBlockInteractionRange()D"))
    private double reachBlock(ClientPlayerEntity player) {
        Reach r = (Reach) ModuleManager.INSTANCE.byName("Reach");
        return r != null ? r.getBlockReach() : player.getBlockInteractionRange();
    }
}
