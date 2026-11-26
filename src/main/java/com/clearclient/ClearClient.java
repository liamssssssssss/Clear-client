package com.clearclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ClearClient implements ClientModInitializer {
    private static KeyBinding openMenu;
    private static KeyBinding openMenuFallback;

    @Override
    public void onInitializeClient() {
        KeyBinding.Category category = KeyBinding.Category.create(
            Identifier.of("clearclient", "main")
        );

        openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.clearclient.open_menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_CONTROL,
            category
        ));

        // F8 is a second, easy-to-find fallback in case another client captures Right Ctrl.
        openMenuFallback = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.clearclient.open_menu_fallback",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean pressed = false;
            while (openMenu.wasPressed()) pressed = true;
            while (openMenuFallback.wasPressed()) pressed = true;

            if (pressed && client.player != null) {
                if (client.currentScreen instanceof ClearClickGui) {
                    client.setScreen(null);
                } else if (client.currentScreen == null) {
                    client.setScreen(new ClearClickGui());
                }
            }
        });
    }
}
