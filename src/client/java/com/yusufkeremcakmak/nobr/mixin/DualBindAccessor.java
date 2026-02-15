package com.yusufkeremcakmak.nobr.mixin;

import net.minecraft.client.util.InputUtil;

/**
 * Interface applied to {@link net.minecraft.client.option.KeyBinding} via mixin
 * to expose the alternate (secondary) key binding.
 */
public interface DualBindAccessor {
    InputUtil.Key nobr$getAlternateKey();

    void nobr$setAlternateKey(InputUtil.Key key);
}
