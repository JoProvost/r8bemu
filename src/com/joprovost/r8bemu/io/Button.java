package com.joprovost.r8bemu.io;

public interface Button {

    static ButtonDispatcher dispatcher() {
        return new ButtonDispatcher();
    }

    void press();
    void release();
}
