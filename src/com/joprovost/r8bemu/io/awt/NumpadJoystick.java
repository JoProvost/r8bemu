package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.io.Button;
import com.joprovost.r8bemu.io.Joystick;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static java.awt.event.KeyEvent.VK_NUMPAD0;
import static java.awt.event.KeyEvent.VK_NUMPAD1;
import static java.awt.event.KeyEvent.VK_NUMPAD2;
import static java.awt.event.KeyEvent.VK_NUMPAD3;
import static java.awt.event.KeyEvent.VK_NUMPAD4;
import static java.awt.event.KeyEvent.VK_NUMPAD5;
import static java.awt.event.KeyEvent.VK_NUMPAD6;
import static java.awt.event.KeyEvent.VK_NUMPAD7;
import static java.awt.event.KeyEvent.VK_NUMPAD8;
import static java.awt.event.KeyEvent.VK_NUMPAD9;

public class NumpadJoystick implements KeyListener {
    private final Joystick joystick;
    private final Button button;

    public NumpadJoystick(Joystick joystick, Button button) {
        this.joystick = joystick;
        this.button = button;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case VK_NUMPAD1:
                joystick.horizontal(0);
                joystick.vertical(63);
                break;
            case VK_NUMPAD2:
                joystick.horizontal(32);
                joystick.vertical(63);
                break;
            case VK_NUMPAD3:
                joystick.horizontal(63);
                joystick.vertical(63);
                break;
            case VK_NUMPAD4:
                joystick.horizontal(0);
                joystick.vertical(32);
                break;
            case VK_NUMPAD5:
                joystick.horizontal(32);
                joystick.vertical(32);
                break;
            case VK_NUMPAD6:
                joystick.horizontal(63);
                joystick.vertical(32);
                break;
            case VK_NUMPAD7:
                joystick.horizontal(0);
                joystick.vertical(0);
                break;
            case VK_NUMPAD8:
                joystick.horizontal(32);
                joystick.vertical(0);
                break;
            case VK_NUMPAD9:
                joystick.horizontal(63);
                joystick.vertical(0);
                break;
            case VK_NUMPAD0:
                button.press();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case VK_NUMPAD1:
            case VK_NUMPAD2:
            case VK_NUMPAD3:
            case VK_NUMPAD4:
            case VK_NUMPAD5:
            case VK_NUMPAD6:
            case VK_NUMPAD7:
            case VK_NUMPAD8:
            case VK_NUMPAD9:
                joystick.horizontal(32);
                joystick.vertical(32);
                break;
            case VK_NUMPAD0:
                button.release();
                break;
        }
    }
}
