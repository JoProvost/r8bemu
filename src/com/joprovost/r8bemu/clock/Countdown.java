package com.joprovost.r8bemu.clock;

public class Countdown implements BusyState {
    private long ticks;
    private long busy;

    public boolean isBusy() {
        return ++ticks < busy;
    }

    @Override
    public void busy(long period) {
        if (busy < ticks) busy = ticks;
        busy += period;
    }
}
