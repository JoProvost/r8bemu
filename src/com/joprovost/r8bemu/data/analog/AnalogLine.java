package com.joprovost.r8bemu.data.analog;

import java.util.ArrayList;
import java.util.List;

public class AnalogLine implements AnalogPort {
    private final List<AnalogOutputHandler> handlers = new ArrayList<>();
    private final String description;
    private double value;

    protected AnalogLine(String description) {
        this.description = description;
    }

    public static AnalogLine named(String description) {
        return new AnalogLine(description);
    }

    @Override
    public void to(AnalogOutputHandler handler) {
        handlers.add(handler);
    }

    @Override
    public void value(double value) {
        if (this.value == value) return;
        this.value = value;
        for (var handler : handlers) handler.handle(this);
    }

    @Override
    public double value() {
        return value;
    }

    @Override
    public String description() {
        return description;
    }
}
