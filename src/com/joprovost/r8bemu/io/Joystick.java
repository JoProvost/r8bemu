package com.joprovost.r8bemu.io;

import java.util.concurrent.Executor;

public interface Joystick extends Button {

    int CENTER = 0;
    int MINIMUM = Short.MIN_VALUE;
    int MAXIMUM = Short.MAX_VALUE;

    static JoystickDispatcher dispatcher(Executor context) {
        return new JoystickDispatcher(context);
    }

    static Joystick button(Button button) {
        return new Joystick() {
            @Override
            public void press() {
                button.press();
            }

            @Override
            public void release() {
                button.release();
            }
        };
    }

    default void horizontal(int value) {

    }

    default void vertical(int value) {

    }
}
