package com.yusufkeremcakmak.nobr.action;

import com.yusufkeremcakmak.nobr.runtime.InputSimulation;
import net.minecraft.client.MinecraftClient;

public final class JumpAction implements ChainAction {
    @Override
    public ActionType getType() {
        return ActionType.JUMP;
    }

    @Override
    public int execute(MinecraftClient client, InputSimulation inputSimulation) {
        inputSimulation.pressForOneTick(client.options.jumpKey);
        return 0;
    }
}
