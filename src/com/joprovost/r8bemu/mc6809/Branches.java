package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.clock.BusySource;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataOutput;

import java.util.function.Supplier;

import static com.joprovost.r8bemu.mc6809.Register.C;
import static com.joprovost.r8bemu.mc6809.Register.CC;
import static com.joprovost.r8bemu.mc6809.Register.E;
import static com.joprovost.r8bemu.mc6809.Register.N;
import static com.joprovost.r8bemu.mc6809.Register.PC;
import static com.joprovost.r8bemu.mc6809.Register.S;
import static com.joprovost.r8bemu.mc6809.Register.V;
import static com.joprovost.r8bemu.mc6809.Register.Z;

public class Branches {

    public static void jumpIf(Supplier<Boolean> condition, DataOutput address) {
        if (condition.get()) {
            jump(address);
        }
    }

    public static void jump(DataOutput address) {
        PC.set(address);
    }

    public static void bvs(DataAccess argument) {
        // IFF V = 1 then PC' ← PC + TEMP
        jumpIf(V::isSet, argument);
    }

    public static void bvc(DataAccess argument) {
        // IFF V = 0 then PC' ← PC + TEMP
        jumpIf(V::isClear, argument);
    }

    public static void brn(DataAccess argument) {
        jumpIf(() -> false, argument);
    }

    public static void bra(DataAccess argument) {
        jumpIf(() -> true, argument);
    }

    public static void bpl(DataAccess argument) {
        // IFF N = 0 then PC' ← PC + TEMP
        jumpIf(N::isClear, argument);
    }

    public static void bne(DataAccess argument) {
        // IFF Z = 0 then PC' ← PC + TEMP
        jumpIf(Z::isClear, argument);
    }

    public static void bmi(DataAccess argument) {
        // IFF N = 1 then PC' ← PC + TEMP
        jumpIf(N::isSet, argument);
    }

    public static void bcc(DataAccess argument) {
        jumpIf(C::isClear, argument);
    }

    public static void bcs(DataAccess argument) {
        jumpIf(C::isSet, argument);
    }

    public static void beq(DataAccess argument) {
        jumpIf(Z::isSet, argument);
    }

    public static void bge(DataAccess argument) {
        // IFF [N ⊕ V] = 0 then PC' ← PC + TEMP
        jumpIf(() -> N.isSet() == V.isSet(), argument);
    }

    public static void bgt(DataAccess argument) {
        // IFF Z ∧ [N ⊕ V] = 0 then PC' ← PC + TEMP
        jumpIf(() -> Z.isClear() == (N.isSet() == V.isSet()), argument);
    }

    public static void blt(DataAccess argument) {
        // IFF [ N ⊕ V ] = 1 then PC' ← PC + TEMP
        jumpIf(() -> N.isSet() != V.isSet(), argument);
    }

    public static void bls(DataAccess argument) {
        // IFF (C ∨ Z) = 1 then PC' ← PC + TEMP
        jumpIf(() -> C.isSet() || Z.isSet(), argument);
    }

    public static void ble(DataAccess argument) {
        // IFF Z ∨ [ N ⊕ V ] = 1 then PC' ← PC + TEMP
        jumpIf(() -> Z.isSet() || (N.isSet() != V.isSet()), argument);
    }

    public static void bhi(DataAccess argument) {
        // IFF [ C ∨ Z ] = 0 then PC' ← PC + TEMP
        jumpIf(() -> C.isClear() && Z.isClear(), argument);
    }

    public static void rts(Stack stack, BusySource clock) {
        stack.pull(PC, Register.S, clock);
    }

    public static void rti(Stack stack, BusySource clock) {
        stack.pullAll(clock);
    }

    public static void jsr(DataOutput address, Stack stack, BusySource clock) {
        stack.push(PC, Register.S, clock);
        jump(address);
    }

    public static void bsr(DataOutput address, Stack stack, BusySource clock) {
        jsr(address, stack, clock);
    }

    public static void interrupt(DataOutput address, Stack stack, BusySource clock) {
        stack.pushAll(clock);
        jump(address);
    }

    public static void fastInterrupt(DataOutput address, Stack stack, BusySource clock) {
        stack.push(PC, S, clock);

        E.clear();
        stack.push(CC, S, clock);

        jump(address);
    }

    public static void nop() {
    }
}
