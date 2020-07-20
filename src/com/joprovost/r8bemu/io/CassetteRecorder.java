package com.joprovost.r8bemu.io;

import java.io.File;
import java.util.concurrent.Executor;

public interface CassetteRecorder {
    static CassetteRecorderDispatcher dispatcher(Executor context) {
        return new CassetteRecorderDispatcher(context);
    }

    void insert(File cassette);

    void rewind();
}
