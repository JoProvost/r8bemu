package com.joprovost.r8bemu.io;

import java.util.ArrayList;
import java.util.List;

public class ButtonDispatcher implements Button {

    private final List<Button> targets = new ArrayList<>();

    public void dispatchTo(Button target) {
        this.targets.add(target);
    }

    @Override
    public void press() {
        for (var target : targets) target.press();
    }

    @Override
    public void release() {
        for (var target : targets) target.release();
    }
}
