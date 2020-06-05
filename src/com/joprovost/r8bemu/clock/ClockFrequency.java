package com.joprovost.r8bemu.clock;

import java.util.concurrent.locks.LockSupport;

public class ClockFrequency implements ClockAware {
    public static final int SMALLEST_DELAY_NS = 10000;

    private final long offset = System.nanoTime();
    private final int kHz;

    public ClockFrequency(int kHz) {
        this.kHz = kHz;
    }

    @Override
    public void tick(long tick) {
        long diff = uptime(tick) - realUptime();
        if (diff > SMALLEST_DELAY_NS) LockSupport.parkNanos(diff);
    }

    public long uptime(long tick) {
        return tick * 1000000 / kHz;
    }

    private long realUptime() {
        return System.nanoTime() - offset;
    }
}
