package com.joprovost.r8bemu.io;

import java.util.ArrayList;
import java.util.List;

public class ScreenDispatcher implements Screen {
    private final List<Screen> targets = new ArrayList<>();

    public void dispatchTo(Screen target) {
        this.targets.add(target);
    }

    @Override
    public void character(char utf8, int row, int column, Color fg, Color bg) {
        for (var target : targets) target.character(utf8, row, column, fg, bg);
    }

    @Override
    public void pixel(int x, int y, Color color) {
        for (var target : targets) target.pixel(x, y, color);
    }
}
