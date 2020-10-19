package com.joprovost.r8bemu.data.binary;

public class BinaryRegister implements BinaryAccess {
    private final int mask;
    int variable;

    public BinaryRegister(int value, int mask) {
        this.mask = mask;
        this.variable = value;
    }

    public static BinaryRegister of(int value, int mask) {
        return new BinaryRegister(value, mask);
    }

    public static BinaryRegister ofMask(int mask) {
        return of(0, mask);
    }

    @Override
    public void value(int value) {
        this.variable = value & mask();
    }

    @Override
    public int value() {
        return variable & mask();
    }

    @Override
    public int mask() {
        return mask;
    }

    @Override
    public String description() {
        return toString();
    }

    @Override
    public String toString() {
        return "$" + hex();
    }
}
