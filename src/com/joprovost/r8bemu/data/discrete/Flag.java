package com.joprovost.r8bemu.data.discrete;

public class Flag implements DiscreteAccess, DiscteteOutputHandler {
    private boolean value = false;

    public static Flag value(boolean value) {
        Flag flag = new Flag();
        flag.set(value);
        return flag;
    }

    public synchronized boolean isSet() {
        return value;
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public synchronized void set(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "" + value;
    }

    @Override
    public void handle(DiscreteOutput state) {
        set(state.isSet());
    }
}
