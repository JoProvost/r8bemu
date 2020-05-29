package com.joprovost.r8bemu.arithmetic;

import org.junit.jupiter.api.Test;

import static com.joprovost.r8bemu.data.Value.asByte;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubtractionTest {
    @Test
    void minus() {
        assertEquals(20, asByte(30).minus(asByte(10)).unsigned());
        assertEquals(20, asByte(30).minus(asByte(10)).signed());

        assertEquals(256 - 10, asByte(0).minus(asByte(10)).unsigned());
        assertEquals(-10, asByte(0).minus(asByte(10)).signed());

        assertEquals(10, asByte(0).minus(asByte(-10)).unsigned());
        assertEquals(10, asByte(0).minus(asByte(-10)).signed());

        assertEquals(0xfe, asByte(0xff).minus(asByte(1)).unsigned());
        assertEquals(-2, asByte(0xff).minus(asByte(1)).signed());
    }

    @Test
    void minusCarry() {
        assertEquals(0xfd, asByte(0xfe).minus(asByte(0x01)).unsigned());
        assertEquals(-3, asByte(0xfe).minus(asByte(0x01)).signed());
        assertFalse(asByte(0xfe).minus(asByte(0x01)).carry().orElseThrow());

        assertEquals(0x01, asByte(0xfe).minus(asByte(0xfd)).unsigned());
        assertEquals(1, asByte(0xfe).minus(asByte(0xfd)).signed());
        assertFalse(asByte(0xfe).minus(asByte(0xfd)).carry().orElseThrow());

        assertEquals(0x02, asByte(0x01).minus(asByte(0xff)).unsigned());
        assertEquals(2, asByte(0x01).minus(asByte(0xff)).signed());
        assertTrue(asByte(0x01).minus(asByte(0xff)).carry().orElseThrow());

        assertEquals(0xff, asByte(0x01).minus(asByte(0x02)).unsigned());
        assertEquals(-1, asByte(0x01).minus(asByte(0x02)).signed());
        assertTrue(asByte(0x01).minus(asByte(0x02)).carry().orElseThrow());

        assertTrue(asByte(4).minus(asByte(5)).carry().orElseThrow());
    }
}
