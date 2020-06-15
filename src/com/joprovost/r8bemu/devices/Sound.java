package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.clock.Uptime;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.DataOutputSubset;
import com.joprovost.r8bemu.data.DataPort;
import com.joprovost.r8bemu.data.Variable;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class Sound implements DataPort.DataConsumer, ClockAware {
    public static final AudioFormat AUDIO_FORMAT = new AudioFormat(44100, 8, 1, false, false);
    private final byte[] buffer = new byte[(int) (AUDIO_FORMAT.getSampleRate() / 10)];

    private final Variable CS5_PA_OUT = Variable.ofMask(0xff);
    private final DataOutput DAC = DataOutputSubset.of(CS5_PA_OUT, 0b11111100);

    private final PipedInputStream reader;
    private final PipedOutputStream writer;
    private final SourceDataLine sourceDataLine;
    private final Uptime uptime;
    private long lastOutput = 0;
    private long lastInput = 0;

    public Sound(Uptime uptime) throws LineUnavailableException, IOException {
        this.uptime = uptime;
        sourceDataLine = AudioSystem.getSourceDataLine(AUDIO_FORMAT);
        sourceDataLine.open(AUDIO_FORMAT, buffer.length);
        reader = new PipedInputStream(65536);
        writer = new PipedOutputStream(reader);
    }

    @Override
    public void consume(DataOutput output) {
        CS5_PA_OUT.set(output.unsigned());
        lastInput = sample();
        sourceDataLine.start();
    }

    public long sample() {
        return (uptime.nanoTime() * (long) AUDIO_FORMAT.getSampleRate() / 1000000000);
    }

    @Override
    public void tick(Clock clock) throws IOException {
        if (lastOutput - lastInput > AUDIO_FORMAT.getSampleRate()) {
            sourceDataLine.stop();
            return;
        }

        int required = sourceDataLine.available();
        if (required > 1 && reader.available() >= required) {
            sourceDataLine.write(buffer, 0, reader.read(buffer, 0, required));
        }
        else if (reader.available() > buffer.length) {
            // reduce latency....
            reader.read(buffer, 0, buffer.length);
        }

        var sample = sample();
        if (lastOutput != sample) writer.write(DAC.unsigned());
        lastOutput = sample;
    }
}
