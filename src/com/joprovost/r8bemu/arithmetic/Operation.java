package com.joprovost.r8bemu.arithmetic;

import com.joprovost.r8bemu.data.DataOutput;

import java.util.Optional;

public interface Operation extends DataOutput {
    boolean overflow();
    default Optional<Boolean> carry() { return Optional.empty(); }
    default boolean negative() { return signed() < 0; }
    default boolean zero() { return unsigned() == 0; }
    default Optional<Boolean> halfCarry() { return Optional.empty(); }
}
