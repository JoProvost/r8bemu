package com.joprovost.r8bemu.data;

public interface BitOutput extends Described {
    default boolean isSet() {
        return !isClear();
    }

    boolean isClear();
}
