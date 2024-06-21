/*
 * Copyright (c) 2024 Edgeburn Media. All rights reserved.
 */

package com.edgeburnmedia.horsehighwayfabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;

public final class HorseHighwayConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
//    private static final JsonParser JSON_PARSER = new JsonParser();

    private static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "horse_highway.json");
    private static final JsonObject defaultConfig = new JsonObject();

    private HashMap<Identifier, Integer> speedsInKph = new HashMap<>(); // for displaying as readable text to the player
    private HashMap<Identifier, Float> speedsInMinecraftSpeed = new HashMap<>(); // for setting the horse speed
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    static {
        JsonObject speedsTmp = new JsonObject();
        defaultConfig.add("speeds", speedsTmp);
    }

    public HorseHighwayConfig() {
        if (!configFile.exists()) {
            setupDefaultConfig();
        }
        init(readRawConfig());
    }

    private JsonObject readRawConfig() {
        String jsonStr = null;
        try {
            jsonStr = Files.readString(configFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config file", e);
        }
        if (jsonStr == null) {
            throw new NullPointerException("Failed to read config file; jsonStr is null");
        }
        return JsonParser.parseString(jsonStr).getAsJsonObject();
    }

    private void init(JsonObject config) {
        JsonObject speeds = config.getAsJsonObject("speeds");
        if (speeds == null) {
            throw new RuntimeException("Failed to read config file; speeds is null");
        }
        logger.info("Loading horse_highway config");
        for (String key : speeds.keySet()) {
            speedsInKph.put(Identifier.tryParse(key), speeds.get(key).getAsInt());
            speedsInMinecraftSpeed.put(Identifier.tryParse(key), (float) SpeedConversionUtil.calculateGenericMovementSpeedFromKph(speeds.get(key).getAsInt()));
        }
        logger.info("Successfully loaded horse_highway config");
    }

    private void setupDefaultConfig() {
        init(defaultConfig);
        saveConfig();
    }

    private JsonObject asJsonObject() {
        JsonObject jsonObject = new JsonObject();
        JsonObject speeds = new JsonObject();
        for (Identifier idKey : speedsInKph.keySet()) {
            speeds.addProperty(idKey.toString(), speedsInKph.get(idKey));
        }
        jsonObject.add("speeds", speeds);
        return jsonObject;
    }

    public void saveConfig() {
        String jsonStr = GSON.toJson(asJsonObject());
        try {
            Files.writeString(configFile.toPath(), jsonStr, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save Horse Highway config", e);
        }
    }

    public float getGameSpeedFor(Identifier idKey) {
        return speedsInMinecraftSpeed.getOrDefault(idKey, -1.0f);
    }

    public int getKphSpeedFor(Identifier idKey) {
        return speedsInKph.getOrDefault(idKey, -1);
    }
}
