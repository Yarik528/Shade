package com.shade.gui;

import com.shade.module.Module;
import com.shade.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HudRenderer {

    private static final int ACCENT = 0xFF8A2BE2;
    private static final int BG     = 0x90000000;
    private static final int TEXT   = 0xFFFFFFFF;
    private static final int MARGIN = 4;

    public static void render(MatrixStack matrices) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;

        TextRenderer tr = mc.textRenderer;

        List<Module> active = new ArrayList<>();
        for (Module m : ModuleManager.INSTANCE.getModules()) {
            if (m.isEnabled()) active.add(m);
        }
        if (active.isEmpty()) return;
        active.sort(Comparator.comparing(Module::getName));

        String title = "Shade";
        int titleW = tr.getWidth(title);
        int x = MARGIN, y = MARGIN;

        DrawableHelper.fill(matrices, x - 2, y - 2, x + titleW + 4, y + 10, BG);
        DrawableHelper.fill(matrices, x - 2, y - 2, x - 1, y + 10, ACCENT);
        tr.draw(matrices, title, x, y, ACCENT);

        y += 14;

        for (Module m : active) {
            String name = m.getName();
            int w = tr.getWidth(name);
            DrawableHelper.fill(matrices, x - 2, y - 1, x + w + 4, y + 10, BG);
            DrawableHelper.fill(matrices, x - 2, y - 1, x - 1, y + 10, ACCENT);
            tr.draw(matrices, name, x, y, TEXT);
            y += 12;
        }
    }
}
