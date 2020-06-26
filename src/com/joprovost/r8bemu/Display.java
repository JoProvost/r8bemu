package com.joprovost.r8bemu;

public interface Display {
    String ASCII_CHARSET = "@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]↑← !\"#$%&'()*+,-./0123456789:;<=>?";
    String GRAPHICS4_CHARSET = " ▗▖▄▝▐▞▟▘▚▌▙▀▜▛█";
    Font FONT = Font.standard();

    default void character(int row, int column, Color fg, Color bg, int ascii) {
        character(row, column, fg, bg, ASCII_CHARSET.charAt(ascii % ASCII_CHARSET.length()));
    }

    default void graphics4(int row, int column, Color fg, Color bg, int graphics4) {
        character(row, column, fg, bg, GRAPHICS4_CHARSET.charAt(graphics4 % GRAPHICS4_CHARSET.length()));
    }

    default void character(int row, int column, Color fg, Color bg, char character) {
        var glyph = FONT.character(character);
        if (glyph == null) return;
        for (int y = 0; y < glyph.size(); y++) {
            int pixels = glyph.get(y);
            for (int x = 0; x < 8; x++) {
                pixel(column * 8 + x - 8, row * 12 + y - 12, (((pixels >> (7 - x)) & 1) == 0) ? bg : fg);
            }
        }
    }

    void pixel(int x, int y, Color color);

    enum Color {
        GREEN, YELLOW, BLUE, RED, BUFF, CYAN, MAGENTA, ORANGE, BLACK
    }
}
