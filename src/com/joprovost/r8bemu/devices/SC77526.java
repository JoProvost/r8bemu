package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.io.AudioSink;
import com.joprovost.r8bemu.io.Joystick;
import com.joprovost.r8bemu.port.DataInputProvider;
import com.joprovost.r8bemu.port.DataOutputHandler;
import com.joprovost.r8bemu.port.LogicOutputHandler;

import static com.joprovost.r8bemu.devices.SC77526.Axis.LEFT_HORIZONTAL;
import static com.joprovost.r8bemu.devices.SC77526.Axis.LEFT_VERTICAL;
import static com.joprovost.r8bemu.devices.SC77526.Axis.RIGHT_HORIZONTAL;
import static com.joprovost.r8bemu.devices.SC77526.Axis.RIGHT_VERTICAL;

public class SC77526 {
    public static final int CENTER = 32;
    private final AudioSink audioSink;
    private final int[] axis = new int[Axis.values().length];

    private int dac;
    private boolean soundOutput;
    private boolean selA;
    private boolean selB;

    public SC77526(AudioSink audioSink) {
        this.audioSink = audioSink;
        for (int i = 1; i < axis.length; i++) axis[i] = CENTER;
    }

    public static Axis axis(boolean horizontalVertical, boolean leftRight) {
        if (leftRight) {
            if (horizontalVertical) return RIGHT_VERTICAL;
            else return RIGHT_HORIZONTAL;
        } else {
            if (horizontalVertical) return LEFT_VERTICAL;
            else return LEFT_HORIZONTAL;
        }
    }

    public DataOutputHandler dac(int mask) {
        return output -> {
            dac = output.subset(mask);
            if (soundOutput) audioSink.sample(dac * 4 - 128);
        };
    }

    public DataInputProvider joystick(int mask) {
        return input -> {
            if (dac <= axis[axis(selA, selB).ordinal()]) {
                input.set(mask);
            } else {
                input.clear(mask);
            }
        };
    }

    public LogicOutputHandler soundOutput() {
        return state -> {
            soundOutput = state.isSet();
            if (soundOutput) audioSink.sample(dac * 4 - 128);
        };
    }

    public LogicOutputHandler selA() {
        return state -> selA = state.isSet();
    }

    public LogicOutputHandler selB() {
        return state -> selB = state.isSet();
    }

    public Joystick left() {
        return new Joystick() {
            @Override
            public void horizontal(int value) {
                axis[LEFT_HORIZONTAL.ordinal()] = fromJoystick(value);
            }

            @Override
            public void vertical(int value) {
                axis[LEFT_VERTICAL.ordinal()] = fromJoystick(value);
            }
        };
    }

    private int fromJoystick(int value) {
        return value / ((Joystick.MAXIMUM - Joystick.MINIMUM) / 64) + 32;
    }

    public Joystick right() {
        return new Joystick() {
            @Override
            public void horizontal(int value) {
                axis[RIGHT_HORIZONTAL.ordinal()] = fromJoystick(value);
            }

            @Override
            public void vertical(int value) {
                axis[RIGHT_VERTICAL.ordinal()] = fromJoystick(value);
            }
        };
    }

    enum Axis {
        LEFT_HORIZONTAL,
        LEFT_VERTICAL,
        RIGHT_HORIZONTAL,
        RIGHT_VERTICAL
    }
}
