package com.joprovost.r8bemu.devices.disk;

import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.data.BitAccess;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.data.link.Line;
import com.joprovost.r8bemu.data.link.LineOutput;
import com.joprovost.r8bemu.data.transform.DataAccessSubset;
import com.joprovost.r8bemu.memory.MemoryDevice;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.stream.IntStream;

import static com.joprovost.r8bemu.data.DataOutput.subset;
import static com.joprovost.r8bemu.devices.disk.Drive.Direction.IN;
import static com.joprovost.r8bemu.devices.disk.Drive.Direction.OUT;

// Based on FD 179x-02 Family documentation
public class FD197x implements MemoryDevice, ClockAware {

    private final Variable latch = Variable.ofMask(0xff);
    private final BitAccess haltMode = DataAccessSubset.bit(latch, 7);
    private final BitAccess secondSide = DataAccessSubset.bit(latch, 6); // or drive 4
    private final BitAccess motorOn = DataAccessSubset.bit(latch, 3);
    private final BitAccess drive3 = DataAccessSubset.bit(latch, 2);
    private final BitAccess drive2 = DataAccessSubset.bit(latch, 1);
    private final BitAccess drive1 = DataAccessSubset.bit(latch, 0);

    private final Variable status = Variable.ofMask(0xff);
    private final BitAccess notReady = DataAccessSubset.bit(status, 7);
    private final BitAccess drq = DataAccessSubset.bit(status, 1);
    private final BitAccess busy = DataAccessSubset.bit(status, 0);

    private final Line irq = Line.named("INTRQ");

    private final Queue<Integer> readQueue = new ArrayDeque<>();
    private final Drive drive;
    private int sector;
    private int data;
    private int command;
    private int track;
    private Drive.Direction direction = IN;

    public FD197x(Drive drive) {
        this.drive = drive;
        this.drive.ready().to(x -> notReady.set(x.isClear()));
    }

    public LineOutput irq() {
        return irq;
    }

    @Override
    public void tick(Clock clock) {
        if (busy.isClear()) return;

        if (!DataOutput.bit(command, 7)) { // Type I
            if (subset(command, 0xf0) == 0) restore();
            if (subset(command, 0xf0) == 1) seek();
            if (subset(command, 0xe0) != 0) {
                if (subset(command, 0xe0) == 2) direction(IN);
                if (subset(command, 0xe0) == 3) direction(OUT);
                step(Update.from(command));
            }
        }

        if (subset(command, 0xe0) == 4) readSector();
    }
    @Override
    public int read(int address) {
        switch (address) {
            case 0xff48:
                irq.clear();
                return status.value();
            case 0xff49:
                return track;
            case 0xff4a:
                return sector;
            case 0xff4b:
                return data();
        }
        return 0;
    }

    @Override
    public void write(int address, int data) {
        switch (address) {
            case 0xff40:
                latch.value(data);
                drive.motor(motorOn.isSet());
                drive.side(secondSide.isSet() ? 1 : 0);
                break;
            case 0xff48:
                irq.clear();
                if (subset(data, 0xf0) == 0xd) {
                    forceInterrupt(data);
                } else if (busy.isClear()) {
                    busy.set();
                    command = data;
                }
                break;
            case 0xff49:
                track = data;
                break;
            case 0xff4a:
                sector = data;
                break;
            case 0xff4b:
                this.data = data;
                break;
        }
    }

    private int data() {
        if (readQueue.isEmpty())
            return data;
        else {
            var value = readQueue.remove();
            if (readQueue.isEmpty()) {
                drq.clear();
                busy.clear();
                irq.set();
            }
            return value;
        }
    }

    private void forceInterrupt(int command) {
        boolean immediateInterrupt = DataOutput.bit(command, 3);
        // boolean indexPulse = DataOutput.bit(command, 2);
        // boolean readyToNotReady = DataOutput.bit(command, 1);
        // boolean notReadyToReady = DataOutput.bit(command, 0);

        if (immediateInterrupt) irq.set();

        busy.clear();
        this.command = command;
    }

    private void readSector() {
        readQueue.clear();
        drive.read().forEach(data -> {
            if (data.id() != sector) return;
            IntStream.range(0, data.size()).forEach(i -> readQueue.add(data.read(i)));

            // TODO: Provide real CRC-32
            //       It actually work ony because Disk Basic skip those two bytes
            readQueue.add(0);
            readQueue.add(0);
        });
        busy.clear();
        drq.set();
    }

    private void direction(Drive.Direction direction) {
        this.direction = direction;
        drive.direction(this.direction);
    }

    private void restore() {
        if (drive.track00().isSet()) {
            track = 0;
            busy.clear();
            return;
        }

        direction(OUT);
        drive.step();
    }

    private void seek() {
        if (track == data) {
            busy.clear();
            return;
        }

        direction(track < data ? IN : OUT);
        drive.step();
        track += direction.offset();
    }

    private void step(Update update) {
        if (direction == OUT && drive.track00().isSet()) {
            if (update == Update.UPDATE_TRACK_REGISTER) track = 0;
            busy.clear();
            return;
        }

        drive.step();
        if (update == Update.UPDATE_TRACK_REGISTER) track += direction.offset();
        busy.clear();
    }

    enum Update {
        NO_UPDATE,
        UPDATE_TRACK_REGISTER;

        public static Update from(int command) {
            switch (subset(command, 0b00000100)) {
                case 0: return NO_UPDATE;
                case 1: return UPDATE_TRACK_REGISTER;
            }
            throw new IllegalArgumentException();
        }
    }
}
