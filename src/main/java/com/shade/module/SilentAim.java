// language: Java, file: src/main/java/com/shade/module/SilentAim.java
package com.shade.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.Vec3d;

public class SilentAim extends Module {
    public SilentAim() { super("SilentAim"); }

    private Entity target;
    private float silentYaw;
    private float silentPitch;

    private boolean gcdSnap = true;
    private boolean microJitter = true;

    public Entity getTarget() { return target; }
    public float getSilentYaw() { return silentYaw; }
    public float getSilentPitch() { return silentPitch; }

    public boolean isGcdSnap() { return gcdSnap; }
    public boolean isMicroJitter() { return microJitter; }
    public void setGcdSnap(boolean v) { gcdSnap = v; }
    public void setMicroJitter(boolean v) { microJitter = v; }

    public void updateSilentAngles() {
        if (!isEnabled() || mc.player == null || mc.world == null) return;

        KillAura aura = (KillAura) ModuleManager.INSTANCE.byName("KillAura");
        if (aura != null && aura.isEnabled()) {
            target = aura.getCurrentTarget();
        } else {
            target = findTarget();
        }

        if (target == null) {
            silentYaw = mc.player.yaw;
            silentPitch = mc.player.pitch;
            return;
        }

        Vec3d eyes = mc.player.getCameraPosVec(1.0f);
        Vec3d targetPos = target.getPos().add(0, target.getHeight() * 0.9, 0);
        Vec3d diff = targetPos.subtract(eyes);

        double dx = diff.x, dy = diff.y, dz = diff.z;
        double dist = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float)(-Math.toDegrees(Math.atan2(dy, dist)));

        if (gcdSnap) {
            float sens = mc.options.mouseSensitivity * 0.6f + 0.2f;
            float gcd = sens * sens * sens * 1.2f;
            yaw = snapGcd(yaw, gcd);
            pitch = snapGcd(pitch, gcd);
        }

        if (microJitter) {
            yaw += (float)((Math.random() - 0.5) * 0.05);
            pitch += (float)((Math.random() - 0.5) * 0.05);
        }

        while (yaw > 180f) yaw -= 360f;
        while (yaw < -180f) yaw += 360f;

        silentYaw = yaw;
        silentPitch = pitch;
    }

    private static float snapGcd(float angle, float gcd) {
        if (gcd <= 0.0f) return angle;
        return Math.round(angle / gcd) * gcd;
    }

    private Entity findTarget() {
        Entity best = null;
        double bestScore = Double.MAX_VALUE;
        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player || !(e instanceof LivingEntity) || !e.isAlive()) continue;
            boolean hostile = e instanceof HostileEntity;
            boolean player = e instanceof PlayerEntity;
            if (!hostile && !player) continue;

            double d = mc.player.squaredDistanceTo(e);
            if (d > 64 * 64) continue;
            if (!mc.player.canSee(e)) continue;

            Vec3d eyes = mc.player.getCameraPosVec(1.0f);
            Vec3d to = e.getPos().add(0, e.getHeight() * 0.5, 0).subtract(eyes).normalize();
            Vec3d look = mc.player.getRotationVec(1.0f);
            double angleScore = 1.0 - to.dotProduct(look);

            if (angleScore < bestScore) {
                bestScore = angleScore;
                best = e;
            }
        }
        return best;
    }
                            }
