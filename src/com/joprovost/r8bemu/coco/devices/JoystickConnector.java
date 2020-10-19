package com.joprovost.r8bemu.coco.devices;

import com.joprovost.r8bemu.data.analog.AnalogLineInput;
import com.joprovost.r8bemu.data.binary.BinaryInputProvider;
import com.joprovost.r8bemu.io.JoystickInput;

public class JoystickConnector implements JoystickInput {
    private final AnalogLineInput horizontal;
    private final AnalogLineInput vertical;

    public JoystickConnector(AnalogLineInput horizontal, AnalogLineInput vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    @Override
    public void horizontal(double value) {
        horizontal.value(value);
    }

    @Override
    public void vertical(double value) {
        vertical.value(value);
    }

    boolean pressed = false;

    @Override
    public void press() {
        pressed = true;
    }

    @Override
    public void release() {
        pressed = false;
    }

    public BinaryInputProvider button() {
        return input -> {
            if (pressed) input.clear();
        };
    }
}
