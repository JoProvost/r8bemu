package com.joprovost.r8bemu.data.link;

import com.joprovost.r8bemu.io.Button;

public class PushButton implements Button {
    boolean pressed = false;

    @Override
    public void press() {
        pressed = true;
    }

    @Override
    public void release() {
        pressed = false;
    }

    public ParallelInputProvider clear(int mask) {
        return input -> {
            if (pressed) input.clear(mask);
        };
    }
}
