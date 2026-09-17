package com.shade.module;

public class Reach extends Module {
    public Reach() { super("Reach"); }

    private double blockReach = 4.5;
    private double entityReach = 4.5;

    public double getBlockReach() { return isEnabled() ? blockReach : 3.0; }
    public double getEntityReach() { return isEnabled() ? entityReach : 3.0; }
    public double getBlockReachRaw() { return blockReach; }
    public double getEntityReachRaw() { return entityReach; }

    public void setBlockReach(double v) { blockReach = Math.min(v, 6.0); }
    public void setEntityReach(double v) { entityReach = Math.min(v, 6.0); }
}
