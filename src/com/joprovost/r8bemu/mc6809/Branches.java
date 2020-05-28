package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataOutput;

import java.util.function.Supplier;

import static com.joprovost.r8bemu.mc6809.Register.CC;
import static com.joprovost.r8bemu.mc6809.Register.E;
import static com.joprovost.r8bemu.mc6809.Register.PC;
import static com.joprovost.r8bemu.mc6809.Register.S;

public class Branches {
    private final Stack stack;

    public Branches(Stack stack) {
        this.stack = stack;
    }

    public void jumpIf(Supplier<Boolean> condition, DataOutput address) {
        if (condition.get()) {
            jump(address);
        }
    }

    public void jump(DataOutput address) {
        PC.set(address);
    }

    public void rts() {
        stack.pull(PC, Register.S);
    }

    public void rti() {
        stack.pullAll();
    }

    public void jsr(DataOutput address) {
        stack.push(PC, Register.S);
        PC.set(address);
    }

    public void bsr(DataOutput address) {
        stack.push(PC, Register.S);
        jump(address);
    }

    public void interrupt(DataOutput address) {
        stack.pushAll();
        jump(address);
    }

    public void fastInterrupt(DataOutput address) {
        stack.push(PC, S);

        E.clear();
        stack.push(CC, S);

        jump(address);
    }
}
