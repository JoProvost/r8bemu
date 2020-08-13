package com.joprovost.r8bemu.io;

import java.util.ArrayList;
import java.util.List;

public class DisplayDispatcher implements Display {
    private final List<Display> targets = new ArrayList<>();

    public void dispatchTo(Display target) {
        this.targets.add(target);
    }

    @Override
    public void glyph(int row, int column, Color fg, Color bg, char glyph, int line) {
        for (var target : targets) target.glyph(row, column, fg, bg, glyph, line);
    }

    @Override
    public void pixel(int x, int y, Color color) {
        for (var target : targets) target.pixel(x, y, color);
    }
}
