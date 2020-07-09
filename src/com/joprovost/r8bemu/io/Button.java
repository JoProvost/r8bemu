package com.joprovost.r8bemu.io;

public interface Button {

    static ButtonDispatcher dispatcher() {
        return new ButtonDispatcher();
    }

    default void press() {

    }

    default void release() {

    }
}
