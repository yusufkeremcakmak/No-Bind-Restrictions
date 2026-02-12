package com.yusufkeremcakmak.nobr.ui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public final class FirstLaunchWarningScreen extends ConfirmScreen {
    public FirstLaunchWarningScreen(Runnable onAcknowledge) {
        super(
                confirmed -> {
                    if (confirmed) {
                        onAcknowledge.run();
                    }
                },
                Text.translatable("nobr.warning.title"),
                Text.translatable("nobr.warning.body")
        );
    }
}
