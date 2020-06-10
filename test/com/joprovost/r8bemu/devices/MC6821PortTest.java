package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.mc6809.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MC6821PortTest {

    private static int DATA_REGISTER = 0;
    private static int CONTROL_REGISTER = 1;

    MC6821Port port = new MC6821Port(Signal.IRQ);

    @BeforeEach
    void setup() {
        Signal.reset();
        portAccess();
    }

    @Test
    void allPinsIn() {
        direction(0b00000000);
        port.in().set(0x32);
        assertEquals(0x32, port.read(DATA_REGISTER));
    }

    @Test
    void allPinsOut() {
        direction(0b11111111);
        port.write(DATA_REGISTER, 0x95);
        assertEquals(0x95, port.out().unsigned());
    }

    @Test
    void halfPinsIn() {
        direction(0b00001111);
        port.in().set(0x32);
        assertEquals(0x30, port.read(DATA_REGISTER));
    }

    @Test
    void halfPinsOut() {
        direction(0b11110000);
        port.write(DATA_REGISTER, 0x95);
        assertEquals(0x90, port.out().unsigned());
    }

    @Test
    void setsIrq1FlagOnTrigger() {
        assertFalse(irq1IsSet());
        port.control();
        assertTrue(irq1IsSet());
    }

    @Test
    void clearsIrq1FlagOnDataRead() {
        port.control();
        port.read(DATA_REGISTER);
        assertFalse(irq1IsSet());
    }

    @Test
    void setsMcuIrqWhenEnabled() {
        enableIrq();
        port.control();
        assertTrue(Signal.IRQ.isSet());
        port.read(DATA_REGISTER);
        assertTrue(Signal.IRQ.isClear());
        assertFalse(irq1IsSet());
    }

    @Test
    void keepsMcuIrqUntouchedWhenDisabled() {
        disableIrq();
        port.control();
        assertTrue(Signal.IRQ.isClear());

        Signal.IRQ.set();
        port.read(DATA_REGISTER);
        assertTrue(Signal.IRQ.isSet());
    }

    private boolean irq1IsSet() {
        return (port.read(CONTROL_REGISTER) & 0x80) == 0x80;
    }

    private void direction(int direction) {
        portDirection();
        port.write(DATA_REGISTER, direction);
        portAccess();
    }

    private void portDirection() {
        port.write(CONTROL_REGISTER, port.read(CONTROL_REGISTER) & 0b11111011);
    }

    private void portAccess() {
        port.write(CONTROL_REGISTER, port.read(CONTROL_REGISTER) | 0b00000100);
    }

    private void enableIrq() {
        port.write(CONTROL_REGISTER, port.read(CONTROL_REGISTER) | 0b00000001);
    }

    private void disableIrq() {
        port.write(CONTROL_REGISTER, port.read(CONTROL_REGISTER) & 0b11111110);
    }
}
