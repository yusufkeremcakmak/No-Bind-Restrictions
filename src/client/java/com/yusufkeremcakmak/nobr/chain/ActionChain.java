package com.yusufkeremcakmak.nobr.chain;

import com.yusufkeremcakmak.nobr.action.ChainAction;
import net.minecraft.client.option.KeyBinding;

import java.util.List;
import java.util.UUID;

public record ActionChain(
        UUID id,
        String name,
        boolean enabled,
        KeyBinding keyBinding,
        List<ChainAction> actions
) {
}
