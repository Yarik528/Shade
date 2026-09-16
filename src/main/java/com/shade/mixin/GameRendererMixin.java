// language: Java, file: src/main/java/com/shade/mixin/GameRendererMixin.java
package com.shade.mixin;

import com.shade.module.*;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Redirect(method = "updateTargetedEntity",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/network/ClientPlayerEntity;getEntityInteractionRange()D"))
    private double reachEntity(net.minecraft.client.network.ClientPlayerEntity player) {
        Reach r = (Reach) ModuleManager.INSTANCE.byName("Reach");
        return r != null ? r.getEntityReach() : player.getEntityInteractionRange();
    }

    @Redirect(method = "updateTargetedEntity",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/network/ClientPlayerEntity;getBlockInteractionRange()D"))
    private double reachBlock(net.minecraft.client.network.ClientPlayerEntity player) {
        Reach r = (Reach) ModuleManager.INSTANCE.byName("Reach");
        return r != null ? r.getBlockReach() : player.getBlockInteractionRange();
    }
}
