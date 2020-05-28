package com.joprovost.r8bemu.clock;

import java.io.IOException;

public interface ClockAware {
    void tick(long tick) throws IOException;
}
