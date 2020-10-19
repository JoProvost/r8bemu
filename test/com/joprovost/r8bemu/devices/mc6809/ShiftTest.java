package com.joprovost.r8bemu.devices.mc6809;

import com.joprovost.r8bemu.data.binary.BinaryRegister;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShiftTest {

    BinaryRegister data = BinaryRegister.ofMask(0xff);

    @BeforeEach
    void setup() {
        Register.reset();
    }

    @Nested
    class LogicalShiftLeft {
        @Test
        void empty() {
            data.value(0b00000000);
            Shift.lsl(data);
            assertEquals(0b00000000, data.value());

            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isClear());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void becomesEmpty() {
            data.value(0b10000000);
            Shift.lsl(data);
            assertEquals(0b00000000, data.value());

            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isSet());
            assertTrue(Register.V.isSet());
            assertTrue(Register.N.isClear());
        }

        @Test
        void becomesNegative() {
            data.value(0b01111110);
            Shift.lsl(data);
            assertEquals(0b11111100, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.V.isSet());
            assertTrue(Register.N.isSet());
        }

        @Test
        void staysPositive() {
            data.value(0b00001111);
            Shift.lsl(data);
            assertEquals(0b00011110, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void overflow() {
            data.value(0b11111110);
            Shift.lsl(data);
            assertEquals(0b11111100, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isSet());
        }
    }

    @Nested
    class LogicalShiftRight {
        @Test
        void empty() {
            data.value(0b00000000);
            Shift.lsr(data);
            assertEquals(0b00000000, data.value());

            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void becomesPositive() {
            data.value(0b11111110);
            Shift.lsr(data);
            assertEquals(0b01111111, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void carryFlagIgnored() {
            Register.C.set();
            data.value(0b00011110);
            Shift.lsr(data);
            assertEquals(0b00001111, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void overflowRight() {
            data.value(0b01111111);
            Shift.lsr(data);
            assertEquals(0b00111111, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
        }
    }

    @Nested
    class ArithmeticShiftRight {
        @Test
        void empty() {
            data.value(0b00000000);
            Shift.asr(data);
            assertEquals(0b00000000, data.value());

            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void staysNegative() {
            data.value(0b11111110);
            Shift.asr(data);
            assertEquals(0b11111111, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isSet());
        }

        @Test
        void carryFlagIgnored() {
            Register.C.set();
            data.value(0b00011110);
            Shift.asr(data);
            assertEquals(0b00001111, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void overflowRight() {
            data.value(0b01111111);
            Shift.asr(data);
            assertEquals(0b00111111, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
        }
    }

    @Nested
    class RotateLeft {
        @Test
        void empty() {
            data.value(0b00000000);
            Shift.rol(data);
            assertEquals(0b00000000, data.value());

            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isClear());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void becomesEmpty() {
            data.value(0b10000000);
            Shift.rol(data);
            assertEquals(0b00000000, data.value());

            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isSet());
            assertTrue(Register.V.isSet());
            assertTrue(Register.N.isClear());
        }

        @Test
        void becomesNegative() {
            data.value(0b01111110);
            Shift.rol(data);
            assertEquals(0b11111100, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.V.isSet());
            assertTrue(Register.N.isSet());
        }

        @Test
        void staysPositive() {
            data.value(0b00001111);
            Shift.rol(data);
            assertEquals(0b00011110, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void carryFlagOnBit0() {
            Register.C.set();
            data.value(0b00011110);
            Shift.rol(data);
            assertEquals(0b00111101, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void overflowLeft() {
            data.value(0b11111110);
            Shift.rol(data);
            assertEquals(0b11111100, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isSet());
        }
    }

    @Nested
    class RotateRight {
        @Test
        void empty() {
            data.value(0b00000000);
            Shift.ror(data);
            assertEquals(0b00000000, data.value());

            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void becomesPositive() {
            data.value(0b11111110);
            Shift.ror(data);
            assertEquals(0b01111111, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void carryFlagOnBit7() {
            Register.C.set();
            data.value(0b00011110);
            Shift.ror(data);
            assertEquals(0b10001111, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isSet());
        }

        @Test
        void overflowRight() {
            data.value(0b01111111);
            Shift.ror(data);
            assertEquals(0b00111111, data.value());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
        }
    }
}
