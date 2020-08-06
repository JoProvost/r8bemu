package com.joprovost.r8bemu.devices.disk;

import com.joprovost.r8bemu.data.link.LineOutput;
import com.joprovost.r8bemu.io.DiskSlot;

import java.util.List;

public interface Drive extends DiskSlot {
    // https://en.wikipedia.org/wiki/Floppy-disk_controller
    // http://retrotechnology.com/herbs_stuff/drive.html

    LineOutput track00();

    LineOutput ready(); // !diskChange

    void motor(boolean on);

    void side(int side);

    void direction(Direction direction); // imply drive select

    void step();

    List<Sector> read(); // imply drive select

    void write(Sector sector); // imply drive select

    enum Direction {
        IN(1), OUT(-1);

        private final int offset;

        Direction(int offset) {
            this.offset = offset;
        }

        public int offset() {
            return offset;
        }
    }
}
