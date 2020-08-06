package com.joprovost.r8bemu.devices.disk;

import com.joprovost.r8bemu.memory.Memory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Disk {
    static Disk blank() {
        return new RamDisk(new Memory(18 * 256 * 35));
    }

    default int sectorsPerTrack() {
        return 18;
    }

    default int sides() {
        return 1;
    }

    default int sectorSize() {
        return 256;
    }

    default int firstSectorId() {
        return 1;
    }

    default int sectorAttributeFlag() {
        return 0;
    }

    default boolean writeProtected() {
        return true;
    }

    default List<Sector> sectors(int side, int track) {
        return IntStream.range(0, sectorsPerTrack())
                        .mapToObj(s -> sector(side, track, s))
                        .collect(Collectors.toList());
    }

    int tracks();

    Sector sector(int side, int track, int sector);

    // http://tlindner.macmess.org/?page_id=86
}
