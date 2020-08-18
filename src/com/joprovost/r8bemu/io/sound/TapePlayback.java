package com.joprovost.r8bemu.io.sound;

import com.joprovost.r8bemu.clock.Uptime;
import com.joprovost.r8bemu.data.link.LineOutputHandler;
import com.joprovost.r8bemu.data.link.ParallelInputProvider;
import com.joprovost.r8bemu.io.CassetteRecorder;

import java.io.File;
import java.io.IOException;

public class TapePlayback implements CassetteRecorder {
    private final Uptime uptime;
    private WaveFile playback = WaveFile.empty();

    // TODO: See why the pitch has to be lowered?
    //       Required in order to read files from https://colorcomputerarchive.com/repo/Cassettes
    private static final double PITCH = 0.9;

    private long offset = 0;
    private long motorOff = 0;
    private boolean motor;

    public TapePlayback(Uptime uptime) {
        this.uptime = uptime;
    }

    public ParallelInputProvider output(int mask) {
        return input -> {
            if (!motor) return;
            var pos = position(uptime.nanoTime() - offset);
            if (pos < playback.data.length) {
                if ((playback.data[pos] & 0xff) >= 128) {
                    input.set(mask);
                } else {
                    input.clear(mask);
                }
            } else {
                input.set(mask);
            }
        };
    }

    public LineOutputHandler motor() {
        return state -> motor(state.isSet());
    }

    private int position(long nanoTime) {
        return (int) Math.round(nanoTime * PITCH * playback.sampleRate / 1000000000.0d);
    }

    private void motor(boolean state) {
        if (motor == state) return;
        motor = state;

        if (motor) {
            offset += uptime.nanoTime() - motorOff;
        } else {
            motorOff = uptime.nanoTime();
        }
    }

    @Override
    public void insert(File cassette) {
        rewind();
        try {
            playback = WaveFile.load(cassette.toPath());
        } catch (IOException ignored) {
        }
    }

    @Override
    public void rewind() {
        motorOff = 0;
        offset = 0;
    }
}
