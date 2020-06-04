package com.joprovost.r8bemu.clock;

public class ClockState implements BusySource {
    private long tick;
    private long busy;

    public ClockState at(long tick) {
        this.tick = tick;
        return this;
    }

    public boolean isBusy() {
        return tick < busy;
    }

    @Override
    public void busy(long period) {
        if (busy < tick) busy = tick;
        busy += period;
    }
}
