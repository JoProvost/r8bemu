package com.joprovost.r8bemu.data;

import com.joprovost.r8bemu.memory.MemoryMapped;

import java.util.Optional;

public class MemoryAccess implements DataAccess {
    private final MemoryMapped device;
    private final int address;
    private final Size size;
    private final String description;

    private MemoryAccess(MemoryMapped device, int address, Size size, String description) {
        this.device = device;
        this.address = address;
        this.size = size;
        this.description = description;
    }

    public MemoryAccess(MemoryMapped device, int address, Size size) {
        this(device, address, size, null);
    }

    public static MemoryAccess of(MemoryMapped device, int address, Size size) {
        return new MemoryAccess(device, address, size);
    }

    public static MemoryAccess of(MemoryMapped device, int address, Size size, String description) {
        return new MemoryAccess(device, address, size, description);
    }

    public static MemoryAccess of(MemoryMapped device, DataOutput address, Size size) {
        return new MemoryAccess(device, address.unsigned(), size, address.description());
    }

    @Override
    public void set(int value) {
        switch (size) {
            case WORD_8:
                device.write(address, value & mask());
                break;
            case WORD_16:
                device.write(address, (value & mask()) >> 8);
                device.write(address + 1, value & 0xff);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported size : " + size);
        }
    }

    @Override
    public int unsigned() {
        switch (size) {
            case WORD_8:
                return device.read(address);
            case WORD_16:
                return device.read(address) << 8 | device.read(address + 1);
            default:
                throw new UnsupportedOperationException("Unsupported size : " + size);
        }
    }

    @Override
    public int mask() {
        return size.mask();
    }

    @Override
    public String description() {
        return Optional.ofNullable(description).orElse("[$"+Integer.toHexString(address)+"]");
    }

    @Override
    public String toString() {
        return "#$" + Integer.toHexString(unsigned())  + " at ($" + Integer.toHexString(address) + ")";
    }
}
