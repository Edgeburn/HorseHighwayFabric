/*
 * Copyright (c) 2024 Edgeburn Media. All rights reserved.
 */

package com.edgeburnmedia.horsehighwayfabric.mixin;

import com.edgeburnmedia.horsehighwayfabric.HorseHighway;
import com.edgeburnmedia.horsehighwayfabric.HorseHighwayConfig;
import com.edgeburnmedia.horsehighwayfabric.component.HorseHighwayComponents;
import com.edgeburnmedia.horsehighwayfabric.component.OriginalSpeedComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnreachableCode")
@Mixin(AbstractHorseEntity.class)
public abstract class AbstractHorseEntityMixin {

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        AbstractHorseEntity abstractHorseEntity = (AbstractHorseEntity) (Object) this;
        setupSpeed(abstractHorseEntity);
    }

    @Unique
    private void setupSpeed(AbstractHorseEntity horse) {
        if (horse.getControllingPassenger() instanceof PlayerEntity) {
            BlockState state = horse.getSteppingBlockState();
            Block block = state.getBlock();
            Identifier blockId = Registries.BLOCK.getId(block);
            HorseHighwayConfig config = HorseHighway.getInstance().getConfig();
            if (blockId.equals(Identifier.tryParse("minecraft", "air"))) {
                BlockPos pos = horse.getSteppingPos().down(1);
                BlockState adjustedBlockState = horse.getEntityWorld().getBlockState(pos);
                blockId = Registries.BLOCK.getId(adjustedBlockState.getBlock());
            }
            if (config != null) {
                float gameSpeed = config.getGameSpeedFor(blockId);
                EntityAttributeInstance attr = horse.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                if (attr == null) {
                    NullPointerException npe = new NullPointerException("Attribute GENERIC_MOVEMENT_SPEED is null");
                    HorseHighway.LOGGER.error("Failure setting speed", npe);
                    return;
                }
                if (gameSpeed > -1.0f) {
                    attr.setBaseValue(gameSpeed);
                } else {
                    OriginalSpeedComponent component = horse.getComponent(HorseHighwayComponents.ORIGINAL_SPEED_COMPONENT);
                    if (component.isBeenSet()) {
                        attr.setBaseValue(component.getOriginalSpeed());
                    } else {
                        HorseHighway.LOGGER.warn("Original speed is unset for {}. Using 30kph default", horse);
                        attr.setBaseValue(OriginalSpeedComponent.THIRTY_KPH);
                    }
                }
            } else if (config == null) {
                NullPointerException npe = new NullPointerException();
                HorseHighway.LOGGER.error("Got null config for Horse Highway", npe);
            }
        }
    }

}
