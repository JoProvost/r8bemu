package com.joprovost.r8bemu.data;

import static com.joprovost.r8bemu.data.Value.ONE;
import static com.joprovost.r8bemu.data.Value.TWO;

public enum Size implements DataOutput {
    WORD_8(0xff, ONE),
    WORD_16(0xffff, TWO);

    private final int mask;
    private final Value bytes;

    Size(int mask, Value bytes) {
        this.mask = mask;
        this.bytes = bytes;
    }

    @Override
    public String description() {
        return name();
    }

    @Override
    public int value() {
        return bytes.value();
    }

    public int mask() {
        return mask;
    }

    public static Size of(DataAccess data) {
        int mask = data.mask();
        for (Size size : values()) {
            if (size.mask() == mask) return size;
        }
        throw new IllegalArgumentException("Invalid mask : " + mask);
    }
}
