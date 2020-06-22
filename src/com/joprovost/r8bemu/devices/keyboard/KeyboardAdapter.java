package com.joprovost.r8bemu.devices.keyboard;

import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.clock.ClockAwareBusyState;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataAccessSubset;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.devices.MC6821;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class KeyboardAdapter implements KeyboardBuffer, ClockAware {

    public static final int TYPE_DELAY = 40000;
    public static final int BOOT_DELAY = 1200000;

    private final ClockAwareBusyState state = new ClockAwareBusyState();
    private final Deque<List<Key>> buffer = new ArrayDeque<>();
    private final DataOutput column;
    private final DataAccessSubset row;
    private List<Key> keyboard = List.of();

    public KeyboardAdapter(MC6821 pia) {
        column = pia.portB().output();
        row = DataAccessSubset.of(pia.portA().input(), 0x7f);
        pia.portA().inputFrom(this::keyScan);
        state.busy(BOOT_DELAY);
    }

    @Override
    public void tick(Clock clock) {
        if (state.at(clock).isBusy()) return;

        state.busy(TYPE_DELAY);
        if (keyboard.isEmpty()) {
            if (!buffer.isEmpty())
                keyboard = buffer.poll();
        } else {
            keyboard = List.of();
        }
    }

    @Override
    public void type(List<Key> key) {
        buffer.add(key);
    }

    @Override
    public void pause(int delay) {
        for (int i = 0; i < delay; i++) buffer.add(List.of());
    }

    private void keyScan(DataAccess input) {
        row.value(0xff);
        for (var key : keyboard) {
            row.clear(~key.row(column.value()));
        }
    }
}
