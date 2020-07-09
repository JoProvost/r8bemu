package com.joprovost.r8bemu.io;

import java.util.ArrayList;
import java.util.List;

public class DisplayDispatcher implements Display {
    private final List<Display> targets = new ArrayList<>();

    public void dispatchTo(Display target) {
        this.targets.add(target);
    }

    @Override
    public void character(int row, int column, Color fg, Color bg, char character) {
        for (var target : targets) target.character(row, column, fg, bg, character);
    }

    @Override
    public void pixel(int x, int y, Color color) {
        for (var target : targets) target.pixel(x, y, color);
    }
}
