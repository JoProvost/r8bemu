package com.joprovost.r8bemu.io;

import java.util.Set;
import java.util.concurrent.Executor;

public interface Keyboard {
    static KeyboardDispatcher dispatcher(Executor emulatorContext) {
        return new KeyboardDispatcher(emulatorContext);
    }

    void type(Set<Key> keys);

    void press(Set<Key> keys);

    void release(Set<Key> keys);

    void pause(int delay);

    default void script(String sequence) {
        for (var line : sequence.split("\n")) {
            for (var character : line.toCharArray()) {
                type(Key.character(character));
            }
            type(Set.of(Key.ENTER));
            pause(4);
        }
    }
}
