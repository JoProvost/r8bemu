package com.joprovost.r8bemu.coco.devices;

import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.clock.Countdown;
import com.joprovost.r8bemu.data.binary.BinaryAccess;
import com.joprovost.r8bemu.data.binary.BinaryLineInput;
import com.joprovost.r8bemu.data.binary.BinaryLineOutput;
import com.joprovost.r8bemu.data.binary.BinaryOutput;
import com.joprovost.r8bemu.data.transform.BinaryAccessSubset;
import com.joprovost.r8bemu.io.Key;
import com.joprovost.r8bemu.io.Keyboard;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import static com.joprovost.r8bemu.io.Key.AT;
import static com.joprovost.r8bemu.io.Key.BREAK;
import static com.joprovost.r8bemu.io.Key.CLEAR;
import static com.joprovost.r8bemu.io.Key.COLON;
import static com.joprovost.r8bemu.io.Key.COMMA;
import static com.joprovost.r8bemu.io.Key.DOT;
import static com.joprovost.r8bemu.io.Key.DOWN;
import static com.joprovost.r8bemu.io.Key.ENTER;
import static com.joprovost.r8bemu.io.Key.KEY_0;
import static com.joprovost.r8bemu.io.Key.KEY_1;
import static com.joprovost.r8bemu.io.Key.KEY_2;
import static com.joprovost.r8bemu.io.Key.KEY_3;
import static com.joprovost.r8bemu.io.Key.KEY_4;
import static com.joprovost.r8bemu.io.Key.KEY_5;
import static com.joprovost.r8bemu.io.Key.KEY_6;
import static com.joprovost.r8bemu.io.Key.KEY_7;
import static com.joprovost.r8bemu.io.Key.KEY_8;
import static com.joprovost.r8bemu.io.Key.KEY_9;
import static com.joprovost.r8bemu.io.Key.KEY_A;
import static com.joprovost.r8bemu.io.Key.KEY_B;
import static com.joprovost.r8bemu.io.Key.KEY_C;
import static com.joprovost.r8bemu.io.Key.KEY_D;
import static com.joprovost.r8bemu.io.Key.KEY_E;
import static com.joprovost.r8bemu.io.Key.KEY_F;
import static com.joprovost.r8bemu.io.Key.KEY_G;
import static com.joprovost.r8bemu.io.Key.KEY_H;
import static com.joprovost.r8bemu.io.Key.KEY_I;
import static com.joprovost.r8bemu.io.Key.KEY_J;
import static com.joprovost.r8bemu.io.Key.KEY_K;
import static com.joprovost.r8bemu.io.Key.KEY_L;
import static com.joprovost.r8bemu.io.Key.KEY_M;
import static com.joprovost.r8bemu.io.Key.KEY_N;
import static com.joprovost.r8bemu.io.Key.KEY_O;
import static com.joprovost.r8bemu.io.Key.KEY_P;
import static com.joprovost.r8bemu.io.Key.KEY_Q;
import static com.joprovost.r8bemu.io.Key.KEY_R;
import static com.joprovost.r8bemu.io.Key.KEY_S;
import static com.joprovost.r8bemu.io.Key.KEY_T;
import static com.joprovost.r8bemu.io.Key.KEY_U;
import static com.joprovost.r8bemu.io.Key.KEY_V;
import static com.joprovost.r8bemu.io.Key.KEY_W;
import static com.joprovost.r8bemu.io.Key.KEY_X;
import static com.joprovost.r8bemu.io.Key.KEY_Y;
import static com.joprovost.r8bemu.io.Key.KEY_Z;
import static com.joprovost.r8bemu.io.Key.LEFT;
import static com.joprovost.r8bemu.io.Key.MINUS;
import static com.joprovost.r8bemu.io.Key.RIGHT;
import static com.joprovost.r8bemu.io.Key.SHIFT;
import static com.joprovost.r8bemu.io.Key.SLASH;
import static com.joprovost.r8bemu.io.Key.SMCOL;
import static com.joprovost.r8bemu.io.Key.SPACE;
import static com.joprovost.r8bemu.io.Key.UP;

public class KeyboardAdapter implements Keyboard, ClockAware {

    public static final int TYPE_DELAY = 10000;
    public static final int BOOT_DELAY = 600000;

    private final Countdown state = new Countdown();
    private final Deque<Set<Key>> buffer = new ArrayDeque<>();
    private final BinaryOutput column;
    private final BinaryAccessSubset row;
    private final Set<Key> keyboard = new HashSet<>();
    private Set<Key> typed = Set.of();

    public static final Key[][] MAPPING = new Key[][]{
            // @formatter:on
            {AT,    KEY_A, KEY_B, KEY_C, KEY_D, KEY_E, KEY_F, KEY_G},
            {KEY_H, KEY_I, KEY_J, KEY_K, KEY_L, KEY_M, KEY_N, KEY_O},
            {KEY_P, KEY_Q, KEY_R, KEY_S, KEY_T, KEY_U, KEY_V, KEY_W},
            {KEY_X, KEY_Y, KEY_Z, UP,    DOWN,  LEFT,  RIGHT, SPACE},
            {KEY_0, KEY_1, KEY_2, KEY_3, KEY_4, KEY_5, KEY_6, KEY_7},
            {KEY_8, KEY_9, COLON, SMCOL, COMMA, MINUS, DOT,   SLASH},
            {ENTER, CLEAR, BREAK, null,  null,  null,  null,  SHIFT},
            // @formatter:off<
    };

    public KeyboardAdapter(BinaryLineInput rowPort, BinaryLineOutput columnPort) {
        column = columnPort.output();
        row = BinaryAccessSubset.of(rowPort.input(), 0x7f);
        rowPort.from(this::keyScan);
        state.busy(BOOT_DELAY);
    }

    @Override
    public void tick(Clock clock) {
        if (state.isBusy()) return;

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

    private void keyScan(BinaryAccess input) {
        row.value(0xff);
        for (var key : keyboard) {
            row(row, key, column.value());
        }
    }

    public static void row(BinaryAccess row, Key key, int column) {
        for (int a = 0; a < MAPPING.length; a++) {
            for (int b = 0; b < MAPPING[a].length; b++) {
                if (key == MAPPING[a][b]) {
                    if ((column & (1 << b)) == 0) {
                        row.clear(1 << a);
                    }
                    return;
                }
            }
        }
    }

}
