package com.yusufkeremcakmak.nobr.config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class NoBindRestrictionsConfig {
    public boolean masterEnabled = true;
    public boolean firstLaunchWarningPending = true;
    public List<ChainConfigEntry> chains = new ArrayList<>();

    public static final class ChainConfigEntry {
        public String id = UUID.randomUUID().toString();
        public String name = "Chain";
        public boolean enabled = true;
        public String keyType = "KEYSYM";
        public int keyCode = -1;
        public List<String> actions = new ArrayList<>();
    }
}
