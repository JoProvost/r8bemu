package com.joprovost.r8bemu;

import java.util.ArrayList;
import java.util.List;

public class Threads implements Services {
    private final List<Thread> threads = new ArrayList<>();

    @Override
    public <T extends Runnable> T declare(T task) {
        threads.add(new Thread(task));
        return task;
    }

    @Override
    public void start() {
        for (Thread task : threads) task.start();
    }

    @Override
    public void stop() {
        for (Thread task : threads) task.interrupt();
    }
}
