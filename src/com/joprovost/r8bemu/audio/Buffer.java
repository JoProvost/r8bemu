package com.joprovost.r8bemu.audio;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

public class Buffer extends InputStream {
    private final byte[] buffer;
    private final int timeout;

    private int write = 0;
    private int read = 0;
    private boolean empty = true;
    private int last = 0;

    public Buffer(int size, int timeout) {
        buffer = new byte[size];
        this.timeout = timeout;
    }

    public synchronized int read() throws IOException {
        checkAvailability();
        if (isEmpty()) return -1;
        int value = buffer[read++] & 255;
        read %= buffer.length;
        if (write == read) empty = true;
        return value;
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean isFull() {
        return write == read && !empty;
    }

    public synchronized void write(int value) throws IOException {
        checkSpace();
        if (isEmpty()) {
            empty = false;
            write = 0;
            read = 0;
        }
        buffer[write++] = (byte) (value & 255);
        write %= buffer.length;
        last = value;
    }

    public synchronized void skip(int count) throws IOException {
        if (count >= buffer.length) return;
        for(int i = 0; i < count; ++i) write(last);
    }

    private void checkAvailability() throws InterruptedIOException {
        if (!isEmpty()) return;
        notifyAll();
        try {
            wait(timeout);
        } catch (InterruptedException ignored) {
            throw new InterruptedIOException();
        }
    }

    private void checkSpace() throws IOException {
        while (isFull()) {
            notifyAll();
            try {
                wait(timeout);
            } catch (InterruptedException ignored) {
                throw new InterruptedIOException();
            }
        }
    }
}
