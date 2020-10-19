package com.joprovost.r8bemu.graphic;

import java.awt.*;

public interface Screen {

    static ScreenDispatcher dispatcher() {
        return new ScreenDispatcher();
    }

    default void character(char utf8, int row, int column, Color fg, Color bg) {
    }

    void pixel(int x, int y, Color color, int width, int height);

    default void pixels(int x, int y, Color[] pixels, int width, int height) {
        for (int i = 0; i < pixels.length; i++) {
            pixel(x + i, y, pixels[i], width, height);
        }
    }
}
