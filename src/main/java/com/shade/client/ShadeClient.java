package com.shade.client;

import net.fabricmc.api.ClientModInitializer;

public class ShadeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("[Shade] loaded");
    }
}
