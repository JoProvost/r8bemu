package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.Debugger;
import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.FakeBusyState;
import com.joprovost.r8bemu.memory.Memory;
import com.joprovost.r8bemu.memory.MemoryMapped;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.joprovost.r8bemu.mc6809.Register.A;
import static com.joprovost.r8bemu.mc6809.Register.B;
import static com.joprovost.r8bemu.mc6809.Register.C;
import static com.joprovost.r8bemu.mc6809.Register.CC;
import static com.joprovost.r8bemu.mc6809.Register.D;
import static com.joprovost.r8bemu.mc6809.Register.DP;
import static com.joprovost.r8bemu.mc6809.Register.F;
import static com.joprovost.r8bemu.mc6809.Register.I;
import static com.joprovost.r8bemu.mc6809.Register.N;
import static com.joprovost.r8bemu.mc6809.Register.PC;
import static com.joprovost.r8bemu.mc6809.Register.S;
import static com.joprovost.r8bemu.mc6809.Register.U;
import static com.joprovost.r8bemu.mc6809.Register.V;
import static com.joprovost.r8bemu.mc6809.Register.X;
import static com.joprovost.r8bemu.mc6809.Register.Y;
import static com.joprovost.r8bemu.mc6809.Register.Z;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MC6809ETest {
    public static final int CMPA_INDEXED = 0xa1;
    public static final int NEG_DIRECT = 0x00;
    public static final int MUL_INHERENT = 0x3d;
    public static final int EXTENDED_INDIRECT = 0b10011111;
    public static final int FIRQ_VECTOR = 0xfff6;
    public static final int IRQ_VECTOR = 0xfff8;
    public static final int NOP = 0x12;

    Debugger debugger = Debugger.none();
    MemoryMapped memory = new Memory(0xffff);
    FakeBusyState clock = new FakeBusyState();
    MC6809E cpu = new MC6809E(memory, debugger, clock);


    @BeforeEach
    void setUp() {
        Register.reset();
        Signal.reset();
        clock.reset();
    }

    public void assertEquals(int expected, int actual) {
        Assertions.assertEquals("0x" + Integer.toHexString(expected), "0x" + Integer.toHexString(actual));
    }

    @Nested
    class OpCodes {
        @Test
        void nopTakesTwoTicks() throws IOException {
            PC.value(0x0000);
            memory.write(0x00, NOP, NOP);

            cpu.tick(Clock.zero());
            assertEquals(0x0001, PC.value());
            clock.assertBusyFor(2);
        }

        @Test
        void abx() throws IOException {
            B.value(0x15);
            X.value(0x1202);
            PC.value(0x0000);
            memory.write(0x00, 0x3a);

            cpu.tick(Clock.zero());
            assertEquals(0x15, B.value());
            assertEquals(0x1217, X.value());
            assertEquals(0x0001, PC.value());
        }

        @Test
        void mul() throws IOException {
            A.value(0x04);
            B.value(0x04);

            memory.write(0x00, MUL_INHERENT);

            cpu.tick(Clock.zero());
            assertEquals(0x10, D.value());
            assertEquals(0x0001, PC.value());
        }

        @Test
        void neg() throws IOException {
            DP.value(0x04);

            memory.write(0x00, NEG_DIRECT, 0x01);
            memory.write(0x0401, 0x01);

            cpu.tick(Clock.zero());
            assertEquals(0xff, memory.read(0x0401));
            assertEquals(0x01, N.value());
            assertEquals(0x00, V.value());
            assertEquals(0x00, Z.value());
            assertEquals(0x01, C.value());
        }

        @Test
        void neg0x0() throws IOException {
            DP.value(0x04);

            memory.write(0x00, NEG_DIRECT, 0x01);
            memory.write(0x0401, 0x80);

            cpu.tick(Clock.zero());
            assertEquals(0x80, memory.read(0x0401));
            assertEquals(0x01, N.value());
            assertEquals(0x01, V.value());
            assertEquals(0x00, Z.value());
            assertEquals(0x01, C.value());
        }

        @Test
        void neg0x00() throws IOException {
            DP.value(0x04);

            memory.write(0x00, NEG_DIRECT, 0x01);
            memory.write(0x0401, 0x00);

            cpu.tick(Clock.zero());
            assertEquals(0x00, memory.read(0x0401));
            assertEquals(0x00, N.value());
            assertEquals(0x00, V.value());
            assertEquals(0x01, Z.value());
            assertEquals(0x00, C.value());
        }

        @Test
        void cmpa() throws IOException {
            A.value(0x10);

            memory.write(0x00, CMPA_INDEXED, EXTENDED_INDIRECT, 0x10, 0x00);
            memory.write(0x1000, 0x20, 0x00);
            memory.write(0x2000, 0x10);

            cpu.tick(Clock.zero());
            assertEquals(0x00, N.value());
            assertEquals(0x00, V.value());
            assertEquals(0x01, Z.value());
            assertEquals(0x00, C.value());
        }

        @Test
        void sbca() throws IOException {
            A.value(0x10);

            memory.write(0x00, 0xa2, EXTENDED_INDIRECT, 0x10, 0x00);
            memory.write(0x1000, 0x20, 0x00);
            memory.write(0x2000, 0x10);

            cpu.tick(Clock.zero());
            assertEquals(0x00, N.value());
            assertEquals(0x00, V.value());
            assertEquals(0x01, Z.value());
            assertEquals(0x00, C.value());
        }
    }

    @Nested
    class InterruptRequest {
        @Test
        void savesFullStateAndLoadsCounterFrom0xfff8() throws IOException {
            memory.write(IRQ_VECTOR, 0xab, 0xcd);

            CC.value(0x01);
            A.value(0x02);
            B.value(0x03);
            DP.value(0x04);
            X.value(0x0506);
            Y.value(0x0708);
            U.value(0x090a);
            PC.value(0x0b0c);

            S.value(0x8000);

            Signal.IRQ.set();
            cpu.tick(Clock.zero());

            assertEquals(0xabcd, PC.value());
            assertEquals(0x7ff4, S.value());

            // Stack content
            assertEquals(0x81, memory.read(0x7ff4)); // CC + E
            assertEquals(0x02, memory.read(0x7ff5)); // A
            assertEquals(0x03, memory.read(0x7ff6)); // B
            assertEquals(0x04, memory.read(0x7ff7)); // DP
            assertEquals(0x05, memory.read(0x7ff8)); // X
            assertEquals(0x06, memory.read(0x7ff9)); // X
            assertEquals(0x07, memory.read(0x7ffa)); // Y
            assertEquals(0x08, memory.read(0x7ffb)); // Y
            assertEquals(0x09, memory.read(0x7ffc)); // U
            assertEquals(0x0a, memory.read(0x7ffd)); // U
            assertEquals(0x0b, memory.read(0x7ffe)); // PC
            assertEquals(0x0c, memory.read(0x7fff)); // PC

            // avoid re-entrant irq
            assertTrue(I.isSet());

            clock.assertBusyFor(18);
        }

        @Test
        void doesNothingWhenIFlagIsSet() throws IOException {
            memory.write(IRQ_VECTOR, 0xab, 0xcd);
            memory.write(0x0000, NOP);

            var clock = new FakeBusyState();
            MC6809E cpu = new MC6809E(memory, debugger, clock);

            PC.value(0x0000);
            S.value(0x8000);

            Signal.IRQ.set();
            I.set();
            cpu.tick(Clock.zero());

            assertEquals(0x0001, PC.value());
            assertEquals(0x8000, S.value());

            clock.assertBusyFor(2);
        }
    }

    @Nested
    class FastInterruptRequest {
        @Test
        void savesProgramCounterAndLoadsCounterFrom0xfff6() throws IOException {
            memory.write(FIRQ_VECTOR, 0xab, 0xcd);

            CC.value(0x01);
            PC.value(0x0b0c);

            S.value(0x8000);

            Signal.FIRQ.set();
            cpu.tick(Clock.zero());

            assertEquals(0xabcd, PC.value());
            assertEquals(0x7ffd, S.value());

            // Stack content
            assertEquals(0x01, memory.read(0x7ffd)); // CC
            assertEquals(0x0b, memory.read(0x7ffe)); // PC
            assertEquals(0x0c, memory.read(0x7fff)); // PC

            // avoid re-entrant irq
            assertTrue(F.isSet());
            assertTrue(I.isSet());

            clock.assertBusyFor(3 + 6);
        }

        @Test
        void doesNothingWhenIFlagIsSet() throws IOException {
            memory.write(FIRQ_VECTOR, 0xab, 0xcd);
            memory.write(0x0000, NOP);

            PC.value(0x0000);
            S.value(0x8000);

            Signal.FIRQ.set();
            F.set();
            cpu.tick(Clock.zero());

            assertEquals(0x0001, PC.value());
            assertEquals(0x8000, S.value());

            clock.assertBusyFor(2);
        }

        @Test
        void hasPriorityOverIRQ() throws IOException {
            memory.write(FIRQ_VECTOR, 0xab, 0xcd);
            memory.write(IRQ_VECTOR, 0x00, 0x00);

            PC.value(0x0b0c);

            Signal.IRQ.set();
            Signal.FIRQ.set();
            cpu.tick(Clock.zero());

            assertEquals(0xabcd, PC.value());
        }
    }
}
