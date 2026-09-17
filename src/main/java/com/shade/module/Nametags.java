package com.shade.module;

public class Nametags extends Module {
    public Nametags() { super("Nametags"); }

    private boolean showHealth = true;
    private boolean showDistance = true;
    private boolean showArmor = false;

    public boolean isShowHealth() { return showHealth; }
    public boolean isShowDistance() { return showDistance; }
    public boolean isShowArmor() { return showArmor; }

    public void setShowHealth(boolean v) { showHealth = v; }
    public void setShowDistance(boolean v) { showDistance = v; }
    public void setShowArmor(boolean v) { showArmor = v; }
}
