package com.shade.module;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    private final List<Module> modules = new ArrayList<>();

    public void register(Module m) { modules.add(m); }

    public void tickAll() {
        for (Module m : modules) if (m.isEnabled()) m.onTick();
    }

    public List<Module> getModules() { return modules; }

    public Module byName(String name) {
        for (Module m : modules) if (m.getName().equalsIgnoreCase(name)) return m;
        return null;
    }
}
