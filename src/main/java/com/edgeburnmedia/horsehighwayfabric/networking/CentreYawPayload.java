/*
 * Copyright (c) 2024 Edgeburn Media. All rights reserved.
 */

package com.edgeburnmedia.horsehighwayfabric.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class CentreYawPayload implements CustomPayload {
    public static final Identifier IDENTIFIER = Identifier.tryParse("horse_highway", "centre_yaw");
    public static final CustomPayload.Id<CentreYawPayload> ID = new CustomPayload.Id<>(IDENTIFIER);
    public static final PacketCodec<RegistryByteBuf, CentreYawPayload> CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, CentreYawPayload::getYaw, CentreYawPayload::new);
    private final float yaw;

    public CentreYawPayload(float yaw) {
        this.yaw = yaw;
    }


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public float getYaw() {
        return yaw;
    }
}
