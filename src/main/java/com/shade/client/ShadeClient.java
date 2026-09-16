// language: Java, file: src/main/java/com/shade/client/ShadeClient.java
package com.shade.client;

import com.shade.config.ConfigManager;
import com.shade.gui.ClickGuiOpener;
import com.shade.module.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ShadeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModuleManager mm = ModuleManager.INSTANCE;
        mm.register(new ESP());
        mm.register(new XRay());
        mm.register(new SilentAim());
        mm.register(new Reach());
        mm.register(new KillAura());
        mm.register(new Velocity());

        ConfigManager.load();
        ClickGuiOpener.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            mm.tickAll();
        });
    }
}
