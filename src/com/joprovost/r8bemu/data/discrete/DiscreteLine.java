package com.joprovost.r8bemu.data.discrete;

import java.util.ArrayList;
import java.util.List;

public class DiscreteLine implements DiscretePort {
    private final List<DiscteteOutputHandler> handlers = new ArrayList<>();
    private final String description;
    private boolean value;

    protected DiscreteLine(String description) {
        this.description = description;
    }

    public static DiscreteLine named(String description) {
        return new DiscreteLine(description);
    }

    @Override
    public void to(DiscteteOutputHandler handler) {
        handlers.add(handler);
    }

    @Override
    public void set(boolean value) {
        if (this.value == value) return;
        this.value = value;
        for (var handler : handlers) handler.handle(this);
    }

    @Override
    public boolean isSet() {
        return value;
    }

    @Override
    public String description() {
        return description;
    }
}
