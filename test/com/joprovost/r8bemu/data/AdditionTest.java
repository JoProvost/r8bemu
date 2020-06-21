package com.joprovost.r8bemu.data;

import org.junit.jupiter.api.Test;

import static com.joprovost.r8bemu.data.Value.asByte;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AdditionTest {
    @Test
    void plus() {
        assertEquals(20, asByte(10).plus(asByte(10)).value());
        assertEquals(20, asByte(10).plus(asByte(10)).signed());

        assertEquals(256 - 10, asByte(0).plus(asByte(-10)).value());
        assertEquals(-10, asByte(0).plus(asByte(-10)).signed());
    }
}
