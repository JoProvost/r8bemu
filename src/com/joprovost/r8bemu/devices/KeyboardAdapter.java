package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.clock.ClockAwareBusyState;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.transform.DataAccessSubset;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.io.Key;
import com.joprovost.r8bemu.io.Keyboard;
import com.joprovost.r8bemu.data.link.ParallelInput;
import com.joprovost.r8bemu.data.link.ParallelOutput;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class KeyboardAdapter implements Keyboard, ClockAware {

    public static final int TYPE_DELAY = 40000;
    public static final int BOOT_DELAY = 1200000;

    private final ClockAwareBusyState state = new ClockAwareBusyState();
    private final Deque<Set<Key>> buffer = new ArrayDeque<>();
    private final DataOutput column;
    private final DataAccessSubset row;
    private final Set<Key> keyboard = new HashSet<>();
    private Set<Key> typed = Set.of();

    public KeyboardAdapter(ParallelInput rowPort, ParallelOutput columnPort) {
        column = columnPort.output();
        row = DataAccessSubset.of(rowPort.input(), 0x7f);
        rowPort.inputFrom(this::keyScan);
        state.busy(BOOT_DELAY);
    }

    @Override
    public void tick(Clock clock) {
        if (state.at(clock).isBusy()) return;

        state.busy(TYPE_DELAY);
        if (typed.isEmpty()) {
            if (!buffer.isEmpty()) {
                typed = buffer.poll();
                press(typed);
            }
        } else {
            release(typed);
            typed = Set.of();
        }
    }

    @Override
    public void type(Set<Key> keys) {
        buffer.add(keys);
    }

    @Override
    public void press(Set<Key> keys) {
        keyboard.addAll(keys);
    }

    @Override
    public void release(Set<Key> keys) {
        keyboard.removeAll(keys);
    }

    @Override
    public void pause(int delay) {
        for (int i = 0; i < delay; i++) buffer.add(Set.of());
    }

    private void keyScan(DataAccess input) {
        row.value(0xff);
        for (var key : keyboard) {
            row.clear(~key.row(column.value()));
        }
    }
}
