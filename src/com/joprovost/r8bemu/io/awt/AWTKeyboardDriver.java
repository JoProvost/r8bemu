package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.data.BitOutput;
import com.joprovost.r8bemu.io.Key;
import com.joprovost.r8bemu.io.Keyboard;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Set;

import static com.joprovost.r8bemu.io.Key.character;

public class AWTKeyboardDriver implements KeyListener {
    private final Keyboard keyboard;
    private final BitOutput buffered;
    private final BitOutput gamepad;
    private Set<Key> state = Set.of();

    public AWTKeyboardDriver(Keyboard keyboard, BitOutput buffered, BitOutput gamepad) {
        this.keyboard = keyboard;
        this.buffered = buffered;
        this.gamepad = gamepad;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_DOWN:
                if (gamepad.isSet()) return;
        }

        var keys = character(e.getKeyChar());

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER: keys = Set.of(Key.ENTER); break;

            case KeyEvent.VK_BACK_SPACE: keys = Set.of(Key.LEFT); break;
            case KeyEvent.VK_LEFT: keys = Set.of(Key.LEFT); break;
            case KeyEvent.VK_UP: keys = Set.of(Key.UP); break;
            case KeyEvent.VK_RIGHT: keys = Set.of(Key.RIGHT); break;
            case KeyEvent.VK_DOWN: keys = Set.of(Key.DOWN); break;

            case KeyEvent.VK_ESCAPE: keys = Set.of(Key.BREAK); break;
            case KeyEvent.VK_HOME: keys = Set.of(Key.CLEAR); break;
            case KeyEvent.VK_DELETE: keys = Set.of(Key.CLEAR); break;
        }

        if (state.equals(keys)) return;
        state = keys;

        if (keys.isEmpty()) return;

        if (buffered.isSet())
            keyboard.type(keys);
        else
            keyboard.press(keys);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (buffered.isClear())
            keyboard.release(state);
        state = Set.of();
    }
}
