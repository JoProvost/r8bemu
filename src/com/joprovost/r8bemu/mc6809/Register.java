package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.Subset;
import com.joprovost.r8bemu.data.Variable;

/**
 * Registers of the Motorola 6809
 * See https://www.maddes.net/m6809pm/sections.htm
 */
public final class Register implements DataAccess {
    public static final Register X = Register.of(Variable.ofMask(0xffff).describedAs("X"));
    public static final Register Y = Register.of(Variable.ofMask(0xffff).describedAs("Y"));

    public static final Register U = Register.of(Variable.ofMask(0xffff).describedAs("U"));
    public static final Register S = Register.of(Variable.ofMask(0xffff).describedAs("S"));

    public static final Register PC = Register.of(Variable.ofMask(0xffff).describedAs("PC"));

    public static final Register D = Register.of(Variable.ofMask(0xffff).describedAs("D"));
    public static final Register A = Register.of(Subset.msb(D).describedAs("A"));
    public static final Register B = Register.of(Subset.lsb(D).describedAs("B"));

    public static final Register DP = Register.of(Variable.ofMask(0xff).describedAs("DP"));

    public static final Register CC = Register.of(Variable.ofMask(0xff).describedAs("CC"));
    public static final Register H = Register.of(Subset.bit(CC, 5).describedAs("H"));
    public static final Register N = Register.of(Subset.bit(CC, 3).describedAs("N"));
    public static final Register Z = Register.of(Subset.bit(CC, 2).describedAs("Z"));
    public static final Register V = Register.of(Subset.bit(CC, 1).describedAs("V"));
    public static final Register C = Register.of(Subset.bit(CC, 0).describedAs("C"));
    public static final Register F = Register.of(Subset.bit(CC, 6).describedAs("E"));
    public static final Register I = Register.of(Subset.bit(CC, 4).describedAs("I"));
    public static final Register E = Register.of(Subset.bit(CC, 7).describedAs("F"));

    private final DataAccess register;

    public Register(DataAccess register) {
        this.register = register;
    }

    private static Register of(DataAccess dataAccess) {
        return new Register(dataAccess);
    }

    public static void reset() {
        D.set(0);
        X.set(0);
        Y.set(0);
        U.set(0);
        S.set(0);
        S.set(0);
        DP.set(0);
        CC.set(0);
        PC.set(0);
    }

    @Override
    public String description() {
        return register.description();
    }

    @Override
    public int unsigned() {
        return register.unsigned();
    }

    @Override
    public int mask() {
        return register.mask();
    }

    @Override
    public void set(int value) {
        register.set(value);
    }

    @Override
    public String toString() {
        return register.toString();
    }
}
