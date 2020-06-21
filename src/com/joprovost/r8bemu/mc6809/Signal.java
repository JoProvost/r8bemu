package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.LogicAccess;
import com.joprovost.r8bemu.data.LogicInput;
import com.joprovost.r8bemu.data.Variable;

import static com.joprovost.r8bemu.data.DataAccessSubset.bit;

/**
 * Input signal pins of the Motorola 6809
 * Note: Those signals are usually low when set... let's just say they are set when they are set().
 */
public final class Signal implements LogicAccess {
    public static final DataAccess SIGNALS = Variable.ofMask(0xff).describedAs("SIGNALS");
    public static final Signal RESET = Signal.of(bit(SIGNALS, 0).describedAs("RESET"));
    public static final Signal IRQ = Signal.of(bit(SIGNALS, 1).describedAs("IRQ"));
    public static final Signal FIRQ = Signal.of(bit(SIGNALS, 2).describedAs("FIRQ"));
    public static final Signal NMI = Signal.of(bit(SIGNALS, 3).describedAs("NMI"));

    private final LogicAccess signal;

    public Signal(LogicAccess signal) {
        this.signal = signal;
    }

    private static Signal of(LogicAccess dataAccess) {
        return new Signal(dataAccess);
    }

    public static void reset() {
        SIGNALS.value(0);
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
        signal.set(value);
    }

    @Override
    public boolean isClear() {
        return signal.isClear();
    }
}
