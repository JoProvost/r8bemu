package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.clock.FakeClock;
import com.joprovost.r8bemu.io.Disk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiskDriveTest {

    FakeClock clock = new FakeClock();
    FakeLineOutputHandler interrupt = new FakeLineOutputHandler();

    DiskDrive drive = new DiskDrive();

    @BeforeEach
    void before() {
        drive.irq().to(interrupt);
    }

    @Nested
    class Restore {
        @Test
        void restoreTrackToZero() {
            drive.write(DiskDrive.COMMAND, 0x00);
            assertEquals(0x01, drive.read(DiskDrive.STATUS) & 0x01); // busy

            drive.tick(clock);

            assertEquals(0x00, drive.read(DiskDrive.STATUS) & 0x01);
            assertEquals(0x00, drive.read(DiskDrive.TRACK));
        }
    }

    @Nested
    class Seek {
        @Test
        void atExpectedTrackSimplyBecomesReady() {
            drive.write(DiskDrive.TRACK, 20);
            drive.write(DiskDrive.DATA, 20);
            drive.write(DiskDrive.COMMAND, 0x10);
            assertEquals(0x01, drive.read(DiskDrive.STATUS) & 0x01); // busy

            assertTrue(interrupt.isEmpty());

            drive.tick(clock);

            assertEquals(20, drive.read(DiskDrive.TRACK));
            assertEquals(0x00, drive.read(DiskDrive.STATUS) & 0x01);
        }

        @Test
        void stepsOutUntilExpectedTrackAndThenBecomesReady() {
            drive.write(DiskDrive.TRACK, 20);
            drive.write(DiskDrive.DATA, 18);
            drive.write(DiskDrive.COMMAND, 0x10);
            assertEquals(0x01, drive.read(DiskDrive.STATUS) & 0x01); // busy

            drive.tick(clock);
            drive.tick(clock);
            assertEquals(18, drive.read(DiskDrive.TRACK));
            assertTrue(interrupt.isEmpty());

            drive.tick(clock);

            assertEquals(0x00, drive.read(DiskDrive.STATUS) & 0x01);
        }
    }

    @Nested
    class StepIn {
        @Test
        void stepsInAndBecomesReady() {
            drive.write(DiskDrive.COMMAND, 0x40);
            assertEquals(0x01, drive.read(DiskDrive.STATUS) & 0x01); // busy
            assertTrue(interrupt.isEmpty());

            drive.tick(clock);

            assertEquals(0x00, drive.read(DiskDrive.STATUS) & 0x01);
        }
    }

    @Nested
    class StepOut {
        @Test
        void stepsOutAndBecomesReady() {
            drive.write(DiskDrive.COMMAND, 0x60);
            assertEquals(0x01, drive.read(DiskDrive.STATUS) & 0x01); // busy
            assertTrue(interrupt.isEmpty());

            drive.tick(clock);

            assertEquals(0x00, drive.read(DiskDrive.STATUS) & 0x01);
        }
    }

    @Nested
    class Read {
        @Test
        void firstSide() {
            var disk = Disk.blank();
            disk.sector(0, 17, 2).write(0, 1, 2, 3, 4, 5);
            disk.sector(0, 17, 2).write(254, 0xfe, 0xff);

            drive.insert(disk);
            drive.write(DiskDrive.TRACK, 17);
            drive.write(DiskDrive.SECTOR, 2);
            drive.write(DiskDrive.COMMAND, 0x80);
            assertEquals(0x01, drive.read(DiskDrive.STATUS) & 0x01); // busy

            assertTrue(interrupt.isEmpty());

            drive.tick(clock);

            assertEquals(0x00, drive.read(DiskDrive.STATUS) & 0x01); // ready
            assertEquals(0x02, drive.read(DiskDrive.STATUS) & 0x02); // drq

            // Read first bytes of sector
            assertEquals(1, drive.read(DiskDrive.DATA));
            assertEquals(2, drive.read(DiskDrive.DATA));
            assertEquals(3, drive.read(DiskDrive.DATA));
            assertEquals(4, drive.read(DiskDrive.DATA));
            assertEquals(5, drive.read(DiskDrive.DATA));

            assertEquals(0x00, drive.read(DiskDrive.STATUS) & 0x01); // ready
            assertEquals(0x02, drive.read(DiskDrive.STATUS) & 0x02); // drq

            IntStream.range(5, 254).forEach(x -> drive.read(DiskDrive.DATA));

            // Read last bytes of sector
            assertEquals(0xfe, drive.read(DiskDrive.DATA));
            assertEquals(0xff, drive.read(DiskDrive.DATA));
            assertEquals(0x02, drive.read(DiskDrive.STATUS) & 0x02); // drq

            // Read CRC-32 (but unchecked by DOS)
            assertEquals(0, drive.read(DiskDrive.DATA));
            assertEquals(0, drive.read(DiskDrive.DATA));
            assertEquals(0x00, drive.read(DiskDrive.STATUS) & 0x02); // no drq
            assertFalse(interrupt.isEmpty());
        }

        @Test
        void secondSide() {
            var disk = Disk.blank();
            disk.sector(1, 17, 2).write(0, 1, 2, 3, 4, 5);
            disk.sector(1, 17, 2).write(254, 0xfe, 0xff);

            drive.insert(disk);

            drive.write(DiskDrive.LATCH, 1 << 6);
            drive.write(DiskDrive.TRACK, 17);
            drive.write(DiskDrive.SECTOR, 2);
            drive.write(DiskDrive.COMMAND, 0x80);
            assertEquals(0x01, drive.read(DiskDrive.STATUS) & 0x01); // busy

            assertTrue(interrupt.isEmpty());

            drive.tick(clock);

            assertEquals(0x00, drive.read(DiskDrive.STATUS) & 0x01); // ready
            assertEquals(0x02, drive.read(DiskDrive.STATUS) & 0x02); // drq

            // Read first bytes of sector
            assertEquals(1, drive.read(DiskDrive.DATA));
            assertEquals(2, drive.read(DiskDrive.DATA));
            assertEquals(3, drive.read(DiskDrive.DATA));
            assertEquals(4, drive.read(DiskDrive.DATA));
            assertEquals(5, drive.read(DiskDrive.DATA));

            assertEquals(0x00, drive.read(DiskDrive.STATUS) & 0x01); // ready
            assertEquals(0x02, drive.read(DiskDrive.STATUS) & 0x02); // drq

            IntStream.range(5, 254).forEach(x -> drive.read(DiskDrive.DATA));

            // Read last bytes of sector
            assertEquals(0xfe, drive.read(DiskDrive.DATA));
            assertEquals(0xff, drive.read(DiskDrive.DATA));
            assertEquals(0x02, drive.read(DiskDrive.STATUS) & 0x02); // drq

            // Read CRC-32 (but unchecked by DOS)
            assertEquals(0, drive.read(DiskDrive.DATA));
            assertEquals(0, drive.read(DiskDrive.DATA));
            assertEquals(0x00, drive.read(DiskDrive.STATUS) & 0x02); // no drq
            assertFalse(interrupt.isEmpty());
        }
    }
}
