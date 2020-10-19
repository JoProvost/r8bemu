package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.io.JoystickInput;

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
    private final JoystickInput joystick;
    private final DiscreteOutput numpadGamepad;
    private final Set<Integer> arrows = new HashSet<>();

    public NumpadJoystickDriver(JoystickInput joystick, DiscreteOutput numpadGamepad) {
        this.joystick = joystick;
        this.numpadGamepad = numpadGamepad;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (numpadGamepad.isClear()) return;
        onArrowEvent(e, arrows::add);
        switch (e.getKeyCode()) {
            case VK_NUMPAD1:
                joystick.horizontal(JoystickInput.MINIMUM);
                joystick.vertical(JoystickInput.MAXIMUM);
                break;
            case VK_NUMPAD2:
                joystick.horizontal(JoystickInput.CENTER);
                joystick.vertical(JoystickInput.MAXIMUM);
                break;
            case VK_NUMPAD3:
                joystick.horizontal(JoystickInput.MAXIMUM);
                joystick.vertical(JoystickInput.MAXIMUM);
                break;
            case VK_NUMPAD4:
                joystick.horizontal(JoystickInput.MINIMUM);
                joystick.vertical(JoystickInput.CENTER);
                break;
            case VK_NUMPAD5:
                joystick.horizontal(JoystickInput.CENTER);
                joystick.vertical(JoystickInput.CENTER);
                break;
            case VK_NUMPAD6:
                joystick.horizontal(JoystickInput.MAXIMUM);
                joystick.vertical(JoystickInput.CENTER);
                break;
            case VK_NUMPAD7:
                joystick.horizontal(JoystickInput.MINIMUM);
                joystick.vertical(JoystickInput.MINIMUM);
                break;
            case VK_NUMPAD8:
                joystick.horizontal(JoystickInput.CENTER);
                joystick.vertical(JoystickInput.MINIMUM);
                break;
            case VK_NUMPAD9:
                joystick.horizontal(JoystickInput.MAXIMUM);
                joystick.vertical(JoystickInput.MINIMUM);
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
        if (numpadGamepad.isClear()) return;
        onArrowEvent(e, keycode -> {
            arrows.remove(keycode);
            if (arrows.isEmpty()) {
                joystick.horizontal(JoystickInput.CENTER);
                joystick.vertical(JoystickInput.CENTER);
            }
        });

        if (e.getKeyCode() == VK_NUMPAD0) {
            joystick.release();
        }
    }
}
