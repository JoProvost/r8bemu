package com.joprovost.r8bemu.devices.keyboard;

import com.joprovost.r8bemu.clock.ClockState;
import com.joprovost.r8bemu.clock.ClockAware;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

public class KeyboardBuffer implements ClockAware {

    // At 900kHz, each key is pressed 44ms and released 44ms
    public static final int TYPE_DELAY = 40000;
    public static final int BOOT_DELAY = 1200000;

    public final ClockState clock = new ClockState();
    private final Deque<List<KeyStroke>> buffer = new ArrayDeque<>();
    private final Keyboard keyboard;

    private int keyEventNumber = 0;

    public KeyboardBuffer(Keyboard keyboard) {
        this.keyboard = keyboard;
        clock.busy(BOOT_DELAY);
    }

    @Override
    public void tick(long tick) {
        if (clock.at(tick).isBusy()) return;
        clock.busy(TYPE_DELAY);

        if (keyEventNumber++ % 2 == 0) {
            if (!buffer.isEmpty())
                keyboard.press(buffer.poll());
        } else {
            keyboard.release();
        }
    }

    public void type(String key) {
        type(keyStroke(key));
    }

    public void type(List<KeyStroke> key) {
        buffer.add(key);
    }

    public void script(String sequence) {
        for (var key : sequence.toCharArray()) {
            type(String.valueOf(key));
        }
    }

    public void script(Path path) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(sequence -> {
                script(sequence);
                type(List.of(KeyStroke.ENTER));
                type(List.of());
            });
        }
    }

    private List<KeyStroke> keyStroke(String key) {
        switch (key.charAt(0)) {
            case '@': return List.of(KeyStroke.KEY_AT);

            case '0': return List.of(KeyStroke.KEY_0);
            case '1': return List.of(KeyStroke.KEY_1);
            case '2': return List.of(KeyStroke.KEY_2);
            case '3': return List.of(KeyStroke.KEY_3);
            case '4': return List.of(KeyStroke.KEY_4);
            case '5': return List.of(KeyStroke.KEY_5);
            case '6': return List.of(KeyStroke.KEY_6);
            case '7': return List.of(KeyStroke.KEY_7);
            case '8': return List.of(KeyStroke.KEY_8);
            case '9': return List.of(KeyStroke.KEY_9);

            case '!': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_1);
            case '"': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_2);
            case '#': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_3);
            case '$': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_4);
            case '%': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_5);
            case '&': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_6);
            case '\'': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_7);
            case '(': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_8);
            case ')': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_9);

            case 'a': return List.of(KeyStroke.KEY_A);
            case 'b': return List.of(KeyStroke.KEY_B);
            case 'c': return List.of(KeyStroke.KEY_C);
            case 'd': return List.of(KeyStroke.KEY_D);
            case 'e': return List.of(KeyStroke.KEY_E);
            case 'f': return List.of(KeyStroke.KEY_F);
            case 'g': return List.of(KeyStroke.KEY_G);
            case 'h': return List.of(KeyStroke.KEY_H);
            case 'i': return List.of(KeyStroke.KEY_I);
            case 'j': return List.of(KeyStroke.KEY_J);
            case 'k': return List.of(KeyStroke.KEY_K);
            case 'l': return List.of(KeyStroke.KEY_L);
            case 'm': return List.of(KeyStroke.KEY_M);
            case 'n': return List.of(KeyStroke.KEY_N);
            case 'o': return List.of(KeyStroke.KEY_O);
            case 'p': return List.of(KeyStroke.KEY_P);
            case 'q': return List.of(KeyStroke.KEY_Q);
            case 'r': return List.of(KeyStroke.KEY_R);
            case 's': return List.of(KeyStroke.KEY_S);
            case 't': return List.of(KeyStroke.KEY_T);
            case 'u': return List.of(KeyStroke.KEY_U);
            case 'v': return List.of(KeyStroke.KEY_V);
            case 'w': return List.of(KeyStroke.KEY_W);
            case 'x': return List.of(KeyStroke.KEY_X);
            case 'y': return List.of(KeyStroke.KEY_Y);
            case 'z': return List.of(KeyStroke.KEY_Z);

            case 'A': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_A);
            case 'B': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_B);
            case 'C': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_C);
            case 'D': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_D);
            case 'E': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_E);
            case 'F': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_F);
            case 'G': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_G);
            case 'H': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_H);
            case 'I': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_I);
            case 'J': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_J);
            case 'K': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_K);
            case 'L': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_L);
            case 'M': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_M);
            case 'N': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_N);
            case 'O': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_O);
            case 'P': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_P);
            case 'Q': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_Q);
            case 'R': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_R);
            case 'S': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_S);
            case 'T': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_T);
            case 'U': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_U);
            case 'V': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_V);
            case 'W': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_W);
            case 'X': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_X);
            case 'Y': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_Y);
            case 'Z': return List.of(KeyStroke.SHIFT, KeyStroke.KEY_Z);

            case ':': return List.of(KeyStroke.COLON);
            case ';': return List.of(KeyStroke.SEMICOLON);
            case ',': return List.of(KeyStroke.COMMA);
            case '-': return List.of(KeyStroke.MINUS);
            case '.': return List.of(KeyStroke.PERIOD);
            case '/': return List.of(KeyStroke.SLASH);

            case '*': return List.of(KeyStroke.SHIFT, KeyStroke.COLON);
            case '+': return List.of(KeyStroke.SHIFT, KeyStroke.SEMICOLON);
            case '<': return List.of(KeyStroke.SHIFT, KeyStroke.COMMA);
            case '=': return List.of(KeyStroke.SHIFT, KeyStroke.MINUS);
            case '>': return List.of(KeyStroke.SHIFT, KeyStroke.PERIOD);
            case '?': return List.of(KeyStroke.SHIFT, KeyStroke.SLASH);

            case '[': return List.of(KeyStroke.SHIFT, KeyStroke.DOWN);
            case ']': return List.of(KeyStroke.SHIFT, KeyStroke.RIGHT);

            case ' ': return List.of(KeyStroke.SPACE);
            case 13: return List.of(KeyStroke.ENTER);
            case 127: return List.of(KeyStroke.LEFT); // BACKSPACE KEY

            case 27:
                if (key.length() < 3) return List.of(KeyStroke.BREAK);
                switch (key.substring(1)) {
                    case "[A": return List.of(KeyStroke.UP);
                    case "[B": return List.of(KeyStroke.DOWN);
                    case "[D": return List.of(KeyStroke.LEFT);
                    case "[C": return List.of(KeyStroke.RIGHT);
                    case "[1~": return List.of(KeyStroke.CLEAR); // HOME KEY
                    case "[3~": return List.of(KeyStroke.CLEAR); // DEL KEY
                }
        }
        return List.of();
    }

}
