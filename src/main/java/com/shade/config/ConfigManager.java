package com.shade.config;

import com.google.gson.*;
import com.shade.module.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
        FabricLoader.getInstance().getConfigDir().resolve("shade.json");

    public static void save() {
        JsonObject root = new JsonObject();
        for (Module m : ModuleManager.INSTANCE.getModules()) {
            JsonObject mod = new JsonObject();
            mod.addProperty("enabled", m.isEnabled());

            if (m instanceof KillAura) {
                KillAura k = (KillAura) m;
                mod.addProperty("minCps", k.getMinCps());
                mod.addProperty("maxCps", k.getMaxCps());
                mod.addProperty("fov", k.getFov());
            } else if (m instanceof Reach) {
                Reach r = (Reach) m;
                mod.addProperty("blockReach", r.getBlockReachRaw());
                mod.addProperty("entityReach", r.getEntityReachRaw());
            } else if (m instanceof SilentAim) {
                SilentAim s = (SilentAim) m;
                mod.addProperty("gcdSnap", s.isGcdSnap());
                mod.addProperty("microJitter", s.isMicroJitter());
            } else if (m instanceof Velocity) {
                Velocity v = (Velocity) m;
                mod.addProperty("horizontal", v.getHorizontalRaw());
                mod.addProperty("vertical", v.getVerticalRaw());
                mod.addProperty("antiDetect", v.isAntiDetect());
            }
            root.add(m.getName(), mod);
        }
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.write(CONFIG_PATH, GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("[Shade] config save failed: " + e.getMessage());
        }
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) return;
        try {
            String raw = new String(Files.readAllBytes(CONFIG_PATH), StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(raw).getAsJsonObject();

            for (Module m : ModuleManager.INSTANCE.getModules()) {
                if (!root.has(m.getName())) continue;
                JsonObject mod = root.getAsJsonObject(m.getName());

                if (mod.has("enabled")) {
                    boolean want = mod.get("enabled").getAsBoolean();
                    if (want != m.isEnabled()) m.toggle();
                }
                if (m instanceof KillAura && mod.has("minCps")) {
                    KillAura k = (KillAura) m;
                    k.setMinCps(mod.get("minCps").getAsDouble());
                    k.setMaxCps(mod.get("maxCps").getAsDouble());
                    k.setFov(mod.get("fov").getAsDouble());
                } else if (m instanceof Reach && mod.has("blockReach")) {
                    Reach r = (Reach) m;
                    r.setBlockReach(mod.get("blockReach").getAsDouble());
                    r.setEntityReach(mod.get("entityReach").getAsDouble());
                } else if (m instanceof SilentAim && mod.has("gcdSnap")) {
                    SilentAim s = (SilentAim) m;
                    s.setGcdSnap(mod.get("gcdSnap").getAsBoolean());
                    s.setMicroJitter(mod.get("microJitter").getAsBoolean());
                } else if (m instanceof Velocity && mod.has("horizontal")) {
                    Velocity v = (Velocity) m;
                    v.setHorizontal(mod.get("horizontal").getAsDouble());
                    v.setVertical(mod.get("vertical").getAsDouble());
                    v.setAntiDetect(mod.get("antiDetect").getAsBoolean());
                }
            }
        } catch (Exception e) {
            System.err.println("[Shade] config load failed: " + e.getMessage());
        }
    }
                                                       }
