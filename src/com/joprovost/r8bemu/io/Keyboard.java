package com.joprovost.r8bemu.io;

import java.util.List;

public interface Keyboard {
    static KeyboardDispatcher dispatcher() {
        return new KeyboardDispatcher();
    }

    void type(List<Key> key);

    void pause(int delay);

    default void script(String sequence) {
        for (var line : sequence.split("\n")) {
            for (var character : line.toCharArray()) {
                type(Key.character(character));
            }
            type(List.of(Key.ENTER));
            pause(4);
        }
    }
}
