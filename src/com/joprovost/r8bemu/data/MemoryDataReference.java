package com.joprovost.r8bemu.data;

import com.joprovost.r8bemu.memory.MemoryDevice;

import java.util.Optional;

import static com.joprovost.r8bemu.data.transform.Addition.incrementBy;

public class MemoryDataReference implements DataAccess {
    private final MemoryDevice device;
    private final int address;
    private final Size size;
    private final String description;

    private MemoryDataReference(MemoryDevice device, int address, Size size, String description) {
        this.device = device;
        this.address = address;
        this.size = size;
        this.description = description;
    }

    public MemoryDataReference(MemoryDevice device, int address, Size size) {
        this(device, address, size, null);
    }

    public static MemoryDataReference of(MemoryDevice device, int address, Size size) {
        return new MemoryDataReference(device, address, size);
    }

    public static MemoryDataReference of(MemoryDevice device, int address, Size size, String description) {
        return new MemoryDataReference(device, address, size, description);
    }

    public static MemoryDataReference of(MemoryDevice device, DataOutput address, Size size) {
        return new MemoryDataReference(device, address.value(), size, address.description());
    }

    public static DataAccess next(MemoryDevice memory, Size size, DataAccess register) {
        return of(memory, register.post(incrementBy(size)), size);
    }

    @Override
    public void value(int value) {
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
    public int value() {
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
        return "#$" + Integer.toHexString(value())  + " at ($" + Integer.toHexString(address) + ")";
    }
}
