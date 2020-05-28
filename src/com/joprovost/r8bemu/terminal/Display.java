package com.joprovost.r8bemu.terminal;

public interface Display {
    void ascii(int row, int column, Color fg, Color bg, int code);
    void sgm4(int row, int column, Color fg, Color bg, int luma);
}
