package com.yusufkeremcakmak.nobr.chain;

import com.yusufkeremcakmak.nobr.action.ChainAction;
import com.yusufkeremcakmak.nobr.runtime.InputSimulation;
import net.minecraft.client.MinecraftClient;

public final class RunningChain {
    private final ActionChain chain;
    private int actionIndex;
    private int waitingTicks;
    private boolean finished;

    public RunningChain(ActionChain chain) {
        this.chain = chain;
    }

    public boolean isFinished() {
        return finished;
    }

    public void tick(MinecraftClient client, InputSimulation inputSimulation) {
        if (finished) {
            return;
        }

        if (waitingTicks > 0) {
            waitingTicks--;
            return;
        }

        if (actionIndex >= chain.actions().size()) {
            finished = true;
            return;
        }

        ChainAction action = chain.actions().get(actionIndex);
        actionIndex++;
        waitingTicks = Math.max(action.execute(client, inputSimulation), 0);

        if (actionIndex >= chain.actions().size() && waitingTicks == 0) {
            finished = true;
        }
    }
}
