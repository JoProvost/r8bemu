package com.joprovost.r8bemu.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class CassetteRecorderDispatcher implements CassetteRecorder {

    private final List<CassetteRecorder> targets = new ArrayList<>();
    private final Executor context;

    public CassetteRecorderDispatcher(Executor context) {
        this.context = context;
    }

    public void dispatchTo(CassetteRecorder target) {
        targets.add(target);
    }

    @Override
    public void insert(File cassette) {
        context.execute(() -> {
            for (var target : targets) target.insert(cassette);
        });
    }

    @Override
    public void rewind() {
        context.execute(() -> {
            for (var target : targets) target.rewind();
        });
    }
}
