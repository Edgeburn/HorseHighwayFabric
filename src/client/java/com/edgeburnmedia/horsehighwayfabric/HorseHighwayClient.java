/*
 * Copyright (c) 2024 Edgeburn Media. All rights reserved.
 */

package com.edgeburnmedia.horsehighwayfabric;

import com.edgeburnmedia.horsehighwayfabric.networking.CentreYawPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.lwjgl.glfw.GLFW;

public class HorseHighwayClient implements ClientModInitializer {
    private static KeyBinding snapYaw;

    @Override
    public void onInitializeClient() {
        snapYaw = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.horse_highway.snap_yaw",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.horse_highway"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (snapYaw.isPressed()) {
                if (client.player.getVehicle() instanceof AbstractHorseEntity vehicle) {
                    float yaw = client.player.getYaw();
                    ClientPlayNetworking.send(new CentreYawPayload(yaw));
                }
            }
        });
    }
}
