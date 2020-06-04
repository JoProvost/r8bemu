package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.clock.BusySource;
import com.joprovost.r8bemu.memory.Addressing;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.memory.MemoryMapped;
import com.joprovost.r8bemu.data.Size;
import com.joprovost.r8bemu.data.Value;
import com.joprovost.r8bemu.data.Reference;

import static com.joprovost.r8bemu.mc6809.Register.DP;

public class Argument {
    public static final DataAccess NO_ARGUMENT = Value.of(0, 0).describedAs("");

    public static DataAccess next(MemoryMapped memory, Addressing mode, Register register, BusySource clock) {
        switch (mode) {
            case INHERENT:
                return NO_ARGUMENT;

            case IMMEDIATE_VALUE_8:
            case IMMEDIATE_VALUE_16:
            case EXTENDED_DATA_8:
            case EXTENDED_DATA_16:
            case DIRECT_DATA_8:
            case DIRECT_DATA_16:
            case INDEXED_DATA_8:
            case INDEXED_DATA_16:
                return data(memory, mode, register, clock);

            case EXTENDED_ADDRESS:
            case DIRECT_ADDRESS:
            case INDEXED_ADDRESS:
            case RELATIVE_ADDRESS_8:
            case RELATIVE_ADDRESS_16:
                return address(memory, mode, register, clock);

            default:
                throw new UnsupportedOperationException("Unsupported memory access : " + mode);
        }
    }

    private static DataAccess data(MemoryMapped memory, Addressing mode, Register register, BusySource clock) {
        switch (mode) {
            case IMMEDIATE_VALUE_8:
            case IMMEDIATE_VALUE_16:
                var value = Reference.next(memory, mode.size, register);
                return Value.of(value).describedAs("#$" + value.hex());

            case EXTENDED_DATA_8:
            case EXTENDED_DATA_16:
            case DIRECT_DATA_8:
            case DIRECT_DATA_16:
            case INDEXED_DATA_8:
            case INDEXED_DATA_16:
                return Reference.of(memory, address(memory, mode, register, clock), mode.size);

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

    private static DataAccess address(MemoryMapped memory, Addressing mode, Register register, BusySource clock) {
        switch (mode) {
            case IMMEDIATE_VALUE_8:
            case IMMEDIATE_VALUE_16:
                throw new UnsupportedOperationException("There is no EA in IMMEDIATE memory mode");
            case EXTENDED_ADDRESS:
            case EXTENDED_DATA_8:
            case EXTENDED_DATA_16:
                return Address.extended(Reference.next(memory, Size.WORD_16, register).unsigned());
            case RELATIVE_ADDRESS_8:
                return Address.offset(Reference.next(memory, Size.WORD_8, register).signed(), register);
            case RELATIVE_ADDRESS_16:
                return Address.offset(Reference.next(memory, Size.WORD_16, register).signed(), register);
            case DIRECT_ADDRESS:
            case DIRECT_DATA_8:
            case DIRECT_DATA_16:
                return Address.direct(Reference.next(memory, Size.WORD_8, register).unsigned(), DP);
            case INDEXED_ADDRESS:
            case INDEXED_DATA_8:
            case INDEXED_DATA_16:
                return IndexedAddress.next(memory, register, clock);
            default:
                throw new UnsupportedOperationException("Unsupported memory access : " + mode);
        }
    }
}
