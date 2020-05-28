package com.joprovost.r8bemu.data;

import com.joprovost.r8bemu.memory.MemoryMapped;
import com.joprovost.r8bemu.memory.StringMemory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemoryAccessTest {
    MemoryMapped memory = new StringMemory();

    @Test
    void readSingleByte() {
        var data = MemoryAccess.of(memory, 10, Size.WORD_8);
        memory.write(10, 55);
        assertEquals(55, data.unsigned());
    }

    @Test
    void writeSingleByte() {
        var data = MemoryAccess.of(memory, 5, Size.WORD_8);
        data.set(88);
        assertEquals(88, memory.read(5));
    }

    @Test
    void read16bitWord() {
        var data = MemoryAccess.of(memory, 10, Size.WORD_16);
        memory.write(10, 0xab, 0xcd);
        assertEquals(0xabcd, data.unsigned());
    }

    @Test
    void write16bitWord() {
        var data = MemoryAccess.of(memory, 5, Size.WORD_16);
        data.set(0x1234);
        assertEquals(0x12, memory.read(5));
        assertEquals(0x34, memory.read(6));
    }

}
