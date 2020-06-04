package com.joprovost.r8bemu.clock;

public interface BusySource {
    void busy(long period);
}
