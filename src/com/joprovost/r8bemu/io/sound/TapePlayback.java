package com.joprovost.r8bemu.io.sound;

import com.joprovost.r8bemu.clock.Uptime;
import com.joprovost.r8bemu.port.DataInputProvider;
import com.joprovost.r8bemu.port.LogicOutputHandler;

import java.io.IOException;
import java.nio.file.Path;

public class TapePlayback {
    private final Uptime uptime;
    private final Path file;
    private WaveFile playback = WaveFile.empty();

    // TODO: See why the pitch has to be lowered?
    //       Required in order to read files from https://colorcomputerarchive.com/repo/Cassettes
    private static final double PITCH = 0.9;

    private long offset = 0;
    private long motorOff = 0;
    private boolean motor;

    public TapePlayback(Uptime uptime, Path file) {
        this.uptime = uptime;
        this.file = file;
    }

    public DataInputProvider output(int mask) {
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

    public LogicOutputHandler motor() {
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
            try {
                playback = WaveFile.load(file);
            } catch (IOException ignored) {
            }
        } else {
            motorOff = uptime.nanoTime();
        }
    }
}
