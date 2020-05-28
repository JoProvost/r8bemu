package com.joprovost.r8bemu.clock;

import java.io.EOFException;
import java.io.IOException;
import java.io.UncheckedIOException;

public class ClockLoop implements Runnable {
    private final ClockBus clock = new ClockBus();
    private long tick = 0;

    public <T extends ClockAware> T aware(T device) {
        return clock.aware(device);
    }

    public void run() {
        try {
            while (true) clock.tick(tick++);
        } catch (EOFException e) {
            // CTRL+C
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
