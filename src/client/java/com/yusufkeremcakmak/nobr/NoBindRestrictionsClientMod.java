package com.yusufkeremcakmak.nobr;

import com.yusufkeremcakmak.nobr.config.ConfigManager;
import com.yusufkeremcakmak.nobr.config.NoBindRestrictionsConfig;
import com.yusufkeremcakmak.nobr.dualbind.DualBindStorage;
import com.yusufkeremcakmak.nobr.runtime.ChainManager;
import com.yusufkeremcakmak.nobr.ui.FirstLaunchWarningScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public final class NoBindRestrictionsClientMod implements ClientModInitializer {
    private static NoBindRestrictionsClientMod instance;

    private final ConfigManager configManager = new ConfigManager();
    private final ChainManager chainManager = new ChainManager();
    private NoBindRestrictionsConfig config;

    @Override
    public void onInitializeClient() {
        instance = this;

        config = configManager.load();
        chainManager.rebuild(config);

        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
    }

    private void onEndClientTick(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return;
        }

        if (config.firstLaunchWarningPending && client.currentScreen == null) {
            client.setScreen(new FirstLaunchWarningScreen(this::acknowledgeWarning));
            return;
        }

        chainManager.tick(client, config);
    }

    private void acknowledgeWarning() {
        config.firstLaunchWarningPending = false;
        saveAndReload();
    }

    public void saveAndReload() {
        configManager.save();
        chainManager.clearState();
        chainManager.rebuild(config);
    }

    public NoBindRestrictionsConfig config() {
        return config;
    }

    public static NoBindRestrictionsClientMod getInstance() {
        if (instance == null) {
            throw new IllegalStateException("NoBindRestrictionsClientMod is not initialized yet");
        }
        return instance;
    }
}
