package com.yusufkeremcakmak.nobr.mixin;

import com.yusufkeremcakmak.nobr.dualbind.DualBindAccessor;
import com.yusufkeremcakmak.nobr.dualbind.DualBindState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds an <b>alternate edit button</b> to every row in the vanilla key-binds
 * list, letting the player assign a second key to each action.
 */
@Mixin(targets = "net.minecraft.client.gui.screen.option.ControlsListWidget$KeyBindingEntry")
public abstract class KeyBindingEntryMixin {

    @Shadow
    @Final
    private KeyBinding binding;

    @Shadow
    @Final
    private ButtonWidget editButton;

    @Shadow
    @Final
    private ButtonWidget resetButton;

    @Shadow
    @Final
    ControlsListWidget field_2742; // synthetic outer-class reference

    @Unique
    private ButtonWidget nobr$alternateEditButton;

    // ── constructor: create the alternate button ───────────────────────

    @Inject(method = "<init>", at = @At("TAIL"))
    private void nobr$init(ControlsListWidget outer, KeyBinding binding,
                           Text bindingName, CallbackInfo ci) {
        // Halve the primary button to make room for our alternate button.
        int origWidth = editButton.getWidth();
        int halfWidth = (origWidth - 2) / 2;
        editButton.setWidth(halfWidth);

        InputUtil.Key altKey = ((DualBindAccessor) binding).nobr$getAlternateKey();
        nobr$alternateEditButton = ButtonWidget.builder(
                altKey.getLocalizedText(),
                btn -> {
                    KeybindsScreen screen = ((ControlsListWidgetAccessor) outer).nobr$getParent();
                    screen.selectedKeyBinding = binding;
                    DualBindState.editingAlternate = true;
                }
        ).width(halfWidth).build();
    }

    // ── render: position & draw the alternate button ───────────────────

    @Inject(method = "render", at = @At("TAIL"))
    private void nobr$render(DrawContext context, int index, int y,
                             boolean hovered, float tickDelta, CallbackInfo ci) {
        if (nobr$alternateEditButton == null) {
            return;
        }
        nobr$alternateEditButton.setX(editButton.getX() + editButton.getWidth() + 2);
        nobr$alternateEditButton.setY(editButton.getY());

        DrawContextAccessor dca = (DrawContextAccessor) (Object) context;
        nobr$alternateEditButton.render(context, dca.nobr$getMouseX(),
                dca.nobr$getMouseY(), tickDelta);
    }

    // ── update: refresh button label & listening indicator ─────────────

    @Inject(method = "update", at = @At("TAIL"))
    private void nobr$update(CallbackInfo ci) {
        if (nobr$alternateEditButton == null) {
            return;
        }

        KeybindsScreen screen = ((ControlsListWidgetAccessor) field_2742).nobr$getParent();
        boolean listeningAlt = screen.selectedKeyBinding == binding
                && DualBindState.editingAlternate;

        InputUtil.Key altKey = ((DualBindAccessor) binding).nobr$getAlternateKey();

        if (listeningAlt) {
            // Show the vanilla-style "> key <" listening indicator.
            nobr$alternateEditButton.setMessage(
                    Text.literal("> ")
                            .formatted(Formatting.YELLOW, Formatting.UNDERLINE)
                            .append(altKey.getLocalizedText().copy()
                                    .formatted(Formatting.YELLOW, Formatting.UNDERLINE))
                            .append(Text.literal(" <")
                                    .formatted(Formatting.YELLOW, Formatting.UNDERLINE))
            );
        } else {
            nobr$alternateEditButton.setMessage(altKey.getLocalizedText());
        }
    }

    // ── children / selectable: include the alternate button ────────────

    @Inject(method = "children", at = @At("RETURN"), cancellable = true)
    private void nobr$children(CallbackInfoReturnable<List<? extends Element>> cir) {
        if (nobr$alternateEditButton == null) {
            return;
        }
        List<Element> list = new ArrayList<>(cir.getReturnValue());
        list.add(nobr$alternateEditButton);
        cir.setReturnValue(list);
    }

    @Inject(method = "selectableChildren", at = @At("RETURN"), cancellable = true)
    private void nobr$selectableChildren(CallbackInfoReturnable<List<? extends Selectable>> cir) {
        if (nobr$alternateEditButton == null) {
            return;
        }
        List<Selectable> list = new ArrayList<>(cir.getReturnValue());
        list.add(nobr$alternateEditButton);
        cir.setReturnValue(list);
    }
}
