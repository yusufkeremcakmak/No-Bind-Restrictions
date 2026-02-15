package com.yusufkeremcakmak.nobr.mixin;

import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor mixin that exposes the parent {@link KeybindsScreen} field
 * of {@link ControlsListWidget} so the key-binding entry mixin can interact
 * with the screen's selected-key-binding state.
 */
@Mixin(ControlsListWidget.class)
public interface ControlsListWidgetAccessor {
    @Accessor("parent")
    KeybindsScreen nobr$getParent();
}
