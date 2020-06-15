package com.joprovost.r8bemu.clock;

import java.util.concurrent.locks.LockSupport;

public class ClockFrequency implements ClockAware, Uptime {
    public static final int SMALLEST_DELAY_NS = 10000;

    private final long offset = System.nanoTime();
    private final int kHz;
    private final Clock clock;

    public ClockFrequency(int kHz, Clock clock) {
        this.kHz = kHz;
        this.clock = clock;
    }

    @Override
    public void tick(Clock clock) {
        long diff = nanoTime() - realNanoTime();
        if (diff > SMALLEST_DELAY_NS) LockSupport.parkNanos(diff);
    }

    @Override
    public long nanoTime() {
        return clock.ticks() * 1000000 / kHz;
    }

    private long realNanoTime() {
        return System.nanoTime() - offset;
    }
}
