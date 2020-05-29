package com.joprovost.r8bemu.data;

import com.joprovost.r8bemu.mc6809.Register;
import com.joprovost.r8bemu.memory.MemoryMapped;

import java.util.Optional;

import static com.joprovost.r8bemu.arithmetic.Addition.incrementBy;

public class Reference implements DataAccess {
    private final MemoryMapped device;
    private final int address;
    private final Size size;
    private final String description;

    private Reference(MemoryMapped device, int address, Size size, String description) {
        this.device = device;
        this.address = address;
        this.size = size;
        this.description = description;
    }

    public Reference(MemoryMapped device, int address, Size size) {
        this(device, address, size, null);
    }

    public static Reference of(MemoryMapped device, int address, Size size) {
        return new Reference(device, address, size);
    }

    public static Reference of(MemoryMapped device, int address, Size size, String description) {
        return new Reference(device, address, size, description);
    }

    public static Reference of(MemoryMapped device, DataOutput address, Size size) {
        return new Reference(device, address.unsigned(), size, address.description());
    }

    public static DataAccess next(MemoryMapped memory, Size size, Register register) {
        return of(memory, register.post(incrementBy(size)), size);
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
