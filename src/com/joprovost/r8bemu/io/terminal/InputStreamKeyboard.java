package com.joprovost.r8bemu.io.terminal;

import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.clock.ClockAwareBusyState;
import com.joprovost.r8bemu.io.Key;
import com.joprovost.r8bemu.io.Keyboard;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class InputStreamKeyboard implements ClockAware {
    // At 900kHz, keys are fetched every 88ms
    private static final int TYPE_DELAY = 80000;

    private final ClockAwareBusyState state = new ClockAwareBusyState();

    private final InputStream inputStream;
    private final Keyboard keyboard;

    public InputStreamKeyboard(InputStream inputStream, Keyboard keyboard) {
        this.inputStream = inputStream;
        this.keyboard = keyboard;
    }

    public static List<Key> keys(String keystroke) {
        List<Key> keys = Key.character(keystroke.charAt(0));

        if (!keys.isEmpty()) return keys;

        switch (keystroke.charAt(0)) {
            case '\r': return List.of(Key.ENTER);
            case 127: return List.of(Key.LEFT); // BACKSPACE KEY

            case 27:
                if (keystroke.length() < 3) return List.of(Key.BREAK);
                switch (keystroke.substring(1)) {
                    case "[A": return List.of(Key.UP);
                    case "[B": return List.of(Key.DOWN);
                    case "[D": return List.of(Key.LEFT);
                    case "[C": return List.of(Key.RIGHT);
                    case "[1~": return List.of(Key.CLEAR); // HOME KEY
                    case "[3~": return List.of(Key.CLEAR); // DEL KEY
                }
        }
        return List.of();
    }

    public void type(String keystroke) {
        keyboard.type(keys(keystroke));
    }

    @Override
    public void tick(Clock clock) throws IOException {
        if (state.at(clock).isBusy()) return;
        state.busy(TYPE_DELAY);
        read();
    }

    private void read() throws IOException {
        byte[] buffer = new byte[16];
        if (inputStream.available() > 0) {
            int len = inputStream.read(buffer);
            if (buffer[0] == 3) throw new EOFException("Closed by user");
            type(new String(buffer).substring(0, len));
        }
    }
}