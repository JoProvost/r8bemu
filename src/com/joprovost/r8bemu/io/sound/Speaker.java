package com.joprovost.r8bemu.io.sound;

import com.joprovost.r8bemu.clock.Uptime;
import com.joprovost.r8bemu.data.NumericRange;
import com.joprovost.r8bemu.data.analog.AnalogInput;
import com.joprovost.r8bemu.data.buffer.BigEndianAudioBuffer;
import com.joprovost.r8bemu.data.discrete.DiscreteOutput;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class Speaker implements Runnable, Mixer {

    public static final int TIMEOUT_MS = 10;
    private static final NumericRange AUDIO_SAMPLE = new NumericRange(-32768, 0, 32767);
    private final AudioFormat format;
    private final Uptime uptime;
    private final BigEndianAudioBuffer buffer;
    private final DiscreteOutput mute;
    private final InputStream input;
    private long last = 0;
    private double volume = VOLUME_DEFAULT;

    public Speaker(AudioFormat format, Uptime uptime, DiscreteOutput mute) {
        this.format = format;
        this.uptime = uptime;
        this.mute = mute;
        buffer = new BigEndianAudioBuffer(
                (int) (format.getSampleRate() / 10),
                TIMEOUT_MS,
                encoded(0)
        );
        input = buffer.input();
    }

    private long position(long nanoTime) {
        return (nanoTime * (long) format.getSampleRate() / 1000000000);
    }

    public AnalogInput input() {
        return amplitude -> {
            try {
                long pos = position(uptime.nanoTime());
                int count = (int) (pos - last);
                if (count > 0) {
                    buffer.skip(count - 1);
                    buffer.write(encoded(amplitude));
                }
                last = pos;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    public int encoded(double amplitude) {
        if (mute.isSet()) return 0;
        return (int) AUDIO_SAMPLE.from(amplitude * volume);
    }

    @Override
    public void run() {
        try {
            SourceDataLine audio = AudioSystem.getSourceDataLine(format);
            audio.open(format, buffer.size() / 2);
            while (audio.isOpen()) {
                byte[] bytes = input.readNBytes(audio.available());
                audio.write(bytes, 0, bytes.length);
                audio.start();
            }
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        } catch (IOException ignored) {
            // Read simply ended (the thread is stopping)
        }
    }

    @Override
    public void volume(double volume) {
        this.volume = volume;
    }
}
