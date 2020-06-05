package com.joprovost.r8bemu.clock;

import org.junit.jupiter.api.Assertions;

public class FakeBusyState implements BusyState {
    private int busy = 0;

    @Override
    public boolean isBusy() {
        return false;
    }

    @Override
    public void busy(long period) {
        busy += period;
    }

    public void assertBusyFor(int expected) {
        Assertions.assertEquals(expected, busy);
    }

    public int cycles() {
        return busy;
    }
}
