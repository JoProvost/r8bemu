package com.joprovost.r8bemu.memory;

import com.joprovost.r8bemu.data.DataOutput;

public interface AddressRange {
    static AddressRange of(int start, int end) {
        return new AddressRange() {
            @Override
            public boolean contains(int address) {
                return address >= start && address <= end;
            }

            public String toString() {
                return "[" + DataOutput.hex(start, 0xffff) + ", " + DataOutput.hex(end, 0xffff) + "]";
            }
        };
    }

    boolean contains(int address);
}
