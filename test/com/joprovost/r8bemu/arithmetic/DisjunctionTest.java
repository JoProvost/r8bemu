package com.joprovost.r8bemu.arithmetic;

import com.joprovost.r8bemu.data.Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DisjunctionTest {
    @Test
    void negative() {
        var result = Value.asByte(0b00010010).or(Value.asByte(0b10100110));
        assertEquals(0b10110110, result.unsigned());
        assertFalse(result.overflow());
        assertTrue(result.negative());
        assertFalse(result.zero());

        var result2 = Value.asByte(0b10010010).or(Value.asByte(0b00100110));
        assertEquals(0b10110110, result2.unsigned());
        assertFalse(result2.overflow());
        assertTrue(result2.negative());
        assertFalse(result2.zero());
    }

    @Test
    void positive() {
        var result = Value.asByte(0b00010010).or(Value.asByte(0b00100110));
        assertEquals(0b00110110, result.unsigned());
        assertFalse(result.overflow());
        assertFalse(result.negative());
        assertFalse(result.zero());
    }

    @Test
    void zero() {
        var result = Value.asByte(0).or(Value.asByte(0));
        assertEquals(0, result.unsigned());
        assertFalse(result.overflow());
        assertFalse(result.negative());
        assertTrue(result.zero());
    }
}
