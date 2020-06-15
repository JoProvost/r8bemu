package com.joprovost.r8bemu.clock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClockFrequencyTest {

    private final FakeClock clock = new FakeClock();

    @Test
    void durationOfOneTick() {
        ClockFrequency clockFrequency1kHz = new ClockFrequency(1, clock);
        clock.ticks(1);
        assertEquals(1000000, clockFrequency1kHz.nanoTime());

        ClockFrequency clockFrequency500kHz = new ClockFrequency(500, clock);
        clock.ticks(1);
        assertEquals(2000, clockFrequency500kHz.nanoTime());
        clock.ticks(5);
        assertEquals(10000, clockFrequency500kHz.nanoTime());

        ClockFrequency clockFrequency900kHz = new ClockFrequency(900, clock);
        clock.ticks(1);
        assertEquals(1111, clockFrequency900kHz.nanoTime());
        clock.ticks(9);
        assertEquals(10000, clockFrequency900kHz.nanoTime());

        ClockFrequency clockFrequency1800kHz = new ClockFrequency(1800, clock);
        clock.ticks(1);
        assertEquals(555, clockFrequency1800kHz.nanoTime());
        clock.ticks(18);
        assertEquals(10000, clockFrequency1800kHz.nanoTime());
    }
}
