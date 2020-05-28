package com.joprovost.r8bemu.memory;

/**
 * Helper class that connects multiple parallel devices on the same address and data bus.
 * All devices will receive read and write events. When reading, the data is "OR"ed between all devices thus
 * each parallel device must return "0" on unmapped addresses.
 */
public class MemoryBus implements MemoryMapped {

    private final MemoryMapped[] devices;

    public MemoryBus(MemoryMapped[] devices) {
        this.devices = devices;
    }

    public static MemoryMapped bus(MemoryMapped... devices) {
        return new MemoryBus(devices);
    }

    @Override
    public int read(int address) {
        int data = 0;
        for (var device : devices) {
            data |= device.read(address);
        }
        return data;
    }

    @Override
    public void write(int address, int data) {
        for (var device : devices) {
            device.write(address, data);
        }
    }
}
