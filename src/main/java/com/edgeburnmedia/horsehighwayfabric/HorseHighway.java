/*
 * Copyright (c) 2024 Edgeburn Media. All rights reserved.
 */

package com.edgeburnmedia.horsehighwayfabric;

import com.edgeburnmedia.horsehighwayfabric.component.HorseHighwayComponents;
import com.edgeburnmedia.horsehighwayfabric.networking.CentreYawPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.literal;

public class HorseHighway implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("horse_highway");
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    private static HorseHighway instance;
    private HorseHighwayConfig config;

    public static HorseHighway getInstance() {
        return instance;
    }

    public static float closest45DegreeAngle(float angle) {
        // Define the 45-degree angles within the -180 to 180 range
        float[] angles = {-180.0f, -135.0f, -90.0f, -45.0f, 0.0f, 45.0f, 90.0f, 135.0f, 180.0f};

        // Find the closest angle
        float closestAngle = angles[0];
        float minDifference = Math.abs(angle - closestAngle);

        for (float a : angles) {
            float difference = Math.abs(angle - a);
            if (difference < minDifference) {
                minDifference = difference;
                closestAngle = a;
            }
        }

        return closestAngle;
    }

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        instance = this;
        config = new HorseHighwayConfig();
        LOGGER.info("Hello HorseHighway!");


        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!world.isClient && entity instanceof AbstractHorseEntity horse) {
                boolean beenSet = horse.getComponent(HorseHighwayComponents.ORIGINAL_SPEED_COMPONENT).isBeenSet();
                if (!beenSet) {
                    horse.getComponent(HorseHighwayComponents.ORIGINAL_SPEED_COMPONENT).setFromHorse(horse);
                    horse.getComponent(HorseHighwayComponents.ORIGINAL_SPEED_COMPONENT).setBeenSet(true);
                }
            }
        });


        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("horse-highway-reload").requires(source -> source.hasPermissionLevel(2)).executes(context -> {
            HorseHighway.getInstance().config = new HorseHighwayConfig();
            context.getSource().sendFeedback(() -> Text.literal("Reloaded HorseHighway config"), true);
            return 1;
        })));


        PayloadTypeRegistry.playC2S().register(CentreYawPayload.ID, CentreYawPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(CentreYawPayload.ID, (payload, context) -> {
            final float yaw = payload.getYaw();
            ServerPlayerEntity player = context.player();
            CompletableFuture.supplyAsync(() -> closest45DegreeAngle(yaw)).thenAcceptAsync(f -> {
                context.server().execute(() -> {
                    if (player.getControllingVehicle() instanceof AbstractHorseEntity vehicle) {
                        vehicle.setYaw(f);
                    }
                });
            });
        });

//        DefaultItemComponentEvents.MODIFY.register(context -> {
//            context.modify(item -> {
//                LOGGER.info("got here item {}", item);
//                return true;
//            }, (builder, item) -> {
//                LOGGER.info("also got here item {}", item);
//                builder.add(DataComponentTypes.LORE, new LoreComponent(List.of(Text.literal("Hello World!")))).build();
//            });
//        });
    }

    public HorseHighwayConfig getConfig() {
        return config;
    }

}
