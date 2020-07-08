package com.joprovost.r8bemu.data;

public class LogicVariable implements LogicAccess {
    private boolean value = false;

    public static LogicVariable of(boolean value) {
        LogicVariable logic = new LogicVariable();
        logic.set(value);
        return logic;
    }

    public boolean isClear() {
        return !value;
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public void set(boolean value) {
        this.value = value;
    }
}
