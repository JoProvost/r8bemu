package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.data.transform.DataAccessSubset;

import static com.joprovost.r8bemu.data.DataOutput.negative;
import static com.joprovost.r8bemu.data.transform.DataAccessSubset.lsb;
import static com.joprovost.r8bemu.data.transform.DataAccessSubset.msb;

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
    public static final Register A = Register.of(msb(D).describedAs("A"));
    public static final Register B = Register.of(lsb(D).describedAs("B"));

    public static final Register DP = Register.of(Variable.ofMask(0xff).describedAs("DP"));

    public static final Register CC = Register.of(Variable.ofMask(0xff).describedAs("CC"));
    public static final Register H = Register.of(DataAccessSubset.bit(CC, 5).describedAs("H"));
    public static final Register N = Register.of(DataAccessSubset.bit(CC, 3).describedAs("N"));
    public static final Register Z = Register.of(DataAccessSubset.bit(CC, 2).describedAs("Z"));
    public static final Register V = Register.of(DataAccessSubset.bit(CC, 1).describedAs("V"));
    public static final Register C = Register.of(DataAccessSubset.bit(CC, 0).describedAs("C"));
    public static final Register F = Register.of(DataAccessSubset.bit(CC, 6).describedAs("E"));
    public static final Register I = Register.of(DataAccessSubset.bit(CC, 4).describedAs("I"));
    public static final Register E = Register.of(DataAccessSubset.bit(CC, 7).describedAs("F"));

    private final DataAccess register;

    public Register(DataAccess register) {
        this.register = register;
    }

    private static Register of(DataAccess dataAccess) {
        return new Register(dataAccess);
    }

    public static void reset() {
        D.value(0);
        X.value(0);
        Y.value(0);
        U.value(0);
        S.value(0);
        DP.value(0);
        CC.value(0);
        PC.value(0);
    }

    public static String dump() {
        return "Registers:\n    " +
                D + "  " + A + " " + B + "\n    " +
                X + "  " + Y + "\n    " +
                U + "  " + S + "\n    " +
                CC + "   " + F + " " + E + " " + H + " " + I + " " + N + " " + Z + " " + V + " " + C + "\n    " +
                DP + "\n    " +
                PC;
    }

    public static void store(Register register, DataAccess memory) {
        int result = register.value();
        Register.N.set(negative(result, register.mask()));
        Register.Z.set(result == 0);
        Register.V.clear();
        memory.value(result);
    }

    public static void load(Register register, DataAccess memory) {
        int result = memory.value();
        Register.N.set(negative(result, register.mask()));
        Register.Z.set(result == 0);
        Register.V.clear();
        register.value(result);
    }

    public static void loadAddress(Register register, DataAccess argument) {
        register.value(argument.value());
        if (register == X || register == Y)
            Z.set(register.isClear());
    }

    @Override
    public String description() {
        return register.description();
    }

    @Override
    public int value() {
        return register.value();
    }

    @Override
    public int mask() {
        return register.mask();
    }

    @Override
    public void value(int value) {
        register.value(value);
    }

    @Override
    public String toString() {
        return register.toString();
    }
}
