package com.joprovost.r8bemu.clock;

import java.io.EOFException;
import java.io.IOException;
import java.io.UncheckedIOException;

public class Clock implements Runnable, BusyState {
    private final ClockBus clock = new ClockBus();
    private long tick = 0;
    private long busy = 0;

    public <T extends ClockAware> T aware(T device) {
        return clock.aware(device);
    }

    public void run() {
        try {
            while (true) {
                clock.tick(tick);
                tick += (busy > 0) ? busy : 1;
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
}
