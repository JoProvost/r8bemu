package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.clock.BusyState;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.MemoryDataReference;
import com.joprovost.r8bemu.data.Size;
import com.joprovost.r8bemu.data.transform.DataAccessSubset;
import com.joprovost.r8bemu.memory.MemoryDevice;

import static com.joprovost.r8bemu.data.transform.Addition.increment;
import static com.joprovost.r8bemu.data.transform.DataAccessSubset.bit;
import static com.joprovost.r8bemu.data.transform.Subtraction.decrement;
import static com.joprovost.r8bemu.mc6809.Register.A;
import static com.joprovost.r8bemu.mc6809.Register.B;
import static com.joprovost.r8bemu.mc6809.Register.CC;
import static com.joprovost.r8bemu.mc6809.Register.DP;
import static com.joprovost.r8bemu.mc6809.Register.E;
import static com.joprovost.r8bemu.mc6809.Register.PC;
import static com.joprovost.r8bemu.mc6809.Register.S;
import static com.joprovost.r8bemu.mc6809.Register.U;
import static com.joprovost.r8bemu.mc6809.Register.X;
import static com.joprovost.r8bemu.mc6809.Register.Y;

// See https://techheap.packetizer.com/processors/6809/6809Instructions.html
public class Stack {
    private final MemoryDevice memory;
    private final BusyState clock;

    public Stack(MemoryDevice memory, BusyState clock) {
        this.memory = memory;
        this.clock = clock;
    }

    public static void rts(Stack stack) {
        stack.pull(PC, Register.S);
    }

    public static void rti(Stack stack) {
        stack.pullAll();
    }

    public static void jsr(DataOutput address, Stack stack) {
        stack.push(PC, Register.S);
        Branches.jump(address);
    }

    public static void bsr(DataOutput address, Stack stack) {
        jsr(address, stack);
    }

    public static void interrupt(DataOutput address, Stack stack) {
        stack.pushAll();
        Branches.jump(address);
    }

    public static void fastInterrupt(DataOutput address, Stack stack) {
        stack.push(PC, S);

        E.clear();
        stack.push(CC, S);

        Branches.jump(address);
    }

    public static void swi(Stack stack) {
        stack.clock.busy(6);
        interrupt(MemoryDataReference.of(stack.memory, 0xfffa, Size.WORD_16), stack);
        Register.I.set();
        Register.F.set();
    }

    public static void swi2(Stack stack) {
        interrupt(MemoryDataReference.of(stack.memory, 0xfff4, Size.WORD_16), stack);
    }

    public static void swi3(Stack stack) {
        stack.clock.busy(7);
        stack.pushAll();
        Branches.jump(MemoryDataReference.of(stack.memory, 0xfff2, Size.WORD_16));
    }

    public void pushAll() {
        push(PC, S);
        push(U, S);
        push(Y, S);
        push(X, S);
        push(DP, S);
        push(B, S);
        push(A, S);

        E.set();
        push(CC, S);
    }

    public void pullAll() {
        pull(CC, S);
        if (E.isSet()) {
            pull(A, S);
            pull(B, S);
            pull(DP, S);
            pull(X, S);
            pull(Y, S);
            pull(U, S);
        }
        pull(PC, S);
    }

    public void pushAll(Register stack, DataAccess configuration) {
        if (bit(configuration, 7).isSet()) push(PC, stack);
        if (bit(configuration, 6).isSet()) push(theOther(stack), stack);
        if (bit(configuration, 5).isSet()) push(Y, stack);
        if (bit(configuration, 4).isSet()) push(X, stack);
        if (bit(configuration, 3).isSet()) push(DP, stack);
        if (bit(configuration, 2).isSet()) push(B, stack);
        if (bit(configuration, 1).isSet()) push(A, stack);
        if (bit(configuration, 0).isSet()) push(CC, stack);
    }

    public void pullAll(Register stack, DataAccess configuration) {
        if (bit(configuration, 0).isSet()) pull(CC, stack);
        if (bit(configuration, 1).isSet()) pull(A, stack);
        if (bit(configuration, 2).isSet()) pull(B, stack);
        if (bit(configuration, 3).isSet()) pull(DP, stack);
        if (bit(configuration, 4).isSet()) pull(X, stack);
        if (bit(configuration, 5).isSet()) pull(Y, stack);
        if (bit(configuration, 6).isSet()) pull(theOther(stack), stack);
        if (bit(configuration, 7).isSet()) pull(PC, stack);
    }

    public void push(Register register, Register stack) {
        if (register.mask() == 0xffff) {
            pushByte(DataAccessSubset.lsb(register), stack);
            pushByte(DataAccessSubset.msb(register), stack);
        } else if (register.mask() == 0xff) {
            pushByte(register, stack);
        } else {
            throw new UnsupportedOperationException("Unstackable register : " + register);
        }
    }

    public void pull(Register register, Register stack) {
        if (register.mask() == 0xffff) {
            pullByte(DataAccessSubset.msb(register), stack);
            pullByte(DataAccessSubset.lsb(register), stack);
        } else if (register.mask() == 0xff) {
            pullByte(register, stack);
        } else {
            throw new UnsupportedOperationException("Unstackable register : " + register);
        }
    }

    private void pushByte(DataAccess data, Register stack) {
        memory.write(stack.pre(decrement()), data.value());
        this.clock.busy(1);
    }

    private void pullByte(DataAccess data, Register stack) {
        data.value(memory.read(stack.post(increment())));
        this.clock.busy(1);
    }

    private Register theOther(Register register) {
        if (register == U) return S;
        if (register == S) return U;
        return register;
    }
}
