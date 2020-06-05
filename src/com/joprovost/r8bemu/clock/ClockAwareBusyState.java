package com.joprovost.r8bemu.clock;

public class ClockAwareBusyState implements BusyState, ClockAware {
    private long tick;
    private long busy;

    public ClockAwareBusyState at(long tick) {
        tick(tick);
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

    @Override
    public void tick(long tick) {
        this.tick = tick;
    }
}
