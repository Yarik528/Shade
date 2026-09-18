package com.shade.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Watermark {

    private static final int ACCENT = 0xFF8A2BE2;
    private static final int BG     = 0x90000000;
    private static final int TEXT   = 0xFFFFFFFF;
    private static final int TEXT_DIM = 0xFFB0B0C0;
    private static final int MARGIN = 4;

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    private static Field FPS_FIELD = null;

    private static int getFps() {
        try {
            if (FPS_FIELD == null) {
                FPS_FIELD = MinecraftClient.class.getDeclaredField("currentFps");
                FPS_FIELD.setAccessible(true);
            }
            return FPS_FIELD.getInt(null);
        } catch (Exception e) {
            return 0;
        }
    }

    public static void render(MatrixStack matrices) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;

        TextRenderer tr = mc.textRenderer;

        String brand   = "Shade";
        String version = "v1.1";
        String name    = mc.player.getName().asString();
        String fps     = getFps() + " fps";
        String time    = TIME_FORMAT.format(new Date());

        int brandW   = tr.getWidth(brand);
        int versionW = tr.getWidth(version);
        int nameW    = tr.getWidth(name);
        int fpsW     = tr.getWidth(fps);
        int timeW    = tr.getWidth(time);

        int sepW = tr.getWidth(" | ");
        int totalW = brandW + 4 + versionW + sepW + nameW + sepW + fpsW + sepW + timeW;

        int x = MARGIN;
        int y = MARGIN;

        DrawableHelper.fill(matrices, x - 2, y - 2, x + totalW + 6, y + 10, BG);
        DrawableHelper.fill(matrices, x - 2, y - 2, x - 1, y + 10, ACCENT);

        int cx = x;
        tr.draw(matrices, brand, cx, y, ACCENT);
        cx += brandW + 4;

        tr.draw(matrices, version, cx, y, ACCENT);
        cx += versionW;

        tr.draw(matrices, " | ", cx, y, TEXT_DIM);
        cx += sepW;

        tr.draw(matrices, name, cx, y, TEXT);
        cx += nameW;

        tr.draw(matrices, " | ", cx, y, TEXT_DIM);
        cx += sepW;

        tr.draw(matrices, fps, cx, y, TEXT_DIM);
        cx += fpsW;

        tr.draw(matrices, " | ", cx, y, TEXT_DIM);
        cx += sepW;

        tr.draw(matrices, time, cx, y, TEXT_DIM);
    }
                }
