package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.LogicAccess;
import com.joprovost.r8bemu.data.LogicInput;
import com.joprovost.r8bemu.data.LogicVariable;
import com.joprovost.r8bemu.port.LogicOutputHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Input signal pins of the Motorola 6809
 * Note: Those signals are usually low when set... let's just say they are set when they are set().
 */
public final class Signal implements LogicAccess {
    public static final Signal RESET = Signal.of(LogicVariable.of(false));
    public static final Signal IRQ = Signal.of(LogicVariable.of(false));
    public static final Signal FIRQ = Signal.of(LogicVariable.of(false));
    public static final Signal NMI = Signal.of(LogicVariable.of(false));

    private final LogicAccess signal;
    private final List<LogicOutputHandler> handlers = new ArrayList<>();

    public Signal(LogicAccess signal) {
        this.signal = signal;
    }

    private static Signal of(LogicAccess dataAccess) {
        return new Signal(dataAccess);
    }

    public static void reset() {
        RESET.clear();
        IRQ.clear();
        FIRQ.clear();
        NMI.clear();
    }

    public static LogicInput none() {
        return value -> { };
    }

    @Override
    public String description() {
        return signal.description();
    }

    @Override
    public String toString() {
        return signal.toString();
    }

    @Override
    public void set(boolean value) {
        boolean oldValue = signal.isSet();
        signal.set(value);
        if (oldValue != value) for (var handler : handlers) handler.handle(this);
    }

    @Override
    public boolean isClear() {
        return signal.isClear();
    }

    public void trigger() {
        set();
        clear();
    }

    public void signalTo(LogicOutputHandler handler) {
        handlers.add(handler);
    }
}
