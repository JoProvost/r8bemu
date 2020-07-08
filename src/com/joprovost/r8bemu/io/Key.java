package com.joprovost.r8bemu.io;

import java.util.List;

public enum Key {
    // @formatter:off
    // PB0          PB1          PB2          PB3          PB4          PB5          PB6          PB7
       AT(0, 0), KEY_A(0, 1), KEY_B(0, 2), KEY_C(0, 3), KEY_D(0, 4), KEY_E(0, 5), KEY_F(0, 6), KEY_G(0, 7), // PA0
    KEY_H(1, 0), KEY_I(1, 1), KEY_J(1, 2), KEY_K(1, 3), KEY_L(1, 4), KEY_M(1, 5), KEY_N(1, 6), KEY_O(1, 7), // PA1
    KEY_P(2, 0), KEY_Q(2, 1), KEY_R(2, 2), KEY_S(2, 3), KEY_T(2, 4), KEY_U(2, 5), KEY_V(2, 6), KEY_W(2, 7), // PA2
    KEY_X(3, 0), KEY_Y(3, 1), KEY_Z(3, 2),    UP(3, 3),  DOWN(3, 4),  LEFT(3, 5), RIGHT(3, 6), SPACE(3, 7), // PA3
    KEY_0(4, 0), KEY_1(4, 1), KEY_2(4, 2), KEY_3(4, 3), KEY_4(4, 4), KEY_5(4, 5), KEY_6(4, 6), KEY_7(4, 7), // PA4
    KEY_8(5, 0), KEY_9(5, 1), COLON(5, 2), SMCOL(5, 3), COMMA(5, 4), MINUS(5, 5),   DOT(5, 6), SLASH(5, 7), // PA5
    ENTER(6, 0), CLEAR(6, 1), BREAK(6, 2),                                                     SHIFT(6, 7); // PA6
    // @formatter:on

    public final int a;
    public final int b;

    Key(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public static List<Key> character(char character) {
        switch (character) {
            case '@': return List.of(AT);

            case '0': return List.of(KEY_0);
            case '1': return List.of(KEY_1);
            case '2': return List.of(KEY_2);
            case '3': return List.of(KEY_3);
            case '4': return List.of(KEY_4);
            case '5': return List.of(KEY_5);
            case '6': return List.of(KEY_6);
            case '7': return List.of(KEY_7);
            case '8': return List.of(KEY_8);
            case '9': return List.of(KEY_9);

            case '!': return List.of(SHIFT, KEY_1);
            case '"': return List.of(SHIFT, KEY_2);
            case '#': return List.of(SHIFT, KEY_3);
            case '$': return List.of(SHIFT, KEY_4);
            case '%': return List.of(SHIFT, KEY_5);
            case '&': return List.of(SHIFT, KEY_6);
            case '\'': return List.of(SHIFT, KEY_7);
            case '(': return List.of(SHIFT, KEY_8);
            case ')': return List.of(SHIFT, KEY_9);

            case 'a': return List.of(KEY_A);
            case 'b': return List.of(KEY_B);
            case 'c': return List.of(KEY_C);
            case 'd': return List.of(KEY_D);
            case 'e': return List.of(KEY_E);
            case 'f': return List.of(KEY_F);
            case 'g': return List.of(KEY_G);
            case 'h': return List.of(KEY_H);
            case 'i': return List.of(KEY_I);
            case 'j': return List.of(KEY_J);
            case 'k': return List.of(KEY_K);
            case 'l': return List.of(KEY_L);
            case 'm': return List.of(KEY_M);
            case 'n': return List.of(KEY_N);
            case 'o': return List.of(KEY_O);
            case 'p': return List.of(KEY_P);
            case 'q': return List.of(KEY_Q);
            case 'r': return List.of(KEY_R);
            case 's': return List.of(KEY_S);
            case 't': return List.of(KEY_T);
            case 'u': return List.of(KEY_U);
            case 'v': return List.of(KEY_V);
            case 'w': return List.of(KEY_W);
            case 'x': return List.of(KEY_X);
            case 'y': return List.of(KEY_Y);
            case 'z': return List.of(KEY_Z);

            case 'A': return List.of(SHIFT, KEY_A);
            case 'B': return List.of(SHIFT, KEY_B);
            case 'C': return List.of(SHIFT, KEY_C);
            case 'D': return List.of(SHIFT, KEY_D);
            case 'E': return List.of(SHIFT, KEY_E);
            case 'F': return List.of(SHIFT, KEY_F);
            case 'G': return List.of(SHIFT, KEY_G);
            case 'H': return List.of(SHIFT, KEY_H);
            case 'I': return List.of(SHIFT, KEY_I);
            case 'J': return List.of(SHIFT, KEY_J);
            case 'K': return List.of(SHIFT, KEY_K);
            case 'L': return List.of(SHIFT, KEY_L);
            case 'M': return List.of(SHIFT, KEY_M);
            case 'N': return List.of(SHIFT, KEY_N);
            case 'O': return List.of(SHIFT, KEY_O);
            case 'P': return List.of(SHIFT, KEY_P);
            case 'Q': return List.of(SHIFT, KEY_Q);
            case 'R': return List.of(SHIFT, KEY_R);
            case 'S': return List.of(SHIFT, KEY_S);
            case 'T': return List.of(SHIFT, KEY_T);
            case 'U': return List.of(SHIFT, KEY_U);
            case 'V': return List.of(SHIFT, KEY_V);
            case 'W': return List.of(SHIFT, KEY_W);
            case 'X': return List.of(SHIFT, KEY_X);
            case 'Y': return List.of(SHIFT, KEY_Y);
            case 'Z': return List.of(SHIFT, KEY_Z);

            case ':': return List.of(COLON);
            case ';': return List.of(SMCOL);
            case ',': return List.of(COMMA);
            case '-': return List.of(MINUS);
            case '.': return List.of(DOT);
            case '/': return List.of(SLASH);

            case '*': return List.of(SHIFT, COLON);
            case '+': return List.of(SHIFT, SMCOL);
            case '<': return List.of(SHIFT, COMMA);
            case '=': return List.of(SHIFT, MINUS);
            case '>': return List.of(SHIFT, DOT);
            case '?': return List.of(SHIFT, SLASH);

            case '[': return List.of(SHIFT, DOWN);
            case ']': return List.of(SHIFT, RIGHT);

            case ' ': return List.of(SPACE);
        }
        return List.of();
    }

    public int row(int column) {
        if ((column & (1 << b)) == 0) {
            return 0xff ^ (1 << a);
        }
        return 0xff;
    }
}
