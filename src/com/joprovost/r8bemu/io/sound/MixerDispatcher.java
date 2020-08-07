package com.joprovost.r8bemu.io.sound;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class MixerDispatcher implements Mixer {
    private final List<Mixer> targets = new ArrayList<>();
    private final Executor context;

    public MixerDispatcher(Executor context) {
        this.context = context;
    }

    public void dispatchTo(Mixer target) {
        targets.add(target);
    }

    @Override
    public void volume(int volume) {
        context.execute(() -> {
            for (var target : targets) target.volume(volume);
        });
    }
}
