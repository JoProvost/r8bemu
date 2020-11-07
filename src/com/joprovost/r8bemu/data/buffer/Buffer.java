package com.joprovost.r8bemu.data.buffer;

import com.joprovost.r8bemu.devices.memory.Addressable;

public class Buffer {

    private final byte[] buffer;
    private int read = 0;
    private int write = 0;

    public Buffer(int capacity) {
        buffer = new byte[capacity];
    }

    public int read() {
        if (isEmpty()) throw new IndexOutOfBoundsException();
        read %= buffer.length;
        return buffer[read++] & 0xff;
    }

    public boolean isEmpty() {
        return size() <= 0;
    }

    public void write(int value) {
        if (isFull()) throw new IndexOutOfBoundsException();
        write %= buffer.length;
        buffer[write++] = (byte) (value & 0xff);
    }

    private boolean isFull() {
        return size() >= buffer.length - 1;
    }

    public int size() {
        int avail = write - read;
        return (avail < 0) ? buffer.length + avail : avail;
    }

    public void reset() {
        read = 0;
        write = 0;
    }

    public void drainTo(Addressable dest, int address) {
        for (int i = 0; size() > 0; i++)
            dest.write(address + i, read());
    }

    public void readFrom(Addressable dest, int address, int size) {
        for (int i = 0; i < size; i++)
            write(dest.read(address + i));
    }
}
