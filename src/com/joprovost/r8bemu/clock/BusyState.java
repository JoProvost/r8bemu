package com.joprovost.r8bemu.clock;

public interface BusyState {
    boolean isBusy();
    void busy(long period);
}
