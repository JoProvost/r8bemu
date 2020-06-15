package com.joprovost.r8bemu.clock;

public class FakeClock implements Clock {
    private long ticks = 0;

    @Override
    public long ticks() {
        return ticks;
    }

    public void ticks(long ticks) {
        this.ticks = ticks;
    }
}
