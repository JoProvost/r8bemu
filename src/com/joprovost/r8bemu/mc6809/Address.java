package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.transform.Addition;
import com.joprovost.r8bemu.data.transform.Subtraction;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.Value;

import static com.joprovost.r8bemu.data.DataOutput.hex;

public class Address {
    public static DataOutput address(int address, String description) {
        return Value.of(address, 0xffff, description);
    }

    public static DataOutput address(DataOutput address, String description) {
        return Value.of(address, description);
    }

    public static DataOutput direct(int offset, Register page) {
        return address((page.value() << 8) | (offset & 0xff), ">$" + hex(offset, 0xff));
    }

    public static DataOutput extended(int address) {
        return Value.of(address, 0xffff, "$" + hex(address, 0xffff));
    }

    public static DataOutput register(Register register) {
        return address(register.value(), "," + register.description());
    }

    public static DataOutput incrementBy(DataOutput offset, Register register) {
        return address(register.post(Addition.incrementBy(offset)), "," + register.description() + "+".repeat(offset.value()));
    }

    public static DataOutput decrementBy(DataOutput offset, Register register) {
        return address(register.pre(Subtraction.decrementBy(offset)), "," + "-".repeat(offset.value()) + register.description());
    }

    public static DataOutput offset(int offset, Register register) {
        return address(offset + register.value(), offset + "," + register.description());
    }

    public static DataOutput accumulatorOffset(Register accumulator, Register register) {
        return address(accumulator.signed() + register.value(), accumulator.description() + "," + register.description());
    }
}
