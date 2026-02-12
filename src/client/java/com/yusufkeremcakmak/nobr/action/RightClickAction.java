package com.yusufkeremcakmak.nobr.action;

import com.yusufkeremcakmak.nobr.runtime.InputSimulation;
import net.minecraft.client.MinecraftClient;

public final class RightClickAction implements ChainAction {
    @Override
    public ActionType getType() {
        return ActionType.RIGHT_CLICK;
    }

    @Override
    public int execute(MinecraftClient client, InputSimulation inputSimulation) {
        inputSimulation.pressForOneTick(client.options.useKey);
        return 0;
    }
}
