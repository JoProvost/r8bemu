package com.joprovost.r8bemu.devices.memory;

import com.joprovost.r8bemu.data.binary.BinaryAccess;
import com.joprovost.r8bemu.data.binary.BinaryOutput;

public enum Size implements BinaryOutput {
    WORD_8(0xff, ONE),
    WORD_16(0xffff, TWO);

    private final int mask;
    private final BinaryOutput bytes;

    Size(int mask, BinaryOutput bytes) {
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

    public static Size of(BinaryAccess data) {
        int mask = data.mask();
        for (Size size : values()) {
            if (size.mask() == mask) return size;
        }
        throw new IllegalArgumentException("Invalid mask : " + mask);
    }
}
