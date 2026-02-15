package com.yusufkeremcakmak.nobr.mixin;

import com.yusufkeremcakmak.nobr.dualbind.DualBindAccessor;
import com.yusufkeremcakmak.nobr.dualbind.DualBindStorage;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Core mixin that adds an <b>alternate key</b> field to every
 * {@link KeyBinding} and hooks into matching / updating logic so
 * both the primary and alternate keys activate the binding.
 */
@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements DualBindAccessor {

    @Shadow
    private static Map<String, KeyBinding> KEYS_BY_ID;

    @Shadow
    private static Map<InputUtil.Key, List<KeyBinding>> KEY_TO_BINDINGS;

    // ── alternate key storage ──────────────────────────────────────────

    @Unique
    private InputUtil.Key nobr$alternateKey = InputUtil.UNKNOWN_KEY;

    @Override
    public InputUtil.Key nobr$getAlternateKey() {
        return nobr$alternateKey;
    }

    @Override
    public void nobr$setAlternateKey(InputUtil.Key key) {
        this.nobr$alternateKey = key != null ? key : InputUtil.UNKNOWN_KEY;
    }

    // ── matching: keyboard ─────────────────────────────────────────────

    @Inject(method = "matchesKey", at = @At("HEAD"), cancellable = true)
    private void nobr$matchesKey(KeyInput keyInput, CallbackInfoReturnable<Boolean> cir) {
        if (nobr$alternateKey != InputUtil.UNKNOWN_KEY) {
            boolean matches =
                    (nobr$alternateKey.getCategory() == InputUtil.Type.KEYSYM
                            && nobr$alternateKey.getCode() == keyInput.key())
                            || (nobr$alternateKey.getCategory() == InputUtil.Type.SCANCODE
                            && nobr$alternateKey.getCode() == keyInput.scancode());
            if (matches) {
                cir.setReturnValue(true);
            }
        }
    }

    // ── matching: mouse ────────────────────────────────────────────────

    @Inject(method = "matchesMouse", at = @At("HEAD"), cancellable = true)
    private void nobr$matchesMouse(Click click, CallbackInfoReturnable<Boolean> cir) {
        if (nobr$alternateKey != InputUtil.UNKNOWN_KEY) {
            if (nobr$alternateKey.getCategory() == InputUtil.Type.MOUSE
                    && nobr$alternateKey.getCode() == click.buttonInfo().button()) {
                cir.setReturnValue(true);
            }
        }
    }

    // ── key-to-binding map: also register alternate keys ───────────────

    @Inject(method = "updateKeysByCode", at = @At("TAIL"))
    private static void nobr$updateKeysByCode(CallbackInfo ci) {
        for (KeyBinding binding : KEYS_BY_ID.values()) {
            InputUtil.Key altKey = ((DualBindAccessor) binding).nobr$getAlternateKey();
            if (altKey != InputUtil.UNKNOWN_KEY) {
                KEY_TO_BINDINGS.computeIfAbsent(altKey, k -> new ArrayList<>()).add(binding);
            }
        }
    }

    // ── isDefault: incorporate alternate key ────────────────────────────

    @Inject(method = "isDefault", at = @At("HEAD"), cancellable = true)
    private void nobr$isDefault(CallbackInfoReturnable<Boolean> cir) {
        // If an alternate key is set the binding is no longer "default",
        // which keeps the per-row reset button active.
        if (nobr$alternateKey != InputUtil.UNKNOWN_KEY) {
            cir.setReturnValue(false);
        }
    }

    // ── reset: clear alternate key ─────────────────────────────────────

    @Inject(method = "reset", at = @At("TAIL"))
    private void nobr$reset(CallbackInfo ci) {
        nobr$alternateKey = InputUtil.UNKNOWN_KEY;
        DualBindStorage.save();
    }
}
