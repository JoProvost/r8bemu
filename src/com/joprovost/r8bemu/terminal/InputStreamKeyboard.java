package com.joprovost.r8bemu.terminal;

import com.joprovost.r8bemu.devices.keyboard.KeyboardMapping;
import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.clock.ClockAwareBusyState;
import com.joprovost.r8bemu.devices.keyboard.Key;
import com.joprovost.r8bemu.devices.keyboard.Keyboard;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

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

    public void type(String keystroke) {
        keyboard.type(KeyboardMapping.keystroke(keystroke));
    }

    public void sequence(String sequence) {
        for (var key : sequence.toCharArray()) {
            type(String.valueOf(key));
        }
    }

    public void script(Path path) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(sequence -> {
                sequence(sequence);
                keyboard.type(List.of(Key.ENTER));
                keyboard.pause(4);
            });
        }
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
