package com.joprovost.r8bemu.arithmetic;

import com.joprovost.r8bemu.data.Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConjunctionTest {
    @Test
    void and() {
        var result = Value.asByte(0b10010010).and(Value.asByte(0b10100110));
        assertEquals(0b10000010, result.unsigned());
        assertFalse(result.overflow());
    }
}
