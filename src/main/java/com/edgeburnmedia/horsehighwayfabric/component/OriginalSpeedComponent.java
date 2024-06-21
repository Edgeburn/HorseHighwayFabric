/*
 * Copyright (c) 2024 Edgeburn Media. All rights reserved.
 */

package com.edgeburnmedia.horsehighwayfabric.component;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class OriginalSpeedComponent implements Component, AutoSyncedComponent {
    private final AbstractHorseEntity horse;
    private double originalSpeed = -1.0f;
    private boolean beenSet = false;
    public static final double THIRTY_KPH = 0.19303529;

    public OriginalSpeedComponent(AbstractHorseEntity horse) {
        this.horse = horse;
        setFromHorse(horse);
    }

    public double getOriginalSpeed() {
        return originalSpeed;
    }

    public void setOriginalSpeed(double originalSpeed) {
        this.originalSpeed = originalSpeed;
        HorseHighwayComponents.ORIGINAL_SPEED_COMPONENT.sync(horse);
    }

    public boolean isBeenSet() {
        return beenSet;
    }

    public void setBeenSet(boolean beenSet) {
        this.beenSet = beenSet;
        HorseHighwayComponents.ORIGINAL_SPEED_COMPONENT.sync(horse);
    }

    public void setFromHorse(AbstractHorseEntity horse) {
        boolean uninitialized = horse.getAttributes() == null;
        EntityAttributeInstance attr = null;
        if (!uninitialized) {
            attr = horse.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        }

        if (attr != null) {
            setFromAttributeInstance(attr);
        } else {
            originalSpeed = -1d;
        }
    }

    public void setFromAttributeInstance(@NotNull EntityAttributeInstance attr) {
        originalSpeed = attr.getBaseValue();
    }

    /**
     * Reads this component's properties from a {@link NbtCompound}.
     *
     * @param tag            a {@code NbtCompound} on which this component's serializable data has been written
     * @param registryLookup access to dynamic registry data
     * @implNote implementations should not assert that the data written on the tag corresponds to any
     * specific scheme, as saved data is susceptible to external tempering, and may come from an earlier
     * version.
     */
    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        originalSpeed = tag.getDouble("OriginalSpeed");
        beenSet = tag.getBoolean("BeenSet");
    }

    /**
     * Writes this component's properties to a {@link NbtCompound}.
     *
     * @param tag            a {@code NbtCompound} on which to write this component's serializable data
     * @param registryLookup access to dynamic registry data
     */
    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putDouble("OriginalSpeed", originalSpeed > -1.0 ? originalSpeed : THIRTY_KPH);
        tag.putBoolean("BeenSet", beenSet);
    }

    @Override
    public boolean isRequiredOnClient() {
        return false;
    }
}
