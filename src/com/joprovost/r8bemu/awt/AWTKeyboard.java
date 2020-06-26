package com.joprovost.r8bemu.awt;

import com.joprovost.r8bemu.devices.keyboard.Key;
import com.joprovost.r8bemu.devices.keyboard.Keyboard;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import static com.joprovost.r8bemu.devices.keyboard.KeyboardMapping.keystroke;

public class AWTKeyboard implements KeyListener {
    private final Keyboard keyboard;

    public AWTKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    public void type(String keystroke) {
        keyboard.type(keystroke(keystroke));
    }

    @Override
    public void keyTyped(KeyEvent e) {
        keyboard.type(keystroke(String.valueOf(e.getKeyChar())));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT: keyboard.type(List.of(Key.LEFT)); break;
            case KeyEvent.VK_UP: keyboard.type(List.of(Key.UP)); break;
            case KeyEvent.VK_RIGHT: keyboard.type(List.of(Key.RIGHT)); break;
            case KeyEvent.VK_DOWN: keyboard.type(List.of(Key.DOWN)); break;
            case KeyEvent.VK_HOME: keyboard.type(List.of(Key.CLEAR)); break;
            case KeyEvent.VK_DELETE: keyboard.type(List.of(Key.CLEAR)); break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
