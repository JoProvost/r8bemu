package com.joprovost.r8bemu.io;

public class JoystickDispatcher implements Joystick {
    Joystick target;

    public void dispatchTo(Joystick target) {
        this.target = target;
    }

    @Override
    public void horizontal(int value) {
        if (target != null) target.horizontal(value);
    }

    @Override
    public void vertical(int value) {
        if (target != null) target.vertical(value);
    }
}
