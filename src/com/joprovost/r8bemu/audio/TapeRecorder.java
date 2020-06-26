package com.joprovost.r8bemu.audio;

import com.joprovost.r8bemu.clock.Uptime;
import com.joprovost.r8bemu.port.LogicOutputHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class TapeRecorder {
    public static final int FREQUENCY = 9600;

    // TODO: See why the pitch has to be lowered?
    //       Required in order to read files from https://colorcomputerarchive.com/repo/Cassettes
    private static final double PITCH = 0.9;

    private final Uptime uptime;
    private final Path file;
    private final ByteArrayOutputStream recording = new ByteArrayOutputStream();

    private int last = 128;
    private long pos = 0;
    private long offset = 0;
    private boolean motor;

    public TapeRecorder(Uptime uptime, Path file) {
        this.uptime = uptime;
        this.file = file;
    }

    public AudioSink input() {
        return amplitude -> {
            if (!motor) return;
            var pos = position(uptime.nanoTime() - offset);
            int count = (int) (pos - this.pos);
            if (count > 0) {
                skip(count - 1);
                recording.write(amplitude);
                last = amplitude;
            }
            this.pos = pos;
        };
    }

    public void skip(long count) {
        for (int i = 0; i < count; i++) {
            recording.write(last);
        }
    }

    public LogicOutputHandler motor() {
        return state -> motor(state.isSet());
    }

    private long position(long nanoTime) {
        return Math.round(nanoTime * PITCH * FREQUENCY / 1000000000.0d);
    }

    private void motor(boolean state) {
        if (motor == state) return;
        motor = state;

        if (motor) {
            offset = uptime.nanoTime();
            pos = 0;
        } else {
            if (recording.size() > 0) {
                skip(position(uptime.nanoTime() - offset) - this.pos);

                try {
                    WaveFile.save(file, recording, FREQUENCY);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
    }
}
