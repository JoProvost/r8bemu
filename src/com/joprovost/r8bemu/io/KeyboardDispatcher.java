package com.joprovost.r8bemu.io;

import java.util.List;

public class KeyboardDispatcher implements Keyboard {
    Keyboard target;

    public void dispatchTo(Keyboard target) {
        this.target = target;
    }

    @Override
    public void type(List<Key> key) {
        if (target != null) target.type(key);
    }

    @Override
    public void pause(int delay) {
        if (target != null) target.pause(delay);
    }
}
