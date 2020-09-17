package com.joprovost.r8bemu.data;

import com.joprovost.r8bemu.data.transform.DataAccessSubset;
import org.junit.jupiter.api.Test;

import static com.joprovost.r8bemu.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataAccessSubsetTest {

    DataAccess source = Variable.ofMask(0xffff);

    @Test
    void lsb() {
        var lsb = DataAccessSubset.lsb(source);

        assertEquals(0xff, lsb.mask());

        source.value(0xfa1a);
        assertEquals(0x1a, lsb.value());

        lsb.value(0xde);
        assertEquals(0xfade, source.value());

        // No overflow
        lsb.value(0x872398de);
        assertEquals(0xfade, source.value());
    }

    @Test
    void msb() {
        var msb = DataAccessSubset.msb(source);

        assertEquals(0xff, msb.mask());

        source.value(0xfa1a);
        assertEquals(0xfa, msb.value());

        msb.value(0xd0);
        assertEquals(0xd01a, source.value());

        // No overflow
        msb.value(0x872398d0);
        assertEquals(0xd01a, source.value());
    }

    @Test
    void bit() {

        var b0 = DataAccessSubset.bit(source, 0);
        var b1 = DataAccessSubset.bit(source, 1);
        var b7 = DataAccessSubset.bit(source, 7);
        var b8 = DataAccessSubset.bit(source, 8);
        var b15 = DataAccessSubset.bit(source, 15);

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

        source.value(0xff00);
        b0.set();
        b1.set();
        b7.set();
        b15.clear();
        assertEquals(0x7f83, source.value());
    }

    @Test
    void multipleConsecutiveBits() {
        var middle = DataAccessSubset.of(source, 0b0000001111111100);
        assertEquals(0xff, middle.mask());

        source.value(0b111111_11111111_11);
        assertEquals(0b11111111, middle.value());

        source.value(0b111111_00000000_11);
        assertEquals(0b00000000, middle.value());

        source.value(0b111111_10001000_11);
        assertEquals(0b10001000, middle.value());

        source.value(0b101010_10101010_10);
        assertEquals(0xaa, middle.value());
        middle.value(0x55);
        assertEquals(0b101010_01010101_10, source.value());
    }

}
