package com.joprovost.r8bemu.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VariableTest {

    @Test
    void storesNumberBasedOnMask() {
        var word8 = Variable.ofMask(0xff);
        word8.set(0xabc);

        assertEquals(0xbc, word8.unsigned());
        assertEquals(0xff, word8.mask());

        var word16 = Variable.ofMask(0xffff);
        word16.set(0xabc);

        assertEquals(0xabc, word16.unsigned());
        assertEquals(0xffff, word16.mask());
    }

    public static void assertEquals(int expected, int actual) {
        Assertions.assertEquals("0x" + Integer.toHexString(expected), "0x" + Integer.toHexString(actual));
    }
}
