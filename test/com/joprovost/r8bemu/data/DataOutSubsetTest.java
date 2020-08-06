package com.joprovost.r8bemu.data;

import com.joprovost.r8bemu.data.transform.DataOutputSubset;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataOutSubsetTest {

    DataAccess source = Variable.ofMask(0xffff);

    @Test
    void bit() {

        var b0 = DataOutputSubset.bit(source, 0);
        var b1 = DataOutputSubset.bit(source, 1);
        var b7 = DataOutputSubset.bit(source, 7);
        var b8 = DataOutputSubset.bit(source, 8);
        var b15 = DataOutputSubset.bit(source, 15);

        assertEquals(0b1, b1.mask());
        assertEquals(0b1, b15.mask());

        source.value(0b1111111111111111);
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

        source.value(0b0000000000000000);
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

        source.value(0b0_000000_1_0_11111_0_1);
        assertTrue(b0.isSet());
        assertTrue(b1.isClear());
        assertTrue(b7.isClear());
        assertTrue(b8.isSet());
        assertTrue(b15.isClear());
    }

    @Test
    void multipleConsecutiveBits() {
        var middle = DataOutputSubset.of(source, 0b0000001111111100);
        assertEquals(0xff, middle.mask());

        source.value(0b111111_11111111_11);
        assertEquals(0b11111111, middle.value());

        source.value(0b111111_00000000_11);
        assertEquals(0b00000000, middle.value());

        source.value(0b111111_10001000_11);
        assertEquals(0b10001000, middle.value());

        source.value(0b101010_10101010_10);
        assertEquals(0xaa, middle.value());
    }

    public void assertEquals(int expected, int actual) {
        Assertions.assertEquals("0x" + Integer.toHexString(expected), "0x" + Integer.toHexString(actual));
    }
}
