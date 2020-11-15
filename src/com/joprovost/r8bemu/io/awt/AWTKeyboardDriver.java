package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.io.Key;
import com.joprovost.r8bemu.io.Keyboard;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Set;

import static com.joprovost.r8bemu.io.Key.character;

public class AWTKeyboardDriver implements KeyListener {
    private final Keyboard keyboard;
    private final DiscreteOutput direct;
    private final DiscreteOutput gamepad;
    private Set<Key> state = Set.of();

    public AWTKeyboardDriver(Keyboard keyboard, DiscreteOutput direct, DiscreteOutput gamepad) {
        this.keyboard = keyboard;
        this.direct = direct;
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

        Set<Key> keys = keys(e);
        if (keys.isEmpty()) return;

        if (direct.isSet()) {
            keyboard.press(keys);
        } else {
            if (state.equals(keys)) return;
            state = keys;
            keyboard.type(keys);
        }
    }

    public Set<Key> keys(KeyEvent e) {
        Set<Key> keys = Set.of();

        if (direct.isSet()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_SHIFT: keys = Set.of(Key.SHIFT); break;

                case KeyEvent.VK_AT: keys = Set.of(Key.AT); break;
                case KeyEvent.VK_OPEN_BRACKET: keys = Set.of(Key.AT); break;
                case KeyEvent.VK_NUMBER_SIGN: keys = Set.of(Key.AT); break;
                case KeyEvent.VK_BACK_QUOTE: keys = Set.of(Key.AT); break;

                case KeyEvent.VK_1: keys = Set.of(Key.KEY_1); break;
                case KeyEvent.VK_2: keys = Set.of(Key.KEY_2); break;
                case KeyEvent.VK_3: keys = Set.of(Key.KEY_3); break;
                case KeyEvent.VK_4: keys = Set.of(Key.KEY_4); break;
                case KeyEvent.VK_5: keys = Set.of(Key.KEY_5); break;
                case KeyEvent.VK_6: keys = Set.of(Key.KEY_6); break;
                case KeyEvent.VK_7: keys = Set.of(Key.KEY_7); break;
                case KeyEvent.VK_8: keys = Set.of(Key.KEY_8); break;
                case KeyEvent.VK_9: keys = Set.of(Key.KEY_9); break;
                case KeyEvent.VK_0: keys = Set.of(Key.KEY_0); break;

                case KeyEvent.VK_A: keys = Set.of(Key.KEY_A); break;
                case KeyEvent.VK_B: keys = Set.of(Key.KEY_B); break;
                case KeyEvent.VK_C: keys = Set.of(Key.KEY_C); break;
                case KeyEvent.VK_D: keys = Set.of(Key.KEY_D); break;
                case KeyEvent.VK_E: keys = Set.of(Key.KEY_E); break;
                case KeyEvent.VK_F: keys = Set.of(Key.KEY_F); break;
                case KeyEvent.VK_G: keys = Set.of(Key.KEY_G); break;
                case KeyEvent.VK_H: keys = Set.of(Key.KEY_H); break;
                case KeyEvent.VK_I: keys = Set.of(Key.KEY_I); break;
                case KeyEvent.VK_J: keys = Set.of(Key.KEY_J); break;
                case KeyEvent.VK_K: keys = Set.of(Key.KEY_K); break;
                case KeyEvent.VK_L: keys = Set.of(Key.KEY_L); break;
                case KeyEvent.VK_M: keys = Set.of(Key.KEY_M); break;
                case KeyEvent.VK_N: keys = Set.of(Key.KEY_N); break;
                case KeyEvent.VK_O: keys = Set.of(Key.KEY_O); break;
                case KeyEvent.VK_P: keys = Set.of(Key.KEY_P); break;
                case KeyEvent.VK_Q: keys = Set.of(Key.KEY_Q); break;
                case KeyEvent.VK_R: keys = Set.of(Key.KEY_R); break;
                case KeyEvent.VK_S: keys = Set.of(Key.KEY_S); break;
                case KeyEvent.VK_T: keys = Set.of(Key.KEY_T); break;
                case KeyEvent.VK_U: keys = Set.of(Key.KEY_U); break;
                case KeyEvent.VK_V: keys = Set.of(Key.KEY_V); break;
                case KeyEvent.VK_W: keys = Set.of(Key.KEY_W); break;
                case KeyEvent.VK_X: keys = Set.of(Key.KEY_X); break;
                case KeyEvent.VK_Y: keys = Set.of(Key.KEY_Y); break;
                case KeyEvent.VK_Z: keys = Set.of(Key.KEY_Z); break;

                case KeyEvent.VK_SPACE: keys = Set.of(Key.SPACE); break;
                case KeyEvent.VK_COLON: keys = Set.of(Key.COLON); break;
                case KeyEvent.VK_SEMICOLON: keys = Set.of(Key.SMCOL); break;
                case KeyEvent.VK_COMMA: keys = Set.of(Key.COMMA); break;
                case KeyEvent.VK_MINUS: keys = Set.of(Key.MINUS); break;
                case KeyEvent.VK_PERIOD: keys = Set.of(Key.DOT); break;
                case KeyEvent.VK_SLASH: keys = Set.of(Key.SLASH); break;
            }
        } else {
            keys = character(e.getKeyChar());
        }

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

            case KeyEvent.VK_ALT: keys = Set.of(Key.ALT); break;
            case KeyEvent.VK_CONTROL: keys = Set.of(Key.CTRL); break;
            case KeyEvent.VK_F1: keys = Set.of(Key.F1); break;
            case KeyEvent.VK_F2: keys = Set.of(Key.F2); break;
        }
        return keys;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (direct.isSet())
            keyboard.release(keys(e));
        else state = Set.of();
    }
}
