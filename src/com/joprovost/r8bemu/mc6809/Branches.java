package com.joprovost.r8bemu.mc6809;

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
    private final Stack stack;

    public Branches(Stack stack) {
        this.stack = stack;
    }

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

    public void rts() {
        stack.pull(PC, Register.S);
    }

    public void rti() {
        stack.pullAll();
    }

    public void jsr(DataOutput address) {
        stack.push(PC, Register.S);
        jump(address);
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

    public static void nop() {
    }
}
