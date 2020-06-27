package com.joprovost.r8bemu.memory;

import java.util.function.Function;

public class ChipSelect implements MemoryMapped {
    private final MemoryMapped device;
    private final AddressRange range;
    private final Function<Integer, Integer> addressing;

    public ChipSelect(MemoryMapped device, AddressRange range, Function<Integer, Integer> addressing) {
        this.range = range;
        this.addressing = addressing;
        this.device = device;
    }

    public static ChipSelect mapping(MemoryMapped peripheral, AddressRange range) {
        return new ChipSelect(peripheral, range, it -> it);
    }

    public static ChipSelect mapping(MemoryMapped peripheral, AddressRange range, Function<Integer, Integer> addressing) {
        return new ChipSelect(peripheral, range, addressing);
    }

    @Override
    public int read(int address) {
        if (range.contains(address)) return device.read(addressing.apply(address));
        return 0;
    }

    @Override
    public void write(int address, int data) {
        if (range.contains(address)) device.write(addressing.apply(address), data);
    }
}
