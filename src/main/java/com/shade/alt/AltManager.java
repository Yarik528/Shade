package com.shade.alt;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AltManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
        FabricLoader.getInstance().getConfigDir().resolve("shade_alts.json");

    private static final List<AltEntry> alts = new ArrayList<>();
    private static AltEntry current = null;

    public static List<AltEntry> getAlts() { return alts; }
    public static AltEntry getCurrent() { return current; }

    public static void addAlt(String username) {
        if (username == null || username.trim().isEmpty()) return;
        username = username.trim();
        // максимум 16 символов (ограничение Minecraft)
        if (username.length() > 16) username = username.substring(0, 16);

        for (AltEntry e : alts) {
            if (e.getUsername().equalsIgnoreCase(username)) return;
        }
        alts.add(new AltEntry(username));
        save();
    }

    public static void removeAlt(AltEntry entry) {
        alts.remove(entry);
        save();
    }

    public static void login(AltEntry entry) {
        if (entry == null) return;
        current = entry;
        SessionHelper.setSession(entry.getUsername());
        save();
    }

    public static void login(String username) {
        addAlt(username);
        for (AltEntry e : alts) {
            if (e.getUsername().equalsIgnoreCase(username)) {
                login(e);
                return;
            }
        }
    }

    public static void save() {
        JsonObject root = new JsonObject();
        JsonArray arr = new JsonArray();
        for (AltEntry e : alts) {
            JsonObject o = new JsonObject();
            o.addProperty("username", e.getUsername());
            arr.add(o);
        }
        root.add("alts", arr);
        if (current != null) root.addProperty("current", current.getUsername());

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.write(CONFIG_PATH, GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("[Shade] alts save failed: " + e.getMessage());
        }
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) return;
        try {
            String raw = new String(Files.readAllBytes(CONFIG_PATH), StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(raw).getAsJsonObject();

            if (root.has("alts")) {
                JsonArray arr = root.getAsJsonArray("alts");
                for (JsonElement el : arr) {
                    JsonObject o = el.getAsJsonObject();
                    if (o.has("username")) {
                        alts.add(new AltEntry(o.get("username").getAsString()));
                    }
                }
            }

            if (root.has("current")) {
                String cur = root.get("current").getAsString();
                for (AltEntry e : alts) {
                    if (e.getUsername().equals(cur)) { current = e; break; }
                }
            }
        } catch (Exception e) {
            System.err.println("[Shade] alts load failed: " + e.getMessage());
        }
    }
          }
