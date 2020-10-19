package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.data.binary.BinaryOutput;
import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.devices.mc6809.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MC6821PortTest {

    private static final int DATA_REGISTER = 0;
    private static final int CONTROL_REGISTER = 1;

    MC6821Port port = new MC6821Port(Signal.IRQ);

    @BeforeEach
    void setup() {
        Signal.reset();
        portAccess();
    }

    @Test
    void allPinsIn() {
        direction(0b00000000);
        port.port().input().value(0x32);
        assertEquals(0x32, port.read(DATA_REGISTER));
    }

    @Test
    void allPinsOut() {
        direction(0b11111111);
        port.write(DATA_REGISTER, 0x95);
        assertEquals(0x95, port.port().output().value());
    }

    @Test
    void halfPinsIn() {
        direction(0b00001111);
        port.port().input().value(0x32);
        assertEquals(0x30, port.read(DATA_REGISTER));
    }

    @Test
    void halfPinsOut() {
        direction(0b11110000);
        port.write(DATA_REGISTER, 0x95);
        assertEquals(0x90, port.port().output().value());
    }

    @Test
    void feedsFromFeeder() {
        direction(0b00000000);
        port.port().from(input -> input.value(0x32));
        assertEquals(0x32, port.read(DATA_REGISTER));
    }

    @Test
    void notifiesConsumer() {
        List<BinaryOutput> consumer = new ArrayList<>();
        direction(0b11111111);
        port.port().to(consumer::add);
        port.write(DATA_REGISTER, 0x95);
        assertEquals(1, consumer.size());
        assertEquals(0x95, consumer.get(0).value());
    }

    @Nested
    class InterruptInput {
        @Test
        void setsIrq1FlagOnTrigger() {
            assertFalse(irq1IsSet());
            port.interrupt().pulse();
            assertTrue(irq1IsSet());
        }

        @Test
        void clearsIrq1FlagOnDataRead() {
            port.interrupt();
            port.read(DATA_REGISTER);
            assertFalse(irq1IsSet());
        }

        @Test
        void setsMcuIrqWhenEnabled() {
            enableIrq1();
            port.interrupt().pulse();
            assertTrue(Signal.IRQ.isSet());
            port.read(DATA_REGISTER);
            assertTrue(Signal.IRQ.isClear());
            assertFalse(irq1IsSet());
        }

        @Test
        void keepsMcuIrqUntouchedWhenDisabled() {
            disableIrq1();
            port.interrupt();
            assertTrue(Signal.IRQ.isClear());

            Signal.IRQ.set();
            port.read(DATA_REGISTER);
            assertTrue(Signal.IRQ.isSet());
        }
    }

    @Nested
    class ControlInput {
        @BeforeEach
        void setup() {
            configureControlAsInput();
        }

        @Test
        void setsIrq2FlagOnTrigger() {
            assertFalse(irq2IsSet());
            port.control().pulse();
            assertTrue(irq2IsSet());
        }

        @Test
        void clearsIrq2FlagOnDataRead() {
            port.control().pulse();
            port.read(DATA_REGISTER);
            assertFalse(irq2IsSet());
        }

        @Test
        void setsMcuIrqWhenEnabled() {
            enableIrq2();
            port.control().pulse();
            assertTrue(Signal.IRQ.isSet());
            port.read(DATA_REGISTER);
            assertTrue(Signal.IRQ.isClear());
            assertFalse(irq2IsSet());
        }

        @Test
        void keepsMcuIrqUntouchedWhenDisabled() {
            disableIrq2();
            port.control().pulse();
            assertTrue(Signal.IRQ.isClear());

            Signal.IRQ.set();
            port.read(DATA_REGISTER);
            assertTrue(Signal.IRQ.isSet());
        }
    }

    @Nested
    class ControlOutput {
        @BeforeEach
        void setup() {
            configureControlAsInput();
        }

        @Test
        void notifiesControlHandlers() {
            List<DiscreteOutput> consumer = new ArrayList<>();
            configureControlAsOutput();
            port.control().to(consumer::add);

            setControl();
            assertEquals(1, consumer.size());
            assertTrue(consumer.get(0).isSet());

            clearControl();
            assertEquals(2, consumer.size());
            assertTrue(consumer.get(1).isClear());
        }
    }

    private boolean irq1IsSet() {
        return (port.read(CONTROL_REGISTER) & 0x80) == 0x80;
    }

    private boolean irq2IsSet() {
        return (port.read(CONTROL_REGISTER) & 0x40) == 0x40;
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

    private void enableIrq1() {
        port.write(CONTROL_REGISTER, port.read(CONTROL_REGISTER) | 0b00000001);
    }

    private void disableIrq1() {
        port.write(CONTROL_REGISTER, port.read(CONTROL_REGISTER) & 0b11111110);
    }

    private void configureControlAsInput() {
        port.write(CONTROL_REGISTER, port.read(CONTROL_REGISTER) & 0b11001111);
    }

    private void enableIrq2() {
        port.write(CONTROL_REGISTER, port.read(CONTROL_REGISTER) | 0b00001000);
    }

    private void disableIrq2() {
        port.write(CONTROL_REGISTER, port.read(CONTROL_REGISTER) & 0b11110111);
    }

    private void configureControlAsOutput() {
        port.write(CONTROL_REGISTER, port.read(CONTROL_REGISTER) | 0b00110000);
    }

    private void setControl() {
        port.write(CONTROL_REGISTER, port.read(CONTROL_REGISTER) | 0b00001000);
    }

    private void clearControl() {
        port.write(CONTROL_REGISTER, port.read(CONTROL_REGISTER) & 0b11110111);
    }
}
