package com.yusufkeremcakmak.nobr.runtime;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public final class InputSimulation {
    private static final Logger LOGGER = LoggerFactory.getLogger("NoBindRestrictions");
    private final Set<KeyBinding> oneTickPressedKeys = new HashSet<>();

    private static Field timesPressed;

    static {
        try {
            for (Field f : KeyBinding.class.getDeclaredFields()) {
                if (f.getType() == int.class && !java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                    // timesPressed is the non-static int field (the only one besides field_63464 which is final)
                    if (!java.lang.reflect.Modifier.isFinal(f.getModifiers())) {
                        timesPressed = f;
                        timesPressed.setAccessible(true);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to find timesPressed field in KeyBinding", e);
        }
    }

    public void beginTick() {
        if (oneTickPressedKeys.isEmpty()) {
            return;
        }

        for (KeyBinding keyBinding : oneTickPressedKeys) {
            keyBinding.setPressed(false);
        }
        oneTickPressedKeys.clear();
    }

    /**
     * Sets the key as "held" for one tick. Good for continuous actions
     * like jumping or sneaking that check {@code isPressed()}.
     */
    public void pressForOneTick(KeyBinding keyBinding) {
        keyBinding.setPressed(true);
        oneTickPressedKeys.add(keyBinding);
    }

    /**
     * Simulates a discrete click by incrementing the internal press counter.
     * Required for actions like attack/use that check {@code wasPressed()}.
     */
    public void click(KeyBinding keyBinding) {
        if (timesPressed != null) {
            try {
                int current = timesPressed.getInt(keyBinding);
                timesPressed.setInt(keyBinding, current + 1);
            } catch (Exception e) {
                LOGGER.error("Failed to simulate click", e);
            }
        }
    }

    public void releaseAllNow() {
        for (KeyBinding keyBinding : oneTickPressedKeys) {
            keyBinding.setPressed(false);
        }
        oneTickPressedKeys.clear();
    }
}
