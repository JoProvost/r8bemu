package com.joprovost.r8bemu.devices.keyboard;

import java.util.List;

public class KeyboardMapping {
    public static List<Key> keystroke(String keystroke) {
        switch (keystroke.charAt(0)) {
            case '@': return List.of(Key.AT);

            case '0': return List.of(Key.KEY_0);
            case '1': return List.of(Key.KEY_1);
            case '2': return List.of(Key.KEY_2);
            case '3': return List.of(Key.KEY_3);
            case '4': return List.of(Key.KEY_4);
            case '5': return List.of(Key.KEY_5);
            case '6': return List.of(Key.KEY_6);
            case '7': return List.of(Key.KEY_7);
            case '8': return List.of(Key.KEY_8);
            case '9': return List.of(Key.KEY_9);

            case '!': return List.of(Key.SHIFT, Key.KEY_1);
            case '"': return List.of(Key.SHIFT, Key.KEY_2);
            case '#': return List.of(Key.SHIFT, Key.KEY_3);
            case '$': return List.of(Key.SHIFT, Key.KEY_4);
            case '%': return List.of(Key.SHIFT, Key.KEY_5);
            case '&': return List.of(Key.SHIFT, Key.KEY_6);
            case '\'': return List.of(Key.SHIFT, Key.KEY_7);
            case '(': return List.of(Key.SHIFT, Key.KEY_8);
            case ')': return List.of(Key.SHIFT, Key.KEY_9);

            case 'a': return List.of(Key.KEY_A);
            case 'b': return List.of(Key.KEY_B);
            case 'c': return List.of(Key.KEY_C);
            case 'd': return List.of(Key.KEY_D);
            case 'e': return List.of(Key.KEY_E);
            case 'f': return List.of(Key.KEY_F);
            case 'g': return List.of(Key.KEY_G);
            case 'h': return List.of(Key.KEY_H);
            case 'i': return List.of(Key.KEY_I);
            case 'j': return List.of(Key.KEY_J);
            case 'k': return List.of(Key.KEY_K);
            case 'l': return List.of(Key.KEY_L);
            case 'm': return List.of(Key.KEY_M);
            case 'n': return List.of(Key.KEY_N);
            case 'o': return List.of(Key.KEY_O);
            case 'p': return List.of(Key.KEY_P);
            case 'q': return List.of(Key.KEY_Q);
            case 'r': return List.of(Key.KEY_R);
            case 's': return List.of(Key.KEY_S);
            case 't': return List.of(Key.KEY_T);
            case 'u': return List.of(Key.KEY_U);
            case 'v': return List.of(Key.KEY_V);
            case 'w': return List.of(Key.KEY_W);
            case 'x': return List.of(Key.KEY_X);
            case 'y': return List.of(Key.KEY_Y);
            case 'z': return List.of(Key.KEY_Z);

            case 'A': return List.of(Key.SHIFT, Key.KEY_A);
            case 'B': return List.of(Key.SHIFT, Key.KEY_B);
            case 'C': return List.of(Key.SHIFT, Key.KEY_C);
            case 'D': return List.of(Key.SHIFT, Key.KEY_D);
            case 'E': return List.of(Key.SHIFT, Key.KEY_E);
            case 'F': return List.of(Key.SHIFT, Key.KEY_F);
            case 'G': return List.of(Key.SHIFT, Key.KEY_G);
            case 'H': return List.of(Key.SHIFT, Key.KEY_H);
            case 'I': return List.of(Key.SHIFT, Key.KEY_I);
            case 'J': return List.of(Key.SHIFT, Key.KEY_J);
            case 'K': return List.of(Key.SHIFT, Key.KEY_K);
            case 'L': return List.of(Key.SHIFT, Key.KEY_L);
            case 'M': return List.of(Key.SHIFT, Key.KEY_M);
            case 'N': return List.of(Key.SHIFT, Key.KEY_N);
            case 'O': return List.of(Key.SHIFT, Key.KEY_O);
            case 'P': return List.of(Key.SHIFT, Key.KEY_P);
            case 'Q': return List.of(Key.SHIFT, Key.KEY_Q);
            case 'R': return List.of(Key.SHIFT, Key.KEY_R);
            case 'S': return List.of(Key.SHIFT, Key.KEY_S);
            case 'T': return List.of(Key.SHIFT, Key.KEY_T);
            case 'U': return List.of(Key.SHIFT, Key.KEY_U);
            case 'V': return List.of(Key.SHIFT, Key.KEY_V);
            case 'W': return List.of(Key.SHIFT, Key.KEY_W);
            case 'X': return List.of(Key.SHIFT, Key.KEY_X);
            case 'Y': return List.of(Key.SHIFT, Key.KEY_Y);
            case 'Z': return List.of(Key.SHIFT, Key.KEY_Z);

            case ':': return List.of(Key.COLON);
            case ';': return List.of(Key.SMCOL);
            case ',': return List.of(Key.COMMA);
            case '-': return List.of(Key.MINUS);
            case '.': return List.of(Key.DOT);
            case '/': return List.of(Key.SLASH);

            case '*': return List.of(Key.SHIFT, Key.COLON);
            case '+': return List.of(Key.SHIFT, Key.SMCOL);
            case '<': return List.of(Key.SHIFT, Key.COMMA);
            case '=': return List.of(Key.SHIFT, Key.MINUS);
            case '>': return List.of(Key.SHIFT, Key.DOT);
            case '?': return List.of(Key.SHIFT, Key.SLASH);

            case '[': return List.of(Key.SHIFT, Key.DOWN);
            case ']': return List.of(Key.SHIFT, Key.RIGHT);

            case ' ': return List.of(Key.SPACE);
            case '\r': return List.of(Key.ENTER);
            case '\n': return List.of(Key.ENTER);
            case '\b': return List.of(Key.LEFT); // BACKSPACE KEY
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
}
