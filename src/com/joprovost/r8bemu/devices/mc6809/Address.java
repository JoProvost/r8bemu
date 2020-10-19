package com.joprovost.r8bemu.devices.mc6809;

import com.joprovost.r8bemu.data.binary.BinaryOutput;
import com.joprovost.r8bemu.data.binary.BinaryValue;
import com.joprovost.r8bemu.data.transform.Addition;
import com.joprovost.r8bemu.data.transform.Subtraction;

import static com.joprovost.r8bemu.data.binary.BinaryOutput.hex;

public class Address {
    public static BinaryOutput address(int address, String description) {
        return BinaryValue.of(address, 0xffff, description);
    }

    public static BinaryOutput address(BinaryOutput address, String description) {
        return BinaryValue.of(address, description);
    }

    public static BinaryOutput direct(int offset, Register page) {
        return address((page.value() << 8) | (offset & 0xff), ">$" + hex(offset, 0xff));
    }

    public static BinaryOutput extended(int address) {
        return BinaryValue.of(address, 0xffff, "$" + hex(address, 0xffff));
    }

    public static BinaryOutput register(Register register) {
        return address(register.value(), "," + register.description());
    }

    public static BinaryOutput incrementBy(BinaryOutput offset, Register register) {
        return address(register.post(Addition.incrementBy(offset)), "," + register.description() + "+".repeat(offset.value()));
    }

    public static BinaryOutput decrementBy(BinaryOutput offset, Register register) {
        return address(register.pre(Subtraction.decrementBy(offset)), "," + "-".repeat(offset.value()) + register.description());
    }

    public static BinaryOutput offset(int offset, Register register) {
        return address(offset + register.value(), offset + "," + register.description());
    }

    public static BinaryOutput accumulatorOffset(Register accumulator, Register register) {
        return address(accumulator.signed() + register.value(), accumulator.description() + "," + register.description());
    }
}
