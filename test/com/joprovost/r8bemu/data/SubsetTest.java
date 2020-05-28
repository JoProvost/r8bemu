package com.joprovost.r8bemu.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubsetTest {

    DataAccess source = Variable.ofMask(0xffff);

    @Test
    void lsb() {
        var lsb = Subset.lsb(source);

        assertEquals(0xff, lsb.mask());

        source.set(0xfa1a);
        assertEquals(0x1a, lsb.unsigned());

        lsb.set(0xde);
        assertEquals(0xfade, source.unsigned());

        // No overflow
        lsb.set(0x872398de);
        assertEquals(0xfade, source.unsigned());
    }

    @Test
    void msb() {
        var msb = Subset.msb(source);

        assertEquals(0xff, msb.mask());

        source.set(0xfa1a);
        assertEquals(0xfa, msb.unsigned());

        msb.set(0xd0);
        assertEquals(0xd01a, source.unsigned());

        // No overflow
        msb.set(0x872398d0);
        assertEquals(0xd01a, source.unsigned());
    }

    @Test
    void bit() {

        var b0 = Subset.bit(source, 0);
        var b1 = Subset.bit(source, 1);
        var b7 = Subset.bit(source, 7);
        var b8 = Subset.bit(source, 8);
        var b15 = Subset.bit(source, 15);

        assertEquals(0b1, b1.mask());
        assertEquals(0b1, b15.mask());

        source.set(0b1111111111111111);
        assertTrue(b0.isSet());
        assertTrue(b1.isSet());
        assertTrue(b7.isSet());
        assertTrue(b8.isSet());
        assertTrue(b15.isSet());
        assertFalse(b0.isClear());
        assertFalse(b1.isClear());
        assertFalse(b7.isClear());
        assertFalse(b8.isClear());
        assertFalse(b15.isClear());

        source.set(0b0000000000000000);
        assertTrue(b0.isClear());
        assertTrue(b1.isClear());
        assertTrue(b7.isClear());
        assertTrue(b8.isClear());
        assertTrue(b15.isClear());
        assertFalse(b0.isSet());
        assertFalse(b1.isSet());
        assertFalse(b7.isSet());
        assertFalse(b8.isSet());
        assertFalse(b15.isSet());

        source.set(0b0_000000_1_0_11111_0_1);
        assertTrue(b0.isSet());
        assertTrue(b1.isClear());
        assertTrue(b7.isClear());
        assertTrue(b8.isSet());
        assertTrue(b15.isClear());

        source.set(0xff00);
        b0.set();
        b1.set();
        b7.set();
        b15.clear();
        assertEquals(0x7f83, source.unsigned());
    }

    @Test
    void multipleConsecutiveBits() {
        var middle = Subset.of(source, 0b0000001111111100);
        assertEquals(0xff, middle.mask());

        source.set(0b111111_11111111_11);
        assertEquals(0b11111111, middle.unsigned());

        source.set(0b111111_00000000_11);
        assertEquals(0b00000000, middle.unsigned());

        source.set(0b111111_10001000_11);
        assertEquals(0b10001000, middle.unsigned());

        source.set(0b101010_10101010_10);
        assertEquals(0xaa, middle.unsigned());
        middle.set(0x55);
        assertEquals(0b101010_01010101_10, source.unsigned());
    }

    public void assertEquals(int expected, int actual) {
        Assertions.assertEquals("0x" + Integer.toHexString(expected), "0x" + Integer.toHexString(actual));
    }
}
