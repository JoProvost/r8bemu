package com.joprovost.r8bemu.io;

import java.util.Set;

public enum Key {
    // @formatter:off
    AT,    KEY_A, KEY_B, KEY_C, KEY_D, KEY_E, KEY_F, KEY_G,
    KEY_H, KEY_I, KEY_J, KEY_K, KEY_L, KEY_M, KEY_N, KEY_O,
    KEY_P, KEY_Q, KEY_R, KEY_S, KEY_T, KEY_U, KEY_V, KEY_W,
    KEY_X, KEY_Y, KEY_Z, UP,    DOWN,  LEFT,  RIGHT, SPACE,
    KEY_0, KEY_1, KEY_2, KEY_3, KEY_4, KEY_5, KEY_6, KEY_7,
    KEY_8, KEY_9, COLON, SMCOL, COMMA, MINUS, DOT,   SLASH,
    ENTER, CLEAR, BREAK,                             SHIFT;
    // @formatter:on

    public static Set<Key> character(char character) {
        switch (character) {
            case '@': return Set.of(AT);

            case '0': return Set.of(KEY_0);
            case '1': return Set.of(KEY_1);
            case '2': return Set.of(KEY_2);
            case '3': return Set.of(KEY_3);
            case '4': return Set.of(KEY_4);
            case '5': return Set.of(KEY_5);
            case '6': return Set.of(KEY_6);
            case '7': return Set.of(KEY_7);
            case '8': return Set.of(KEY_8);
            case '9': return Set.of(KEY_9);

            case '!': return Set.of(SHIFT, KEY_1);
            case '"': return Set.of(SHIFT, KEY_2);
            case '#': return Set.of(SHIFT, KEY_3);
            case '$': return Set.of(SHIFT, KEY_4);
            case '%': return Set.of(SHIFT, KEY_5);
            case '&': return Set.of(SHIFT, KEY_6);
            case '\'': return Set.of(SHIFT, KEY_7);
            case '(': return Set.of(SHIFT, KEY_8);
            case ')': return Set.of(SHIFT, KEY_9);
            case '_': return Set.of(SHIFT, KEY_0);

            case 'a': return Set.of(KEY_A);
            case 'b': return Set.of(KEY_B);
            case 'c': return Set.of(KEY_C);
            case 'd': return Set.of(KEY_D);
            case 'e': return Set.of(KEY_E);
            case 'f': return Set.of(KEY_F);
            case 'g': return Set.of(KEY_G);
            case 'h': return Set.of(KEY_H);
            case 'i': return Set.of(KEY_I);
            case 'j': return Set.of(KEY_J);
            case 'k': return Set.of(KEY_K);
            case 'l': return Set.of(KEY_L);
            case 'm': return Set.of(KEY_M);
            case 'n': return Set.of(KEY_N);
            case 'o': return Set.of(KEY_O);
            case 'p': return Set.of(KEY_P);
            case 'q': return Set.of(KEY_Q);
            case 'r': return Set.of(KEY_R);
            case 's': return Set.of(KEY_S);
            case 't': return Set.of(KEY_T);
            case 'u': return Set.of(KEY_U);
            case 'v': return Set.of(KEY_V);
            case 'w': return Set.of(KEY_W);
            case 'x': return Set.of(KEY_X);
            case 'y': return Set.of(KEY_Y);
            case 'z': return Set.of(KEY_Z);

            case 'A': return Set.of(SHIFT, KEY_A);
            case 'B': return Set.of(SHIFT, KEY_B);
            case 'C': return Set.of(SHIFT, KEY_C);
            case 'D': return Set.of(SHIFT, KEY_D);
            case 'E': return Set.of(SHIFT, KEY_E);
            case 'F': return Set.of(SHIFT, KEY_F);
            case 'G': return Set.of(SHIFT, KEY_G);
            case 'H': return Set.of(SHIFT, KEY_H);
            case 'I': return Set.of(SHIFT, KEY_I);
            case 'J': return Set.of(SHIFT, KEY_J);
            case 'K': return Set.of(SHIFT, KEY_K);
            case 'L': return Set.of(SHIFT, KEY_L);
            case 'M': return Set.of(SHIFT, KEY_M);
            case 'N': return Set.of(SHIFT, KEY_N);
            case 'O': return Set.of(SHIFT, KEY_O);
            case 'P': return Set.of(SHIFT, KEY_P);
            case 'Q': return Set.of(SHIFT, KEY_Q);
            case 'R': return Set.of(SHIFT, KEY_R);
            case 'S': return Set.of(SHIFT, KEY_S);
            case 'T': return Set.of(SHIFT, KEY_T);
            case 'U': return Set.of(SHIFT, KEY_U);
            case 'V': return Set.of(SHIFT, KEY_V);
            case 'W': return Set.of(SHIFT, KEY_W);
            case 'X': return Set.of(SHIFT, KEY_X);
            case 'Y': return Set.of(SHIFT, KEY_Y);
            case 'Z': return Set.of(SHIFT, KEY_Z);

            case ':': return Set.of(COLON);
            case ';': return Set.of(SMCOL);
            case ',': return Set.of(COMMA);
            case '-': return Set.of(MINUS);
            case '.': return Set.of(DOT);
            case '/': return Set.of(SLASH);

            case '*': return Set.of(SHIFT, COLON);
            case '+': return Set.of(SHIFT, SMCOL);
            case '<': return Set.of(SHIFT, COMMA);
            case '=': return Set.of(SHIFT, MINUS);
            case '>': return Set.of(SHIFT, DOT);
            case '?': return Set.of(SHIFT, SLASH);

            case '[': return Set.of(SHIFT, DOWN);
            case ']': return Set.of(SHIFT, RIGHT);

            case ' ': return Set.of(SPACE);
        }
        return Set.of();
    }
}
