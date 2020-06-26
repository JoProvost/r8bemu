package com.joprovost.r8bemu.awt;

import com.joprovost.r8bemu.devices.keyboard.Key;
import com.joprovost.r8bemu.devices.keyboard.Keyboard;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import static com.joprovost.r8bemu.devices.keyboard.Key.character;

public class AWTKeyboard implements KeyListener {
    private final Keyboard keyboard;

    public AWTKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        var keys = character(e.getKeyChar());
        if (!keys.isEmpty()) keyboard.type(keys);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER: keyboard.type(List.of(Key.ENTER)); break;

            case KeyEvent.VK_BACK_SPACE: keyboard.type(List.of(Key.LEFT)); break;
            case KeyEvent.VK_LEFT: keyboard.type(List.of(Key.LEFT)); break;
            case KeyEvent.VK_UP: keyboard.type(List.of(Key.UP)); break;
            case KeyEvent.VK_RIGHT: keyboard.type(List.of(Key.RIGHT)); break;
            case KeyEvent.VK_DOWN: keyboard.type(List.of(Key.DOWN)); break;

            case KeyEvent.VK_ESCAPE: keyboard.type(List.of(Key.BREAK)); break;
            case KeyEvent.VK_HOME: keyboard.type(List.of(Key.CLEAR)); break;
            case KeyEvent.VK_DELETE: keyboard.type(List.of(Key.CLEAR)); break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
