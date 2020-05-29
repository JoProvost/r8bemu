package com.joprovost.r8bemu.arithmetic;

import com.joprovost.r8bemu.data.DataOutput;

import java.util.Optional;
import java.util.function.Function;

import static com.joprovost.r8bemu.data.Value.ONE;
import static com.joprovost.r8bemu.data.Value.ZERO;

public class Subtraction implements Operation {
    private final DataOutput origin;
    private final DataOutput offset;
    private final DataOutput carry;
    private final String description;

    private Subtraction(DataOutput origin, DataOutput offset, DataOutput carry, String description) {
        this.origin = origin;
        this.offset = offset;
        this.carry = carry;
        this.description = description;
    }

    public static Subtraction of(DataOutput origin, DataOutput offset) {
        return new Subtraction(origin, offset, ZERO, null);
    }

    public static Subtraction of(DataOutput origin, DataOutput offset, DataOutput carry) {
        return new Subtraction(origin, offset, carry, null);
    }

    public static Function<DataOutput, Operation> decrementBy(DataOutput offset) {
        return origin -> new Subtraction(origin, offset, ZERO, null);
    }

    public static Function<DataOutput, Operation> decrement() {
        return decrementBy(ONE);
    }

    @Override
    public int unsigned() {
        return (origin.unsigned() - offset.unsigned() - carry.unsigned()) & mask();
    }

    @Override
    public int mask() {
        return origin.mask();
    }

    @Override
    public String description() {
        return Optional.ofNullable(description).orElse("{" + origin + "} - {" + offset + "})");
    }

    @Override
    public String toString() {
        return description() + "' = $" + hex();
    }

    public Optional<Boolean> carry() {
        return Optional.of(unsigned() != origin.unsigned() - offset.unsigned() - carry.unsigned());
    }

    @Override
    public boolean overflow() {
        return signed() != origin.signed() - offset.signed() - carry.unsigned();
    }
}
