package com.shade.module;

public class NoRender extends Module {
    public NoRender() { super("NoRender"); }

    private boolean fire = true;
    private boolean water = true;
    private boolean portal = true;
    private boolean blindness = true;

    public boolean isFire() { return fire; }
    public boolean isWater() { return water; }
    public boolean isPortal() { return portal; }
    public boolean isBlindness() { return blindness; }

    public void setFire(boolean v) { fire = v; }
    public void setWater(boolean v) { water = v; }
    public void setPortal(boolean v) { portal = v; }
    public void setBlindness(boolean v) { blindness = v; }
}
