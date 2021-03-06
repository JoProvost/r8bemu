package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.data.discrete.DiscreteLine;

public class VideoTimer implements ClockAware {
    public static final int LINES = 250;

    private final DiscreteLine horizontalSync = DiscreteLine.named("Horizontal Sync");
    private final DiscreteLine verticalSync = DiscreteLine.named("Vertical Sync");
    private int line;

    @Override
    public void tick(Clock clock) {
        line = (line + 1) % LINES;
        if (line == 0) verticalSync.pulse();
        horizontalSync.pulse();
    }

    public DiscreteLine horizontalSync() {
        return horizontalSync;
    }

    public DiscreteLine verticalSync() {
        return verticalSync;
    }
}
