package com.joprovost.r8bemu.arithmetic;

import com.joprovost.r8bemu.data.DataOutput;

import java.util.Optional;

public class ExclusiveDisjunction implements Operation {
    private final DataOutput origin;
    private final DataOutput offset;
    private final String description;

    private ExclusiveDisjunction(DataOutput origin, DataOutput offset, String description) {
        this.origin = origin;
        this.offset = offset;
        this.description = description;
    }

    public static ExclusiveDisjunction of(DataOutput origin, DataOutput offset) {
        return new ExclusiveDisjunction(origin, offset, null);
    }

    @Override
    public int unsigned() {
        return origin.unsigned() ^ offset.unsigned();
    }

    @Override
    public int mask() {
        return origin.mask();
    }

    @Override
    public String description() {
        return Optional.ofNullable(description).orElse("{" + origin + "} XOR {" + offset + "})");
    }

    @Override
    public String toString() {
        return description() + "' = $" + hex();
    }

    @Override
    public boolean overflow() {
        return false;
    }

    public Optional<Boolean> halfCarry() {
        return Optional.empty();
    }
}
