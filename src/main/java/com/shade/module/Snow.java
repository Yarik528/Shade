package com.shade.module;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class Snow extends Module {
    public Snow() { super("Snow"); }

    private final Random rng = new Random();
    private int radius = 4;
    private int density = 6;

    public int getRadius() { return radius; }
    public int getDensity() { return density; }
    public void setRadius(int v) { radius = Math.max(1, Math.min(10, v)); }
    public void setDensity(int v) { density = Math.max(1, Math.min(30, v)); }

    @Override
    public void onTick() {
        if (!isEnabled() || mc.player == null || mc.world == null) return;

        ClientPlayerEntity p = mc.player;
        Vec3d pos = p.getPos();

        for (int i = 0; i < density; i++) {
            double ox = (rng.nextDouble() - 0.5) * radius * 2;
            double oy = rng.nextDouble() * 3 + 1;
            double oz = (rng.nextDouble() - 0.5) * radius * 2;

            mc.world.addParticle(
                ParticleTypes.SNOWFLAKE,
                pos.x + ox,
                pos.y + oy,
                pos.z + oz,
                0, -0.02, 0
            );
        }
    }
}
