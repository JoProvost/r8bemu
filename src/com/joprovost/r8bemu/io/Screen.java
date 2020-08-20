package com.joprovost.r8bemu.io;

public interface Screen {
    Font FONT = Font.coco8x12();

    static ScreenDispatcher dispatcher() {
        return new ScreenDispatcher();
    }

    default void glyph(int row, int column, Color fg, Color bg, char glyph, int line) {
        for (int x = 0; x < 8; x++) {
            pixel(column * 8 + x, row * 12 + line, FONT.pixel(glyph, line, x) ? fg : bg);
        }
    }

    void pixel(int x, int y, Color color);

    enum Color {
        GREEN, YELLOW, BLUE, RED, BUFF, CYAN, MAGENTA, ORANGE, BLACK
    }
}
