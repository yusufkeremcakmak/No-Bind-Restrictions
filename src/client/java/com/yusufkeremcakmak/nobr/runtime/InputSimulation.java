package com.yusufkeremcakmak.nobr.runtime;

import net.minecraft.client.option.KeyBinding;

import java.util.HashSet;
import java.util.Set;

public final class InputSimulation {
    private final Set<KeyBinding> oneTickPressedKeys = new HashSet<>();

    public void beginTick() {
        if (oneTickPressedKeys.isEmpty()) {
            return;
        }

        for (KeyBinding keyBinding : oneTickPressedKeys) {
            keyBinding.setPressed(false);
        }
        oneTickPressedKeys.clear();
    }

    public void pressForOneTick(KeyBinding keyBinding) {
        keyBinding.setPressed(true);
        oneTickPressedKeys.add(keyBinding);
    }

    public void releaseAllNow() {
        for (KeyBinding keyBinding : oneTickPressedKeys) {
            keyBinding.setPressed(false);
        }
        oneTickPressedKeys.clear();
    }
}
