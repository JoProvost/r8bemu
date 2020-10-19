package com.joprovost.r8bemu.io.sound;

import com.joprovost.r8bemu.clock.Uptime;
import com.joprovost.r8bemu.data.NumericRange;
import com.joprovost.r8bemu.data.analog.AnalogInput;
import com.joprovost.r8bemu.data.discrete.DiscteteOutputHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class TapeRecorder {
    public static final int FREQUENCY = 22050;

    // TODO: See why the pitch has to be lowered?
    //       Required in order to read files from https://colorcomputerarchive.com/repo/Cassettes
    private static final double PITCH = 0.9;

    private static final NumericRange AUDIO_SAMPLE = new NumericRange(0, 128, 255);

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

    public AnalogInput input() {
        return amplitude -> {
            if (!motor) return;
            var pos = position(uptime.nanoTime() - offset);
            int count = (int) (pos - this.pos);
            if (count > 0) {
                skip(count - 1);
                last = unsigned(amplitude);
                recording.write(last);
            }
            this.pos = pos;
        };
    }

    public int unsigned(double amplitude) {
        return (int) AUDIO_SAMPLE.from(amplitude);
    }

    public void skip(long count) {
        for (int i = 0; i < count; i++) {
            recording.write(last);
        }
    }

    public DiscteteOutputHandler motor() {
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
