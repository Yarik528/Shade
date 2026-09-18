package com.shade.alt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SessionHelper {

    public static void setSession(String username) {
        try {
            UUID uuid = UUID.nameUUIDFromBytes(
                ("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8)
            );

            Session newSession = new Session(
                username,
                uuid.toString().replace("-", ""),
                "0",
                "mojang"
            );

            MinecraftClient mc = MinecraftClient.getInstance();
            Field field = MinecraftClient.class.getDeclaredField("session");
            field.setAccessible(true);
            field.set(mc, newSession);

            System.out.println("[Shade] session set to " + username);
        } catch (Exception e) {
            System.err.println("[Shade] session set failed: " + e.getMessage());
        }
    }
        }
