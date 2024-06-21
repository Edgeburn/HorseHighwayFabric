/*
 * Copyright (c) 2024 Edgeburn Media. All rights reserved.
 */

package com.edgeburnmedia.horsehighwayfabric.mixin;

import com.edgeburnmedia.horsehighwayfabric.component.HorseHighwayComponents;
import com.edgeburnmedia.horsehighwayfabric.component.OriginalSpeedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    private static final Logger log = LoggerFactory.getLogger(EntityMixin.class);

    @Inject(at = @At("HEAD"), method = "removePassenger")
    public void removePassenger(Entity passenger, CallbackInfo ci) {
        log.info("Removing passenger {} from vehicle {}", passenger, (Entity) (Object) this);
        __removePassenger(passenger, ci);
    }

    @Unique
    private void __removePassenger(Entity passenger, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof AbstractHorseEntity) {
            log.info("horsie");
        }
        if (entity instanceof AbstractHorseEntity horse && !entity.getEntityWorld().isClient()) {
            if (passenger instanceof ServerPlayerEntity player) {
                player.sendMessageToClient(Text.literal("Horse speed reset"), true);
            }
            OriginalSpeedComponent comp = horse.getComponent(HorseHighwayComponents.ORIGINAL_SPEED_COMPONENT);
            EntityAttributeInstance movementSpeed = horse.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            movementSpeed.setBaseValue(comp.getOriginalSpeed());
            horse.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f);
        }
    }
}
