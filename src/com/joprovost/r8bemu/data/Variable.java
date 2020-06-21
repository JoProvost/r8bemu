package com.joprovost.r8bemu.data;

public class Variable implements DataAccess {
    private final int mask;
    int variable;

    public Variable(int mask) {
        this.mask = mask;
        variable = 0;
    }

    public static Variable ofMask(int mask) {
        return new Variable(mask);
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
