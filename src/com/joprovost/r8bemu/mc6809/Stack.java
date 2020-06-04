package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.clock.BusySource;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataAccessSubset;
import com.joprovost.r8bemu.memory.MemoryMapped;

import static com.joprovost.r8bemu.data.Addition.increment;
import static com.joprovost.r8bemu.data.Subtraction.decrement;
import static com.joprovost.r8bemu.data.DataAccessSubset.bit;
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

public class Stack {
    private final MemoryMapped memory;

    public Stack(MemoryMapped memory) {
        this.memory = memory;
    }

    public void pushAll(BusySource clock) {
        push(PC, S, clock);
        push(U, S, clock);
        push(Y, S, clock);
        push(X, S, clock);
        push(DP, S, clock);
        push(B, S, clock);
        push(A, S, clock);

        E.set();
        push(CC, S, clock);
    }

    public void pullAll(BusySource clock) {
        pull(CC, S, clock);
        if (E.isSet()) {
            pull(A, S, clock);
            pull(B, S, clock);
            pull(DP, S, clock);
            pull(X, S, clock);
            pull(Y, S, clock);
            pull(U, S, clock);
        }
        pull(PC, S, clock);
    }

    public void pushAll(Register stack, DataAccess configuration, BusySource clock) {
        if (bit(configuration, 7).isSet()) push(PC, stack, clock);
        if (bit(configuration, 6).isSet()) push(theOther(stack), stack, clock);
        if (bit(configuration, 5).isSet()) push(Y, stack, clock);
        if (bit(configuration, 4).isSet()) push(X, stack, clock);
        if (bit(configuration, 3).isSet()) push(DP, stack, clock);
        if (bit(configuration, 2).isSet()) push(B, stack, clock);
        if (bit(configuration, 1).isSet()) push(A, stack, clock);
        if (bit(configuration, 0).isSet()) push(CC, stack, clock);
    }

    public void pullAll(Register stack, DataAccess configuration, BusySource clock) {
        if (bit(configuration, 0).isSet()) pull(CC, stack, clock);
        if (bit(configuration, 1).isSet()) pull(A, stack, clock);
        if (bit(configuration, 2).isSet()) pull(B, stack, clock);
        if (bit(configuration, 3).isSet()) pull(DP, stack, clock);
        if (bit(configuration, 4).isSet()) pull(X, stack, clock);
        if (bit(configuration, 5).isSet()) pull(Y, stack, clock);
        if (bit(configuration, 6).isSet()) pull(theOther(stack), stack, clock);
        if (bit(configuration, 7).isSet()) pull(PC, stack, clock);
    }

    public void push(Register register, Register stack, BusySource clock) {
        if (register.mask() == 0xffff) {
            pushByte(DataAccessSubset.lsb(register), stack, clock);
            pushByte(DataAccessSubset.msb(register), stack, clock);
        } else if (register.mask() == 0xff) {
            pushByte(register, stack, clock);
        } else {
            throw new UnsupportedOperationException("Unstackable register : " + register);
        }
    }

    public void pull(Register register, Register stack, BusySource clock) {
        if (register.mask() == 0xffff) {
            pullByte(DataAccessSubset.msb(register), stack, clock);
            pullByte(DataAccessSubset.lsb(register), stack, clock);
        } else if (register.mask() == 0xff) {
            pullByte(register, stack, clock);
        } else {
            throw new UnsupportedOperationException("Unstackable register : " + register);
        }
    }

    private void pushByte(DataAccess data, Register stack, BusySource clock) {
        memory.write(stack.pre(decrement()), data.unsigned());
        clock.busy(1);
    }

    private void pullByte(DataAccess data, Register stack, BusySource clock) {
        data.set(memory.read(stack.post(increment())));
        clock.busy(1);
    }

    private Register theOther(Register register) {
        if (register == U) return S;
        if (register == S) return U;
        return register;
    }
}
