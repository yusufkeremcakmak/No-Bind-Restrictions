package com.yusufkeremcakmak.nobr.dualbind;

/**
 * Lightweight static state shared between the keybinds-screen mixins
 * to track whether the player is currently assigning an alternate key.
 */
public final class DualBindState {
    private DualBindState() {
    }

    /**
     * {@code true} while the key-binds screen is waiting for the user
     * to press a key/mouse button that will be stored as the <b>alternate</b>
     * binding rather than the primary one.
     */
    public static boolean editingAlternate = false;
}
