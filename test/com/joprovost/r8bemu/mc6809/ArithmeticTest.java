package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArithmeticTest {

    @BeforeEach
    void setup() {
        Register.reset();
    }

    @Nested
    class SignExtended {
        @Test
        void affectsRegisterDWithSignedValueOfRegisterB() {
            Register.B.value(-24);
            Arithmetic.sex();
            assertEquals(-24, Register.D.signed());
            assertTrue(Register.N.isSet());
            assertTrue(Register.Z.isClear());
        }

        @Test
        void positive() {
            Register.B.value(50);
            Arithmetic.sex();
            assertEquals(50, Register.D.signed());
            assertTrue(Register.N.isClear());
            assertTrue(Register.Z.isClear());
        }

        @Test
        void ignoresRegisterA() {
            Register.A.value(100);
            Register.B.value(50);
            Arithmetic.sex();
            assertEquals(50, Register.D.signed());
            assertTrue(Register.N.isClear());
            assertTrue(Register.Z.isClear());
        }

        @Test
        void zero() {
            Register.B.value(0);
            Arithmetic.sex();
            assertEquals(0, Register.D.signed());
            assertTrue(Register.N.isClear());
            assertTrue(Register.Z.isSet());
        }
    }

    @Nested
    class Multiply {
        @Test
        void unsigned() {
            Register.A.value(150);
            Register.B.value(200);
            Arithmetic.mul();
            assertEquals(150 * 200, Register.D.value());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
        }

        @Test
        void affectsCarryFlag() {
            Register.A.value(64);
            Register.B.value(2);
            Arithmetic.mul();
            assertEquals(128, Register.D.value());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
        }

        @Test
        void affectsZeroFlag() {
            Register.A.value(64);
            Register.B.value(0);
            Arithmetic.mul();
            assertEquals(0, Register.D.value());
            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isClear());
        }
    }

    @Nested
    class AddBToX {
        @Test
        void unsigned() {
            Register.B.value(200);
            Register.X.value(17000);
            Arithmetic.abx();
            assertEquals(17200, Register.X.value());
        }
    }

    @Nested
    class Negate {
        @Test
        void signed() {
            Register.X.value(-1024);
            Arithmetic.neg(Register.X);
            assertEquals(1024, Register.X.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }

        @Test
        void negative() {
            Register.X.value(1024);
            Arithmetic.neg(Register.X);
            assertEquals(-1024, Register.X.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isSet());
            assertTrue(Register.V.isClear());
        }

        @Test
        void zero() {
            Register.X.value(0);
            Arithmetic.neg(Register.X);
            assertEquals(0, Register.X.signed());
            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }

        @Test
        void overflow() {
            Register.X.value(0b1000000000000000);
            Arithmetic.neg(Register.X);
            assertEquals(0b1000000000000000, Register.X.value());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isSet());
            assertTrue(Register.V.isSet());
        }
    }

    @Nested
    class AddWithCarry {
        @Test
        void signed() {
            Register.A.value(20);
            Register.C.value(1);
            Value argument = Value.asByte(-10);

            Arithmetic.adc(Register.A, argument);
            assertEquals(11, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }

        @Test
        void zero() {
            Register.A.value(20);
            Register.C.value(1);
            Value argument  = Value.asByte(-21);

            Arithmetic.adc(Register.A, argument);
            assertEquals(0, Register.A.signed());
            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }

        @Test
        void negative() {
            Register.A.value(20);
            Register.C.value(1);
            Value argument = Value.asByte(-22);

            Arithmetic.adc(Register.A, argument);
            assertEquals(-1, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isSet());
            assertTrue(Register.V.isClear());
        }

        @Test
        void positiveOverflow() {
            Register.A.value(127);
            Register.C.value(1);
            Value argument = Value.asByte(127);

            Arithmetic.adc(Register.A, argument);
            assertEquals(255, Register.A.value());
            assertEquals(-1, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isSet());
            assertTrue(Register.V.isSet());
        }

        @Test
        void negativeOverflowAndCarry() {
            Register.A.value(-127);
            Register.C.value(1);
            Value argument = Value.asByte(-127);

            Arithmetic.adc(Register.A, argument);
            assertEquals(3, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isSet());
        }

        @Test
        void positiveHalfCarry() {
            Register.A.value(0x1e);
            Register.C.value(1);
            Value argument = Value.asByte(0x01);

            Arithmetic.adc(Register.A, argument);
            assertEquals(0x20, Register.A.signed());
            assertTrue(Register.H.isSet());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }

        @Test
        void noHalfCarry() {
            Register.A.value(0x1d);
            Register.C.value(1);
            Value argument = Value.asByte(0x01);

            Arithmetic.adc(Register.A, argument);
            assertEquals(0x1f, Register.A.signed());
            assertTrue(Register.H.isClear());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }
    }

    @Nested
    class Add {
        @Test
        void signed() {
            Register.A.value(20);
            Value argument = Value.asByte(-10);

            Arithmetic.add(Register.A, argument);
            assertEquals(10, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }

        @Test
        void zero() {
            Register.A.value(20);
            Value argument = Value.asByte(-20);

            Arithmetic.add(Register.A, argument);
            assertEquals(0, Register.A.signed());
            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }

        @Test
        void negative() {
            Register.A.value(20);
            Value argument = Value.asByte(-21);

            Arithmetic.adc(Register.A, argument);
            assertEquals(-1, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isSet());
            assertTrue(Register.V.isClear());
        }

        @Test
        void positiveOverflow() {
            Register.A.value(127);
            Value argument = Value.asByte(127);

            Arithmetic.adc(Register.A, argument);
            assertEquals(254, Register.A.value());
            assertEquals(-2, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isSet());
            assertTrue(Register.V.isSet());
        }

        @Test
        void negativeOverflowAndCarry() {
            Register.A.value(-127);
            Value argument = Value.asByte(-127);

            Arithmetic.adc(Register.A, argument);
            assertEquals(2, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isSet());
        }

        @Test
        void positiveHalfCarry() {
            Register.A.value(0x1e);
            Value argument = Value.asByte(0x02);

            Arithmetic.adc(Register.A, argument);
            assertEquals(0x20, Register.A.signed());
            assertTrue(Register.H.isSet());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }

        @Test
        void noHalfCarry() {
            Register.A.value(0x1d);
            Value argument = Value.asByte(0x02);

            Arithmetic.adc(Register.A, argument);
            assertEquals(0x1f, Register.A.signed());
            assertTrue(Register.H.isClear());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }
    }


    @Nested
    class SubtractionWithCarry {
        @Test
        void signed() {
            Register.A.value(20);
            Register.C.value(1);
            Value argument = Value.asByte(10);

            Arithmetic.sbc(Register.A, argument);
            assertEquals(9, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }

        @Test
        void zero() {
            Register.A.value(20);
            Register.C.value(1);
            Value argument = Value.asByte(19);

            Arithmetic.sbc(Register.A, argument);
            assertEquals(0, Register.A.signed());
            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }

        @Test
        void negative() {
            Register.A.value(-20);
            Register.C.value(1);
            Value argument = Value.asByte(21);

            Arithmetic.sbc(Register.A, argument);
            assertEquals(-42, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isSet());
            assertTrue(Register.V.isClear());
        }

        @Test
        void carry() {
            Register.A.value(20);
            Register.C.value(1);
            Value argument = Value.asByte(21);

            Arithmetic.sbc(Register.A, argument);
            assertEquals(-2, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isSet());
            assertTrue(Register.V.isClear());
        }
    }

    @Nested
    class Subtraction {
        @Test
        void signed() {
            Register.A.value(20);
            Value argument = Value.asByte(10);

            Arithmetic.sub(Register.A, argument);
            assertEquals(10, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }

        @Test
        void zero() {
            Register.A.value(20);
            Value argument = Value.asByte(20);

            Arithmetic.sub(Register.A, argument);
            assertEquals(0, Register.A.signed());
            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }

        @Test
        void negative() {
            Register.A.value(-20);
            Value argument = Value.asByte(21);

            Arithmetic.sub(Register.A, argument);
            assertEquals(-41, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isSet());
            assertTrue(Register.V.isClear());
        }

        @Test
        void carry() {
            Register.A.value(20);
            Value argument = Value.asByte(21);

            Arithmetic.sbc(Register.A, argument);
            assertEquals(-1, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isSet());
            assertTrue(Register.V.isClear());
        }
    }
}
