package com.yusufkeremcakmak.nobr.action;

import com.yusufkeremcakmak.nobr.runtime.InputSimulation;
import net.minecraft.client.MinecraftClient;

public final class SneakAction implements ChainAction {
    @Override
    public ActionType getType() {
        return ActionType.SNEAK;
    }

    @Override
    public int execute(MinecraftClient client, InputSimulation inputSimulation) {
        inputSimulation.pressForOneTick(client.options.sneakKey);
        return 0;
    }
}
