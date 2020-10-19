package com.joprovost.r8bemu.io.linux;

import com.joprovost.r8bemu.data.NumericRange;
import com.joprovost.r8bemu.io.JoystickInput;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LinuxJoystickEvent {
    public static final NumericRange LINUX_AXIS_RANGE = new NumericRange(-32768, 0, 32767);
    public static final int BUTTON = 1;
    public static final int AXIS = 2;

    private final int timestamp;
    private final short value;
    private final int type;
    private final int number;

    private LinuxJoystickEvent(int timestamp, short value, int type, int number) {
        this.timestamp = timestamp;
        this.value = value;
        this.type = type;
        this.number = number;
    }

    // https://www.kernel.org/doc/html/latest/input/joydev/joystick-api.html#event-reading
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

    public void applyTo(JoystickInput joystick) {
        switch (type) {
            case BUTTON:
                if (value == 0) joystick.release();
                if (value == 1) joystick.press();
                break;
            case AXIS:
                if (number == 0) joystick.horizontal(JoystickInput.AXIS_RANGE.from(value, LINUX_AXIS_RANGE));
                if (number == 1) joystick.vertical(JoystickInput.AXIS_RANGE.from(value, LINUX_AXIS_RANGE));
                break;
        }
    }
}
