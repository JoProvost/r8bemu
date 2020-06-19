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

    // At 900kHz, each key is pressed 44ms and released 44ms
    public static final int TYPE_DELAY = 40000;
    public static final int BOOT_DELAY = 1200000;

    private final ClockAwareBusyState state = new ClockAwareBusyState();
    private final Deque<List<Key>> buffer = new ArrayDeque<>();
    private final DataAccess row;
    private List<Key> keyboard = List.of();

    public KeyboardAdapter(MC6821 pia) {
        row = DataAccessSubset.of(pia.portA().input(), 0x7f);
        pia.portB().outputTo(this::keyScan);
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

    private void keyScan(DataOutput column) {
        if (keyboard.isEmpty()) {
            row.set(0xff);
        } else {
            int portA = 0xff;
            for (var key : keyboard) {
                portA &= key.row(column.unsigned());
            }
            row.set(portA);
        }
    }
}
