package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.Debugger;
import com.joprovost.r8bemu.memory.MemoryMapped;
import com.joprovost.r8bemu.memory.Memory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.joprovost.r8bemu.mc6809.Register.A;
import static com.joprovost.r8bemu.mc6809.Register.B;
import static com.joprovost.r8bemu.mc6809.Register.C;
import static com.joprovost.r8bemu.mc6809.Register.D;
import static com.joprovost.r8bemu.mc6809.Register.DP;
import static com.joprovost.r8bemu.mc6809.Register.N;
import static com.joprovost.r8bemu.mc6809.Register.PC;
import static com.joprovost.r8bemu.mc6809.Register.V;
import static com.joprovost.r8bemu.mc6809.Register.X;
import static com.joprovost.r8bemu.mc6809.Register.Z;

class MC6809ETest {

    public static final int CMPA_INDEXED = 0xa1;
    public static final int NEG_DIRECT = 0x00;
    public static final int MUL_INHERENT = 0x3d;
    public static final int EXTENDED_INDIRECT = 0b10011111;
    private Debugger debugger = Debugger.none();
    private long tick = 0;

    @BeforeEach
    void setUp() {
        Register.reset();
    }

    @Test
    void nop() throws IOException {
        MemoryMapped memory = new Memory(0xffff);
        MC6809E cpu = new MC6809E(new Memory(new byte[]{(byte) 0x12, (byte) 0x12}), debugger);
        PC.set(0x0000);
        memory.write(0x00, 0x12, 0x12);

        cpu.tick(tick++);
        assertEquals(0x0001, PC.unsigned());
        cpu.tick(tick++);
        assertEquals(0x0002, PC.unsigned());
    }

    // TODO flags
    @Test
    void abx() throws IOException {
        MemoryMapped memory = new Memory(0xffff);
        MC6809E cpu = new MC6809E(memory, debugger);
        B.set(0x15);
        X.set(0x1202);
        PC.set(0x0000);
        memory.write(0x00, 0x3a);

        cpu.tick(tick++);
        assertEquals(0x15, B.unsigned());
        assertEquals(0x1217, X.unsigned());
        assertEquals(0x0001, PC.unsigned());
    }

    // TODO flags
    @Test
    void mul() throws IOException {
        MemoryMapped memory = new Memory(0xffff);
        MC6809E cpu = new MC6809E(memory, debugger);
        A.set(0x04);
        B.set(0x04);

        memory.write(0x00, 0x3d);

        cpu.tick(tick++);
        assertEquals(0x10, D.unsigned());
        assertEquals(0x0001, PC.unsigned());
    }

    // TODO flags
    @Test
    void neg() throws IOException {
        MemoryMapped memory = new Memory(0xffff);
        MC6809E cpu = new MC6809E(memory, debugger);
        DP.set(0x04);

        memory.write(0x00, NEG_DIRECT, 0x01);
        memory.write(0x0401, 0x01);

        cpu.tick(tick++);
        assertEquals(0xff, memory.read(0x0401));
        assertEquals(0x01, N.unsigned());
        assertEquals(0x00, V.unsigned());
        assertEquals(0x00, Z.unsigned());
        assertEquals(0x00, C.unsigned());

    }

    @Test
    void neg0x0() throws IOException {
        MemoryMapped memory = new Memory(0xffff);
        MC6809E cpu = new MC6809E(memory, debugger);
        DP.set(0x04);

        memory.write(0x00, NEG_DIRECT, 0x01);
        memory.write(0x0401, 0x80);

        cpu.tick(tick++);
        assertEquals(0x80, memory.read(0x0401));
        assertEquals(0x01, N.unsigned());
        assertEquals(0x01, V.unsigned());
        assertEquals(0x00, Z.unsigned());
        assertEquals(0x00, C.unsigned());
    }

    @Test
    void neg0x00() throws IOException {
        MemoryMapped memory = new Memory(0xffff);
        MC6809E cpu = new MC6809E(memory, debugger);
        DP.set(0x04);

        memory.write(0x00, NEG_DIRECT, 0x01);
        memory.write(0x0401, 0x00);

        cpu.tick(tick++);
        assertEquals(0x00, memory.read(0x0401));
        assertEquals(0x00, N.unsigned());
        assertEquals(0x00, V.unsigned());
        assertEquals(0x01, Z.unsigned());
        assertEquals(0x01, C.unsigned());
    }

    @Test
    void cmpa() throws IOException {
        MemoryMapped memory = new Memory(0xffff);
        MC6809E cpu = new MC6809E(memory, debugger);
        A.set(0x10);

        memory.write(0x00, CMPA_INDEXED, EXTENDED_INDIRECT, 0x10, 0x00);
        memory.write(0x1000, 0x20, 0x00);
        memory.write(0x2000, 0x10);

        cpu.tick(tick++);
        assertEquals(0x00, N.unsigned());
        assertEquals(0x00, V.unsigned());
        assertEquals(0x01, Z.unsigned());
        assertEquals(0x00, C.unsigned());
    }

    @Test
    void sbca() throws IOException {
        MemoryMapped memory = new Memory(0xffff);
        MC6809E cpu = new MC6809E(memory, debugger);
        A.set(0x10);

        memory.write(0x00, 0xa2, EXTENDED_INDIRECT, 0x10, 0x00);
        memory.write(0x1000, 0x20, 0x00);
        memory.write(0x2000, 0x10);

        cpu.tick(tick++);
        assertEquals(0x00, N.unsigned());
        assertEquals(0x00, V.unsigned());
        assertEquals(0x01, Z.unsigned());
        assertEquals(0x00, C.unsigned());
    }

    public void assertEquals(int expected, int actual) {
        Assertions.assertEquals("0x" + Integer.toHexString(expected), "0x" + Integer.toHexString(actual));
    }
}
