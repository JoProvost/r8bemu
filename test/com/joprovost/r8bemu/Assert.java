package com.joprovost.r8bemu;

import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.devices.memory.BinaryReference;
import com.joprovost.r8bemu.devices.memory.Size;
import org.junit.jupiter.api.Assertions;

import static com.joprovost.r8bemu.data.binary.BinaryOutput.hex;

public class Assert {
    public static void assertEquals(int expected, int actual) {
        Assertions.assertEquals("0x" + Integer.toHexString(expected), "0x" + Integer.toHexString(actual));
    }

    public static void assertEquals(String expected, String actual) {
        Assertions.assertEquals(expected, actual);
    }

    public static void assertRead(String expected, Addressable device, int address) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expected.length(); i++) sb.append((char) device.read(address + i));
        assertEquals(expected, sb.toString());
    }

    public static void assertReadWord(int expected, Addressable device, int address) {
        assertEquals(hex(expected, 0xffff), BinaryReference.of(device, address, Size.WORD_16).hex());
    }
}
