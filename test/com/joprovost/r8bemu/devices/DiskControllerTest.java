package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.clock.FakeClock;
import com.joprovost.r8bemu.storage.Disk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiskControllerTest {

    FakeClock clock = new FakeClock();
    FakeDiscteteOutputHandler interrupt = new FakeDiscteteOutputHandler();

    DiskDrive drive = new DiskDrive();
    DiskController controller = new DiskController(drive);

    @BeforeEach
    void before() {
        controller.irq().to(interrupt);
        drive.write(DiskDrive.LATCH, 1); // Select drive 1
    }

    @Nested
    class Restore {
        @Test
        void restoreTrackToZero() {
            controller.write(DiskController.COMMAND, 0x00);
            assertEquals(0x01, controller.read(DiskController.STATUS) & 0x01); // busy

            controller.tick(clock);

            assertEquals(0x00, controller.read(DiskController.STATUS) & 0x01);
            assertEquals(0x00, controller.read(DiskController.TRACK));
        }
    }

    @Nested
    class Seek {
        @Test
        void atExpectedTrackSimplyBecomesReady() {
            controller.write(DiskController.TRACK, 20);
            controller.write(DiskController.DATA, 20);
            controller.write(DiskController.COMMAND, 0x10);
            assertEquals(0x01, controller.read(DiskController.STATUS) & 0x01); // busy

            assertTrue(interrupt.isEmpty());

            controller.tick(clock);

            assertEquals(20, controller.read(DiskController.TRACK));
            assertEquals(0x00, controller.read(DiskController.STATUS) & 0x01);
        }

        @Test
        void stepsOutUntilExpectedTrackAndThenBecomesReady() {
            controller.write(DiskController.TRACK, 20);
            controller.write(DiskController.DATA, 18);
            controller.write(DiskController.COMMAND, 0x10);
            assertEquals(0x01, controller.read(DiskController.STATUS) & 0x01); // busy

            controller.tick(clock);
            controller.tick(clock);
            assertEquals(18, controller.read(DiskController.TRACK));
            assertTrue(interrupt.isEmpty());

            controller.tick(clock);

            assertEquals(0x00, controller.read(DiskController.STATUS) & 0x01);
        }
    }

    @Nested
    class StepIn {
        @Test
        void stepsInAndBecomesReady() {
            controller.write(DiskController.COMMAND, 0x40);
            assertEquals(0x01, controller.read(DiskController.STATUS) & 0x01); // busy
            assertTrue(interrupt.isEmpty());

            controller.tick(clock);

            assertEquals(0x00, controller.read(DiskController.STATUS) & 0x01);
        }
    }

    @Nested
    class StepOut {
        @Test
        void stepsOutAndBecomesReady() {
            controller.write(DiskController.COMMAND, 0x60);
            assertEquals(0x01, controller.read(DiskController.STATUS) & 0x01); // busy
            assertTrue(interrupt.isEmpty());

            controller.tick(clock);

            assertEquals(0x00, controller.read(DiskController.STATUS) & 0x01);
        }
    }

    @Nested
    class Read {
        @Test
        void firstSide() {
            var disk = Disk.blank();
            disk.sector(0, 17, 2).write(0, 1, 2, 3, 4, 5);
            disk.sector(0, 17, 2).write(254, 0xfe, 0xff);

            DiskControllerTest.this.drive.slot0().insert(disk);
            controller.write(DiskController.TRACK, 17);
            controller.write(DiskController.SECTOR, 2);
            controller.write(DiskController.COMMAND, 0x80);
            assertEquals(0x01, controller.read(DiskController.STATUS) & 0x01); // busy

            assertTrue(interrupt.isEmpty());

            controller.tick(clock);

            assertEquals(0x00, controller.read(DiskController.STATUS) & 0x01); // ready
            assertEquals(0x02, controller.read(DiskController.STATUS) & 0x02); // drq

            // Read first bytes of sector
            assertEquals(1, controller.read(DiskController.DATA));
            assertEquals(2, controller.read(DiskController.DATA));
            assertEquals(3, controller.read(DiskController.DATA));
            assertEquals(4, controller.read(DiskController.DATA));
            assertEquals(5, controller.read(DiskController.DATA));

            assertEquals(0x00, controller.read(DiskController.STATUS) & 0x01); // ready
            assertEquals(0x02, controller.read(DiskController.STATUS) & 0x02); // drq

            IntStream.range(5, 254).forEach(x -> controller.read(DiskController.DATA));

            // Read last bytes of sector
            assertEquals(0xfe, controller.read(DiskController.DATA));
            assertEquals(0xff, controller.read(DiskController.DATA));
            assertEquals(0x02, controller.read(DiskController.STATUS) & 0x02); // drq

            // Read CRC-32 (but unchecked by DOS)
            assertEquals(0, controller.read(DiskController.DATA));
            assertEquals(0, controller.read(DiskController.DATA));
            assertEquals(0x00, controller.read(DiskController.STATUS) & 0x02); // no drq
            assertFalse(interrupt.isEmpty());
        }

        @Test
        void secondSide() {
            var disk = Disk.blank();
            disk.sector(1, 17, 2).write(0, 1, 2, 3, 4, 5);
            disk.sector(1, 17, 2).write(254, 0xfe, 0xff);

            drive.slot0().insert(disk);

            drive.write(DiskDrive.LATCH, 65);
            controller.write(DiskController.TRACK, 17);
            controller.write(DiskController.SECTOR, 2);
            controller.write(DiskController.COMMAND, 0x80);
            assertEquals(0x01, controller.read(DiskController.STATUS) & 0x01); // busy

            assertTrue(interrupt.isEmpty());

            controller.tick(clock);

            assertEquals(0x00, controller.read(DiskController.STATUS) & 0x01); // ready
            assertEquals(0x02, controller.read(DiskController.STATUS) & 0x02); // drq

            // Read first bytes of sector
            assertEquals(1, controller.read(DiskController.DATA));
            assertEquals(2, controller.read(DiskController.DATA));
            assertEquals(3, controller.read(DiskController.DATA));
            assertEquals(4, controller.read(DiskController.DATA));
            assertEquals(5, controller.read(DiskController.DATA));

            assertEquals(0x00, controller.read(DiskController.STATUS) & 0x01); // ready
            assertEquals(0x02, controller.read(DiskController.STATUS) & 0x02); // drq

            IntStream.range(5, 254).forEach(x -> controller.read(DiskController.DATA));

            // Read last bytes of sector
            assertEquals(0xfe, controller.read(DiskController.DATA));
            assertEquals(0xff, controller.read(DiskController.DATA));
            assertEquals(0x02, controller.read(DiskController.STATUS) & 0x02); // drq

            // Read CRC-32 (but unchecked by DOS)
            assertEquals(0, controller.read(DiskController.DATA));
            assertEquals(0, controller.read(DiskController.DATA));
            assertEquals(0x00, controller.read(DiskController.STATUS) & 0x02); // no drq
            assertFalse(interrupt.isEmpty());
        }
    }

    @Nested
    class Write{
        @Test
        void sector() {
            var disk = Disk.blank();

            drive.slot0().insert(disk);
            controller.write(DiskController.TRACK, 17);
            controller.write(DiskController.SECTOR, 2);
            controller.write(DiskController.COMMAND, 0xa0);
            assertEquals(0x01, controller.read(DiskController.STATUS) & 0x01); // busy

            assertTrue(interrupt.isEmpty());

            controller.tick(clock);

            assertEquals(0x01, controller.read(DiskController.STATUS) & 0x01); // busy
            assertEquals(0x02, controller.read(DiskController.STATUS) & 0x02); // drq

            // Write first bytes of sector
            controller.write(DiskController.DATA, 0);
            controller.write(DiskController.DATA, 1);
            controller.write(DiskController.DATA, 2);
            controller.write(DiskController.DATA, 3);
            controller.write(DiskController.DATA, 4);
            controller.write(DiskController.DATA, 5);

            assertEquals(0x01, controller.read(DiskController.STATUS) & 0x01); // busy
            assertEquals(0x02, controller.read(DiskController.STATUS) & 0x02); // drq

            IntStream.range(6, 255).forEach(x -> controller.write(DiskController.DATA, x));

            // Write last bytes of sector
            controller.write(DiskController.DATA, 0xff);
            controller.tick(clock);
            controller.tick(clock);

            assertEquals(0x00, controller.read(DiskController.STATUS) & 0x01); // ready
            assertEquals(0x00, controller.read(DiskController.STATUS) & 0x02); // no drq
            assertFalse(interrupt.isEmpty());

            assertEquals(0, disk.sector(0, 17, 2).read(0));
            assertEquals(1, disk.sector(0, 17, 2).read(1));
            assertEquals(2, disk.sector(0, 17, 2).read(2));
            assertEquals(3, disk.sector(0, 17, 2).read(3));
            assertEquals(4, disk.sector(0, 17, 2).read(4));
            assertEquals(253, disk.sector(0, 17, 2).read(253));
            assertEquals(254, disk.sector(0, 17, 2).read(254));
            assertEquals(255, disk.sector(0, 17, 2).read(255));
        }
    }
}
