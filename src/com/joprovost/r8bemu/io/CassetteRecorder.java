package com.joprovost.r8bemu.io;

import java.io.File;

public interface CassetteRecorder {
    static CassetteRecorderDispatcher dispatcher() {
        return new CassetteRecorderDispatcher();
    }

    void insert(File cassette);

    void rewind();
}
