package com.joprovost.r8bemu.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KeyboardDispatcher implements Keyboard {
    private final List<Keyboard> targets = new ArrayList<>();

    public void dispatchTo(Keyboard target) {
        this.targets.add(target);
    }

    @Override
    public void type(Set<Key> keys) {
        for (var target : targets) target.type(keys);
    }

    @Override
    public void press(Set<Key> keys) {
        for (var target : targets) target.press(keys);
    }

    @Override
    public void release(Set<Key> keys) {
        for (var target : targets) target.release(keys);
    }

    @Override
    public void pause(int delay) {
        for (var target : targets) target.pause(delay);
    }
}
