package com.joprovost.r8bemu.data;

import com.joprovost.r8bemu.data.link.LineOutputHandler;

public class Flag implements BitAccess, LineOutputHandler {
    private boolean value = false;

    public static Flag value(boolean value) {
        Flag flag = new Flag();
        flag.set(value);
        return flag;
    }

    public synchronized boolean isClear() {
        return !value;
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
    public void handle(BitOutput state) {
        set(state.isSet());
    }
}
