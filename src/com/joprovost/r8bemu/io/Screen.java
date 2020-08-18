package com.joprovost.r8bemu.io;

import java.util.List;

public interface Screen {
    Font FONT = Font.coco8x12();

    static ScreenDispatcher dispatcher() {
        return new ScreenDispatcher();
    }

    default void glyph(int row, int column, Color fg, Color bg, char glyph, int line) {
        List<Integer> image = FONT.image(glyph);
        if (image == null) return;
        int pixels = image.get(line);
        for (int x = 0; x < 8; x++) {
            pixel(column * 8 + x, row * 12 + line, (((pixels >> (7 - x)) & 1) == 0) ? bg : fg);
        }
    }

    void pixel(int x, int y, Color color);

    enum Color {
        GREEN, YELLOW, BLUE, RED, BUFF, CYAN, MAGENTA, ORANGE, BLACK
    }
}
