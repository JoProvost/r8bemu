package com.joprovost.r8bemu.io;

public interface Display {
    Font FONT = Font.coco8x12();

    static DisplayDispatcher dispatcher() {
        return new DisplayDispatcher();
    }

    default void character(int row, int column, Color fg, Color bg, char character) {
        var glyph = FONT.character(character);
        if (glyph == null) return;
        for (int y = 0; y < glyph.size(); y++) {
            int pixels = glyph.get(y);
            for (int x = 0; x < 8; x++) {
                pixel(column * 8 + x, row * 12 + y, (((pixels >> (7 - x)) & 1) == 0) ? bg : fg);
            }
        }
    }

    void pixel(int x, int y, Color color);

    enum Color {
        GREEN, YELLOW, BLUE, RED, BUFF, CYAN, MAGENTA, ORANGE, BLACK
    }
}
