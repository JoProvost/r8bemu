package com.joprovost.r8bemu.io;

public class DisplayDispatcher implements Display {
    Display target;

    public void dispatchTo(Display target) {
        this.target = target;
    }

    @Override
    public void character(int row, int column, Color fg, Color bg, char character) {
        if (target != null) target.character(row, column, fg, bg, character);
    }

    @Override
    public void pixel(int x, int y, Color color) {
        if (target != null) target.pixel(x, y, color);
    }
}
