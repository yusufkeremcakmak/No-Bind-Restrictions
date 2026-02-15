package com.yusufkeremcakmak.nobr.mixin;

import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor mixin that exposes the mouse coordinates stored in
 * {@link DrawContext} so entry render methods can forward them
 * to child widget rendering.
 */
@Mixin(DrawContext.class)
public interface DrawContextAccessor {
    @Accessor("mouseX")
    int nobr$getMouseX();

    @Accessor("mouseY")
    int nobr$getMouseY();
}
