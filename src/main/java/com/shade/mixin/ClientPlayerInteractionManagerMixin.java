// language: Java, file: src/main/java/com/shade/mixin/ClientPlayerInteractionManagerMixin.java
package com.shade.mixin;

import com.shade.module.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void onAttack(PlayerEntity player, Entity target, CallbackInfo ci) {
        Reach r = (Reach) ModuleManager.INSTANCE.byName("Reach");
        if (r == null || !r.isEnabled() || !(player instanceof ClientPlayerEntity)) return;

        ClientPlayerEntity cp = (ClientPlayerEntity) player;
        Vec3d eyes = cp.getCameraPosVec(1.0f);
        Vec3d targetCenter = target.getPos().add(0, target.getHeight() * 0.5, 0);
        double dist = eyes.distanceTo(targetCenter);
        if (dist <= 3.0) return;

        Vec3d dir = targetCenter.subtract(eyes).normalize();
        Vec3d fakePos = targetCenter.subtract(dir.multiply(2.9));
        cp.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
            fakePos.x, fakePos.y, fakePos.z, cp.isOnGround()));
    }

    @Inject(method = "attackEntity", at = @At("TAIL"))
    private void afterAttack(PlayerEntity player, Entity target, CallbackInfo ci) {
        Reach r = (Reach) ModuleManager.INSTANCE.byName("Reach");
        if (r == null || !r.isEnabled() || !(player instanceof ClientPlayerEntity)) return;

        ClientPlayerEntity cp = (ClientPlayerEntity) player;
        cp.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
            cp.getX(), cp.getY(), cp.getZ(), cp.isOnGround()));
    }
                                      }
