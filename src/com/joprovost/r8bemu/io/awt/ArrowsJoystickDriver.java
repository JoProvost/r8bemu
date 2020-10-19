package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.io.JoystickInput;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_UP;

public class ArrowsJoystickDriver implements KeyListener {
    private final JoystickInput joystick;
    private final DiscreteOutput keyboardGamepad;

    public ArrowsJoystickDriver(JoystickInput joystick, DiscreteOutput keyboardGamepad) {
        this.joystick = joystick;
        this.keyboardGamepad = keyboardGamepad;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (keyboardGamepad.isClear()) return;
        switch (e.getKeyCode()) {
            case VK_UP:
                joystick.vertical(JoystickInput.MINIMUM);
                break;
            case VK_LEFT:
                joystick.horizontal(JoystickInput.MINIMUM);
                break;
            case VK_RIGHT:
                joystick.horizontal(JoystickInput.MAXIMUM);
                break;
            case VK_DOWN:
                joystick.vertical(JoystickInput.MAXIMUM);
                break;
            case VK_SHIFT:
                joystick.press();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (keyboardGamepad.isClear()) return;
        switch (e.getKeyCode()) {
            case VK_UP:
                joystick.vertical(JoystickInput.CENTER);
                break;
            case VK_LEFT:
                joystick.horizontal(JoystickInput.CENTER);
                break;
            case VK_RIGHT:
                joystick.horizontal(JoystickInput.CENTER);
                break;
            case VK_DOWN:
                joystick.vertical(JoystickInput.CENTER);
                break;
            case VK_SHIFT:
                joystick.release();
                break;
        }
    }
}
