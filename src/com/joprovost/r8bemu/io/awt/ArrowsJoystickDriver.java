package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.data.BitOutput;
import com.joprovost.r8bemu.io.Joystick;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_UP;

public class ArrowsJoystickDriver implements KeyListener {
    private final Joystick joystick;
    private final BitOutput keyboardGamepad;

    public ArrowsJoystickDriver(Joystick joystick, BitOutput keyboardGamepad) {
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
                joystick.vertical(Joystick.MINIMUM);
                break;
            case VK_LEFT:
                joystick.horizontal(Joystick.MINIMUM);
                break;
            case VK_RIGHT:
                joystick.horizontal(Joystick.MAXIMUM);
                break;
            case VK_DOWN:
                joystick.vertical(Joystick.MAXIMUM);
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
                joystick.vertical(Joystick.CENTER);
                break;
            case VK_LEFT:
                joystick.horizontal(Joystick.CENTER);
                break;
            case VK_RIGHT:
                joystick.horizontal(Joystick.CENTER);
                break;
            case VK_DOWN:
                joystick.vertical(Joystick.CENTER);
                break;
            case VK_SHIFT:
                joystick.release();
                break;
        }
    }
}
