package com.joprovost.r8bemu;

public interface Display {
    void ascii(int row, int column, Color fg, Color bg, int code);
    void sgm4(int row, int column, Color fg, Color bg, int luma);

    enum Color {
        GREEN, YELLOW, BLUE, RED, BUFF, CYAN, MAGENTA, ORANGE, BLACK
    }
}
