package com.joprovost.r8bemu.io.sound;

import com.joprovost.r8bemu.clock.Uptime;
import com.joprovost.r8bemu.data.Buffer;
import com.joprovost.r8bemu.io.AudioSink;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_UNSIGNED;

public class Speaker implements Runnable {

    public static final int TIMEOUT_MS = 10;
    private final AudioFormat format;
    private final Uptime uptime;
    private final Buffer buffer;
    private final InputStream input;
    private long last = 0;

    public Speaker(AudioFormat format, Uptime uptime) {
        this.format = format;
        this.uptime = uptime;
        buffer = new Buffer(
                (int) (format.getSampleRate() / 10),
                TIMEOUT_MS,
                encoded(0)
        );
        input = buffer.input();
    }

    private long position(long nanoTime) {
        return (nanoTime * (long) format.getSampleRate() / 1000000000);
    }

    public AudioSink input() {
        return amplitude -> {
            try {
                var pos = position(uptime.nanoTime());
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

    public int encoded(int amplitude) {
        return amplitude + (format.getEncoding() == PCM_UNSIGNED ? 128 : 0);
    }

    @Override
    public void run() {
        try {
            SourceDataLine audio = AudioSystem.getSourceDataLine(format);
            audio.open(format, buffer.size() / 2);
            while (audio.isOpen()) {
                var bytes = input.readNBytes(audio.available());
                audio.write(bytes, 0, bytes.length);
                audio.start();
            }
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        } catch (IOException ignored) {
            // Read simply ended (the thread is stopping)
        }
    }
}
