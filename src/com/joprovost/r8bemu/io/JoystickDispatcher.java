package com.joprovost.r8bemu.io;

import java.util.ArrayList;
import java.util.List;

public class JoystickDispatcher implements Joystick {
    private final List<Joystick> targets = new ArrayList<>();

    public void dispatchTo(Joystick target) {
        this.targets.add(target);
    }

    @Override
    public void horizontal(int value) {
        for (var target : targets) target.horizontal(value);
    }

    @Override
    public void vertical(int value) {
        for (var target : targets) target.vertical(value);
    }

    @Override
    public void press() {
        for (var target : targets) target.press();
    }

    @Override
    public void release() {
        for (var target : targets) target.release();
    }
}
