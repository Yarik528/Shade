package com.shade.gui;

import com.shade.alt.AltEntry;
import com.shade.alt.AltManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class AltScreen extends Screen {

    private static final int ACCENT = 0xFF8A2BE2;
    private static final int BG = 0xF0101014;
    private static final int TEXT = 0xFFE0E0E8;
    private static final int TEXT_DIM = 0xFF808090;
    private static final int PANEL_W = 260;
    private static final int PANEL_H = 220;

    private final Screen parent;
    private TextFieldWidget inputField;

    public AltScreen(Screen parent) {
        super(new LiteralText("Alts"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int px = (width - PANEL_W) / 2;
        int py = (height - PANEL_H) / 2;

        // поле ввода ника
        inputField = new TextFieldWidget(textRenderer, px + 12, py + PANEL_H - 60, PANEL_W - 100, 20,
            new LiteralText("Username"));
        inputField.setMaxLength(16);
        addButton(inputField);

        // кнопка Add
        addButton(new ButtonWidget(px + PANEL_W - 80, py + PANEL_H - 60, 68, 20,
            new LiteralText("Add"), b -> {
                String name = inputField.getText();
                if (!name.isEmpty()) {
                    AltManager.addAlt(name);
                    inputField.setText("");
                    refresh();
                }
            }));

        // кнопка Back
        addButton(new ButtonWidget(px + 12, py + PANEL_H - 30, 60, 20,
            new LiteralText("Back"), b -> client.openScreen(parent)));

        refresh();
    }

    private void refresh() {
        // удалить старые кнопки альтов и пересоздать
        // в 1.16.5 addButton — просто добавление, чистить неудобно
        // вместо этого используем рендер и mouseClicked для альтов
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, 0, 0, width, height, 0x80000000);

        int px = (width - PANEL_W) / 2;
        int py = (height - PANEL_H) / 2;

        fill(matrices, px, py, px + PANEL_W, py + PANEL_H, BG);
        fill(matrices, px, py, px + PANEL_W, py + 22, 0xFF16161C);
        fill(matrices, px, py, px + 3, py + 22, ACCENT);
        textRenderer.draw(matrices, "ALT MANAGER", px + 10, py + 7, ACCENT);

        // список альтов
        int listY = py + 30;
        AltEntry current = AltManager.getCurrent();

        for (AltEntry e : AltManager.getAlts()) {
            boolean isCurrent = (current != null && current.getUsername().equals(e.getUsername()));
            boolean hover = mouseX >= px + 12 && mouseX <= px + PANEL_W - 100
                         && mouseY >= listY && mouseY <= listY + 16;

            int bg = isCurrent ? 0xFF2A1B3D : (hover ? 0xFF26262E : 0xFF1E1E26);
            fill(matrices, px + 12, listY, px + PANEL_W - 12, listY + 16, bg);
            if (isCurrent) fill(matrices, px + 12, listY, px + 14, listY + 16, ACCENT);

            textRenderer.draw(matrices, e.getUsername(),
                px + 20, listY + 4, isCurrent ? 0xFFFFFFFF : TEXT);

            // кнопка Login / Remove
            textRenderer.draw(matrices, "Login", px + PANEL_W - 80, listY + 4, ACCENT);
            textRenderer.draw(matrices, "X", px + PANEL_W - 30, listY + 4, 0xFFFF5555);

            listY += 18;
            if (listY > py + PANEL_H - 70) break; // защита от переполнения
        }

        if (AltManager.getAlts().isEmpty()) {
            textRenderer.draw(matrices, "No alts yet. Add one below.",
                px + 20, py + 40, TEXT_DIM);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int px = (width - PANEL_W) / 2;
        int py = (height - PANEL_H) / 2;

        int listY = py + 30;
        for (AltEntry e : AltManager.getAlts()) {
            if (my >= listY && my <= listY + 16) {
                // Login
                if (mx >= px + PANEL_W - 85 && mx <= px + PANEL_W - 50) {
                    AltManager.login(e);
                    return true;
                }
                // Remove
                if (mx >= px + PANEL_W - 35 && mx <= px + PANEL_W - 15) {
                    AltManager.removeAlt(e);
                    return true;
                }
            }
            listY += 18;
            if (listY > py + PANEL_H - 70) break;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public void onClose() {
        AltManager.save();
        client.openScreen(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }
                 }
