package com.joprovost.r8bemu.data.discrete;

import java.util.ArrayList;
import java.util.List;

public class Flag implements DiscretePort {
    private final List<DiscteteOutputHandler> handlers = new ArrayList<>();
    private boolean value = false;

    public static Flag value(boolean value) {
        Flag flag = new Flag();
        flag.set(value);
        return flag;
    }

    public synchronized boolean isSet() {
        return value;
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public synchronized void set(boolean value) {
        if (this.value == value) return;
        this.value = value;
        for (var handler : handlers) handler.handle(this);
    }

    @Override
    public String toString() {
        return "" + value;
    }

    @Override
    public void handle(DiscreteOutput state) {
        set(state.isSet());
    }

    @Override
    public void to(DiscteteOutputHandler handler) {
        handlers.add(handler);
    }
}
