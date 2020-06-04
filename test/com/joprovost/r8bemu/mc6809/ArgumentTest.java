package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.clock.BusySource;
import com.joprovost.r8bemu.clock.ClockState;
import com.joprovost.r8bemu.data.Reference;
import com.joprovost.r8bemu.data.Size;
import com.joprovost.r8bemu.memory.Addressing;
import com.joprovost.r8bemu.memory.Memory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.joprovost.r8bemu.mc6809.Register.DP;
import static com.joprovost.r8bemu.mc6809.Register.PC;
import static com.joprovost.r8bemu.mc6809.Register.S;

class ArgumentTest {

    Memory memory = new Memory(0xffff);
    BusySource clock = new ClockState();

    @BeforeEach
    void setUp() {
        Register.reset();
    }

    @Test
    void loadImmediate() {
        memory.write(0x00, 0x12, 0x34);

        PC.set(0x01);
        assertEquals(0x34, Argument.next(memory, Addressing.IMMEDIATE_VALUE_8, PC, clock).unsigned());
        assertEquals(0x02, PC.unsigned());
    }

    @Test
    void loadImmediate16() {
        memory.write(0x00, 0x12, 0x34, 0x56, 0x7a);
        PC.set(0x01);
        assertEquals(0x3456, Argument.next(memory, Addressing.IMMEDIATE_VALUE_16, PC, clock).unsigned());
        assertEquals(0x03, PC.unsigned());
    }

    @Test
    void loadExtended() {
        PC.set(0x01);
        memory.write(0x01, 0x12, 0x34);
        memory.write(0x1234, 0x56);

        var access = Argument.next(memory, Addressing.EXTENDED_DATA_8, PC, clock);
        assertEquals(0x56, access.unsigned());

        access.set(0x7a);
        assertEquals(0x7a, memory.read(0x1234));

        assertEquals(0x03, PC.unsigned());
    }

    @Test
    void loadDirect() {
        PC.set(0x01);
        DP.set(0x12);
        memory.write(0x01, 0x34);
        memory.write(0x1234, 0x56);

        var access = Argument.next(memory, Addressing.DIRECT_DATA_8, PC, clock);
        assertEquals(0x56, access.unsigned());

        access.set(0x7a);
        assertEquals(0x7a, memory.read(0x1234));

        assertEquals(0x02, PC.unsigned());
    }

    @Test
    void loadExtendedIndirect() {
        PC.set(0x01);
        memory.write(0x01, 0b10011111, 0x12, 0x34);
        memory.write(0x1234, 0x56, 0x78);
        memory.write(0x5678, 0x9a);

        var access = Argument.next(memory, Addressing.INDEXED_DATA_8, PC, clock);
        assertEquals(0x9a, access.unsigned());

        access.set(0x7a);
        assertEquals(0x7a, memory.read(0x5678));

        assertEquals(0x04, PC.unsigned());
    }

    @Test
    void loadDirect8BitsConstantOffsetFromProgramCounter() {
        PC.set(0x01);
        memory.write(0x01, 0b10001100, 0x10);
        memory.write(0x13, 0x56);

        var access = Argument.next(memory, Addressing.INDEXED_DATA_8, PC, clock);
        assertEquals(0x56, access.unsigned());

        access.set(0x7a);
        assertEquals(0x7a, memory.read(0x13));

        assertEquals(0x03, PC.unsigned());
    }

    @Test
    void loadDirect16BitsConstantOffsetFromProgramCounter() {
        PC.set(0x01);
        memory.write(0x01, 0b10001101, 0x10, 0x00);
        memory.write(0x1004, 0x56);

        var access = Argument.next(memory, Addressing.INDEXED_DATA_8, PC, clock);
        assertEquals(0x56, access.unsigned());

        access.set(0x7a);
        assertEquals(0x7a, memory.read(0x1004));

        assertEquals(0x04, PC.unsigned());
    }

    @Test
    void loadIndirect8BitsConstantOffsetFromProgramCounter() {
        PC.set(0x01);
        memory.write(0x01, 0b10011100, 0x10);
        memory.write(0x13, 0x56, 0x78);

        memory.write(0x5678, 0x99);

        var access = Argument.next(memory, Addressing.INDEXED_DATA_8, PC, clock);
        assertEquals(0x99, access.unsigned());

        access.set(0x7a);
        assertEquals(0x7a, memory.read(0x5678));

        assertEquals(0x03, PC.unsigned());
    }

    @Test
    void loadIndirect16BitsConstantOffsetFromProgramCounter() {
        PC.set(0x01);
        memory.write(0x01, 0b10011101, 0x10, 0x00);
        memory.write(0x1004, 0x56, 0x78);

        memory.write(0x5678, 0x99);

        var access = Argument.next(memory, Addressing.INDEXED_DATA_8, PC, clock);
        assertEquals(0x99, access.unsigned());

        access.set(0x7a);
        assertEquals(0x7a, memory.read(0x5678));

        assertEquals(0x04, PC.unsigned());
    }

    // indexed 0b01111100
    @Test
    void loadConstantOffsetFromRegister5Bits() {

        // S + 0b11100 => S - 4
        PC.set(0x01);
        S.set(0x1014);

        memory.write(0x01, 0b01111100);
        memory.write(0x1010, 0x99);

        var access = Argument.next(memory, Addressing.INDEXED_DATA_8, PC, clock);
        assertEquals(0x99, access.unsigned());

        access.set(0x7a);
        assertEquals(0x7a, memory.read(0x1010));

        assertEquals(0x02, PC.unsigned());
    }

    // indexed 0b11110100 No offset Indirect
    @Test
    void loadIndirectFromRegister() {

        // S
        PC.set(0x01);
        S.set(0x1000);

        memory.write(0x01, 0b11110100);
        memory.write(0x1000, 0x20, 0x00);
        memory.write(0x2000, 0x30, 0x00);

        var access = Argument.next(memory, Addressing.INDEXED_DATA_16, PC, clock);
        assertEquals(0x3000, access.unsigned());

        access.set(0x4000);
        assertEquals(0x4000, Reference.of(memory, 0x2000, Size.WORD_16).unsigned());

        assertEquals(0x02, PC.unsigned());
    }

    public void assertEquals(int expected, int actual) {
        Assertions.assertEquals("0x" + Integer.toHexString(expected), "0x" + Integer.toHexString(actual));
    }
}
