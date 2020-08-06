package com.joprovost.r8bemu.devices.disk;

import com.joprovost.r8bemu.clock.FakeClock;
import com.joprovost.r8bemu.data.Flag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.joprovost.r8bemu.devices.disk.Drive.Direction.IN;
import static com.joprovost.r8bemu.devices.disk.Drive.Direction.OUT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FD197xTest {

    FakeClock clock = new FakeClock();
    FakeDrive drive = new FakeDrive();
    FakeLineOutputHandler interrupt = new FakeLineOutputHandler();

    FD197x fd179x = new FD197x(drive);

    @BeforeEach
    void before() {
        fd179x.irq().to(interrupt);
    }

    @Nested
    class Restore {
        @Test
        void atTrack00SimplyBecomesReady() {
            drive.track00().set();
            fd179x.write(0xff48, 0x00);
            assertEquals(0x01, fd179x.read(0xff48) & 0x01); // busy

            assertTrue(interrupt.isEmpty());

            fd179x.tick(clock);

            assertEquals(0x00, fd179x.read(0xff48) & 0x01);
        }

        @Test
        void stepsUntilTrack00AndThenBecomesReady() {
            drive.track00().clear();
            fd179x.write(0xff48, 0x00);
            assertEquals(0x01, fd179x.read(0xff48) & 0x01); // busy

            fd179x.tick(clock);
            fd179x.tick(clock);
            fd179x.tick(clock);
            assertEquals(List.of(OUT, OUT, OUT), drive.steps());
            assertTrue(interrupt.isEmpty());

            drive.track00().set();
            fd179x.tick(clock);

            assertEquals(List.of(OUT, OUT, OUT), drive.steps());
            assertEquals(0x00, fd179x.read(0xff48) & 0x01);

            drive.steps().clear();
            interrupt.clear();
            fd179x.tick(clock);

            assertTrue(drive.steps().isEmpty());
            assertTrue(interrupt.isEmpty());
        }

        @Test
        void isTriggeredByReset() {
            drive.track00().clear();
            fd179x.reset().handle(Flag.value(true));
            assertEquals(0x01, fd179x.read(0xff48) & 0x01); // busy

            fd179x.tick(clock);
            fd179x.tick(clock);
            fd179x.tick(clock);
            drive.track00().set();
            fd179x.tick(clock);

            assertEquals(List.of(OUT, OUT, OUT), drive.steps());
            assertEquals(0x00, fd179x.read(0xff48) & 0x01);
        }
    }

    @Nested
    class Seek {
        @Test
        void atExpectedTrackSimplyBecomesReady() {
            fd179x.write(0xff49, 20);
            fd179x.write(0xff4b, 20);
            fd179x.write(0xff48, 0x10);
            assertEquals(0x01, fd179x.read(0xff48) & 0x01); // busy

            assertTrue(interrupt.isEmpty());

            fd179x.tick(clock);

            assertEquals(20, fd179x.read(0xff49));
            assertEquals(0x00, fd179x.read(0xff48) & 0x01);
        }

        @Test
        void stepsOutUntilExpectedTrackAndThenBecomesReady() {
            fd179x.write(0xff49, 20);
            fd179x.write(0xff4b, 18);
            fd179x.write(0xff48, 0x10);
            assertEquals(0x01, fd179x.read(0xff48) & 0x01); // busy

            fd179x.tick(clock);
            fd179x.tick(clock);
            assertEquals(18, fd179x.read(0xff49));
            assertEquals(List.of(OUT, OUT), drive.steps());
            assertTrue(interrupt.isEmpty());

            fd179x.tick(clock);

            assertEquals(List.of(OUT, OUT), drive.steps());
            assertEquals(0x00, fd179x.read(0xff48) & 0x01);
        }
    }

    @Nested
    class StepIn {
        @Test
        void stepsInAndBecomesReady() {
            fd179x.write(0xff48, 0x40);
            assertEquals(0x01, fd179x.read(0xff48) & 0x01); // busy
            assertTrue(interrupt.isEmpty());

            fd179x.tick(clock);

            assertEquals(List.of(IN), drive.steps());
            assertEquals(0x00, fd179x.read(0xff48) & 0x01);
        }
    }

    @Nested
    class StepOut {
        @Test
        void stepsOutAndBecomesReady() {
            fd179x.write(0xff48, 0x60);
            assertEquals(0x01, fd179x.read(0xff48) & 0x01); // busy
            assertTrue(interrupt.isEmpty());

            fd179x.tick(clock);

            assertEquals(List.of(OUT), drive.steps());
            assertEquals(0x00, fd179x.read(0xff48) & 0x01);
        }
    }
}
