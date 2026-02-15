package com.yusufkeremcakmak.nobr.mixin;

import com.yusufkeremcakmak.nobr.dualbind.DualBindState;
import com.yusufkeremcakmak.nobr.dualbind.DualBindStorage;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Intercepts key / mouse input on the key-binds screen so that, when the
 * player is assigning an <b>alternate</b> binding, the pressed key is stored
 * as the alternate rather than the primary.
 */
@Mixin(KeybindsScreen.class)
public abstract class KeybindsScreenMixin {

    private static final int GLFW_KEY_ESCAPE = 256;

    @Shadow
    public KeyBinding selectedKeyBinding;

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void nobr$keyPressed(KeyInput keyInput, CallbackInfoReturnable<Boolean> cir) {
        if (selectedKeyBinding != null && DualBindState.editingAlternate) {
            if (keyInput.key() == GLFW_KEY_ESCAPE) {
                ((DualBindAccessor) selectedKeyBinding).nobr$setAlternateKey(InputUtil.UNKNOWN_KEY);
            } else {
                ((DualBindAccessor) selectedKeyBinding).nobr$setAlternateKey(
                        InputUtil.fromKeyCode(keyInput));
            }
            selectedKeyBinding = null;
            DualBindState.editingAlternate = false;
            KeyBinding.updateKeysByCode();
            DualBindStorage.save();
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void nobr$mouseClicked(Click click, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (selectedKeyBinding != null && DualBindState.editingAlternate) {
            ((DualBindAccessor) selectedKeyBinding).nobr$setAlternateKey(
                    InputUtil.Type.MOUSE.createFromCode(click.buttonInfo().button()));
            selectedKeyBinding = null;
            DualBindState.editingAlternate = false;
            KeyBinding.updateKeysByCode();
            DualBindStorage.save();
            cir.setReturnValue(true);
        }
    }
}
