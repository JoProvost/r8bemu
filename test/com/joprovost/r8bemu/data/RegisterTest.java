package com.joprovost.r8bemu.data;

import com.joprovost.r8bemu.data.binary.BinaryRegister;
import org.junit.jupiter.api.Test;

import static com.joprovost.r8bemu.Assert.assertEquals;

class RegisterTest {

    @Test
    void storesNumberBasedOnMask() {
        var word8 = BinaryRegister.ofMask(0xff);
        word8.value(0xabc);

        assertEquals(0xbc, word8.value());
        assertEquals(0xff, word8.mask());

        var word16 = BinaryRegister.ofMask(0xffff);
        word16.value(0xabc);

        assertEquals(0xabc, word16.value());
        assertEquals(0xffff, word16.mask());
    }
}
