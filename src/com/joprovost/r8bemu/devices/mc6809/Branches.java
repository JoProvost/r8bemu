package com.joprovost.r8bemu.devices.mc6809;

import com.joprovost.r8bemu.data.binary.BinaryAccess;
import com.joprovost.r8bemu.data.binary.BinaryOutput;

import java.util.function.Supplier;

import static com.joprovost.r8bemu.devices.mc6809.Register.C;
import static com.joprovost.r8bemu.devices.mc6809.Register.N;
import static com.joprovost.r8bemu.devices.mc6809.Register.PC;
import static com.joprovost.r8bemu.devices.mc6809.Register.V;
import static com.joprovost.r8bemu.devices.mc6809.Register.Z;

public class Branches {

    public static void jumpIf(Supplier<Boolean> condition, BinaryOutput address) {
        if (condition.get()) {
            jump(address);
        }
    }

    public static void jump(BinaryOutput address) {
        PC.value(address);
    }

    public static void bvs(BinaryAccess argument) {
        // IFF V = 1 then PC' ← PC + TEMP
        jumpIf(V::isSet, argument);
    }

    public static void bvc(BinaryAccess argument) {
        // IFF V = 0 then PC' ← PC + TEMP
        jumpIf(V::isClear, argument);
    }

    public static void brn(BinaryAccess argument) {
        jumpIf(() -> false, argument);
    }

    public static void bra(BinaryAccess argument) {
        jumpIf(() -> true, argument);
    }

    public static void bpl(BinaryAccess argument) {
        // IFF N = 0 then PC' ← PC + TEMP
        jumpIf(N::isClear, argument);
    }

    public static void bne(BinaryAccess argument) {
        // IFF Z = 0 then PC' ← PC + TEMP
        jumpIf(Z::isClear, argument);
    }

    public static void bmi(BinaryAccess argument) {
        // IFF N = 1 then PC' ← PC + TEMP
        jumpIf(N::isSet, argument);
    }

    public static void bcc(BinaryAccess argument) {
        jumpIf(C::isClear, argument);
    }

    public static void bcs(BinaryAccess argument) {
        jumpIf(C::isSet, argument);
    }

    public static void beq(BinaryAccess argument) {
        jumpIf(Z::isSet, argument);
    }

    public static void bge(BinaryAccess argument) {
        // IFF [N ⊕ V] = 0 then PC' ← PC + TEMP
        jumpIf(() -> N.isSet() == V.isSet(), argument);
    }

    public static void bgt(BinaryAccess argument) {
        // IFF Z ∧ [N ⊕ V] = 0 then PC' ← PC + TEMP
        jumpIf(() -> Z.isClear() == (N.isSet() == V.isSet()), argument);
    }

    public static void blt(BinaryAccess argument) {
        // IFF [ N ⊕ V ] = 1 then PC' ← PC + TEMP
        jumpIf(() -> N.isSet() != V.isSet(), argument);
    }

    public static void bls(BinaryAccess argument) {
        // IFF (C ∨ Z) = 1 then PC' ← PC + TEMP
        jumpIf(() -> C.isSet() || Z.isSet(), argument);
    }

    public static void ble(BinaryAccess argument) {
        // IFF Z ∨ [ N ⊕ V ] = 1 then PC' ← PC + TEMP
        jumpIf(() -> Z.isSet() || (N.isSet() != V.isSet()), argument);
    }

    public static void bhi(BinaryAccess argument) {
        // IFF [ C ∨ Z ] = 0 then PC' ← PC + TEMP
        jumpIf(() -> C.isClear() && Z.isClear(), argument);
    }

    public static void nop() {
    }
}
