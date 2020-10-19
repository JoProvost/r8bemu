package com.joprovost.r8bemu.data.transform;

import com.joprovost.r8bemu.data.binary.BinaryOutput;

import java.util.function.Function;

public class Addition implements BinaryOutput {
    private static final Function<BinaryOutput, BinaryOutput> INCREMENT = incrementBy(ONE);

    private final BinaryOutput origin;
    private final BinaryOutput offset;

    private Addition(BinaryOutput origin, BinaryOutput offset) {
        this.origin = origin;
        this.offset = offset;
    }

    public static Addition of(BinaryOutput origin, BinaryOutput offset) {
        return new Addition(origin, offset);
    }

    public static Function<BinaryOutput, BinaryOutput> incrementBy(BinaryOutput offset) {
        return origin -> new Addition(origin, offset);
    }

    public static Function<BinaryOutput, BinaryOutput> increment() {
        return INCREMENT;
    }

    @Override
    public int value() {
        return (origin.value() + offset.value()) & mask();
    }

    @Override
    public int mask() {
        return origin.mask();
    }

    @Override
    public String description() {
        return origin.description() + " + " + offset.description();
    }

    @Override
    public String toString() {
        return description() +"' = $" + hex();
    }
}
