package com.joprovost.r8bemu.terminal;

import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.devices.keyboard.KeyStroke;
import com.joprovost.r8bemu.devices.keyboard.Keyboard;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;


public class Terminal implements Display, ClockAware {
    public static final String ASCII_CHARSET = "@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]↑← !\"#$%&'()*+,-./0123456789:;<=>?";
    public static final String SGM4_CHARSET = " ▗▖▄▝▐▞▟▘▚▌▙▀▜▛█";

    private static final int MARGIN_TOP = 1;
    private static final int MARGIN_LEFT = 4;
    private static final int HEIGHT = 16;
    private final InputStream inputStream;
    private final PrintStream printStream;
    private final Keyboard keyboard;

    Deque<List<KeyStroke>> keystrokes = new ArrayDeque<>();
    int keyEventNumber = 0;

    public Terminal(InputStream inputStream, PrintStream printStream, Keyboard keyboard) {
        this.inputStream = inputStream;
        this.printStream = printStream;
        this.keyboard = keyboard;
    }

    @Override
    public void ascii(int row, int column, Color fg, Color bg, int code) {
        begin();
        move(row, column);
        color(fg, bg);
        printStream.print(ASCII_CHARSET.charAt(code));
        end();
    }

    @Override
    public void sgm4(int row, int column, Color fg, Color bg, int luma) {
        begin();
        move(row, column);
        color(fg, bg);
        printStream.print(SGM4_CHARSET.charAt(luma));
        end();
    }

    public void begin() {
        printStream.print("\u001b[?25l");
    }

    public void end() {
        printStream.print("\u001b[" + (1 + HEIGHT + (MARGIN_TOP * 2)) + ";1f");
        printStream.print("\u001b[0m");
        printStream.print("\u001b[?25h");
    }

    public void move(int row, int column) {
        printStream.print("\u001b[" + (MARGIN_TOP + row) + ";" + (MARGIN_LEFT + column) + "f");
    }

    public void color(Color fg, Color bg) {
        printStream.print("\u001b[38;5;" + fg.ansi256 + "m");
        printStream.print("\u001b[48;5;" + bg.ansi256 + "m");
    }

    @Override
    public void tick(long tick) throws IOException {
        var terminalKey = readKey();
        if (terminalKey.filter(x -> x.charAt(0) == 3).isPresent()) {
            throw new EOFException("Closed by user");
        }

        terminalKey.map(this::keyStroke).ifPresent(keystrokes::add);

        if (tick % 8000 == 0) {
            if (keyEventNumber % 2 == 0) {
                if (!keystrokes.isEmpty())
                    keyboard.press(keystrokes.poll());
            } else {
                keyboard.release();
            }
            keyEventNumber++;
        }
    }

    private Optional<String> readKey() throws IOException {
        byte[] buffer = new byte[16];
        if (inputStream.available() > 0) {
            int len = inputStream.read(buffer);
            return Optional.of(new String(buffer).substring(0, len));

        }
        return Optional.empty();
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
            case 127: return List.of(KeyStroke.CLEAR);

            case 27:
                if (key.length() < 3) return List.of(KeyStroke.BREAK);
                switch (key.charAt(2)) {
                    case 65: return List.of(KeyStroke.UP);
                    case 66: return List.of(KeyStroke.DOWN);
                    case 68: return List.of(KeyStroke.LEFT);
                    case 67: return List.of(KeyStroke.RIGHT);
                }
                return List.of(KeyStroke.PERIOD);
        }
        return List.of();
    }
}
