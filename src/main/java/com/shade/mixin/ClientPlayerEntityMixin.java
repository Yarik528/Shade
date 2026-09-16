// language: Java, file: src/main/java/com/shade/mixin/ClientPlayerEntityMixin.java
package com.shade.mixin;

import com.shade.module.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Redirect(method = "sendMovementPackets",
              at = @At(value = "NEW",
                       target = "(DDDFFZ)Lnet/minecraft/network/packet/c2s/play/PlayerMoveC2SPacket$LookAndOnGround;"))
    private PlayerMoveC2SPacket.LookAndOnGround silentLook(
            double x, double y, double z, float yaw, float pitch, boolean onGround) {

        SilentAim aim = (SilentAim) ModuleManager.INSTANCE.byName("SilentAim");
        if (aim != null && aim.isEnabled()) {
            aim.updateSilentAngles();
            return new PlayerMoveC2SPacket.LookAndOnGround(
                aim.getSilentYaw(), aim.getSilentPitch(), onGround);
        }
        return new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, onGround);
    }
}
