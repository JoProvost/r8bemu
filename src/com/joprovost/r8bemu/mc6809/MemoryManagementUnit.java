package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.memory.Addressing;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.memory.MemoryMapped;
import com.joprovost.r8bemu.data.Size;
import com.joprovost.r8bemu.data.Value;
import com.joprovost.r8bemu.data.Reference;

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
                var value = Reference.next(memory, mode.size, PC);
                return Value.of(value).describedAs("#$" + value.hex());

            case EXTENDED_DATA_8:
            case EXTENDED_DATA_16:
            case DIRECT_DATA_8:
            case DIRECT_DATA_16:
            case INDEXED_DATA_8:
            case INDEXED_DATA_16:
                return Reference.of(memory, address(mode), mode.size);

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
                return Address.extended(Reference.next(memory, Size.WORD_16, PC).unsigned());
            case RELATIVE_ADDRESS_8:
                return Address.offset(Reference.next(memory, Size.WORD_8, PC).signed(), PC);
            case RELATIVE_ADDRESS_16:
                return Address.offset(Reference.next(memory, Size.WORD_16, PC).signed(), PC);
            case DIRECT_ADDRESS:
            case DIRECT_DATA_8:
            case DIRECT_DATA_16:
                return Address.direct(Reference.next(memory, Size.WORD_8, PC).unsigned(), DP);
            case INDEXED_ADDRESS:
            case INDEXED_DATA_8:
            case INDEXED_DATA_16:
                return IndexedAddress.next(memory, PC);
            default:
                throw new UnsupportedOperationException("Unsupported memory access : " + mode);
        }
    }

    public int read(int address) {
        return memory.read(address);
    }

    public void write(int address, int value) {
        memory.write(address, value);
    }
}
