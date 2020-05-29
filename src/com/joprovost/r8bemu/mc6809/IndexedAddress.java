package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.Reference;
import com.joprovost.r8bemu.data.Size;
import com.joprovost.r8bemu.data.Value;
import com.joprovost.r8bemu.memory.MemoryMapped;

import static com.joprovost.r8bemu.data.DataOutput.signed;
import static com.joprovost.r8bemu.data.Value.ONE;
import static com.joprovost.r8bemu.data.Value.TWO;
import static com.joprovost.r8bemu.mc6809.Register.A;
import static com.joprovost.r8bemu.mc6809.Register.B;
import static com.joprovost.r8bemu.mc6809.Register.D;
import static com.joprovost.r8bemu.mc6809.Register.PC;

public class IndexedAddress {
    public static DataAccess from(MemoryMapped memory, int postByte) {
        final Value address;
        // Constant Offset from Register (twos Complement Offset)
        // 5-Bit Offset
        if ((postByte & 0b10000000) == 0) {
            address = Address.offset(signed(postByte, 0b11111), register(postByte));
        } else {
            switch (postByte & 0b00001111) {
                case 0b0000: // Auto Increment/Decrement from Register
                    address = Address.incrementBy(ONE, register(postByte));
                    break;
                case 0b0001:
                    address = Address.incrementBy(TWO, register(postByte));
                    break;
                case 0b0010:
                    address = Address.decrementBy(ONE, register(postByte));
                    break;
                case 0b0011:
                    address = Address.decrementBy(TWO, register(postByte));
                    break;

                case 0b1000: // 8 bit offset
                    address = Address.offset(Reference.next(memory, Size.WORD_8, PC).signed(), register(postByte));
                    break;

                case 0b1001: // 16 bit offset
                    address = Address.offset(Reference.next(memory, Size.WORD_16, PC).signed(), register(postByte));
                    break;

                case 0b0100: // No Offset from Register
                    address = Address.register(register(postByte));
                    break;

                case 0b0110: // A Accumulator Offset
                    address = Address.accumulatorOffset(A, register(postByte));
                    break;
                case 0b0101: // B Accumulator Offset
                    address = Address.accumulatorOffset(B, register(postByte));
                    break;
                case 0b1011: // D Accumulator Offset
                    address = Address.accumulatorOffset(D, register(postByte));
                    break;

                case 0b1100: // Constant Offset from Program Counter 8 bits Direct
                    address = Address.offset(Reference.next(memory, Size.WORD_8, PC).signed(), PC);
                    break;

                case 0b1101:// Constant Offset from Program Counter 16 bits Direct
                    address = Address.offset(Reference.next(memory, Size.WORD_16, PC).signed(), PC);
                    break;

                case 0b1111: // Extended
                    address = Address.extended(Reference.next(memory, Size.WORD_16, PC).unsigned());
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported indexed access : 0b" + Integer.toBinaryString(postByte) + " at 0x" + Integer.toHexString(PC.unsigned()));
            }
        }

        // indirect or not
        if ((postByte & 0b10010000) == 0b10010000) {
            return Reference.of(memory, address.unsigned(), Size.WORD_16, "[" + address.description() + "]");
        } else {
            return address;
        }
    }

    private static Register register(int postByte) {
        switch ((postByte >> 5) & 0b11) {
            case 0b00: return Register.X;
            case 0b01: return Register.Y;
            case 0b10: return Register.U;
            case 0b11: return Register.S;
            default: throw new UnsupportedOperationException("Unsupported indexed register : 0b" + Integer.toBinaryString(postByte));
        }
    }

    public static DataAccess next(MemoryMapped memory, Register register) {
        return from(memory, Reference.next(memory, Size.WORD_8, register).unsigned());
    }
}
