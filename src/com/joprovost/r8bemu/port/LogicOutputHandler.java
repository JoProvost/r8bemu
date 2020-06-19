package com.joprovost.r8bemu.port;

import com.joprovost.r8bemu.data.LogicOutput;

public interface LogicOutputHandler {
    void handle(LogicOutput state);
}
