package com.shade.module;

import net.minecraft.client.network.ClientPlayerEntity;

public class Sprint extends Module {
    public Sprint() { super("Sprint"); }

    @Override
    public void onTick() {
        if (!isEnabled() || mc.player == null) return;

        ClientPlayerEntity player = mc.player;

        boolean movingForward = mc.options.keyForward.isPressed();
        boolean canSprint = !player.isSneaking()
                         && !player.isUsingItem()
                         && player.getHungerManager().getFoodLevel() > 6;

        if (movingForward && canSprint && !player.isSprinting()) {
            player.setSprinting(true);
        }
    }
}
