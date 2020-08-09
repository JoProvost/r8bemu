package com.joprovost.r8bemu.io;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class DisplayPageDispatcher implements DisplayPage {
    private final List<DisplayPage> targets = new ArrayList<>();
    private final Executor context;

    public DisplayPageDispatcher(Executor context) {
        this.context = context;
    }

    public void dispatchTo(DisplayPage target) {
        this.targets.add(target);
    }

    @Override
    public void previous() {
        context.execute(() -> {
            for (var target : targets) target.previous();
        });
    }

    @Override
    public void next() {
        context.execute(() -> {
            for (var target : targets) target.next();
        });
    }
}
