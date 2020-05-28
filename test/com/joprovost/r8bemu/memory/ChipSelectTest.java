package com.joprovost.r8bemu.memory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChipSelectTest {

    @Test
    void writesOnBusSelectingTheRightDevice() {
        MemoryMapped first = new Memory(0xf);
        MemoryMapped second = new Memory(0xf);
        MemoryMapped third = new Memory(0xf);

        MemoryMapped bus = MemoryBus.bus(
                ChipSelect.mapping(AddressRange.of(0x0, 0xf), first),
                ChipSelect.mapping(AddressRange.of(0x10, 0x1f), second),
                ChipSelect.mapping(AddressRange.of(0x20, 0x2f), third)
        );

        bus.write(0x10, 100);
        assertEquals(0, first.read(0x10));
        assertEquals(100, second.read(0x10));
        assertEquals(0, third.read(0x10));
    }

    @Test
    void readsFromBusSelectingTheRightDevice() {
        MemoryMapped first = new Memory(0xf);
        MemoryMapped second = new Memory(0xf);
        MemoryMapped third = new Memory(0xf);

        MemoryMapped bus = MemoryBus.bus(
                ChipSelect.mapping(AddressRange.of(0x0, 0xf), first),
                ChipSelect.mapping(AddressRange.of(0x10, 0x1f), second),
                ChipSelect.mapping(AddressRange.of(0x20, 0x2f), third)
        );

        third.write(0x5, 200);
        assertEquals(0, bus.read(0x05));
        assertEquals(0, bus.read(0x15));
        assertEquals(200, bus.read(0x25));
    }
}
