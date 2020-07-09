package com.joprovost.r8bemu.io.linux;

import com.joprovost.r8bemu.io.Joystick;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LinuxJoystickEvent {
    private final int timestamp;
    private final short value;
    private final int type;
    private final int number;

    public LinuxJoystickEvent(int timestamp, short value, int type, int number) {
        this.timestamp = timestamp;
        this.value = value;
        this.type = type;
        this.number = number;
    }

    public static LinuxJoystickEvent readFrom(InputStream input) throws IOException {
        var event = new byte[8];
        if (input.read(event) < 0) throw new EOFException();
        ByteBuffer buffer = ByteBuffer.wrap(event).order(ByteOrder.LITTLE_ENDIAN);
        return new LinuxJoystickEvent(
                buffer.getInt(),
                buffer.getShort(),
                buffer.get() & 0x7f,
                buffer.get()
        );
    }

    @Override
    public String toString() {
        return "JoystickEvent{" +
                "timestamp=" + timestamp +
                ", value=" + value +
                ", type=" + type +
                ", number=" + number +
                '}';
    }

    public void applyTo(Joystick joystick) {
        switch (type) {
            case 1: // button
                if (value == 0) joystick.release();
                if (value == 1) joystick.press();
                break;
            case 2: // axis
                if (number == 0) joystick.horizontal(value);
                if (number == 1) joystick.vertical(value);
                break;
        }
    }
}
