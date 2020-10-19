package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.data.binary.BinaryOutput;
import com.joprovost.r8bemu.data.binary.BinaryRegister;
import com.joprovost.r8bemu.data.discrete.DiscreteAccess;
import com.joprovost.r8bemu.data.discrete.DiscreteLine;
import com.joprovost.r8bemu.data.discrete.DiscreteLineOutput;
import com.joprovost.r8bemu.data.transform.BinaryAccessSubset;
import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.storage.Disk;
import com.joprovost.r8bemu.storage.DiskSlot;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.stream.IntStream;

import static com.joprovost.r8bemu.data.binary.BinaryOutput.subset;

// Based on FD 179x-02 Family documentation
public class DiskDrive implements Addressable, ClockAware, DiskSlot {

    public static final int IN = 1;
    public static final int OUT = -1;
    public static final int LATCH = 0xff40;
    public static final int COMMAND = 0xff48;
    public static final int STATUS = 0xff48;
    public static final int TRACK = 0xff49;
    public static final int SECTOR = 0xff4a;
    public static final int DATA = 0xff4b;

    private final BinaryRegister latch = BinaryRegister.ofMask(0xff);
    private final DiscreteAccess haltMode = BinaryAccessSubset.bit(latch, 7);
    private final DiscreteAccess secondSide = BinaryAccessSubset.bit(latch, 6); // or drive 4
    private final DiscreteAccess motorOn = BinaryAccessSubset.bit(latch, 3);
    private final DiscreteAccess drive3 = BinaryAccessSubset.bit(latch, 2);
    private final DiscreteAccess drive2 = BinaryAccessSubset.bit(latch, 1);
    private final DiscreteAccess drive1 = BinaryAccessSubset.bit(latch, 0);

    private final BinaryRegister status = BinaryRegister.ofMask(0xff);
    private final DiscreteAccess notReady = BinaryAccessSubset.bit(status, 7);
    private final DiscreteAccess drq = BinaryAccessSubset.bit(status, 1);
    private final DiscreteAccess busy = BinaryAccessSubset.bit(status, 0);

    private final DiscreteLine irq = DiscreteLine.named("INTRQ");

    private final Queue<Integer> readQueue = new ArrayDeque<>();
    private int sector;
    private int data;
    private int command;
    private int track;
    private int direction = 1;
    private Disk disk;

    public DiscreteLineOutput irq() {
        return irq;
    }

    @Override
    public void insert(Disk disk) {
        this.disk = disk;
        notReady.set(disk == null);
    }

    @Override
    public void tick(Clock clock) {
        if (busy.isClear()) return;

        if (!BinaryOutput.bit(command, 7)) { // Type I
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
            case STATUS:
                irq.clear();
                return status.value();
            case TRACK:
                return track;
            case SECTOR:
                return sector;
            case DATA:
                return data();
        }
        return 0;
    }

    @Override
    public void write(int address, int data) {
        switch (address) {
            case LATCH:
                latch.value(data);
                break;
            case COMMAND:
                irq.clear();
                if (subset(data, 0xf0) == 0xd) {
                    forceInterrupt(data);
                } else if (busy.isClear()) {
                    busy.set();
                    command = data;
                }
                break;
            case TRACK:
                track = data;
                break;
            case SECTOR:
                sector = data;
                break;
            case DATA:
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
        boolean immediateInterrupt = BinaryOutput.bit(command, 3);
        // boolean indexPulse = DataOutput.bit(command, 2);
        // boolean readyToNotReady = DataOutput.bit(command, 1);
        // boolean notReadyToReady = DataOutput.bit(command, 0);

        if (immediateInterrupt) irq.set();

        busy.clear();
        this.command = command;
    }

    private void readSector() {
        readQueue.clear();

        if (disk == null) {
            notReady.set();
            busy.clear();
            return;
        }

        var data = disk.sector(secondSide.isSet() ? 1 : 0, track, sector);
        IntStream.range(0, data.size()).forEach(i -> readQueue.add(data.read(i)));

        // TODO: Provide real CRC-32
        //       It actually work ony because Disk Basic skip those two bytes
        readQueue.add(0);
        readQueue.add(0);
        busy.clear();
        drq.set();
    }

    private void direction(int direction) {
        this.direction = direction;
    }

    private void restore() {
        track = 0;
        busy.clear();
    }

    private void seek() {
        if (track == data) {
            busy.clear();
            return;
        }

        direction(track < data ? IN : OUT);
        track += direction;
    }

    private void step(Update update) {
        if (update == Update.UPDATE_TRACK_REGISTER) track += direction;
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
