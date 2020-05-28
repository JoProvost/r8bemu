package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.memory.Addressing;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.memory.MemoryMapped;
import com.joprovost.r8bemu.data.Size;
import com.joprovost.r8bemu.data.Constant;
import com.joprovost.r8bemu.data.MemoryAccess;

import static com.joprovost.r8bemu.data.DataOutput.signed;
import static com.joprovost.r8bemu.arithmetic.Addition.incrementBy;
import static com.joprovost.r8bemu.arithmetic.Subtraction.decrementBy;
import static com.joprovost.r8bemu.data.Constant.ONE;
import static com.joprovost.r8bemu.data.Constant.TWO;
import static com.joprovost.r8bemu.mc6809.Register.A;
import static com.joprovost.r8bemu.mc6809.Register.B;
import static com.joprovost.r8bemu.mc6809.Register.D;
import static com.joprovost.r8bemu.mc6809.Register.DP;
import static com.joprovost.r8bemu.mc6809.Register.PC;

public class MemoryManagementUnit implements MemoryMapped {
    private final MemoryMapped memory;

    public MemoryManagementUnit(MemoryMapped memory) {
        this.memory = memory;
    }

    public DataAccess data(Addressing mode) {
        switch (mode) {
            case IMMEDIATE_VALUE_8:
            case IMMEDIATE_VALUE_16:
                var value = next(PC, mode.size);
                return Constant.of(value).describedAs("#$" + value.hex());

            case EXTENDED_DATA_8:
            case EXTENDED_DATA_16:
            case DIRECT_DATA_8:
            case DIRECT_DATA_16:
            case INDEXED_DATA_8:
            case INDEXED_DATA_16:
                return MemoryAccess.of(memory, address(mode), mode.size);

            case EXTENDED_ADDRESS:
            case DIRECT_ADDRESS:
            case INDEXED_ADDRESS:
            case RELATIVE_ADDRESS_8:
            case RELATIVE_ADDRESS_16:
                throw new UnsupportedOperationException("There is no data access in " + mode);

            default:
                throw new UnsupportedOperationException("Unsupported memory access : " + mode);
        }
    }

    public DataAccess address(Addressing mode) {
        switch (mode) {
            case IMMEDIATE_VALUE_8:
            case IMMEDIATE_VALUE_16:
                throw new UnsupportedOperationException("There is no EA in IMMEDIATE memory mode");
            case EXTENDED_ADDRESS:
            case EXTENDED_DATA_8:
            case EXTENDED_DATA_16:
                return Constant.address(next(PC, Size.WORD_16));
            case RELATIVE_ADDRESS_8:
                return Constant.relativeAddress(next(PC, Size.WORD_8).signed(), PC.unsigned());
            case RELATIVE_ADDRESS_16:
                return Constant.relativeAddress(next(PC, Size.WORD_16).signed(), PC.unsigned());
            case DIRECT_ADDRESS:
            case DIRECT_DATA_8:
            case DIRECT_DATA_16:
                return Constant.directAddress(next(PC, Size.WORD_8).unsigned(), DP.unsigned());
            case INDEXED_ADDRESS:
            case INDEXED_DATA_8:
            case INDEXED_DATA_16:
                return indexedAddress(next(PC, Size.WORD_8).unsigned());
            default:
                throw new UnsupportedOperationException("Unsupported memory access : " + mode);
        }
    }

    private DataAccess indexedAddress(int postByte) {
        final Constant address;
        // Constant Offset from Register (twos Complement Offset)
        // 5-Bit Offset
        if ((postByte & 0b10000000) == 0b00000000) {
            Register register = indexedRegister(postByte);
            int offset = signed(postByte, 0b11111);
            address = Constant.address(register.unsigned() + offset, offset + "," + register.description());
        } else {
            switch (postByte & 0b00001111) {
                case 0b0000: // Auto Increment/Decrement from Register
                case 0b0001:
                case 0b0010:
                case 0b0011:
                    Register register = indexedRegister(postByte);
                    Constant offset = (postByte & 0b00000001) == 1 ? TWO : ONE;
                    boolean increment = (postByte & 0b00000010) == 0;
                    address =
                            increment
                                    ? Constant.address(register.post(incrementBy(offset)), "," + register.description() + "+".repeat(offset.unsigned()))
                                    : Constant.address(register.pre(decrementBy(offset)), "," + "-".repeat(offset.unsigned()) + register.description());
                    break;

                case 0b1000: // 8 bit offset
                    var offset8 = next(PC, Size.WORD_8).signed();
                    address = Constant.address(offset8 + indexedRegister(postByte).unsigned(), offset8 + "," + indexedRegister(postByte).description());
                    break;

                case 0b1001: // 16 bit offset
                    var offset16 = next(PC, Size.WORD_16).signed();
                    address = Constant.address(offset16 + indexedRegister(postByte).unsigned(), offset16 + "," + indexedRegister(postByte).description());
                    break;

                case 0b0100: // No Offset from Register
                    address = Constant.address(indexedRegister(postByte).unsigned(), "," + indexedRegister(postByte).description());
                    break;

                case 0b0110: // A Accumulator Offset
                    address = Constant.address(indexedRegister(postByte).unsigned() + signed(A.unsigned(), 0xff), "A," + indexedRegister(postByte).description());
                    break;
                case 0b0101: // B Accumulator Offset
                    address = Constant.address(indexedRegister(postByte).unsigned() + signed(B.unsigned(), 0xff), "B," + indexedRegister(postByte).description());
                    break;
                case 0b1011: // D Accumulator Offset
                    address = Constant.address(indexedRegister(postByte).unsigned() + signed(D.unsigned(), 0xffff), "D," + indexedRegister(postByte).description());
                    break;

                case 0b1100: // Constant Offset from Program Counter 8 bits Direct
                    offset8 = next(PC, Size.WORD_8).signed();
                    address = Constant.address(offset8 + PC.unsigned(), offset8 + ",PCR");
                    break;

                case 0b1101:// Constant Offset from Program Counter 16 bits Direct
                    offset16 = next(PC, Size.WORD_16).signed();
                    address = Constant.address(offset16 + PC.unsigned(), offset16 + ",PCR");
                    break;

                case 0b1111: // Extended
                    address = Constant.address(next(PC, Size.WORD_16).unsigned(), ",PCR");
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported indexed access : 0b" + Integer.toBinaryString(postByte) + " at 0x" + Integer.toHexString(PC.unsigned()));
            }
        }

        // indirect or not
        if ((postByte & 0b10010000) == 0b10010000) {
            return MemoryAccess.of(memory, address.unsigned(), Size.WORD_16, "[" + address.description() + "]");
        } else {
            return address;
        }
    }

    public DataAccess next(Register register, Size size) {
        return MemoryAccess.of(memory, register.post(incrementBy(size)), size);
    }

    private Register indexedRegister(int postByte) {
        switch ((postByte >> 5) & 0b11) {
            case 0b00: return Register.X;
            case 0b01: return Register.Y;
            case 0b10: return Register.U;
            case 0b11: return Register.S;
            default: throw new UnsupportedOperationException("Unsupported indexed register : 0b" + Integer.toBinaryString(postByte));
        }
    }

    public int read(int address) {
        return memory.read(address);
    }

    public void write(int address, int value) {
        memory.write(address, value);
    }
}
