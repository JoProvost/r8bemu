package com.joprovost.r8bemu.devices.mc6809;

import com.joprovost.r8bemu.clock.BusyState;
import com.joprovost.r8bemu.data.binary.BinaryAccess;
import com.joprovost.r8bemu.data.binary.BinaryOutput;
import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.devices.memory.Addressing;
import com.joprovost.r8bemu.devices.memory.BinaryReference;
import com.joprovost.r8bemu.devices.memory.Size;

import static com.joprovost.r8bemu.devices.mc6809.Register.DP;

public class Argument {
    public static final BinaryOutput NO_ARGUMENT = BinaryOutput.NONE;

    public static BinaryAccess next(Addressable memory, Addressing mode, Register register, BusyState clock) {
        switch (mode) {
            case INHERENT:
                return BinaryAccess.of(NO_ARGUMENT);

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
                return BinaryAccess.of(address(memory, mode, register, clock));

            default:
                throw new UnsupportedOperationException("Unsupported memory access : " + mode);
        }
    }

    private static BinaryAccess data(Addressable memory, Addressing mode, Register register, BusyState clock) {
        switch (mode) {
            case IMMEDIATE_VALUE_8:
            case IMMEDIATE_VALUE_16:
                var value = BinaryReference.next(memory, mode.size, register);
                return value.describedAs("#$" + value.hex());

            case EXTENDED_DATA_8:
            case EXTENDED_DATA_16:
            case DIRECT_DATA_8:
            case DIRECT_DATA_16:
            case INDEXED_DATA_8:
            case INDEXED_DATA_16:
                return BinaryReference.of(memory, address(memory, mode, register, clock), mode.size);

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

    private static BinaryOutput address(Addressable memory, Addressing mode, Register register, BusyState clock) {
        switch (mode) {
            case IMMEDIATE_VALUE_8:
            case IMMEDIATE_VALUE_16:
                throw new UnsupportedOperationException("There is no EA in IMMEDIATE memory mode");
            case EXTENDED_ADDRESS:
            case EXTENDED_DATA_8:
            case EXTENDED_DATA_16:
                return Address.extended(BinaryReference.next(memory, Size.WORD_16, register).value());
            case RELATIVE_ADDRESS_8:
                return Address.offset(BinaryReference.next(memory, Size.WORD_8, register).signed(), register);
            case RELATIVE_ADDRESS_16:
                return Address.offset(BinaryReference.next(memory, Size.WORD_16, register).signed(), register);
            case DIRECT_ADDRESS:
            case DIRECT_DATA_8:
            case DIRECT_DATA_16:
                return Address.direct(BinaryReference.next(memory, Size.WORD_8, register).value(), DP);
            case INDEXED_ADDRESS:
            case INDEXED_DATA_8:
            case INDEXED_DATA_16:
                return IndexedAddress.next(memory, register, clock);
            default:
                throw new UnsupportedOperationException("Unsupported memory access : " + mode);
        }
    }
}
