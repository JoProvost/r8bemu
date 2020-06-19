package com.joprovost.r8bemu.data;

public interface LogicOutput extends Described {
    default boolean isSet() {
        return !isClear();
    }

    boolean isClear();
}
