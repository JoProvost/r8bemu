package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogicTest {

    @BeforeEach
    void setup() {
        Register.reset();
    }

    @Nested
    class Conjunction {
        @Test
        void overflowIsAlwaysClear() {
            Register.A.set(0b10010010);
            var memory = Value.of(0b00100110, 0xff);
            Logic.and(Register.A, memory);

            assertEquals(0b00000010, Register.A.unsigned());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.Z.isClear());
        }

        @Test
        void negative() {
            Register.A.set(0b10010010);
            var memory = Value.of(0b10100110, 0xff);
            Logic.and(Register.A, memory);

            assertEquals(0b10000010, Register.A.unsigned());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isSet());
            assertTrue(Register.Z.isClear());
        }

        @Test
        void zero() {
            Register.A.set(0b010010010);
            var memory = Value.of(0b101101101, 0xff);
            Logic.and(Register.A, memory);

            assertEquals(0b00000000, Register.A.unsigned());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.Z.isSet());
        }

        @Test
        void conditionCodesRegisterParticularCase() {
            Register.CC.set(0b10010010);
            var memory = Value.of(0b00100110, 0xff);
            Logic.and(Register.CC, memory);

            assertEquals(0b00000010, Register.CC.unsigned());
        }
    }

    @Nested
    class Disjunction {
        @Test
        void overflowIsAlwaysClear() {
            Register.A.set(0b00010010);
            var memory = Value.of(0b00100110, 0xff);
            Logic.or(Register.A, memory);

            assertEquals(0b00110110, Register.A.unsigned());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.Z.isClear());
        }

        @Test
        void negative() {
            Register.A.set(0b10010010);
            var memory = Value.of(0b00100110, 0xff);
            Logic.or(Register.A, memory);

            assertEquals(0b10110110, Register.A.unsigned());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isSet());
            assertTrue(Register.Z.isClear());
        }

        @Test
        void zero() {
            Register.A.set(0b000000000);
            var memory = Value.of(0b000000000, 0xff);
            Logic.or(Register.A, memory);

            assertEquals(0b00000000, Register.A.unsigned());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.Z.isSet());
        }

        @Test
        void conditionCodesRegisterParticularCase() {
            Register.CC.set(0b10010010);
            var memory = Value.of(0b00100110, 0xff);
            Logic.or(Register.CC, memory);

            assertEquals(0b10110110, Register.CC.unsigned());
        }
    }

    @Nested
    class ExclusiveConjunction {
        @Test
        void overflowIsAlwaysClear() {
            Register.A.set(0b10010010);
            var memory = Value.of(0b10100110, 0xff);
            Logic.xor(Register.A, memory);

            assertEquals(0b00110100, Register.A.unsigned());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.Z.isClear());
        }

        @Test
        void negative() {
            Register.A.set(0b10010010);
            var memory = Value.of(0b00100110, 0xff);
            Logic.xor(Register.A, memory);

            assertEquals(0b10110100, Register.A.unsigned());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isSet());
            assertTrue(Register.Z.isClear());
        }

        @Test
        void zero() {
            Register.A.set(0b010010010);
            var memory = Value.of(0b010010010, 0xff);
            Logic.xor(Register.A, memory);

            assertEquals(0b00000000, Register.A.unsigned());
            assertTrue(Register.V.isClear());
            assertTrue(Register.N.isClear());
            assertTrue(Register.Z.isSet());
        }
    }

    @Nested
    class Complement {
        @Test
        void overflowIsAlwaysClearAndCarryIsAlwaysSet() {
            Register.A.set(0b10101010);
            Logic.com(Register.A);

            assertEquals(0b01010101, Register.A.unsigned());
            assertTrue(Register.V.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
            assertTrue(Register.Z.isClear());
        }

        @Test
        void carryIsAlwaysSet() {
            Register.A.set(0b10101010);
            Logic.com(Register.A);

            assertEquals(0b01010101, Register.A.unsigned());
            assertTrue(Register.V.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
            assertTrue(Register.Z.isClear());
        }

        @Test
        void negative() {
            Register.A.set(0b00000000);
            Logic.com(Register.A);

            assertEquals(0b11111111, Register.A.unsigned());
            assertTrue(Register.V.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isSet());
            assertTrue(Register.Z.isClear());
        }

        @Test
        void zero() {
            Register.A.set(0b11111111);
            Logic.com(Register.A);

            assertEquals(0b00000000, Register.A.unsigned());
            assertTrue(Register.V.isClear());
            assertTrue(Register.C.isSet());
            assertTrue(Register.N.isClear());
            assertTrue(Register.Z.isSet());
        }
    }
}
