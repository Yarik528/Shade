package com.shade.gui;

import com.shade.config.ConfigManager;
import com.shade.module.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClickGuiScreen extends Screen {

    private static final int BG_DARK      = 0xF0101014;
    private static final int BG_PANEL     = 0xFF16161C;
    private static final int BG_MODULE    = 0xFF1E1E26;
    private static final int BG_MODULE_H  = 0xFF26262E;
    private static final int BG_ENABLED   = 0xFF2A1B3D;
    private static final int ACCENT       = 0xFF8A2BE2;
    private static final int ACCENT_DIM   = 0xFF5A1B92;
    private static final int TEXT         = 0xFFE0E0E8;
    private static final int TEXT_DIM     = 0xFF808090;
    private static final int TEXT_ENABLED = 0xFFFFFFFF;

    private static final int PANEL_W = 280;
    private static final int PANEL_H = 220;
    private static final int CAT_W   = 90;
    private static final int ROW_H   = 18;

    private final String[] categories = { "Combat", "Render", "Cosmetic", "Movement", "World" };
    private int selectedCategory = 0;
    private Module expanded = null;
    private Slider dragging = null;

    private static class Slider {
        int x, y, w, h;
        float min, max, value;
        Consumer<Float> onChange;
    }

    public ClickGuiScreen() {
        super(new LiteralText("Shade"));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, 0, 0, width, height, 0x80000000);

        int px = (width - PANEL_W) / 2;
        int py = (height - PANEL_H) / 2;

        fill(matrices, px, py, px + PANEL_W, py + PANEL_H, BG_DARK);
        drawBorder(matrices, px, py, PANEL_W, PANEL_H, ACCENT_DIM);

        fill(matrices, px, py, px + PANEL_W, py + 22, BG_PANEL);
        fill(matrices, px, py, px + 3, py + 22, ACCENT);
        textRenderer.draw(matrices, "SHADE", px + 10, py + 7, ACCENT);
        textRenderer.draw(matrices, "v1.2", px + PANEL_W - 34, py + 7, TEXT_DIM);

        int catY = py + 30;
        for (int i = 0; i < categories.length; i++) {
            boolean sel = i == selectedCategory;
            boolean hover = mouseX >= px + 6 && mouseX <= px + 6 + CAT_W
                         && mouseY >= catY && mouseY <= catY + ROW_H - 2;
            int bg = sel ? BG_ENABLED : (hover ? BG_MODULE_H : BG_MODULE);
            fill(matrices, px + 6, catY, px + 6 + CAT_W, catY + ROW_H - 2, bg);
            if (sel) fill(matrices, px + 6, catY, px + 8, catY + ROW_H - 2, ACCENT);
            int color = sel ? TEXT_ENABLED : (hover ? TEXT : TEXT_DIM);
            textRenderer.draw(matrices, categories[i], px + 14, catY + 5, color);
            catY += ROW_H;
        }

        int modX = px + CAT_W + 16;
        int modY = py + 30;
        int modW = PANEL_W - CAT_W - 22;

        for (Module m : filteredModules()) {
            boolean enabled = m.isEnabled();
            boolean hover = mouseX >= modX && mouseX <= modX + modW
                         && mouseY >= modY && mouseY <= modY + ROW_H - 2;
            int bg = enabled ? BG_ENABLED : (hover ? BG_MODULE_H : BG_MODULE);
            fill(matrices, modX, modY, modX + modW, modY + ROW_H - 2, bg);
            if (enabled) fill(matrices, modX, modY, modX + 3, modY + ROW_H - 2, ACCENT);
            textRenderer.draw(matrices, m.getName(), modX + 10, modY + 5,
                enabled ? TEXT_ENABLED : TEXT);
            String state = enabled ? "ON" : "OFF";
            textRenderer.draw(matrices, state,
                modX + modW - textRenderer.getWidth(state) - 8,
                modY + 5, enabled ? ACCENT : TEXT_DIM);
            modY += ROW_H;

            if (expanded == m) {
                modY = renderSettings(matrices, m, modX, modY, modW, mouseX, mouseY);
            }
        }

        fill(matrices, px, py + PANEL_H - 16, px + PANEL_W, py + PANEL_H, BG_PANEL);
        textRenderer.draw(matrices, "RShift close · LMB toggle · RMB expand",
            px + 8, py + PANEL_H - 12, TEXT_DIM);

        super.render(matrices, mouseX, mouseY, delta);
    }

    private int renderSettings(MatrixStack matrices, Module m, int x, int y, int w,
                               int mouseX, int mouseY) {
        if (m instanceof KillAura) {
            KillAura k = (KillAura) m;
            y = slider(matrices, "Min CPS", x + 6, y + 2, w - 12,
                1f, 20f, (float) k.getMinCps(), mouseX, mouseY, v -> k.setMinCps(v));
            y = slider(matrices, "Max CPS", x + 6, y + 2, w - 12,
                1f, 20f, (float) k.getMaxCps(), mouseX, mouseY, v -> k.setMaxCps(v));
            y = slider(matrices, "FOV", x + 6, y + 2, w - 12,
                30f, 360f, (float) k.getFov(), mouseX, mouseY, v -> k.setFov(v));
        } else if (m instanceof Reach) {
            Reach r = (Reach) m;
            y = slider(matrices, "Block Reach", x + 6, y + 2, w - 12,
                3f, 6f, (float) r.getBlockReachRaw(), mouseX, mouseY, v -> r.setBlockReach(v));
            y = slider(matrices, "Entity Reach", x + 6, y + 2, w - 12,
                3f, 6f, (float) r.getEntityReachRaw(), mouseX, mouseY, v -> r.setEntityReach(v));
        } else if (m instanceof SilentAim) {
            SilentAim s = (SilentAim) m;
            y = toggleRow(matrices, "GCD Snap", x + 6, y + 2, w - 12,
                s.isGcdSnap(), mouseX, mouseY, v -> s.setGcdSnap(v));
            y = toggleRow(matrices, "Jitter", x + 6, y + 2, w - 12,
                s.isMicroJitter(), mouseX, mouseY, v -> s.setMicroJitter(v));
        } else if (m instanceof Velocity) {
            Velocity v = (Velocity) m;
            y = slider(matrices, "Horizontal", x + 6, y + 2, w - 12,
                0f, 1f, (float) v.getHorizontalRaw(), mouseX, mouseY, val -> v.setHorizontal(val));
            y = slider(matrices, "Vertical", x + 6, y + 2, w - 12,
                0f, 1f, (float) v.getVerticalRaw(), mouseX, mouseY, val -> v.setVertical(val));
            y = toggleRow(matrices, "AntiDetect", x + 6, y + 2, w - 12,
                v.isAntiDetect(), mouseX, mouseY, val -> v.setAntiDetect(val));
        } else if (m instanceof NoRender) {
            NoRender nr = (NoRender) m;
            y = toggleRow(matrices, "Fire", x + 6, y + 2, w - 12,
                nr.isFire(), mouseX, mouseY, v -> nr.setFire(v));
            y = toggleRow(matrices, "Water", x + 6, y + 2, w - 12,
                nr.isWater(), mouseX, mouseY, v -> nr.setWater(v));
            y = toggleRow(matrices, "Portal", x + 6, y + 2, w - 12,
                nr.isPortal(), mouseX, mouseY, v -> nr.setPortal(v));
        } else if (m instanceof Tracers) {
            Tracers t = (Tracers) m;
            y = toggleRow(matrices, "Players", x + 6, y + 2, w - 12,
                t.isShowPlayers(), mouseX, mouseY, v -> t.setShowPlayers(v));
            y = toggleRow(matrices, "Hostile", x + 6, y + 2, w - 12,
                t.isShowHostile(), mouseX, mouseY, v -> t.setShowHostile(v));
            y = toggleRow(matrices, "Passive", x + 6, y + 2, w - 12,
                t.isShowPassive(), mouseX, mouseY, v -> t.setShowPassive(v));
        } else if (m instanceof Nametags) {
            Nametags n = (Nametags) m;
            y = toggleRow(matrices, "Health", x + 6, y + 2, w - 12,
                n.isShowHealth(), mouseX, mouseY, v -> n.setShowHealth(v));
            y = toggleRow(matrices, "Distance", x + 6, y + 2, w - 12,
                n.isShowDistance(), mouseX, mouseY, v -> n.setShowDistance(v));
        } else if (m instanceof Snow) {
            Snow s = (Snow) m;
            y = slider(matrices, "Radius", x + 6, y + 2, w - 12,
                1f, 10f, (float) s.getRadius(), mouseX, mouseY,
                v -> s.setRadius(v.intValue()));
            y = slider(matrices, "Density", x + 6, y + 2, w - 12,
                1f, 30f, (float) s.getDensity(), mouseX, mouseY,
                v -> s.setDensity(v.intValue()));
        } else if (m instanceof Halo) {
            Halo h = (Halo) m;
            y = slider(matrices, "Red", x + 6, y + 2, w - 12,
                0f, 1f, h.getRed(), mouseX, mouseY, v -> h.setRed(v));
            y = slider(matrices, "Green", x + 6, y + 2, w - 12,
                0f, 1f, h.getGreen(), mouseX, mouseY, v -> h.setGreen(v));
            y = slider(matrices, "Blue", x + 6, y + 2, w - 12,
                0f, 1f, h.getBlue(), mouseX, mouseY, v -> h.setBlue(v));
            y = slider(matrices, "Size", x + 6, y + 2, w - 12,
                0.5f, 2f, h.getSize(), mouseX, mouseY, v -> h.setSize(v));
        } else if (m instanceof Trail) {
            Trail t = (Trail) m;
            y = slider(matrices, "Length", x + 6, y + 2, w - 12,
                5f, 50f, (float) t.getMaxLength(), mouseX, mouseY,
                v -> t.setMaxLength(v.intValue()));
            y = slider(matrices, "Density", x + 6, y + 2, w - 12,
                1f, 10f, (float) t.getDensity(), mouseX, mouseY,
                v -> t.setDensity(v.intValue()));
        }
        return y + 6;
    }

    private int slider(MatrixStack matrices, String label, int x, int y, int w,
                       float min, float max, float val,
                       int mouseX, int mouseY, Consumer<Float> onChange) {
        textRenderer.draw(matrices, label, x, y, TEXT_DIM);
        int barY = y + 11;
        int barH = 4;
        fill(matrices, x, barY, x + w, barY + barH, BG_MODULE);
        float t = (val - min) / (max - min);
        int fillW = (int)(w * t);
        fill(matrices, x, barY, x + fillW, barY + barH, ACCENT);
        fill(matrices, x + fillW - 2, barY - 2, x + fillW + 2, barY + barH + 2, TEXT_ENABLED);
        return barY + barH + 6;
    }

    private int toggleRow(MatrixStack matrices, String label, int x, int y, int w,
                          boolean on, int mouseX, int mouseY, Consumer<Boolean> onChange) {
        textRenderer.draw(matrices, label, x, y, TEXT_DIM);
        int bx = x + w - 22;
        boolean hover = mouseX >= bx && mouseX <= bx + 20 && mouseY >= y && mouseY <= y + 9;
        fill(matrices, bx, y, bx + 20, y + 9, on ? ACCENT : (hover ? BG_MODULE_H : BG_MODULE));
        fill(matrices, on ? bx + 11 : bx + 1, y + 1,
             on ? bx + 19 : bx + 9, y + 8, TEXT_ENABLED);
        return y + 14;
    }

    private void drawBorder(MatrixStack matrices, int x, int y, int w, int h, int color) {
        fill(matrices, x, y, x + w, y + 1, color);
        fill(matrices, x, y + h - 1, x + w, y + h, color);
        fill(matrices, x, y, x + 1, y + h, color);
        fill(matrices, x + w - 1, y, x + w, y + h, color);
    }

    private List<Module> filteredModules() {
        List<Module> out = new ArrayList<>();
        for (Module m : ModuleManager.INSTANCE.getModules()) {
            if (categoryOf(m).equals(categories[selectedCategory])) out.add(m);
        }
        return out;
    }

    private String categoryOf(Module m) {
        String n = m.getName();
        switch (n) {
            case "KillAura":
            case "SilentAim":
            case "Reach":
            case "Velocity":
                return "Combat";
            case "ESP":
            case "XRay":
            case "Tracers":
            case "Nametags":
            case "NoRender":
                return "Render";
            case "Snow":
            case "Halo":
            case "Trail":
                return "Cosmetic";
            case "Sprint":
                return "Movement";
            default:
                return "World";
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int px = (width - PANEL_W) / 2;
        int py = (height - PANEL_H) / 2;

        int catY = py + 30;
        for (int i = 0; i < categories.length; i++) {
            if (mx >= px + 6 && mx <= px + 6 + CAT_W
             && my >= catY && my <= catY + ROW_H - 2) {
                selectedCategory = i;
                expanded = null;
                return true;
            }
            catY += ROW_H;
        }

        int modX = px + CAT_W + 16;
        int modY = py + 30;
        int modW = PANEL_W - CAT_W - 22;

        for (Module m : filteredModules()) {
            if (mx >= modX && mx <= modX + modW
             && my >= modY && my <= modY + ROW_H - 2) {
                if (button == 0) {
                    m.toggle();
                    ConfigManager.save();
                } else if (button == 1) {
                    expanded = (expanded == m) ? null : m;
                }
                return true;
            }
            modY += ROW_H;

            if (expanded == m) {
                int settingsTop = modY;
                if (tryClickSettings(m, modX, settingsTop, modW, mx, my, button)) {
                    return true;
                }
                modY = settingsTop + settingsHeight(m);
            }
        }
        return false;
    }

    private int settingsHeight(Module m) {
        if (m instanceof KillAura) return 66;
        if (m instanceof Reach) return 46;
        if (m instanceof SilentAim) return 32;
        if (m instanceof Velocity) return 66;
        if (m instanceof NoRender) return 46;
        if (m instanceof Tracers) return 46;
        if (m instanceof Nametags) return 32;
        if (m instanceof Snow) return 32;
        if (m instanceof Halo) return 60;
        if (m instanceof Trail) return 32;
        return 0;
    }

    private boolean tryClickSettings(Module m, int x, int y, int w,
                                     double mx, double my, int button) {
        if (m instanceof KillAura) {
            KillAura k = (KillAura) m;
            if (hitSlider(x + 6, y + 13, w - 12, 6, mx, my)) {
                dragging = makeSlider(x + 6, y + 13, w - 12, 1f, 20f, (float) k.getMinCps(), v -> k.setMinCps(v));
                applyDrag(mx); return true;
            }
            if (hitSlider(x + 6, y + 33, w - 12, 6, mx, my)) {
                dragging = makeSlider(x + 6, y + 33, w - 12, 1f, 20f, (float) k.getMaxCps(), v -> k.setMaxCps(v));
                applyDrag(mx); return true;
            }
            if (hitSlider(x + 6, y + 53, w - 12, 6, mx, my)) {
                dragging = makeSlider(x + 6, y + 53, w - 12, 30f, 360f, (float) k.getFov(), v -> k.setFov(v));
                applyDrag(mx); return true;
            }
        } else if (m instanceof Reach) {
            Reach r = (Reach) m;
            if (hitSlider(x + 6, y + 13, w - 12, 6, mx, my)) {
                dragging = makeSlider(x + 6, y + 13, w - 12, 3f, 6f, (float) r.getBlockReachRaw(), v -> r.setBlockReach(v));
                applyDrag(mx); return true;
            }
            if (hitSlider(x + 6, y + 33, w - 12, 6, mx, my)) {
                dragging = makeSlider(x + 6, y + 33, w - 12, 3f, 6f, (float) r.getEntityReachRaw(), v -> r.setEntityReach(v));
                applyDrag(mx); return true;
            }
        } else if (m instanceof Velocity) {
            Velocity v = (Velocity) m;
            if (hitSlider(x + 6, y + 13, w - 12, 6, mx, my)) {
                dragging = makeSlider(x + 6, y + 13, w - 12, 0f, 1f, (float) v.getHorizontalRaw(), val -> v.setHorizontal(val));
                applyDrag(mx); return true;
            }
            if (hitSlider(x + 6, y + 33, w - 12, 6, mx, my)) {
                dragging = makeSlider(x + 6, y + 33, w - 12, 0f, 1f, (float) v.getVerticalRaw(), val -> v.setVertical(val));
                applyDrag(mx); return true;
            }
            if (hitToggle(x + w - 28, y + 49, mx, my)) {
                v.setAntiDetect(!v.isAntiDetect()); ConfigManager.save(); return true;
            }
        } else if (m instanceof SilentAim) {
            SilentAim s = (SilentAim) m;
            if (hitToggle(x + w - 28, y + 2, mx, my)) { s.setGcdSnap(!s.isGcdSnap()); ConfigManager.save(); return true; }
            if (hitToggle(x + w - 28, y + 16, mx, my)) { s.setMicroJitter(!s.isMicroJitter()); ConfigManager.save(); return true; }
        } else if (m instanceof NoRender) {
            NoRender nr = (NoRender) m;
            if (hitToggle(x + w - 28, y + 2, mx, my)) { nr.setFire(!nr.isFire()); ConfigManager.save(); return true; }
            if (hitToggle(x + w - 28, y + 16, mx, my)) { nr.setWater(!nr.isWater()); ConfigManager.save(); return true; }
            if (hitToggle(x + w - 28, y + 30, mx, my)) { nr.setPortal(!nr.isPortal()); ConfigManager.save(); return true; }
        } else if (m instanceof Tracers) {
            Tracers t = (Tracers) m;
            if (hitToggle(x + w - 28, y + 2, mx, my)) { t.setShowPlayers(!t.isShowPlayers()); ConfigManager.save(); return true; }
            if (hitToggle(x + w - 28, y + 16, mx, my)) { t.setShowHostile(!t.isShowHostile()); ConfigManager.save(); return true; }
            if (hitToggle(x + w - 28, y + 30, mx, my)) { t.setShowPassive(!t.isShowPassive()); ConfigManager.save(); return true; }
        } else if (m instanceof Nametags) {
            Nametags n = (Nametags) m;
            if (hitToggle(x + w - 28, y + 2, mx, my)) { n.setShowHealth(!n.isShowHealth()); ConfigManager.save(); return true; }
            if (hitToggle(x + w - 28, y + 16, mx, my)) { n.setShowDistance(!n.isShowDistance()); ConfigManager.save(); return true; }
        } else if (m instanceof Snow) {
            Snow s = (Snow) m;
            if (hitSlider(x + 6, y + 13, w - 12, 6, mx, my)) {
                dragging = makeSlider(x + 6, y + 13, w - 12, 1f, 10f, (float) s.getRadius(), v -> s.setRadius(v.intValue()));
                applyDrag(mx); return true;
            }
            if (hitSlider(x + 6, y + 33, w - 12, 6, mx, my)) {
                dragging = makeSlider(x + 6, y + 33, w - 12, 1f, 30f, (float) s.getDensity(), v -> s.setDensity(v.intValue()));
                applyDrag(mx); return true;
            }
        } else if (m instanceof Halo) {
            Halo h = (Halo) m;
            if (hitSlider(x + 6, y + 13, w - 12, 6, mx, my)) {
                dragging = makeSlider(x + 6, y + 13, w - 12, 0f, 1f, h.getRed(), v -> h.setRed(v));
                applyDrag(mx); return true;
            }
            if (hitSlider(x + 6, y + 33, w - 12, 6, mx, my)) {
                dragging = makeSlider(x + 6, y + 33, w - 12, 0f, 1f, h.getGreen(), v -> h.setGreen(v));
                applyDrag(mx); return true;
            }
            if (hitSlider(x + 6, y + 53, w - 12, 6, mx, my)) {
                dragging = makeSlider(x + 6, y + 53, w - 12, 0f, 1f, h.getBlue(), v -> h.setBlue(v));
                applyDrag(mx); return true;
            }
            if (hitSlider(x + 6, y + 73, w - 12, 6, mx, my)) {
                dragging = makeSlider(x + 6, y + 73, w - 12, 0.5f, 2f, h.getSize(), v -> h.setSize(v));
                applyDrag(mx); return true;
            }
        } else if (m instanceof Trail) {
            Trail t = (Trail) m;
            if (hitSlider(x + 6, y + 13, w - 12, 6, mx, my)) {
                dragging = makeSlider(x + 6, y + 13, w - 12, 5f, 50f, (float) t.getMaxLength(), v -> t.setMaxLength(v.intValue()));
                applyDrag(mx); return true;
            }
            if (hitSlider(x + 6, y + 33, w - 12, 6, mx, my)) {
                dragging = makeSlider(x + 6, y + 33, w - 12, 1f, 10f, (float) t.getDensity(), v -> t.setDensity(v.intValue()));
                applyDrag(mx); return true;
            }
        }
        return false;
    }

    private Slider makeSlider(int x, int y, int w, float min, float max,
                              float val, Consumer<Float> onChange) {
        Slider s = new Slider();
        s.x = x; s.y = y - 2; s.w = w; s.h = 10;
        s.min = min; s.max = max; s.value = val;
        s.onChange = onChange;
        return s;
    }

    private boolean hitSlider(int x, int y, int w, int h, double mx, double my) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private boolean hitToggle(int x, int y, double mx, double my) {
        return mx >= x && mx <= x + 20 && my >= y && my <= y + 9;
    }

    private void applyDrag(double mx) {
        if (dragging == null) return;
        float t = (float)((mx - dragging.x) / (double) dragging.w);
        t = Math.max(0f, Math.min(1f, t));
        float val = dragging.min + t * (dragging.max - dragging.min);
        dragging.value = val;
        dragging.onChange.accept(val);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragging != null) {
            applyDrag(mx);
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (dragging != null) {
            dragging = null;
            ConfigManager.save();
            return true;
        }
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean isPauseScreen() { return false; }
            }
