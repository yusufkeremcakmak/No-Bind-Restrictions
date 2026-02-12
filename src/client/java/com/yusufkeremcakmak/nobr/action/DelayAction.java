package com.yusufkeremcakmak.nobr.action;

import com.yusufkeremcakmak.nobr.runtime.InputSimulation;
import net.minecraft.client.MinecraftClient;

public final class DelayAction implements ChainAction {
    private final int delayMs;

    public DelayAction(int delayMs) {
        this.delayMs = Math.max(delayMs, 0);
    }

    public int delayMs() {
        return delayMs;
    }

    @Override
    public ActionType getType() {
        return ActionType.DELAY;
    }

    @Override
    public int execute(MinecraftClient client, InputSimulation inputSimulation) {
        return toTicks(delayMs);
    }

    public static int toTicks(int delayMilliseconds) {
        if (delayMilliseconds <= 0) {
            return 0;
        }
        return (delayMilliseconds + 49) / 50;
    }
}
