package com.joprovost.r8bemu.data.transform;

import com.joprovost.r8bemu.data.binary.BinaryOutput;

import java.util.function.Function;

public class Subtraction implements BinaryOutput {
    private final BinaryOutput origin;
    private final BinaryOutput offset;

    private Subtraction(BinaryOutput origin, BinaryOutput offset) {
        this.origin = origin;
        this.offset = offset;
    }

    public static Subtraction of(BinaryOutput origin, BinaryOutput offset) {
        return new Subtraction(origin, offset);
    }

    public static Function<BinaryOutput, BinaryOutput> decrementBy(BinaryOutput offset) {
        return origin -> new Subtraction(origin, offset);
    }

    public static Function<BinaryOutput, BinaryOutput> decrement() {
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
