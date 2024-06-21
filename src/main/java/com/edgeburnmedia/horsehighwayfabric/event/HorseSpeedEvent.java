/*
 * Copyright (c) 2024 Edgeburn Media. All rights reserved.
 */

package com.edgeburnmedia.horsehighwayfabric.event;

public interface HorseSpeedEvent {
    /**
     * @implNote calculated using a separate client-side speedometer mod's speed reading, and the
     * distance given and solving for time using the formula speed = distance / time. Probably a
     * better way to do this, but this works good enough
     */
    double TIME_MAGIC_NUMBER = 0.013890542896511;

    // TIME_MAGIC_NUMBER calculated using a separate client-side speedometer mod's
    // speed reading, and the distance given and solving for time using the formula
    // speed = distance / time
    // Probably a better way to do this, but this works good enough

//    Event<HorseSpeedEvent> EVENT = EventFactory.createArrayBacked(HorseSpeedEvent.class, horseSpeedEvents -> (block, horse) -> {
//        for (HorseSpeedEvent event : horseSpeedEvents) {
//
//        }
//    })
//
//    float getHorseSpeed(Identifier block, AbstractHorseEntity horse);

}
