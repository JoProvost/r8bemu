package com.joprovost.r8bemu.io.linux;

import com.joprovost.r8bemu.io.Joystick;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class LinuxJoystickDriver implements Runnable {

    private final Path device;
    private final Joystick joystick;

    public LinuxJoystickDriver(Path device, Joystick joystick) {
        this.device = device;
        this.joystick = joystick;
    }

    @Override
    public void run() {
        try {
            for (;;) {
                try (var input = new FileInputStream(device.toFile())) {
                    for (;;) LinuxJoystickEvent.readFrom(input).applyTo(joystick);
                } catch (IOException ignored) {
                    Thread.sleep(500);
                }
            }
        } catch (InterruptedException ignored) {
        }
    }
}
