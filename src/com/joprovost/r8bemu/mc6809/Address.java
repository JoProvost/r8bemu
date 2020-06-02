package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.Addition;
import com.joprovost.r8bemu.data.Subtraction;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.Value;

import static com.joprovost.r8bemu.data.DataOutput.hex;

public class Address {
    public static Value address(int address, String description) {
        return Value.of(address, 0xffff, description);
    }

    public static Value address(DataOutput address, String description) {
        return Value.of(address, description);
    }

    public static Value direct(int offset, Register page) {
        return address((page.unsigned() << 8) | (offset & 0xff), ">$" + hex(offset, 0xff));
    }

    public static Value extended(int address) {
        return Value.of(address, 0xffff, "$" + hex(address, 0xffff));
    }

    public static Value register(Register register) {
        return address(register.unsigned(), "," + register.description());
    }

    public static Value incrementBy(Value offset, Register register) {
        return address(register.post(Addition.incrementBy(offset)), "," + register.description() + "+".repeat(offset.unsigned()));
    }

    public static Value decrementBy(Value offset, Register register) {
        return address(register.pre(Subtraction.decrementBy(offset)), "," + "-".repeat(offset.unsigned()) + register.description());
    }

    public static Value offset(int offset, Register register) {
        return address(offset + register.unsigned(), offset + "," + register.description());
    }

    public static Value accumulatorOffset(Register accumulator, Register register) {
        return address(accumulator.signed() + register.unsigned(), accumulator.description() + "," + register.description());
    }
}
