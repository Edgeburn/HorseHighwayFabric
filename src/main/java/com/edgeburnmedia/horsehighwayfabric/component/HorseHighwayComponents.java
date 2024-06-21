/*
 * Copyright (c) 2024 Edgeburn Media. All rights reserved.
 */

package com.edgeburnmedia.horsehighwayfabric.component;

import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class HorseHighwayComponents implements EntityComponentInitializer {
    public static final ComponentKey<OriginalSpeedComponent> ORIGINAL_SPEED_COMPONENT = ComponentRegistry.getOrCreate(Identifier.of("horse_highway", "original_speed"), OriginalSpeedComponent.class);

    /**
     * Called to register component factories for statically declared component types.
     *
     * <p><strong>The passed registry must not be held onto!</strong> Static component factories
     * must not be registered outside of this method.
     *
     * @param registry an {@link EntityComponentFactoryRegistry} for <em>statically declared</em> components
     */
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(AbstractHorseEntity.class, ORIGINAL_SPEED_COMPONENT, OriginalSpeedComponent::new);
    }
}
