package com.joprovost.r8bemu.clock;

public class ClockAwareBusyState implements BusyState, ClockAware {
    private long ticks;
    private long busy;

    public ClockAwareBusyState at(Clock clock) {
        tick(clock);
        return this;
    }

    public boolean isBusy() {
        return ticks < busy;
    }

    @Override
    public void busy(long period) {
        if (busy < ticks) busy = ticks;
        busy += period;
    }

    @Override
    public void tick(Clock clock) {
        this.ticks = clock.ticks();
    }
}
