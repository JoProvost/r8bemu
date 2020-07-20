package com.joprovost.r8bemu.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

public class KeyboardDispatcher implements Keyboard {
    private final List<Keyboard> targets = new ArrayList<>();
    private final Executor context;

    public KeyboardDispatcher(Executor context) {
        this.context = context;
    }

    public void dispatchTo(Keyboard target) {
        this.targets.add(target);
    }

    @Override
    public void type(Set<Key> keys) {
        context.execute(() -> {
            for (var target : targets) target.type(keys);
        });
    }

    @Override
    public void press(Set<Key> keys) {
        context.execute(() -> {
            for (var target : targets) target.press(keys);
        });
    }

    @Override
    public void release(Set<Key> keys) {
        context.execute(() -> {
            for (var target : targets) target.release(keys);
        });
    }

    @Override
    public void pause(int delay) {
        context.execute(() -> {
            for (var target : targets) target.pause(delay);
        });
    }
}
