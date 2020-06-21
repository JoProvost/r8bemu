package com.joprovost.r8bemu.data;

import java.util.function.Function;

import static com.joprovost.r8bemu.data.Value.ONE;

public class Subtraction implements DataOutput {
    private final DataOutput origin;
    private final DataOutput offset;

    private Subtraction(DataOutput origin, DataOutput offset) {
        this.origin = origin;
        this.offset = offset;
    }

    public static Subtraction of(DataOutput origin, DataOutput offset) {
        return new Subtraction(origin, offset);
    }

    public static Function<DataOutput, DataOutput> decrementBy(DataOutput offset) {
        return origin -> new Subtraction(origin, offset);
    }

    public static Function<DataOutput, DataOutput> decrement() {
        return decrementBy(ONE);
    }

    @Override
    public int value() {
        return (origin.value() - offset.value()) & mask();
    }

    @Override
    public int mask() {
        return origin.mask();
    }

    @Override
    public String description() {
        return origin.description() + " - " + offset.description();
    }

    @Override
    public String toString() {
        return description() + "' = $" + hex();
    }
}
