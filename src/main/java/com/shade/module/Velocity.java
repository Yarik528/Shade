// language: Java, file: src/main/java/com/shade/module/Velocity.java
package com.shade.module;

public class Velocity extends Module {
    public Velocity() { super("Velocity"); }

    private double horizontal = 0.8;
    private double vertical = 0.9;
    private boolean antiDetect = true;

    public double getHorizontalRaw() { return horizontal; }
    public double getVerticalRaw() { return vertical; }
    public boolean isAntiDetect() { return antiDetect; }

    public double getHorizontal() {
        if (antiDetect && horizontal < 0.7) return 0.7;
        return horizontal;
    }

    public double getVertical() {
        if (antiDetect && vertical < 0.8) return 0.8;
        return vertical;
    }

    public void setHorizontal(double v) { horizontal = Math.max(0.0, Math.min(1.0, v)); }
    public void setVertical(double v) { vertical = Math.max(0.0, Math.min(1.0, v)); }
    public void setAntiDetect(boolean v) { antiDetect = v; }
}
