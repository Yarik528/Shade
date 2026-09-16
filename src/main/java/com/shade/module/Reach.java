// language: Java, file: src/main/java/com/shade/module/Reach.java
package com.shade.module;

public class Reach extends Module {
    public Reach() { super("Reach"); }

    private double blockReach = 4.5;
    private double entityReach = 4.5;

    public double getBlockReachRaw() { return blockReach; }
    public double getEntityReachRaw() { return entityReach; }

    public double getBlockReach() { return isEnabled() ? blockReach : 3.0; }
    public double getEntityReach() { return isEnabled() ? entityReach : 3.0; }

    public void setBlockReach(double v) { blockReach = Math.max(3.0, Math.min(6.0, v)); }
    public void setEntityReach(double v) { entityReach = Math.max(3.0, Math.min(6.0, v)); }
}
