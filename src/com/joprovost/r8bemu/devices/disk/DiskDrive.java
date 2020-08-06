package com.joprovost.r8bemu.devices.disk;

import com.joprovost.r8bemu.data.link.Line;
import com.joprovost.r8bemu.data.link.LineOutput;

import java.util.List;

public class DiskDrive implements Drive {
    private final Line track00 = Line.named("TRACK00");
    private final Line ready = Line.named("READY");

    private final Disk disk;

    private boolean motor;
    private int track = 0;
    private int side;
    private Direction direction;

    public DiskDrive(Disk disk) {
        this.disk = disk;
    }

    @Override
    public LineOutput track00() {
        return track00;
    }

    @Override
    public LineOutput ready() {
        return ready;
    }

    @Override
    public void motor(boolean on) {
        motor = on;
    }

    @Override
    public void side(int side) {
        this.side = side;
    }

    @Override
    public void direction(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void step() {
        if (direction == Direction.IN) track++;
        else track--;
        if (track < 0) track = 0;
        track00.set(track == 0);
    }

    @Override
    public List<Sector> read() {
        if (!motor) return List.of();
        return disk.sectors(side, track);
    }

    @Override
    public void write(Sector sector) {

    }
}
