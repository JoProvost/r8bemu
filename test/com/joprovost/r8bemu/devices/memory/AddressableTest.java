package com.joprovost.r8bemu.devices.memory;

import com.joprovost.r8bemu.data.binary.BinaryValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressableTest {

    Addressable memory = new StringMemory(new StringBuffer("                "));

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
        assertEquals(99, memory.read(BinaryValue.asByte(4)));
    }

    @Test
    void writeWithDataOutputTypeAddreess() {
        memory.write(BinaryValue.asByte(4), 88);
        assertEquals(88, memory.read(4));
    }
}
