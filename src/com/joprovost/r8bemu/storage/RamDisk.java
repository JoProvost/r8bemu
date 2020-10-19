package com.joprovost.r8bemu.storage;

import com.joprovost.r8bemu.devices.memory.Memory;

// Reference : http://tlindner.macmess.org/?page_id=86
public class RamDisk implements Disk {

    private final Memory content;

    public RamDisk(Memory content) {
        this.content = content;
    }

    @Override
    public int sides() {
        return content.size() > 18 * 256 * 40 ? 2 : 1;
    }

    @Override
    public int tracks() {
        if (sectorAttributeFlag() == 0)
            return content.size() / (sectorsPerTrack() * sectorSize()) / sides();
        else
            return content.size() / (sectorsPerTrack() * (sectorSize() + 1)) / sides();
    }

    @Override
    public Sector sector(int side, int track, int sector) {
        int sectorIndex = sector - firstSectorId();
        var index = track * sectorsPerTrack() * sides() + side * sectorsPerTrack() + sectorIndex;
        var start = (sectorAttributeFlag() == 0) ? index * sectorSize() : index * (sectorSize() + 1);
        var offset = (sectorAttributeFlag() == 0) ? start : start + 1;
        return new Sector() {
            @Override
            public int id() {
                return sector;
            }

            @Override
            public int attribute() {
                if (sectorAttributeFlag() == 0) return 0;
                return content.read(start);
            }

            @Override
            public int size() {
                return sectorSize();
            }

            @Override
            public int read(int address) {
                return content.read(address + offset);
            }

            @Override
            public void write(int address, int data) {
                content.write(address + offset, data);
            }
        };
    }
}
