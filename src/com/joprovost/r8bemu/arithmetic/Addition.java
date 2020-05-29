package com.joprovost.r8bemu.arithmetic;

import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.Value;

import java.util.Optional;
import java.util.function.Function;

import static com.joprovost.r8bemu.data.Value.ONE;

public class Addition implements Operation {
    private final DataOutput origin;
    private final DataOutput offset;
    private final DataOutput carry;
    private final String description;

    private Addition(DataOutput origin, DataOutput offset, DataOutput carry, String description) {
        this.origin = origin;
        this.offset = offset;
        this.carry = carry;
        this.description = description;
    }

    public static Addition of(DataOutput origin, DataOutput offset) {
        return new Addition(origin, offset, Value.ZERO, null);
    }

    public static Addition of(DataOutput origin, DataOutput offset, DataOutput carry) {
        return new Addition(origin, offset, carry, null);
    }

    public static Function<DataOutput, Operation> incrementBy(DataOutput offset) {
        return origin -> new Addition(origin, offset, Value.ZERO, null);
    }

    public static Function<DataOutput, Operation> increment() {
        return incrementBy(ONE);
    }

    @Override
    public int unsigned() {
        return (origin.unsigned() + offset.unsigned() + carry.unsigned()) & mask();
    }

    @Override
    public int mask() {
        return origin.mask();
    }

    @Override
    public String description() {
        return Optional.ofNullable(description).orElse("{" + origin + "} + {" + offset + "})");
    }

    @Override
    public String toString() {
        return description() +"' = $" + hex();
    }

    public Optional<Boolean> carry() {
        return Optional.of(unsigned() != origin.unsigned() + offset.unsigned() + carry.unsigned());
    }

    @Override
    public boolean overflow() {
        return signed() != origin.signed() + offset.signed() + carry.unsigned();
    }

    public Optional<Boolean> halfCarry() {
        if (mask() != 0xff) return Optional.empty();
        return Optional.of((origin.unsigned() & 0x0f) + (offset.unsigned() & 0x0f) + carry.unsigned() > 0x0f);
    }
}
