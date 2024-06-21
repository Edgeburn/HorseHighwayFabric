/*
 * Copyright (c) 2024 Edgeburn Media. All rights reserved.
 */

package com.edgeburnmedia.horsehighwayfabric;

import com.edgeburnmedia.horsehighwayfabric.component.HorseHighwayComponents;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HorseHighway implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    private static HorseHighway instance;
    public static final Logger LOGGER = LoggerFactory.getLogger("horse_highway");
    private HorseHighwayConfig config;

    public static HorseHighway getInstance() {
        return instance;
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
