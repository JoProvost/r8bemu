package com.joprovost.r8bemu.io;

public interface Screen {

    static ScreenDispatcher dispatcher() {
        return new ScreenDispatcher();
    }

    default void character(char utf8, int row, int column, Color fg, Color bg) {
    }

    void pixel(int x, int y, Color color);

    enum Color {
        GREEN, YELLOW, BLUE, RED, BUFF, CYAN, MAGENTA, ORANGE, BLACK
    }
}
