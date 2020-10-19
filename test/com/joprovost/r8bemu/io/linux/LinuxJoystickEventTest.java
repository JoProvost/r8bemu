package com.joprovost.r8bemu.io.linux;

import com.joprovost.r8bemu.io.FakeJoystickInput;
import com.joprovost.r8bemu.io.JoystickInput;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LinuxJoystickEventTest {

    @Test
    void buttonPressEvent() throws IOException {
        FakeJoystickInput joystick = new FakeJoystickInput();

        LinuxJoystickEvent.readFrom(button(0, 1)).applyTo(joystick);
        assertTrue(joystick.pressed());

        LinuxJoystickEvent.readFrom(button(0, 0)).applyTo(joystick);
        assertTrue(joystick.released());
    }

    @Test
    void axisEvent() throws IOException {
        FakeJoystickInput joystick = new FakeJoystickInput();

        LinuxJoystickEvent.readFrom(axis(0, -32768)).applyTo(joystick);
        assertEquals(JoystickInput.MINIMUM, joystick.horizontal());

        LinuxJoystickEvent.readFrom(axis(0, 32767)).applyTo(joystick);
        assertEquals(JoystickInput.MAXIMUM, joystick.horizontal());

        LinuxJoystickEvent.readFrom(axis(0, 0)).applyTo(joystick);
        assertEquals(JoystickInput.CENTER, joystick.horizontal());

        LinuxJoystickEvent.readFrom(axis(1, -32768)).applyTo(joystick);
        assertEquals(JoystickInput.MINIMUM, joystick.vertical());

        LinuxJoystickEvent.readFrom(axis(1, 32767)).applyTo(joystick);
        assertEquals(JoystickInput.MAXIMUM, joystick.vertical());

        LinuxJoystickEvent.readFrom(axis(1, 0)).applyTo(joystick);
        assertEquals(JoystickInput.CENTER, joystick.vertical());
    }

    private InputStream button(int number, int value) {
        return encoded(1, number, value);
    }

    private InputStream axis(int number, int value) {
        return encoded(2, number, value);
    }

    private InputStream encoded(int type, int number, int value) {
        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt((int) (Instant.now().getEpochSecond() & Integer.MAX_VALUE));
        buffer.putShort((short) value);
        buffer.put((byte) (type & 0xff));
        buffer.put((byte) (number & 0xff));
        return new ByteArrayInputStream(buffer.array());
    }

}
