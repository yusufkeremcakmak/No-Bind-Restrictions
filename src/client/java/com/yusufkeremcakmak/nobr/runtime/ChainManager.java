package com.yusufkeremcakmak.nobr.runtime;

import com.yusufkeremcakmak.nobr.action.ActionCodec;
import com.yusufkeremcakmak.nobr.action.ChainAction;
import com.yusufkeremcakmak.nobr.chain.ActionChain;
import com.yusufkeremcakmak.nobr.chain.RunningChain;
import com.yusufkeremcakmak.nobr.config.NoBindRestrictionsConfig;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ChainManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("NoBindRestrictions");

    private final Map<UUID, KeyBinding> keyBindingsByChainId = new HashMap<>();
    private final Map<KeyBinding, ActionChain> chainsByKeyBinding = new HashMap<>();
    private final List<RunningChain> runningChains = new ArrayList<>();
    private final InputSimulation inputSimulation = new InputSimulation();

    private static KeyBinding.Category cachedCategory;

    private static KeyBinding.Category getOrCreateCategory() {
        if (cachedCategory == null) {
            cachedCategory = KeyBinding.Category.create(Identifier.of("nobindrestrictions", "chains"));
        }
        return cachedCategory;
    }

    private static KeyBinding registerKeyBinding(KeyBinding keyBinding) {
        try {
            return KeyBindingHelper.registerKeyBinding(keyBinding);
        } catch (IllegalStateException e) {
            // GameOptions already initialised — new chain won't be triggerable until restart
            LOGGER.warn("New chain key binding added at runtime; restart the game for it to take effect.");
            return keyBinding;
        }
    }

    public void rebuild(NoBindRestrictionsConfig config) {
        chainsByKeyBinding.clear();

        for (NoBindRestrictionsConfig.ChainConfigEntry entry : config.chains) {
            List<ChainAction> actions = new ArrayList<>();
            for (String actionLine : entry.actions) {
                try {
                    actions.add(ActionCodec.parse(actionLine));
                } catch (Exception ignored) {
                }
            }

            UUID chainId;
            try {
                chainId = UUID.fromString(entry.id);
            } catch (Exception ex) {
                chainId = UUID.randomUUID();
                entry.id = chainId.toString();
            }

            KeyBinding keyBinding = keyBindingsByChainId.computeIfAbsent(chainId, id -> registerKeyBinding(
                    new KeyBinding(
                        "key.nobr.chain." + id,
                        parseKey(entry.keyType, entry.keyCode).getCode(),
                        getOrCreateCategory()
                    )
            ));

            keyBinding.setBoundKey(parseKey(entry.keyType, entry.keyCode));

            chainsByKeyBinding.put(keyBinding, new ActionChain(chainId, entry.name, entry.enabled, keyBinding, actions));
        }
    }

    public void tick(MinecraftClient client, NoBindRestrictionsConfig config) {
        inputSimulation.beginTick();

        if (!config.masterEnabled) {
            runningChains.clear();
            inputSimulation.releaseAllNow();
            return;
        }

        for (Map.Entry<KeyBinding, ActionChain> entry : chainsByKeyBinding.entrySet()) {
            KeyBinding keyBinding = entry.getKey();
            ActionChain chain = entry.getValue();

            if (!chain.enabled() || chain.actions().isEmpty()) {
                continue;
            }

            while (keyBinding.wasPressed()) {
                runningChains.add(new RunningChain(chain));
            }
        }

        Iterator<RunningChain> iterator = runningChains.iterator();
        while (iterator.hasNext()) {
            RunningChain runningChain = iterator.next();
            runningChain.tick(client, inputSimulation);
            if (runningChain.isFinished()) {
                iterator.remove();
            }
        }
    }

    public void clearState() {
        runningChains.clear();
        inputSimulation.releaseAllNow();
    }

    public static InputUtil.Key parseKey(String type, int code) {
        try {
            return InputUtil.Type.valueOf(type).createFromCode(code);
        } catch (Exception ex) {
            return InputUtil.UNKNOWN_KEY;
        }
    }

    public static String keyTypeOf(InputUtil.Key key) {
        return key.getCategory().name();
    }

    public static int keyCodeOf(InputUtil.Key key) {
        return key.getCode();
    }
}
