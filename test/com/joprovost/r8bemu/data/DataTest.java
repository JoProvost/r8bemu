package com.joprovost.r8bemu.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DataTest {

    @Test
    void storesNumberBasedOnMask() {
        var word8 = Variable.ofMask(0xff);
        word8.value(0xabc);

        assertEquals(0xbc, word8.value());
        assertEquals(0xff, word8.mask());

        var word16 = Variable.ofMask(0xffff);
        word16.value(0xabc);

        assertEquals(0xabc, word16.value());
        assertEquals(0xffff, word16.mask());
    }

    public static void assertEquals(int expected, int actual) {
        Assertions.assertEquals("0x" + Integer.toHexString(expected), "0x" + Integer.toHexString(actual));
    }
}
