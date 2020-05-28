package com.joprovost.r8bemu.devices.keyboard;

import java.util.List;

// Mapping on the MC6821 ports
//      PB0   PB1   PB2   PB3   PB4   PB5   PB6   PB7
// PA0    @     A     B     C     D     E     F     G
// PA1    H     I     J     K     L     M     N     O
// PA2    P     Q     R     S     T     U     V     W
// PA3    X     Y     Z    Up  Down  Left Right Space
// PA4    0     1     2     3     4     5     6     7
// PA5    8     9     :     ;     ,     -     .     /
// PA6  ENT   CLR   BRK   N/C   N/C   N/C   N/C  SHFT
public enum KeyStroke {
    // @formatter:off
    KEY_AT(0, 0),  KEY_A(0, 1),  KEY_B(0, 2),     KEY_C(0, 3),  KEY_D(0, 4),  KEY_E(0, 5),  KEY_F(0, 6),  KEY_G(0, 7),
     KEY_H(1, 0),  KEY_I(1, 1),  KEY_J(1, 2),     KEY_K(1, 3),  KEY_L(1, 4),  KEY_M(1, 5),  KEY_N(1, 6),  KEY_O(1, 7),
     KEY_P(2, 0),  KEY_Q(2, 1),  KEY_R(2, 2),     KEY_S(2, 3),  KEY_T(2, 4),  KEY_U(2, 5),  KEY_V(2, 6),  KEY_W(2, 7),
     KEY_X(3, 0),  KEY_Y(3, 1),  KEY_Z(3, 2),        UP(3, 3),   DOWN(3, 4),   LEFT(3, 5),  RIGHT(3, 6),  SPACE(3, 7),
     KEY_0(4, 0),  KEY_1(4, 1),  KEY_2(4, 2),     KEY_3(4, 3),  KEY_4(4, 4),  KEY_5(4, 5),  KEY_6(4, 6),  KEY_7(4, 7),
     KEY_8(5, 0),  KEY_9(5, 1),  COLON(5, 2), SEMICOLON(5, 3),  COMMA(5, 4),  MINUS(5, 5), PERIOD(5, 6),  SLASH(5, 7),
     ENTER(6, 0),  CLEAR(6, 1),  BREAK(6, 2),                                                             SHIFT(6, 7);
    // @formatter:on

    public final int row;
    public final int column;

    KeyStroke(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int portA(int portB) {
        if ((portB & (1 << column)) == 0) {
            return 0xff ^ (1 << row);
        }
        return 0xff;
    }
}
