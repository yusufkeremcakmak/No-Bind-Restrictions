package com.yusufkeremcakmak.nobr.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("nobindrestrictions.json");

    private NoBindRestrictionsConfig config;

    public NoBindRestrictionsConfig load() {
        if (!Files.exists(CONFIG_PATH)) {
            config = new NoBindRestrictionsConfig();
            save();
            return config;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            config = GSON.fromJson(reader, NoBindRestrictionsConfig.class);
            if (config == null) {
                config = new NoBindRestrictionsConfig();
            }
        } catch (IOException ex) {
            config = new NoBindRestrictionsConfig();
        }

        normalize();
        save();
        return config;
    }

    public void save() {
        if (config == null) {
            return;
        }

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException ignored) {
        }
    }

    private void normalize() {
        if (config.chains == null) {
            config.chains = new ArrayList<>();
        }

        for (NoBindRestrictionsConfig.ChainConfigEntry chain : config.chains) {
            if (chain.id == null || chain.id.isBlank()) {
                chain.id = UUID.randomUUID().toString();
            }
            if (chain.name == null || chain.name.isBlank()) {
                chain.name = "Chain";
            }
            if (chain.keyType == null || chain.keyType.isBlank()) {
                chain.keyType = "KEYSYM";
            }
            if (chain.actions == null) {
                chain.actions = new ArrayList<>();
            }
        }
    }
}
