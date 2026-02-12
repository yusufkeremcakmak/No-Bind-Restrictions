package com.yusufkeremcakmak.nobr.action;

import java.util.Locale;

public final class ActionCodec {
    private ActionCodec() {
    }

    public static ChainAction parse(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Action is empty");
        }

        String upper = normalized.toUpperCase(Locale.ROOT);
        if (upper.equals("JUMP")) {
            return new JumpAction();
        }
        if (upper.equals("SNEAK")) {
            return new SneakAction();
        }
        if (upper.equals("LEFT_CLICK")) {
            return new LeftClickAction();
        }
        if (upper.equals("RIGHT_CLICK")) {
            return new RightClickAction();
        }
        if (upper.startsWith("HOTBAR:")) {
            int slot = Integer.parseInt(upper.substring("HOTBAR:".length()).trim());
            return new HotbarSwapAction(slot);
        }
        if (upper.startsWith("DELAY:")) {
            int milliseconds = Integer.parseInt(upper.substring("DELAY:".length()).trim());
            return new DelayAction(milliseconds);
        }

        throw new IllegalArgumentException("Unsupported action: " + value);
    }

    public static String stringify(ChainAction action) {
        return switch (action.getType()) {
            case JUMP -> "JUMP";
            case SNEAK -> "SNEAK";
            case LEFT_CLICK -> "LEFT_CLICK";
            case RIGHT_CLICK -> "RIGHT_CLICK";
            case HOTBAR_SWAP -> "HOTBAR:" + ((HotbarSwapAction) action).slot();
            case DELAY -> "DELAY:" + ((DelayAction) action).delayMs();
        };
    }
}
