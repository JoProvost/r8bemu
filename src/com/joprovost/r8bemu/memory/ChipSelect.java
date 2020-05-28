package com.joprovost.r8bemu.memory;

public class ChipSelect implements MemoryMapped {
    private final AddressRange range;
    private final MemoryMapped device;

    public ChipSelect(AddressRange range, MemoryMapped device) {
        this.range = range;
        this.device = device;
    }

    public static ChipSelect mapping(AddressRange range, MemoryMapped peripheral) {
        return new ChipSelect(range, peripheral);
    }

    @Override
    public int read(int address) {
        if (range.contains(address)) return device.read(address);
        return 0;
    }

    @Override
    public void write(int address, int data) {
        if (range.contains(address)) device.write(address, data);
    }
}
