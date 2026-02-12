package com.yusufkeremcakmak.nobr.action;

import com.yusufkeremcakmak.nobr.runtime.InputSimulation;
import net.minecraft.client.MinecraftClient;

public interface ChainAction {
    ActionType getType();

    int execute(MinecraftClient client, InputSimulation inputSimulation);
}
