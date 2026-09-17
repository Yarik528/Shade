package com.shade.module;

public class Halo extends Module {
    public Halo() { super("Halo"); }

    private float red = 1.0f;
    private float green = 0.9f;
    private float blue = 0.3f;
    private float size = 1.0f;

    public float getRed() { return red; }
    public float getGreen() { return green; }
    public float getBlue() { return blue; }
    public float getSize() { return size; }

    public void setRed(float v) { red = Math.max(0f, Math.min(1f, v)); }
    public void setGreen(float v) { green = Math.max(0f, Math.min(1f, v)); }
    public void setBlue(float v) { blue = Math.max(0f, Math.min(1f, v)); }
    public void setSize(float v) { size = Math.max(0.5f, Math.min(2f, v)); }
}
