package com.joprovost.r8bemu;

public interface Services {
    <T extends Runnable> T declare(T task);

    void start();

    void stop();
}
