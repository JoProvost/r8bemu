package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataInput;
import com.joprovost.r8bemu.data.Variable;

import static com.joprovost.r8bemu.data.DataAccessSubset.bit;

/**
 * Input signal pins of the Motorola 6809
 * Note: Those signals are usually low when set... let's just say they are set when they are set().
 */
public final class Signal implements DataAccess {
    public static final Signal SIGNALS = Signal.of(Variable.ofMask(0xff).describedAs("SIGNALS"));
    public static final Signal RESET = Signal.of(bit(SIGNALS, 0).describedAs("RESET"));
    public static final Signal IRQ = Signal.of(bit(SIGNALS, 1).describedAs("IRQ"));
    public static final Signal FIRQ = Signal.of(bit(SIGNALS, 2).describedAs("FIRQ"));
    public static final Signal NMI = Signal.of(bit(SIGNALS, 3).describedAs("NMI"));

    private final DataAccess signal;

    public Signal(DataAccess signal) {
        this.signal = signal;
    }

    private static Signal of(DataAccess dataAccess) {
        return new Signal(dataAccess);
    }

    public static void reset() {
        SIGNALS.set(0);
    }

    public static DataInput none() {
        return new DataInput() {
            @Override
            public void set(boolean value) {
            }

            @Override
            public void set(int value) {
            }
        };
    }

    @Override
    public String description() {
        return signal.description();
    }

    @Override
    public int unsigned() {
        return signal.unsigned();
    }

    @Override
    public int mask() {
        return signal.mask();
    }

    @Override
    public void set(int value) {
        signal.set(value);
    }

    @Override
    public String toString() {
        return signal.toString();
    }
}
