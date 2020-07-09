package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.io.Joystick;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

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

public class NumpadJoystickDriver implements KeyListener {
    private final Joystick joystick;
    private Set<Integer> arrows = new HashSet<>();

    public NumpadJoystickDriver(Joystick joystick) {
        this.joystick = joystick;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        onArrowEvent(e, arrows::add);
        switch (e.getKeyCode()) {
            case VK_NUMPAD1:
                joystick.horizontal(Joystick.MINIMUM);
                joystick.vertical(Joystick.MAXIMUM);
                break;
            case VK_NUMPAD2:
                joystick.horizontal(Joystick.CENTER);
                joystick.vertical(Joystick.MAXIMUM);
                break;
            case VK_NUMPAD3:
                joystick.horizontal(Joystick.MAXIMUM);
                joystick.vertical(Joystick.MAXIMUM);
                break;
            case VK_NUMPAD4:
                joystick.horizontal(Joystick.MINIMUM);
                joystick.vertical(Joystick.CENTER);
                break;
            case VK_NUMPAD5:
                joystick.horizontal(Joystick.CENTER);
                joystick.vertical(Joystick.CENTER);
                break;
            case VK_NUMPAD6:
                joystick.horizontal(Joystick.MAXIMUM);
                joystick.vertical(Joystick.CENTER);
                break;
            case VK_NUMPAD7:
                joystick.horizontal(Joystick.MINIMUM);
                joystick.vertical(Joystick.MINIMUM);
                break;
            case VK_NUMPAD8:
                joystick.horizontal(Joystick.CENTER);
                joystick.vertical(Joystick.MINIMUM);
                break;
            case VK_NUMPAD9:
                joystick.horizontal(Joystick.MAXIMUM);
                joystick.vertical(Joystick.MINIMUM);
                break;
            case VK_NUMPAD0:
                joystick.press();
                break;
        }
    }

    private void onArrowEvent(KeyEvent e, Consumer<Integer> action) {
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
                action.accept(e.getKeyCode());
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        onArrowEvent(e, keycode -> {
            arrows.remove(keycode);
            if (arrows.isEmpty()) {
                joystick.horizontal(Joystick.CENTER);
                joystick.vertical(Joystick.CENTER);
            }
        });

        if (e.getKeyCode() == VK_NUMPAD0) {
            joystick.release();
        }
    }
}
