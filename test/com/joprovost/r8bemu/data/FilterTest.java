package com.joprovost.r8bemu.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FilterTest {

    DataAccess variable = Variable.ofMask(0xff).describedAs("variable");
    DataAccess filterMask = Variable.ofMask(0xff).describedAs("filtered");
    Filter filtered = Filter.of(variable, filterMask);

    @Test
    void isDescribed() {
        assertEquals("variable", filtered.description());
    }

    @Test
    void filterAllowOutput() {
        variable.value(0xff);

        filterMask.value(0xff);
        assertEquals(0xff, filtered.value());

        filterMask.value(0x55);
        assertEquals(0x55, filtered.value());

        variable.value(0x55);
        filterMask.value(0xff);
        assertEquals(0x55, filtered.value());

        variable.value(0xf0);
        filterMask.value(0x0f);
        assertEquals(0x00, filtered.value());
    }

    @Test
    void filterAllowInput() {
        variable.value(0x00);
        filterMask.value(0xff);
        filtered.value(0xff);
        assertEquals(0xff, variable.value());

        variable.value(0x00);
        filterMask.value(0x55);
        filtered.value(0xff);
        assertEquals(0x55, variable.value());

        variable.value(0xff);
        filterMask.value(0x55);
        filtered.value(0x00);
        assertEquals(0xaa, variable.value());

        variable.value(0x00);
        filterMask.value(0xf0);
        filtered.value(0x0f);
        assertEquals(0x00, variable.value());
    }

    @Test
    void keepUnFilteredBitsUntouched() {
        variable.value(0xaa);
        filterMask.value(0x0f);
        filtered.value(0x00);
        assertEquals(0xa0, variable.value());

        variable.value(0xaa);
        filterMask.value(0x0f);
        filtered.value(0xff);
        assertEquals(0xaf, variable.value());
    }

    public void assertEquals(int expected, int actual) {
        Assertions.assertEquals("0x" + Integer.toHexString(expected), "0x" + Integer.toHexString(actual));
    }

    public void assertEquals(String expected, String actual) {
        Assertions.assertEquals(expected, actual);
    }
}
