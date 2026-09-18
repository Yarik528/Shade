package com.shade.module;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Trail extends Module {
    public Trail() { super("Trail"); }

    private final List<Vec3d> history = new ArrayList<>();
    private int maxLength = 20;
    private int density = 2;
    private float red = 0.5f;
    private float green = 0.2f;
    private float blue = 1.0f;

    public int getMaxLength() { return maxLength; }
    public int getDensity() { return density; }
    public float getRed() { return red; }
    public float getGreen() { return green; }
    public float getBlue() { return blue; }

    public void setMaxLength(int v) { maxLength = Math.max(5, Math.min(50, v)); }
    public void setDensity(int v) { density = Math.max(1, Math.min(10, v)); }
    public void setRed(float v) { red = Math.max(0f, Math.min(1f, v)); }
    public void setGreen(float v) { green = Math.max(0f, Math.min(1f, v)); }
    public void setBlue(float v) { blue = Math.max(0f, Math.min(1f, v)); }

    @Override
    public void onDisable() {
        history.clear();
    }

    @Override
    public void onTick() {
        if (!isEnabled() || mc.player == null || mc.world == null) {
            history.clear();
            return;
        }

        ClientPlayerEntity p = mc.player;
        Vec3d pos = p.getPos().add(0, 0.1, 0);

        // если игрок двигается — записываем позицию
        Vec3d last = history.isEmpty() ? null : history.get(history.size() - 1);
        if (last == null || last.squaredDistanceTo(pos) > 0.01) {
            history.add(pos);
            while (history.size() > maxLength) history.remove(0);
        }

        // спавним частицы в каждой точке истории
        for (int i = 0; i < history.size(); i++) {
            Vec3d v = history.get(i);
            // градиент от старой к свежей
            float t = i / (float) history.size();
            float r = red * t;
            float g = green * t;
            float b = blue * t;

            // плотность — сколько раз спавнить в этой точке
            for (int d = 0; d < density; d++) {
                mc.world.addParticle(
                    ParticleTypes.END_ROD,
                    v.x + (Math.random() - 0.5) * 0.2,
                    v.y + (Math.random() - 0.5) * 0.2,
                    v.z + (Math.random() - 0.5) * 0.2,
                    0, 0.01, 0
                );
            }
        }
    }
            }
