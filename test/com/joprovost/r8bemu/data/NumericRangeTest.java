package com.joprovost.r8bemu.data;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumericRangeTest {

    @Nested
    class Normal {
        NumericRange range = new NumericRange(-100, 0, 100);

        @Test
        void normalize() {
            assertEquals(-1.0, range.normalize(-100));
            assertEquals(0.0, range.normalize(0));
            assertEquals(1.0, range.normalize(100));
        }

        @Test
        void from() {
            assertEquals(-100, range.from(-1.0));
            assertEquals(0, range.from(0.0));
            assertEquals(100, range.from(1.0));
        }
    }

    @Nested
    class Positive {
        NumericRange range = new NumericRange(0, 50, 100);

        @Test
        void normalize() {
            assertEquals(-1.0, range.normalize(0));
            assertEquals(0.0, range.normalize(50));
            assertEquals(1.0, range.normalize(100));
        }

        @Test
        void from() {
            assertEquals(0, range.from(-1.0));
            assertEquals(50, range.from(0.0));
            assertEquals(100, range.from(1.0));
        }
    }

    @Nested
    class Asymmetric {
        NumericRange range = new NumericRange(-50, 0, 100);

        @Test
        void normalize() {
            assertEquals(-1.0, range.normalize(-50));
            assertEquals(0.0, range.normalize(0));
            assertEquals(1.0, range.normalize(100));
        }

        @Test
        void from() {
            assertEquals(-50, range.from(-1.0));
            assertEquals(0, range.from(0.0));
            assertEquals(100, range.from(1.0));
        }
    }

    @Nested
    class Reversed {
        NumericRange range = new NumericRange(100, 0, -100);

        @Test
        void normalize() {
            assertEquals(1.0, range.normalize(-100));
            assertEquals(0.0, range.normalize(0));
            assertEquals(-1.0, range.normalize(100));
        }

        @Test
        void from() {
            assertEquals(100, range.from(-1.0));
            assertEquals(0, range.from(0.0));
            assertEquals(-100, range.from(1.0));
        }
    }

    @Nested
    class OutOfBound {
        NumericRange range = new NumericRange(100, 0, -100);

        @Test
        void normalize() {
            assertEquals(1.0, range.normalize(-200));
            assertEquals(0.0, range.normalize(0));
            assertEquals(-1.0, range.normalize(200));
        }

        @Test
        void from() {
            assertEquals(100, range.from(-2.0));
            assertEquals(0, range.from(0.0));
            assertEquals(-100, range.from(2.0));
        }
    }

    @Test
    void convert() {
        NumericRange b256 = new NumericRange(-128, 0, 127);
        NumericRange b64 = new NumericRange(0, 32, 64);

        assertEquals(127, b256.from(64, b64));
    }
}
