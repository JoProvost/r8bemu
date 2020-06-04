package com.joprovost.r8bemu.clock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClockFrequencyTest {

    @Test
    void durationOfOneTick() {
        ClockFrequency clockFrequency1kHz = new ClockFrequency(1);
        assertEquals(1000000, clockFrequency1kHz.uptime(1));

        ClockFrequency clockFrequency500kHz = new ClockFrequency(500);
        assertEquals(2000, clockFrequency500kHz.uptime(1));
        assertEquals(10000, clockFrequency500kHz.uptime(5));

        ClockFrequency clockFrequency900kHz = new ClockFrequency(900);
        assertEquals(1111, clockFrequency900kHz.uptime(1));
        assertEquals(10000, clockFrequency900kHz.uptime(9));

        ClockFrequency clockFrequency1800kHz = new ClockFrequency(1800);
        assertEquals(555, clockFrequency1800kHz.uptime(1));
        assertEquals(10000, clockFrequency1800kHz.uptime(18));
    }
}
