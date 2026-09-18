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
            } else if (m instanceof NoRender) {
                NoRender nr = (NoRender) m;
                mod.addProperty("fire", nr.isFire());
                mod.addProperty("water", nr.isWater());
                mod.addProperty("portal", nr.isPortal());
            } else if (m instanceof Tracers) {
                Tracers t = (Tracers) m;
                mod.addProperty("players", t.isShowPlayers());
                mod.addProperty("hostile", t.isShowHostile());
                mod.addProperty("passive", t.isShowPassive());
            } else if (m instanceof Nametags) {
                Nametags n = (Nametags) m;
                mod.addProperty("health", n.isShowHealth());
                mod.addProperty("distance", n.isShowDistance());
            } else if (m instanceof Snow) {
                Snow s = (Snow) m;
                mod.addProperty("radius", s.getRadius());
                mod.addProperty("density", s.getDensity());
            } else if (m instanceof Halo) {
                Halo h = (Halo) m;
                mod.addProperty("red", h.getRed());
                mod.addProperty("green", h.getGreen());
                mod.addProperty("blue", h.getBlue());
                mod.addProperty("size", h.getSize());
            } else if (m instanceof Trail) {
                Trail t = (Trail) m;
                mod.addProperty("maxLength", t.getMaxLength());
                mod.addProperty("density", t.getDensity());
                mod.addProperty("red", t.getRed());
                mod.addProperty("green", t.getGreen());
                mod.addProperty("blue", t.getBlue());
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
                } else if (m instanceof NoRender && mod.has("fire")) {
                    NoRender nr = (NoRender) m;
                    nr.setFire(mod.get("fire").getAsBoolean());
                    nr.setWater(mod.get("water").getAsBoolean());
                    nr.setPortal(mod.get("portal").getAsBoolean());
                } else if (m instanceof Tracers && mod.has("players")) {
                    Tracers t = (Tracers) m;
                    t.setShowPlayers(mod.get("players").getAsBoolean());
                    t.setShowHostile(mod.get("hostile").getAsBoolean());
                    t.setShowPassive(mod.get("passive").getAsBoolean());
                } else if (m instanceof Nametags && mod.has("health")) {
                    Nametags n = (Nametags) m;
                    n.setShowHealth(mod.get("health").getAsBoolean());
                    n.setShowDistance(mod.get("distance").getAsBoolean());
                } else if (m instanceof Snow && mod.has("radius")) {
                    Snow s = (Snow) m;
                    s.setRadius(mod.get("radius").getAsInt());
                    s.setDensity(mod.get("density").getAsInt());
                } else if (m instanceof Halo && mod.has("red")) {
                    Halo h = (Halo) m;
                    h.setRed(mod.get("red").getAsFloat());
                    h.setGreen(mod.get("green").getAsFloat());
                    h.setBlue(mod.get("blue").getAsFloat());
                    h.setSize(mod.get("size").getAsFloat());
                } else if (m instanceof Trail && mod.has("maxLength")) {
                    Trail t = (Trail) m;
                    t.setMaxLength(mod.get("maxLength").getAsInt());
                    t.setDensity(mod.get("density").getAsInt());
                    t.setRed(mod.get("red").getAsFloat());
                    t.setGreen(mod.get("green").getAsFloat());
                    t.setBlue(mod.get("blue").getAsFloat());
                }
            }
        } catch (Exception e) {
            System.err.println("[Shade] config load failed: " + e.getMessage());
        }
    }
                    }
