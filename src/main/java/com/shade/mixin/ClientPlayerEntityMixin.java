package com.shade.mixin;

import com.shade.module.ModuleManager;
import com.shade.module.SilentAim;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    private int aimTick = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity)(Object)this;
        SilentAim aim = (SilentAim) ModuleManager.INSTANCE.byName("SilentAim");
        if (aim == null || !aim.isEnabled()) return;
        if (player.world == null) return;

        aim.updateSilentAngles();

        if (++aimTick % 2 == 0) {
            player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(
                aim.getSilentYaw(), aim.getSilentPitch(), player.isOnGround()));
        }
    }
}
