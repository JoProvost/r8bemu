package com.joprovost.r8bemu.memory;

import com.joprovost.r8bemu.data.DataOutput;

import java.util.Iterator;
import java.util.stream.IntStream;

public class AddressRange implements Iterable<Integer> {
    private final int start;
    private final int end;

    private AddressRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public static AddressRange of(int start, int end) {
        return new AddressRange(start, end);
    }

    public boolean contains(int address) {
        return address >= start && address <= end;
    }

    @Override
    public String toString() {
        return "[" + DataOutput.hex(start, 0xffff) + ", " + DataOutput.hex(end, 0xffff) + "]";
    }

    @Override
    public Iterator<Integer> iterator() {
        return IntStream.rangeClosed(start, end).iterator();
    }
}
