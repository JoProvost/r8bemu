package com.joprovost.r8bemu.devices.memory;

public class StringMemory implements Addressable {
    private final StringBuffer buffer;

    public StringMemory(StringBuffer buffer) {
        this.buffer = buffer;
    }

    public StringMemory() {
        this(new StringBuffer(" ".repeat(256)));
    }

    public int read(int address) {
        return buffer.charAt(address);
    }

    public void write(int address, int data) {
        buffer.setCharAt(address, (char) data);
    }

    public String toString() {
        return buffer.toString();
    }
}
