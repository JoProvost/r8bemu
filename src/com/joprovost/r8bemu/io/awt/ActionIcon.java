package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.data.discrete.DiscreteOutput;

import javax.swing.*;

public enum ActionIcon {
    CASSETTE("/images/cassette_32x32.png"),
    CASSETTE_REWIND("/images/rewind_32x32.png"),
    DISK("/images/disk_32x32.png"),
    HALT("/images/halt_32x32.png", "/images/halt_selected_32x32.png"),
    KEYBOARD_BUFFER("/images/keyboard_abc_32x32.png", "/images/keyboard_abc_selected_32x32.png"),
    KEYBOARD_DPAD_LEFT("/images/keyboard_dpad_left_32x32.png", "/images/keyboard_dpad_left_selected_32x32.png"),
    MOUSE("/images/mouse_32x32.png", "/images/mouse_selected_32x32.png"),
    MUTE("/images/mute_32x32.png", "/images/muted_32x32.png"),
    REBOOT("/images/power_32x32.png"),
    RESET("/images/reset_32x32.png"),
    COMPOSITE("/images/composite_32x32.png", "/images/composite_selected_32x32.png");

    private final ImageIcon normal;
    private final ImageIcon selected;

    ActionIcon(String normal, String selected) {
        this.normal = new ImageIcon(ActionIcon.class.getResource(normal));
        this.selected = new ImageIcon(ActionIcon.class.getResource(selected));
    }

    ActionIcon(String normal) {
        this.normal = this.selected = new ImageIcon(ActionIcon.class.getResource(normal));
    }

    public Icon icon(DiscreteOutput state) {
        return icon(state.isSet());
    }

    public Icon icon(boolean state) {
        return state ? selected : normal;
    }

    public Icon icon() {
        return icon(false);
    }
}


