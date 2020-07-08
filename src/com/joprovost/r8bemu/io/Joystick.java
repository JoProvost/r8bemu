package com.joprovost.r8bemu.io;

public interface Joystick {

    static JoystickDispatcher dispatcher() {
        return new JoystickDispatcher();
    }

    void horizontal(int value);
    void vertical(int value);
}
