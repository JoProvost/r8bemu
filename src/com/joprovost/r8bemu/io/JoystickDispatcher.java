package com.joprovost.r8bemu.io;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class JoystickDispatcher implements Joystick {
    private final List<Joystick> targets = new ArrayList<>();
    private final Executor context;

    public JoystickDispatcher(Executor context) {
        this.context = context;
    }

    public void dispatchTo(Joystick target) {
        this.targets.add(target);
    }

    @Override
    public void horizontal(int value) {
        context.execute(() -> {
            for (var target : targets) target.horizontal(value);
        });
    }

    @Override
    public void vertical(int value) {
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
