package com.joprovost.r8bemu.data.transform;

import com.joprovost.r8bemu.data.DataOutput;

import java.util.function.Function;

public class Addition implements DataOutput {
    private static final Function<DataOutput, DataOutput> INCREMENT = incrementBy(ONE);

    private final DataOutput origin;
    private final DataOutput offset;

    private Addition(DataOutput origin, DataOutput offset) {
        this.origin = origin;
        this.offset = offset;
    }

    public static Addition of(DataOutput origin, DataOutput offset) {
        return new Addition(origin, offset);
    }

    public static Function<DataOutput, DataOutput> incrementBy(DataOutput offset) {
        return origin -> new Addition(origin, offset);
    }

    public static Function<DataOutput, DataOutput> increment() {
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
