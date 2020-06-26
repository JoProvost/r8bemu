package com.joprovost.r8bemu.devices.keyboard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static com.joprovost.r8bemu.devices.keyboard.KeyboardMapping.keystroke;

public interface Keyboard {
    void type(List<Key> key);
    void pause(int delay);

    default void sequence(String sequence) {
        for (var key : sequence.toCharArray()) {
            type(keystroke(String.valueOf(key)));
        }
    }

    default void script(Path path) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(sequence -> {
                sequence(sequence);
                type(List.of(Key.ENTER));
                pause(4);
            });
        }
    }

    static KeyboardDispatcher dispatcher() {
        return new KeyboardDispatcher();
    }
}
