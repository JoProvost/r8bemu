package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.memory.Memory;
import com.joprovost.r8bemu.memory.MemoryMapped;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.joprovost.r8bemu.mc6809.Register.PC;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InterpreterTest {
    MemoryMapped memory = new Memory(8);

    @BeforeEach
    void setUp() {
        Register.reset();
    }

    @Test
    void readInstructionFromOpCodeIncrementingRegister() {
        memory.write(0, 0x12);
        var instruction = Instruction.next(memory, PC);

        assertEquals(Mnemonic.NOP, instruction.mnemonic());
        assertEquals(0x01, PC.unsigned());
    }

    @Test
    void readExtendedInstructionFromOpCodeIncrementingRegister() {
        memory.write(0, 0x10, 0x21);
        var instruction = Instruction.next(memory, PC);

        assertEquals(Mnemonic.LBRN, instruction.mnemonic());
        assertEquals(0x02, PC.unsigned());
    }
}
