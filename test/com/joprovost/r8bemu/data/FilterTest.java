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
        variable.set(0xff);

        filterMask.set(0xff);
        assertEquals(0xff, filtered.unsigned());

        filterMask.set(0x55);
        assertEquals(0x55, filtered.unsigned());

        variable.set(0x55);
        filterMask.set(0xff);
        assertEquals(0x55, filtered.unsigned());

        variable.set(0xf0);
        filterMask.set(0x0f);
        assertEquals(0x00, filtered.unsigned());
    }

    @Test
    void filterAllowInput() {
        variable.set(0x00);
        filterMask.set(0xff);
        filtered.set(0xff);
        assertEquals(0xff, variable.unsigned());

        variable.set(0x00);
        filterMask.set(0x55);
        filtered.set(0xff);
        assertEquals(0x55, variable.unsigned());

        variable.set(0xff);
        filterMask.set(0x55);
        filtered.set(0x00);
        assertEquals(0xaa, variable.unsigned());

        variable.set(0x00);
        filterMask.set(0xf0);
        filtered.set(0x0f);
        assertEquals(0x00, variable.unsigned());
    }

    @Test
    void keepUnFilteredBitsUntouched() {
        variable.set(0xaa);
        filterMask.set(0x0f);
        filtered.set(0x00);
        assertEquals(0xa0, variable.unsigned());

        variable.set(0xaa);
        filterMask.set(0x0f);
        filtered.set(0xff);
        assertEquals(0xaf, variable.unsigned());
    }

    public void assertEquals(int expected, int actual) {
        Assertions.assertEquals("0x" + Integer.toHexString(expected), "0x" + Integer.toHexString(actual));
    }

    public void assertEquals(String expected, String actual) {
        Assertions.assertEquals(expected, actual);
    }
}
