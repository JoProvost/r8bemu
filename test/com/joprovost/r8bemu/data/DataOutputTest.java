package com.joprovost.r8bemu.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataOutputTest {

    @Test
    void signed() {
        assertEquals(0, data(0x00, 0xff).signed());
        assertEquals(-2, data(0xfe, 0xff).signed());
        assertEquals(127, data(0x7f, 0xff).signed());
        assertEquals(-128, data(0x80, 0xff).signed());

        assertEquals(0, data(0x0000, 0xffff).signed());
        assertEquals(-2, data(0xfffe, 0xffff).signed());
        assertEquals(32767, data(0x7fff, 0xffff).signed());
        assertEquals(-32768, data(0x8000, 0xffff).signed());

        assertEquals(0, data(0x0, 0xf).signed());
        assertEquals(-2, data(0xe, 0xf).signed());
        assertEquals(7, data(0x7, 0xf).signed());
        assertEquals(-8, data(0x8, 0xf).signed());
    }

    @Test
    void isClearOrSet() {
        assertTrue(data(0b0, 0b1).isClear());
        assertFalse(data(0b0, 0b1).isSet());

        assertFalse(data(0b1, 0b1).isClear());
        assertTrue(data(0b1, 0b1).isSet());

        assertTrue(data(0x0, 0xf).isClear());
        assertFalse(data(0x0, 0xf).isSet());

        assertFalse(data(0x1, 0xf).isClear());
        assertTrue(data(0x1, 0xf).isSet());
    }

    private DataOutput data(int unsigned, int mask) {
        return new DataOutput() {
            public int unsigned() { return unsigned; }
            public int mask() { return mask; }
        };
    }
}
