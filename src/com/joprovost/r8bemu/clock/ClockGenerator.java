package com.joprovost.r8bemu.clock;

import java.io.EOFException;
import java.io.IOException;
import java.io.UncheckedIOException;

public class ClockGenerator implements Runnable, BusyState, Clock {
    private final ClockBus clockBus = new ClockBus();
    private long ticks = 0;
    private long busy = 0;

    public <T extends ClockAware> T aware(T device) {
        return clockBus.aware(device);
    }

    public void run() {
        try {
            while (true) {
                clockBus.tick(this);
                ticks += (busy > 0) ? busy : 1;
                busy = 0;
            }
        } catch (EOFException e) {
            // CTRL+C
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean isBusy() {
        return busy > 0;
    }

    @Override
    public void busy(long period) {
        busy += period;
    }

    @Override
    public long ticks() {
        return ticks;
    }
}
