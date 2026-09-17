package com.shade.gui;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ClickGuiOpener {

    private static KeyBinding openKey;

    public static void register() {
        openKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.shade.menu",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "Shade"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.openScreen(new ClickGuiScreen());
                } else if (client.currentScreen instanceof ClickGuiScreen) {
                    client.openScreen(null);
                }
            }
        });
    }
}
