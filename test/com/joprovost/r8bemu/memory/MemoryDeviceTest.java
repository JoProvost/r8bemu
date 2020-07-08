package com.joprovost.r8bemu.memory;

import com.joprovost.r8bemu.data.Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryDeviceTest {

    MemoryDevice memory = new StringMemory(new StringBuffer("                "));

    @Test
    void writeMultipleIntegers() {
        memory.write(2, '1', '2', '3', '7', '8', '9');
        assertEquals("  123789        ", memory.toString());
    }

    @Test
    void writeByteArray() {
        memory.write(4, new byte[]{'5', '4', '3', '2', '1', '0'});
        assertEquals("    543210      ", memory.toString());
    }

    @Test
    void readWithDataOutputTypeAddreess() {
        memory.write(4, 99);
        assertEquals(99, memory.read(Value.asByte(4)));
    }

    @Test
    void writeWithDataOutputTypeAddreess() {
        memory.write(Value.asByte(4), 88);
        assertEquals(88, memory.read(4));
    }

    @Test
    void writesOnBusSelectingTheRightDevice() {
        MemoryDevice first = new Memory(0xf);
        MemoryDevice second = new Memory(0xf);
        MemoryDevice third = new Memory(0xf);

        MemoryDevice bus = MemoryDevice.bus(
                MemoryDevice.map(AddressRange.range(0x0, 0xf), first),
                MemoryDevice.map(AddressRange.range(0x10, 0x1f), second),
                MemoryDevice.map(AddressRange.range(0x20, 0x2f), third)
        );

        bus.write(0x10, 100);
        assertEquals(0, first.read(0x10));
        assertEquals(100, second.read(0x10));
        assertEquals(0, third.read(0x10));
    }

    @Test
    void readsFromBusSelectingTheRightDevice() {
        MemoryDevice first = new Memory(0xf);
        MemoryDevice second = new Memory(0xf);
        MemoryDevice third = new Memory(0xf);

        MemoryDevice bus = MemoryDevice.bus(
                MemoryDevice.map(AddressRange.range(0x0, 0xf), first),
                MemoryDevice.map(AddressRange.range(0x10, 0x1f), second),
                MemoryDevice.map(AddressRange.range(0x20, 0x2f), third)
        );

        third.write(0x5, 200);
        assertEquals(0, bus.read(0x05));
        assertEquals(0, bus.read(0x15));
        assertEquals(200, bus.read(0x25));
    }
}
