package com.joprovost.r8bemu.data;

import org.junit.jupiter.api.Test;

import static com.joprovost.r8bemu.data.Value.asByte;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtractionTest {
    @Test
    void minus() {
        assertEquals(20, asByte(30).minus(asByte(10)).value());
        assertEquals(20, asByte(30).minus(asByte(10)).signed());

        assertEquals(10, asByte(0).minus(asByte(-10)).value());
        assertEquals(10, asByte(0).minus(asByte(-10)).signed());

        assertEquals(256 - 10, asByte(-20).minus(asByte(-10)).value());
        assertEquals(-10, asByte(-20).minus(asByte(-10)).signed());

        assertEquals(256 - 10, asByte(0).minus(asByte(10)).value());
        assertEquals(-10, asByte(0).minus(asByte(10)).signed());
    }
}
