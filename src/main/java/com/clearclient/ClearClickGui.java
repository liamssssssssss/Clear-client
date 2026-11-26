package com.clearclient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClearClickGui extends Screen {
    private static final int BG = 0xE8101018;
    private static final int PANEL = 0xF01B1B27;
    private static final int PURPLE = 0xFF9B6BFF;
    private static final int MUTED = 0xFFAAA7B8;

    public ClearClickGui() {
        super(Text.literal("Clear Client"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        int w = 330, h = 230;
        int x = (width - w) / 2, y = (height - h) / 2;

        ctx.fill(x, y, x + w, y + h, BG);
        ctx.fill(x, y, x + w, y + 2, PURPLE);
        ctx.fill(x + 10, y + 10, x + 100, y + h - 10, PANEL);
        ctx.fill(x + 108, y + 10, x + w - 10, y + h - 10, PANEL);

        ctx.drawText(textRenderer, "CLEAR", x + 20, y + 22, 0xFFFFFFFF, false);
        ctx.drawText(textRenderer, "CLIENT", x + 20, y + 34, PURPLE, false);
        ctx.drawText(textRenderer, "VISUAL", x + 20, y + 66, MUTED, false);
        ctx.drawText(textRenderer, "UTILITY", x + 20, y + 84, MUTED, false);
        ctx.drawText(textRenderer, "MOVEMENT", x + 20, y + 102, MUTED, false);

        ctx.drawText(textRenderer, "Welcome to Clear Client", x + 122, y + 22, 0xFFFFFFFF, false);
        ctx.drawText(textRenderer, "A clean setup. Nothing extra.", x + 122, y + 38, MUTED, false);

        drawModule(ctx, x + 122, y + 72, "Keystrokes HUD", true);
        drawModule(ctx, x + 122, y + 104, "Coordinates", true);
        drawModule(ctx, x + 122, y + 136, "FPS Counter", true);
        drawModule(ctx, x + 122, y + 168, "Toggle Sprint", false);

        ctx.drawText(textRenderer, "RIGHT SHIFT  •  CLOSE MENU", x + 122, y + h - 25, PURPLE, false);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawModule(DrawContext ctx, int x, int y, String name, boolean enabled) {
        ctx.fill(x, y, x + 180, y + 25, 0xFF252532);
        ctx.drawText(textRenderer, name, x + 9, y + 9, 0xFFEDEAF5, false);
        int color = enabled ? PURPLE : 0xFF55515F;
        ctx.fill(x + 157, y + 8, x + 171, y + 17, color);
    }

    @Override
    public boolean shouldPause() { return false; }
}
