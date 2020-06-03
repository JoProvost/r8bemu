package com.joprovost.r8bemu.clock;

public class ClockState {
    private long tick;
    private long busy;

    public ClockState at(long tick) {
        this.tick = tick;
        return this;
    }

    public boolean isBusy() {
        return tick < busy;
    }

    public void busy(long period) {
        busy = tick + period;
    }
}
