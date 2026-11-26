package com.clearclient;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ClearClickGui extends Screen {
    private static final int WINDOW = 0xF0121318;
    private static final int PANEL = 0xFF1B1D24;
    private static final int PANEL_HOVER = 0xFF232631;
    private static final int PURPLE = 0xFF7565FF;
    private static final int WHITE = 0xFFF1F1F5;
    private static final int MUTED = 0xFF858894;

    private static final String[] MODULES = {
        "Keystrokes HUD", "Coordinates", "FPS Counter", "Toggle Sprint"
    };
    private static final String[] DESCRIPTIONS = {
        "Display movement keys", "Show your position",
        "Display frames per second", "Keep sprint enabled"
    };
    private final boolean[] enabled = { true, true, true, false };
    private int panelX, panelY, panelW, panelH;
    private final int[] cardX = new int[4];
    private int cardY, cardW;

    public ClearClickGui() {
        super(Text.literal("Clear Client"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Flat overlay: avoids the blur conflict with other client mods.
        ctx.fill(0, 0, width, height, 0x90000000);

        panelW = Math.min(520, width - 24);
        panelH = Math.min(270, height - 24);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;

        ctx.fill(panelX + 3, panelY + 3, panelX + panelW + 3, panelY + panelH + 3, 0x55000000);
        ctx.fill(panelX, panelY, panelX + panelW, panelY + panelH, WINDOW);

        // Header
        ctx.fill(panelX, panelY, panelX + panelW, panelY + 1, PURPLE);
        ctx.drawText(textRenderer, "Clear", panelX + 15, panelY + 13, WHITE, false);
        ctx.drawText(textRenderer, "CLIENT", panelX + 49, panelY + 14, PURPLE, false);
        ctx.drawText(textRenderer, "v0.1.0", panelX + panelW - 45, panelY + 15, MUTED, false);

        // Main navigation
        int navY = panelY + 37;
        ctx.fill(panelX + 12, navY, panelX + panelW - 12, navY + 1, 0xFF292B34);
        ctx.drawText(textRenderer, "Modules", panelX + 16, navY + 9, WHITE, false);
        ctx.drawText(textRenderer, "Config", panelX + 72, navY + 9, MUTED, false);
        ctx.drawText(textRenderer, "Friends", panelX + 119, navY + 9, MUTED, false);
        ctx.drawText(textRenderer, "HUD", panelX + 174, navY + 9, MUTED, false);
        ctx.fill(panelX + 15, navY + 25, panelX + 57, navY + 27, PURPLE);

        // Category strip
        int categoryY = navY + 39;
        String[] categories = { "All", "Visual", "Utility", "Movement" };
        int[] offsets = { 16, 48, 99, 151 };
        for (int i = 0; i < categories.length; i++) {
            ctx.drawText(textRenderer, categories[i], panelX + offsets[i], categoryY,
                i == 0 ? WHITE : MUTED, false);
        }

        // Module cards
        int gap = 9;
        cardW = (panelW - 32 - gap * 3) / 4;
        cardY = categoryY + 23;
        for (int i = 0; i < MODULES.length; i++) {
            int cx = panelX + 16 + i * (cardW + gap);
            cardX[i] = cx;
            boolean hovered = mouseX >= cx && mouseX <= cx + cardW
                && mouseY >= cardY && mouseY <= cardY + 88;
            ctx.fill(cx, cardY, cx + cardW, cardY + 88, hovered ? PANEL_HOVER : PANEL);
            ctx.fill(cx, cardY, cx + cardW, cardY + 1, enabled[i] ? PURPLE : 0xFF30323B);

            ctx.fill(cx + 10, cardY + 11, cx + 18, cardY + 19, 0xFF343743);
            ctx.fill(cx + 12, cardY + 13, cx + 16, cardY + 17, enabled[i] ? PURPLE : MUTED);

            int sx = cx + cardW - 34;
            int sy = cardY + 11;
            ctx.fill(sx, sy, sx + 25, sy + 12, enabled[i] ? PURPLE : 0xFF343741);
            int knobX = enabled[i] ? sx + 15 : sx + 2;
            ctx.fill(knobX, sy + 2, knobX + 8, sy + 10, 0xFFF4F4F7);

            ctx.drawText(textRenderer, MODULES[i], cx + 9, cardY + 57, WHITE, false);
            ctx.drawText(textRenderer, DESCRIPTIONS[i], cx + 9, cardY + 70, MUTED, false);
        }

        ctx.drawText(textRenderer, "Click a module to toggle  -  Right Ctrl / F8 to close",
            panelX + 16, panelY + panelH - 17, MUTED, false);
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubleClick) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            for (int i = 0; i < MODULES.length; i++) {
                if (mouseX >= cardX[i] && mouseX <= cardX[i] + cardW
                    && mouseY >= cardY && mouseY <= cardY + 88) {
                    enabled[i] = !enabled[i];
                    return true;
                }
            }
        }
        return super.mouseClicked(click, doubleClick);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
