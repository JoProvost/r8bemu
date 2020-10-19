package com.joprovost.r8bemu.io.sound;

import com.joprovost.r8bemu.clock.EmulatorContext;

public interface Mixer {
    double VOLUME_MUTED = 0;
    double VOLUME_DEFAULT = .15;
    double VOLUME_MAX = 1;

    static MixerDispatcher dispatcher(EmulatorContext context) {
        return new MixerDispatcher(context);
    }

    void volume(double volume);
}
