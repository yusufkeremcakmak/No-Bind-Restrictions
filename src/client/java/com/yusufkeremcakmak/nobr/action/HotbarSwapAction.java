package com.yusufkeremcakmak.nobr.action;

import com.yusufkeremcakmak.nobr.runtime.InputSimulation;
import net.minecraft.client.MinecraftClient;

public final class HotbarSwapAction implements ChainAction {
    private final int slot;

    public HotbarSwapAction(int slot) {
        if (slot < 0 || slot > 8) {
            throw new IllegalArgumentException("Hotbar slot must be in range 0-8");
        }
        this.slot = slot;
    }

    public int slot() {
        return slot;
    }

    @Override
    public ActionType getType() {
        return ActionType.HOTBAR_SWAP;
    }

    @Override
    public int execute(MinecraftClient client, InputSimulation inputSimulation) {
        if (client.player != null) {
            client.player.getInventory().setSelectedSlot(slot);
        }
        return 0;
    }
}
