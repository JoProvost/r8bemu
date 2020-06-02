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
            Register.B.set(-24);
            Arithmetic.sex();
            assertEquals(-24, Register.D.signed());
            assertTrue(Register.N.isSet());
            assertTrue(Register.Z.isClear());
        }

        @Test
        void positive() {
            Register.B.set(50);
            Arithmetic.sex();
            assertEquals(50, Register.D.signed());
            assertTrue(Register.N.isClear());
            assertTrue(Register.Z.isClear());
        }

        @Test
        void ignoresRegisterA() {
            Register.A.set(100);
            Register.B.set(50);
            Arithmetic.sex();
            assertEquals(50, Register.D.signed());
            assertTrue(Register.N.isClear());
            assertTrue(Register.Z.isClear());
        }

        @Test
        void zero() {
            Register.B.set(0);
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
            Register.A.set(150);
            Register.B.set(200);
            Arithmetic.mul();
            assertEquals(150 * 200, Register.D.unsigned());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
        }

        @Test
        void affectsCarryFlag() {
            Register.A.set(64);
            Register.B.set(2);
            Arithmetic.mul();
            assertEquals(128, Register.D.unsigned());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isSet());
        }

        @Test
        void affectsZeroFlag() {
            Register.A.set(64);
            Register.B.set(0);
            Arithmetic.mul();
            assertEquals(0, Register.D.unsigned());
            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isClear());
        }
    }

    @Nested
    class AddBToX {
        @Test
        void unsigned() {
            Register.B.set(200);
            Register.X.set(17000);
            Arithmetic.abx();
            assertEquals(17200, Register.X.unsigned());
        }
    }

    @Nested
    class Negate {
        @Test
        void signed() {
            Register.X.set(-1024);
            Arithmetic.neg(Register.X);
            assertEquals(1024, Register.X.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }

        @Test
        void negative() {
            Register.X.set(1024);
            Arithmetic.neg(Register.X);
            assertEquals(-1024, Register.X.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isSet());
            assertTrue(Register.V.isClear());
        }

        @Test
        void zero() {
            Register.X.set(0);
            Arithmetic.neg(Register.X);
            assertEquals(0, Register.X.signed());
            assertTrue(Register.Z.isSet());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
            assertTrue(Register.V.isClear());
        }

        @Test
        void overflow() {
            Register.X.set(0b1000000000000000);
            Arithmetic.neg(Register.X);
            assertEquals(0b1000000000000000, Register.X.unsigned());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isSet());
            assertTrue(Register.V.isSet());
        }
    }

    @Nested
    class AddWithCarry {
        @Test
        void signed() {
            Register.A.set(20);
            Register.C.set(1);
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
            Register.A.set(20);
            Register.C.set(1);
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
            Register.A.set(20);
            Register.C.set(1);
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
            Register.A.set(127);
            Register.C.set(1);
            Value argument = Value.asByte(127);

            Arithmetic.adc(Register.A, argument);
            assertEquals(255, Register.A.unsigned());
            assertEquals(-1, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isSet());
            assertTrue(Register.V.isSet());
        }

        @Test
        void negativeOverflowAndCarry() {
            Register.A.set(-127);
            Register.C.set(1);
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
            Register.A.set(0x1e);
            Register.C.set(1);
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
            Register.A.set(0x1d);
            Register.C.set(1);
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
            Register.A.set(20);
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
            Register.A.set(20);
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
            Register.A.set(20);
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
            Register.A.set(127);
            Value argument = Value.asByte(127);

            Arithmetic.adc(Register.A, argument);
            assertEquals(254, Register.A.unsigned());
            assertEquals(-2, Register.A.signed());
            assertTrue(Register.Z.isClear());
            assertTrue(Register.C.isClear());
            assertTrue(Register.N.isSet());
            assertTrue(Register.V.isSet());
        }

        @Test
        void negativeOverflowAndCarry() {
            Register.A.set(-127);
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
            Register.A.set(0x1e);
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
            Register.A.set(0x1d);
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
            Register.A.set(20);
            Register.C.set(1);
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
            Register.A.set(20);
            Register.C.set(1);
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
            Register.A.set(-20);
            Register.C.set(1);
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
            Register.A.set(20);
            Register.C.set(1);
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
            Register.A.set(20);
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
            Register.A.set(20);
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
            Register.A.set(-20);
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
            Register.A.set(20);
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
