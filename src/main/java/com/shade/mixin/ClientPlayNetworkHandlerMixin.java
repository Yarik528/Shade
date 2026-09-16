// language: Java, file: src/main/java/com/shade/mixin/ClientPlayNetworkHandlerMixin.java
package com.shade.mixin;

import com.shade.module.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
    private void onVelocity(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        Velocity v = (Velocity) ModuleManager.INSTANCE.byName("Velocity");
        if (v == null || !v.isEnabled()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || packet.getId() != mc.player.getEntityId()) return;

        if (v.getHorizontal() == 0.0 && v.getVertical() == 0.0) {
            ci.cancel();
            return;
        }

        double vx = packet.getVelocityX() * v.getHorizontal();
        double vy = packet.getVelocityY() * v.getVertical();
        double vz = packet.getVelocityZ() * v.getHorizontal();

        mc.player.setVelocity(vx, vy, vz);
        ci.cancel();
    }
}
