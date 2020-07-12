package com.joprovost.r8bemu.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CassetteRecorderDispatcher implements CassetteRecorder {

    private final List<CassetteRecorder> targets = new ArrayList<>();

    public void dispatchTo(CassetteRecorder target) {
        targets.add(target);
    }

    @Override
    public void insert(File cassette) {
        for (var target : targets) target.insert(cassette);
    }

    @Override
    public void rewind() {
        for (var target : targets) target.rewind();
    }
}
