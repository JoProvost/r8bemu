package com.joprovost.r8bemu.arithmetic;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.Constant;
import org.junit.jupiter.api.Test;

import static com.joprovost.r8bemu.data.Constant.asByte;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdditionTest {

    @Test
    void signed() {
        DataAccess minusTen = Constant.of(-10, 0xff);
        assertEquals(-10, minusTen.signed());
        assertEquals(-10, Constant.of(minusTen.unsigned(), 0xff).signed());
    }

    @Test
    void unsigned() {
        DataAccess minusTen = Constant.of(-10, 0xff);
        assertEquals(256 - 10, minusTen.unsigned());
        assertEquals(-10, Constant.of(minusTen.unsigned(), 0xff).signed());
    }

    @Test
    void plus() {
        assertEquals(20, asByte(10).plus(asByte(10)).unsigned());
        assertEquals(20, asByte(10).plus(asByte(10)).signed());

        assertEquals(256 - 10, asByte(0).plus(asByte(-10)).unsigned());
        assertEquals(-10, asByte(0).plus(asByte(-10)).signed());
    }

    @Test
    void plusCarry() {
        assertEquals(0xff, asByte(0xfe).plus(asByte(0x01)).unsigned());
        assertEquals(-1, asByte(0xfe).plus(asByte(0x01)).signed());
        assertFalse(asByte(0xfe).plus(asByte(0x01)).carry().orElseThrow());

        assertEquals(0, asByte(0xff).plus(asByte(0x01)).unsigned());
        assertEquals(0, asByte(0xff).plus(asByte(0x01)).signed());
        assertTrue(asByte(0xff).plus(asByte(0x01)).carry().orElseThrow());
    }
}
