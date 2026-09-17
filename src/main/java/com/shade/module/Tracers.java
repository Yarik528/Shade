package com.shade.module;

public class Tracers extends Module {
    public Tracers() { super("Tracers"); }

    private boolean showPlayers = true;
    private boolean showHostile = true;
    private boolean showPassive = false;

    public boolean isShowPlayers() { return showPlayers; }
    public boolean isShowHostile() { return showHostile; }
    public boolean isShowPassive() { return showPassive; }

    public void setShowPlayers(boolean v) { showPlayers = v; }
    public void setShowHostile(boolean v) { showHostile = v; }
    public void setShowPassive(boolean v) { showPassive = v; }
}
