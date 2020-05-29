package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.Variable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShiftTest {

    Variable variable = Variable.ofMask(0xff);

    @BeforeEach
    void setup() {
        Register.reset();
    }

    @Nested
    class LogicalShiftLeft {
        @Test
        void empty() {
            variable.set(0b00000000);
            Shift.lsl(variable);
            assertEquals(0b00000000, variable.unsigned());

            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isClear());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void becomesNegative() {
            variable.set(0b01111110);
            Shift.lsl(variable);
            assertEquals(0b11111100, variable.unsigned());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.V.isSet());
            assertTrue(Register.N.isSet());
        }

        @Test
        void staysPositive() {
            variable.set(0b00001111);
            Shift.lsl(variable);
            assertEquals(0b00011110, variable.unsigned());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void overflow() {
            variable.set(0b11111110);
            Shift.lsl(variable);
            assertEquals(0b11111100, variable.unsigned());

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
            variable.set(0b00000000);
            Shift.lsr(variable);
            assertEquals(0b00000000, variable.unsigned());

            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void becomesPositive() {
            variable.set(0b11111110);
            Shift.lsr(variable);
            assertEquals(0b01111111, variable.unsigned());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void carryFlagIgnored() {
            Register.C.set();
            variable.set(0b00011110);
            Shift.lsr(variable);
            assertEquals(0b00001111, variable.unsigned());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void overflowRight() {
            variable.set(0b01111111);
            Shift.lsr(variable);
            assertEquals(0b00111111, variable.unsigned());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
        }
    }

    @Nested
    class ArithmeticShiftRight {
        @Test
        void empty() {
            variable.set(0b00000000);
            Shift.asr(variable);
            assertEquals(0b00000000, variable.unsigned());

            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void staysNegative() {
            variable.set(0b11111110);
            Shift.asr(variable);
            assertEquals(0b11111111, variable.unsigned());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isSet());
        }

        @Test
        void carryFlagIgnored() {
            Register.C.set();
            variable.set(0b00011110);
            Shift.asr(variable);
            assertEquals(0b00001111, variable.unsigned());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void overflowRight() {
            variable.set(0b01111111);
            Shift.asr(variable);
            assertEquals(0b00111111, variable.unsigned());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
        }
    }

    @Nested
    class RotateLeft {
        @Test
        void empty() {
            variable.set(0b00000000);
            Shift.rol(variable);
            assertEquals(0b00000000, variable.unsigned());

            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isClear());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void becomesNegative() {
            variable.set(0b01111110);
            Shift.rol(variable);
            assertEquals(0b11111100, variable.unsigned());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.V.isSet());
            assertTrue(Register.N.isSet());
        }

        @Test
        void staysPositive() {
            variable.set(0b00001111);
            Shift.rol(variable);
            assertEquals(0b00011110, variable.unsigned());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void carryFlagOnBit0() {
            Register.C.set();
            variable.set(0b00011110);
            Shift.rol(variable);
            assertEquals(0b00111101, variable.unsigned());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void overflowLeft() {
            variable.set(0b11111110);
            Shift.rol(variable);
            assertEquals(0b11111100, variable.unsigned());

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
            variable.set(0b00000000);
            Shift.ror(variable);
            assertEquals(0b00000000, variable.unsigned());

            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void becomesPositive() {
            variable.set(0b11111110);
            Shift.ror(variable);
            assertEquals(0b01111111, variable.unsigned());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
        }

        @Test
        void carryFlagOnBit7() {
            Register.C.set();
            variable.set(0b00011110);
            Shift.ror(variable);
            assertEquals(0b10001111, variable.unsigned());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isSet());
        }

        @Test
        void overflowRight() {
            variable.set(0b01111111);
            Shift.ror(variable);
            assertEquals(0b00111111, variable.unsigned());

            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
        }
    }
}
