package com.yusufkeremcakmak.nobr.dualbind;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yusufkeremcakmak.nobr.dualbind.DualBindAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class DualBindStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path STORAGE_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("nobindrestrictions_dualbinds.json");
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {
    }.getType();

    private DualBindStorage() {
    }

    public static void load() {
        if (!Files.exists(STORAGE_PATH)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(STORAGE_PATH)) {
            Map<String, String> data = GSON.fromJson(reader, MAP_TYPE);
            if (data == null) {
                return;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            for (KeyBinding binding : client.options.allKeys) {
                String altKeyStr = data.get(binding.getId());
                if (altKeyStr != null) {
                    try {
                        InputUtil.Key key = InputUtil.fromTranslationKey(altKeyStr);
                        ((DualBindAccessor) binding).nobr$setAlternateKey(key);
                    } catch (Exception ignored) {
                    }
                }
            }
            KeyBinding.updateKeysByCode();
        } catch (IOException ignored) {
        }
    }

    public static void save() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            return;
        }

        Map<String, String> data = new HashMap<>();
        for (KeyBinding binding : client.options.allKeys) {
            InputUtil.Key altKey = ((DualBindAccessor) binding).nobr$getAlternateKey();
            if (altKey != InputUtil.UNKNOWN_KEY) {
                data.put(binding.getId(), altKey.getTranslationKey());
            }
        }

        try {
            Files.createDirectories(STORAGE_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(STORAGE_PATH)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException ignored) {
        }
    }
}
