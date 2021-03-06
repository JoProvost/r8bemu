package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.graphic.Sprite;

import javax.swing.*;

import static com.joprovost.r8bemu.io.awt.ActionSprites.CASSETTE;
import static com.joprovost.r8bemu.io.awt.ActionSprites.DISK;
import static com.joprovost.r8bemu.io.awt.ActionSprites.DPAD;
import static com.joprovost.r8bemu.io.awt.ActionSprites.KEYBOARD;
import static com.joprovost.r8bemu.io.awt.ActionSprites.LEFT;
import static com.joprovost.r8bemu.io.awt.ActionSprites.MAXIMIZE;
import static com.joprovost.r8bemu.io.awt.ActionSprites.MINIMIZE;
import static com.joprovost.r8bemu.io.awt.ActionSprites.MOUSE;
import static com.joprovost.r8bemu.io.awt.ActionSprites.NOT;
import static com.joprovost.r8bemu.io.awt.ActionSprites.ONE;
import static com.joprovost.r8bemu.io.awt.ActionSprites.PAUSE;
import static com.joprovost.r8bemu.io.awt.ActionSprites.POWER;
import static com.joprovost.r8bemu.io.awt.ActionSprites.RESET;
import static com.joprovost.r8bemu.io.awt.ActionSprites.REWIND;
import static com.joprovost.r8bemu.io.awt.ActionSprites.RIGHT;
import static com.joprovost.r8bemu.io.awt.ActionSprites.SPEAKER;
import static com.joprovost.r8bemu.io.awt.ActionSprites.THREE;
import static com.joprovost.r8bemu.io.awt.ActionSprites.TV;
import static com.joprovost.r8bemu.io.awt.ActionSprites.TWO;
import static com.joprovost.r8bemu.io.awt.ActionSprites.ZERO;

public enum ActionIcon {
    CASSETTE_ICON(CASSETTE),
    REWIND_ICON(REWIND),
    DISK0_ICON(DISK.with(ZERO)),
    DISK1_ICON(DISK.with(ONE)),
    DISK2_ICON(DISK.with(TWO)),
    DISK3_ICON(DISK.with(THREE)),
    HALT_ICON(PAUSE),
    KEYBOARD_ICON(KEYBOARD),
    DPAD_LEFT_ICON(DPAD.with(LEFT)),
    MOUSE_ICON(MOUSE.with(LEFT).with(RIGHT)),
    MUTE_ICON(SPEAKER.with(NOT)),
    FULL_SCREEN_ICON(MAXIMIZE, MINIMIZE),
    REBOOT_ICON(POWER),
    RESET_ICON(RESET),
    TV_ICON(TV);

    private final Icon normal;
    private final Icon selected;

    ActionIcon(Sprite sprite) {
        this(sprite, sprite.xor(ActionSprites.SELECTED));
    }

    ActionIcon(Sprite normal, Sprite selected) {
        this(SpriteIcon.of(normal), SpriteIcon.of(selected));
    }

    ActionIcon(Icon normal, Icon selected) {
        this.normal = normal;
        this.selected = selected;
    }

    public Icon icon(DiscreteOutput state) {
        return state.isSet() ? selected() : normal();
    }

    public Icon icon() {
        return normal();
    }

    public Icon normal() {
        return normal;
    }

    public Icon selected() {
        return selected;
    }
}


