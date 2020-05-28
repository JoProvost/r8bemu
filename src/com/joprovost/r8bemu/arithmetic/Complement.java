package com.joprovost.r8bemu.arithmetic;

import com.joprovost.r8bemu.data.DataOutput;

import java.util.Optional;

public class Complement implements Operation {
    private final DataOutput origin;
    private final String description;

    private Complement(DataOutput origin, String description) {
        this.origin = origin;
        this.description = description;
    }

    public static Complement of(DataOutput origin) {
        return new Complement(origin, null);
    }

    @Override
    public int unsigned() {
        return (~origin.unsigned()) & mask();
    }

    @Override
    public int mask() {
        return origin.mask();
    }

    @Override
    public String description() {
        return Optional.ofNullable(description).orElse("NOT {" + origin + "}");
    }

    @Override
    public String toString() {
        return description() + "' = $" + hex();
    }

    @Override
    public boolean overflow() {
        return false;
    }

    @Override
    public Optional<Boolean> carry() {
        return Optional.of(true);
    }

    public Optional<Boolean> halfCarry() {
        return Optional.empty();
    }
}
