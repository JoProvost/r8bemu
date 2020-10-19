package com.joprovost.r8bemu.io;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class JoystickDispatcher implements JoystickInput {
    private final List<JoystickInput> targets = new ArrayList<>();
    private final Executor context;

    public JoystickDispatcher(Executor context) {
        this.context = context;
    }

    public void dispatchTo(JoystickInput target) {
        this.targets.add(target);
    }

    @Override
    public void horizontal(double value) {
        context.execute(() -> {
            for (var target : targets) target.horizontal(value);
        });
    }

    @Override
    public void vertical(double value) {
        context.execute(() -> {
            for (var target : targets) target.vertical(value);
        });
    }

    @Override
    public void press() {
        context.execute(() -> {
            for (var target : targets) target.press();
        });
    }

    @Override
    public void release() {
        context.execute(() -> {
            for (var target : targets) target.release();
        });
    }
}
