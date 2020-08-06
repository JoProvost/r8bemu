package com.joprovost.r8bemu.clock;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class EmulatorContext implements Runnable, BusyState, Clock, Executor {
    private final ClockBus clockBus = new ClockBus();
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final List<Runnable> buffer = new ArrayList<>();
    private final List<Consumer<Exception>> errorHandlers = new ArrayList<>();
    private long ticks = 0;
    private long busy = 0;

    public <T extends ClockAware> T aware(T device) {
        return clockBus.aware(device);
    }

    public void run() {
        while (true) {
            try {
                clockBus.tick(this);
                ticks += (busy > 0) ? busy : 1;
                busy = 0;

                queue.drainTo(buffer);
                buffer.forEach(Runnable::run);
                buffer.clear();
            } catch (EOFException e) {
                // CTRL+C
                return;
            } catch (Exception e) {
                for (var handler : errorHandlers) handler.accept(e);
            }
        }
    }

    @Override
    public boolean isBusy() {
        return busy > 0;
    }

    @Override
    public void busy(long period) {
        busy += period;
    }

    @Override
    public long ticks() {
        return ticks;
    }

    @Override
    public void execute(Runnable command) {
        queue.add(command);
    }

    public void onError(Consumer<Exception> handler) {
        errorHandlers.add(handler);
    }
}
