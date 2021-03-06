package com.joprovost.r8bemu.clock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClockBus implements ClockAware {
    private final List<ClockAware> devices = new ArrayList<>();

    public <T extends ClockAware> T aware(T device) {
        devices.add(device);
        return device;
    }

    public void tick(Clock clock) throws IOException {
        for (var device : devices) device.tick(clock);
    }
}
