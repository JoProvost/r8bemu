package com.joprovost.r8bemu.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class Speaker implements AudioSink, Runnable {

    private final AudioFormat format;
    private final Buffer buffer;
    private final InputStream input;
    private long last = 0;

    public Speaker(AudioFormat format) {
        this.format = format;
        buffer = new Buffer((int) (format.getSampleRate() / 10), 10);
        input = buffer.input();
    }

    private long position(long nanoTime) {
        return (nanoTime * (long) format.getSampleRate() / 1000000000);
    }

    @Override
    public void sample(int amplitude, long atNanoTime) {
        try {
            var pos = position(atNanoTime);
            int count = (int) (pos - last);
            if (count > 0) {
                buffer.skip(count - 1);
                buffer.write(amplitude);
            }
            last = pos;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
