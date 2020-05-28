package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.memory.MemoryMapped;
import com.joprovost.r8bemu.memory.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.joprovost.r8bemu.mc6809.Register.PC;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InterpreterTest {
    MemoryMapped memory = new Memory(8);

    MemoryManagementUnit mmu = new MemoryManagementUnit(memory);

    Interpreter interpreter = new Interpreter(mmu);

    @BeforeEach
    void setUp() {
        Register.reset();
    }

    @Test
    void readInstructionFromOpCodeIncrementingRegister() {
        memory.write(0, 0x12);
        var instruction = interpreter.next();

        assertEquals(Mnemonic.NOP, instruction.mnemonic());
        assertEquals(0x01, ((DataAccess) PC).unsigned());
    }

    @Test
    void readExtendedInstructionFromOpCodeIncrementingRegister() {
        memory.write(0, 0x10, 0x21);
        var instruction = interpreter.next();

        assertEquals(Mnemonic.LBRN, instruction.mnemonic());
        assertEquals(0x02, ((DataAccess) PC).unsigned());
    }
}
