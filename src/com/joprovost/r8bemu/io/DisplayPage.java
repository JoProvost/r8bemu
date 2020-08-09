package com.joprovost.r8bemu.io;

import java.util.concurrent.Executor;

public interface DisplayPage {
    static DisplayPageDispatcher dispatcher(Executor context) {
        return new DisplayPageDispatcher(context);
    }

    void previous();
    void next();
}
