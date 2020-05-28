package com.joprovost.r8bemu.arithmetic;

import com.joprovost.r8bemu.data.Constant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConjunctionTest {
    @Test
    void and() {
        var result = Constant.asByte(0b10010010).and(Constant.asByte(0b10100110));
        assertEquals(0b10000010, result.unsigned());
        assertFalse(result.overflow());
    }
}
