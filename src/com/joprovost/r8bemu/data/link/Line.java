package com.joprovost.r8bemu.data.link;

import java.util.ArrayList;
import java.util.List;

public class Line implements LinePort {
    private final List<LineOutputHandler> handlers = new ArrayList<>();
    private final String description;
    private boolean value;

    protected Line(String description) {
        this.description = description;
    }

    public static Line named(String description) {
        return new Line(description);
    }

    @Override
    public void to(LineOutputHandler handler) {
        handlers.add(handler);
    }

    @Override
    public void set(boolean value) {
        if (this.value == value) return;
        this.value = value;
        for (var handler : handlers) handler.handle(this);
    }

    @Override
    public boolean isClear() {
        return !value;
    }

    @Override
    public String description() {
        return description;
    }
}
