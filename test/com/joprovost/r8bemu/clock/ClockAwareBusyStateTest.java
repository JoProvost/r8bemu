package com.joprovost.r8bemu.clock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClockAwareBusyStateTest {
    ClockAwareBusyState clockAwareBusyState = new ClockAwareBusyState();

    @Test
    void isNotBusyByDefault() {
        assertFalse(clockAwareBusyState.isBusy());
    }

    @Test
    void isBusyUntilTickRefreshes() {
        clockAwareBusyState.busy(10);
        assertTrue(clockAwareBusyState.isBusy());
        assertTrue(clockAwareBusyState.at(9).isBusy());
        assertFalse(clockAwareBusyState.at(10).isBusy());
        assertFalse(clockAwareBusyState.at(11).isBusy());
    }

    @Test
    void worksInRelativeTimings() {
        clockAwareBusyState.at(1000);
        clockAwareBusyState.busy(10);
        assertTrue(clockAwareBusyState.isBusy());
        assertTrue(clockAwareBusyState.at(1009).isBusy());
        assertFalse(clockAwareBusyState.at(1010).isBusy());
        assertFalse(clockAwareBusyState.at(1011).isBusy());
    }
}
