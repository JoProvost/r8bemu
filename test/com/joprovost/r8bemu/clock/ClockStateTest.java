package com.joprovost.r8bemu.clock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClockStateTest {
    ClockState clockState = new ClockState();

    @Test
    void isNotBusyByDefault() {
        assertFalse(clockState.isBusy());
    }

    @Test
    void isBusyUntilTickRefreshes() {
        clockState.busy(10);
        assertTrue(clockState.isBusy());
        assertTrue(clockState.at(9).isBusy());
        assertFalse(clockState.at(10).isBusy());
        assertFalse(clockState.at(11).isBusy());
    }

    @Test
    void worksInRelativeTimings() {
        clockState.at(1000);
        clockState.busy(10);
        assertTrue(clockState.isBusy());
        assertTrue(clockState.at(1009).isBusy());
        assertFalse(clockState.at(1010).isBusy());
        assertFalse(clockState.at(1011).isBusy());
    }
}
