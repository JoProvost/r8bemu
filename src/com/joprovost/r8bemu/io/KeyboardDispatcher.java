package com.joprovost.r8bemu.io;

import java.util.ArrayList;
import java.util.List;

public class KeyboardDispatcher implements Keyboard {
    private final List<Keyboard> targets = new ArrayList<>();

    public void dispatchTo(Keyboard target) {
        this.targets.add(target);
    }

    @Override
    public void type(List<Key> key) {
        for (var target : targets) target.type(key);
    }

    @Override
    public void pause(int delay) {
        for (var target : targets) target.pause(delay);
    }
}
