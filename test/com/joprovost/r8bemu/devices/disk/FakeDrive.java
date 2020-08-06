package com.joprovost.r8bemu.devices.disk;

import com.joprovost.r8bemu.data.link.Line;
import com.joprovost.r8bemu.io.Disk;

import java.util.ArrayList;
import java.util.List;

class FakeDrive implements Drive {
    private final Line ready = Line.named("ready");
    private final Line track00 = Line.named("track00");
    private final List<Direction> steps = new ArrayList<>();
    private final List<Sector> writes = new ArrayList<>();
    private Direction direction;

    @Override
    public Line track00() {
        return track00;
    }

    @Override
    public Line ready() {
        return ready;
    }

    @Override
    public void motor(boolean on) {
    }

    @Override
    public void side(int side) {
    }

    @Override
    public void direction(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void step() {
        this.steps.add(direction);
    }

    @Override
    public List<Sector> read() {
        return List.of();
    }

    @Override
    public void write(Sector sector) {
        writes.add(sector);
    }

    @Override
    public void insert(Disk disk) {

    }

    public List<Direction> steps() {
        return steps;
    }
}
