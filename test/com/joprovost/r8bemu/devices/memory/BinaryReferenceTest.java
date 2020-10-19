package com.joprovost.r8bemu.devices.memory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BinaryReferenceTest {
    Addressable memory = new StringMemory();

    @Test
    void readSingleByte() {
        var data = BinaryReference.of(memory, 10, Size.WORD_8);
        memory.write(10, 55);
        assertEquals(55, data.value());
    }

    @Test
    void writeSingleByte() {
        var data = BinaryReference.of(memory, 5, Size.WORD_8);
        data.value(88);
        assertEquals(88, memory.read(5));
    }

    @Test
    void read16bitWord() {
        var data = BinaryReference.of(memory, 10, Size.WORD_16);
        memory.write(10, 0xab, 0xcd);
        assertEquals(0xabcd, data.value());
    }

    @Test
    void write16bitWord() {
        var data = BinaryReference.of(memory, 5, Size.WORD_16);
        data.value(0x1234);
        assertEquals(0x12, memory.read(5));
        assertEquals(0x34, memory.read(6));
    }

}
