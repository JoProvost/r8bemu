package com.joprovost.r8bemu.io.sound;

import com.joprovost.r8bemu.clock.EmulatorContext;

public interface Mixer {
    int VOLUME_MUTED = 0;
    int VOLUME_DEFAULT = 30;
    int VOLUME_MAX = 256;

    static MixerDispatcher dispatcher(EmulatorContext context) {
        return new MixerDispatcher(context);
    }

    void volume(int volume);
}
