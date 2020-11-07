package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.data.binary.BinaryRegister;
import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.data.transform.BinaryAccessSubset;
import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.storage.Disk;
import com.joprovost.r8bemu.storage.DiskSlot;
import com.joprovost.r8bemu.storage.DiskTracks;
import com.joprovost.r8bemu.storage.Sector;

import java.util.List;

import static com.joprovost.r8bemu.data.discrete.DiscreteOutput.and;
import static com.joprovost.r8bemu.data.discrete.DiscreteOutput.or;

public class DiskDrive implements Addressable, DiskTracks {

    public static final int LATCH = 0xff40;

    private final BinaryRegister latch = BinaryRegister.ofMask(0xff);
    private final DiscreteOutput haltMode = BinaryAccessSubset.bit(latch, 7);
    private final DiscreteOutput drive3 = BinaryAccessSubset.bit(latch, 6);
    private final DiscreteOutput motorOn = BinaryAccessSubset.bit(latch, 3);
    private final DiscreteOutput drive2 = BinaryAccessSubset.bit(latch, 2);
    private final DiscreteOutput drive1 = BinaryAccessSubset.bit(latch, 1);
    private final DiscreteOutput drive0 = BinaryAccessSubset.bit(latch, 0);
    private final DiscreteOutput secondSide = and(drive3, or(drive0, or(drive1, drive2)));

    private Disk disk0 = Disk.blank();
    private Disk disk1 = Disk.blank();
    private Disk disk2 = Disk.blank();
    private Disk disk3 = Disk.blank();

    public DiskSlot slot0() {
        return disk -> disk0 = disk;
    }

    public DiskSlot slot1() {
        return disk -> disk1 = disk;
    }

    public DiskSlot slot2() {
        return disk -> disk2 = disk;
    }

    public DiskSlot slot3() {
        return disk -> disk3 = disk;
    }

    @Override
    public int read(int address) {
        if (address == LATCH) {
            return latch.value();
        }
        return 0;
    }

    @Override
    public void write(int address, int data) {
        if (address == LATCH) {
            latch.value(data);
        }
    }

    Disk disk() {
        if (drive0.isSet()) return disk0;
        if (drive1.isSet()) return disk1;
        if (drive2.isSet()) return disk2;
        if (drive3.isSet()) return disk3;
        return null;
    }

    @Override
    public List<Sector> sectors(int track) {
        return disk().sectors(secondSide.isSet() ? 1 : 0, track);
    }

    @Override
    public Sector sector(int track, int sector) {
        return disk().sector(secondSide.isSet() ? 1 : 0, track, sector);
    }
}
