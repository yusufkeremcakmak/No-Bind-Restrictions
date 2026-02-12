package com.yusufkeremcakmak.nobr.ui;

import com.yusufkeremcakmak.nobr.NoBindRestrictionsClientMod;
import com.yusufkeremcakmak.nobr.action.ActionCodec;
import com.yusufkeremcakmak.nobr.config.NoBindRestrictionsConfig;
import com.yusufkeremcakmak.nobr.runtime.ChainManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Tooltip;
import me.shedaniel.clothconfig2.gui.entries.AbstractConfigListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public final class ConfigScreenBuilder {
    private ConfigScreenBuilder() {
    }

    public static Screen build(Screen parent) {
        NoBindRestrictionsClientMod mod = NoBindRestrictionsClientMod.getInstance();
        NoBindRestrictionsConfig config = mod.config();
        Set<String> chainsToRemove = new HashSet<>();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("nobr.config.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("nobr.config.general"));
        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("nobr.config.master_enabled"), config.masterEnabled)
                .setTooltip(Tooltip.of(Text.translatable("nobr.config.master_enabled.tooltip")))
                .setSaveConsumer(value -> config.masterEnabled = value)
                .build());

        ConfigCategory chains = builder.getOrCreateCategory(Text.translatable("nobr.config.chains"));
        chains.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("nobr.config.add_chain"), false)
                .setYesNoTextSupplier(value -> value ? Text.of("Will add on save") : Text.of("No"))
                .setSaveConsumer(shouldAdd -> {
                    if (shouldAdd) {
                        config.chains.add(defaultChain());
                    }
                })
                .build());

        for (NoBindRestrictionsConfig.ChainConfigEntry chain : config.chains) {
            chains.addEntry(buildChainSubCategory(entryBuilder, chain, chainsToRemove));
        }

        builder.setSavingRunnable(() -> {
            if (!chainsToRemove.isEmpty()) {
                config.chains.removeIf(chain -> chainsToRemove.contains(chain.id));
            }
            mod.saveAndReload();
        });

        return builder.build();
    }

    private static AbstractConfigListEntry<?> buildChainSubCategory(
            ConfigEntryBuilder entryBuilder,
            NoBindRestrictionsConfig.ChainConfigEntry chain,
            Set<String> chainsToRemove
    ) {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();

        entries.add(entryBuilder
                .startStrField(Text.translatable("nobr.config.chain_name"), chain.name)
                .setSaveConsumer(value -> chain.name = value == null || value.isBlank() ? "Chain" : value)
                .build());

        entries.add(entryBuilder
                .startBooleanToggle(Text.translatable("nobr.config.chain_enabled"), chain.enabled)
                .setSaveConsumer(value -> chain.enabled = value)
                .build());

        entries.add(entryBuilder
                .startBooleanToggle(Text.translatable("nobr.config.chain_remove"), false)
                .setSaveConsumer(remove -> {
                    if (remove) {
                        chainsToRemove.add(chain.id);
                    }
                })
                .build());

        InputUtil.Key currentKey = ChainManager.parseKey(chain.keyType, chain.keyCode);
        entries.add(entryBuilder
                .startKeyCodeField(Text.translatable("nobr.config.chain_key"), currentKey)
                .setSaveConsumer(key -> {
                    chain.keyType = ChainManager.keyTypeOf(key);
                    chain.keyCode = ChainManager.keyCodeOf(key);
                })
                .build());

        entries.add(entryBuilder
                .startStrList(Text.translatable("nobr.config.chain_actions"), new ArrayList<>(chain.actions))
                .setTooltip(Tooltip.of(Text.translatable("nobr.config.chain_actions.tooltip")))
                .setCellErrorSupplier(value -> validateActionLine(value).map(Text::of).orElse(null))
                .setSaveConsumer(values -> {
                    List<String> normalized = new ArrayList<>();
                    for (String line : values) {
                        String trimmed = line == null ? "" : line.trim();
                        if (trimmed.isEmpty()) {
                            continue;
                        }
                        normalized.add(trimmed.toUpperCase(Locale.ROOT));
                    }
                    chain.actions = normalized;
                })
                .build());

        return entryBuilder.startSubCategory(Text.of(chain.name), entries).build();
    }

    private static java.util.Optional<String> validateActionLine(String line) {
        try {
            ActionCodec.stringify(ActionCodec.parse(line));
            return java.util.Optional.empty();
        } catch (Exception ex) {
            return java.util.Optional.of("Invalid action format");
        }
    }

    private static NoBindRestrictionsConfig.ChainConfigEntry defaultChain() {
        NoBindRestrictionsConfig.ChainConfigEntry entry = new NoBindRestrictionsConfig.ChainConfigEntry();
        entry.id = UUID.randomUUID().toString();
        entry.name = "Chain " + (int) (Math.random() * 1000);
        entry.enabled = true;
        entry.keyType = "KEYSYM";
        entry.keyCode = -1;
        entry.actions = new ArrayList<>(List.of("JUMP"));
        return entry;
    }
}
