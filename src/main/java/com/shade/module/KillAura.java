package com.shade.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class KillAura extends Module {
    public KillAura() { super("KillAura"); }

    private final Random rng = new Random();

    private double minCps = 8.0;
    private double maxCps = 12.0;
    private double fov = 120.0;
    private int priority = 0;
    private boolean requireLos = true;
    private int switchDelayMs = 150;

    private long lastAttack = 0L;
    private long lastTargetSwitch = 0L;
    private Entity currentTarget = null;

    public double getMinCps() { return minCps; }
    public double getMaxCps() { return maxCps; }
    public double getFov() { return fov; }
    public void setMinCps(double v) { minCps = Math.max(1.0, Math.min(20.0, v)); }
    public void setMaxCps(double v) { maxCps = Math.max(minCps, Math.min(20.0, v)); }
    public void setFov(double v) { fov = Math.max(30.0, Math.min(360.0, v)); }
    public Entity getCurrentTarget() { return currentTarget; }

    @Override
    public void onTick() {
        if (!isEnabled() || mc.player == null || mc.world == null) return;
        if (mc.interactionManager == null) return;

        long now = System.currentTimeMillis();
        double cps = minCps + rng.nextDouble() * (maxCps - minCps);
        long interval = (long)(1000.0 / cps);

        Entity target = pickTarget();

        if (target != currentTarget) {
            if (now - lastTargetSwitch < switchDelayMs) return;
            currentTarget = target;
            lastTargetSwitch = now;
        }

        if (target == null) return;
        if (now - lastAttack < interval) return;

        attack(target);
        lastAttack = now;
    }

    private Entity pickTarget() {
        Entity best = null;
        double bestScore = Double.MAX_VALUE;

        Vec3d eyes = mc.player.getCameraPosVec(1.0f);
        Vec3d look = mc.player.getRotationVec(1.0f);
        double fovCos = Math.cos(Math.toRadians(fov / 2.0));

        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player || !(e instanceof LivingEntity) || !e.isAlive()) continue;
            boolean hostile = e instanceof HostileEntity;
            boolean player = e instanceof PlayerEntity;
            if (!hostile && !player) continue;
            if (player && e.isInvisible() && !mc.player.canSee(e)) continue;

            double dist = mc.player.distanceTo(e);
            if (dist > 6.0) continue;
            if (requireLos && !mc.player.canSee(e)) continue;

            Vec3d to = e.getPos().add(0, e.getHeight() * 0.5, 0).subtract(eyes).normalize();
            if (to.dotProduct(look) < fovCos) continue;

            double score;
            switch (priority) {
                case 1: score = dist; break;
                case 2: score = ((LivingEntity)e).getHealth(); break;
                default: score = 1.0 - to.dotProduct(look); break;
            }

            if (score < bestScore) {
                bestScore = score;
                best = e;
            }
        }
        return best;
    }

    private void attack(Entity target) {
        SilentAim aim = (SilentAim) ModuleManager.INSTANCE.byName("SilentAim");
        if (aim != null && aim.isEnabled()) aim.updateSilentAngles();

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }
              }
