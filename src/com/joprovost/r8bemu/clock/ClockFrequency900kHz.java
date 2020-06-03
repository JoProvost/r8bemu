package com.joprovost.r8bemu.clock;

import java.util.concurrent.locks.LockSupport;

public class ClockFrequency900kHz implements ClockAware {
    private final ClockState clock = new ClockState();

    @Override
    public void tick(long tick) {
        if (clock.at(tick).isBusy()) return;

        // As the smallest amount of time Java allows us to wait without burning our CPU is
        // 10 us (10 000 ns), let's wait 10 microseconds every 9 tics ==> 0.9 MHz
        // MPU clock of an NRTSC CoCo2 is at .89 MHz
        clock.busy(9);
        LockSupport.parkNanos(10000); // 10 micro seconds (smallest unit)
    }
}
