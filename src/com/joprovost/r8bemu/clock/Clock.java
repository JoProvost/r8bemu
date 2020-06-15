package com.joprovost.r8bemu.clock;

public interface Clock {
    long ticks();
    static Clock fixed(long cycles) { return () -> cycles; }
    static Clock zero() { return fixed(0); }
}
