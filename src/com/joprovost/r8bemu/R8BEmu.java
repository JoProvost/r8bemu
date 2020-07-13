package com.joprovost.r8bemu;

import com.joprovost.r8bemu.data.LogicVariable;
import com.joprovost.r8bemu.io.CassetteRecorder;
import com.joprovost.r8bemu.io.awt.NumpadJoystickDriver;
import com.joprovost.r8bemu.clock.ClockGenerator;
import com.joprovost.r8bemu.io.Joystick;
import com.joprovost.r8bemu.io.Keyboard;
import com.joprovost.r8bemu.io.Display;
import com.joprovost.r8bemu.io.linux.LinuxJoystickDriver;
import com.joprovost.r8bemu.io.terminal.InputStreamKeyboard;
import com.joprovost.r8bemu.io.terminal.Terminal;
import com.joprovost.r8bemu.io.awt.AWTKeyboardDriver;
import com.joprovost.r8bemu.io.awt.FrameBuffer;
import com.joprovost.r8bemu.io.awt.UserInterface;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.joprovost.r8bemu.io.awt.UserInterface.SEPARATOR;

public class R8BEmu {

    public static void main(String[] args) throws IOException {
        var options = parse(args);
        var ui = options.getOrDefault("interface", "awt");
        var home = options.getOrDefault("home", System.getProperty("user.home") + "/.r8bemu");
        var script = Path.of(options.getOrDefault("script", home + "/script/autorun.bas"));
        var playback = Path.of(options.getOrDefault("playback", home + "/cassette/playback.wav"));
        var recording = Path.of(options.getOrDefault("recording", home + "/cassette/recording.wav"));

        var keyboardBuffer = new LogicVariable();
        keyboardBuffer.set(Boolean.parseBoolean(options.getOrDefault("keyboard-buffer", "true")));

        var clock = new ClockGenerator();
        var keyboard = Keyboard.dispatcher();
        var joystickLeft = Joystick.dispatcher();
        var joystickRight = Joystick.dispatcher();
        var display = Display.dispatcher();
        var cassette = CassetteRecorder.dispatcher();

        switch (ui) {
            case "terminal":
                clock.aware(new InputStreamKeyboard(System.in, keyboard));
                display.dispatchTo(new Terminal(System.out));
                break;

            case "awt":
                var frameBuffer = new FrameBuffer();
                display.dispatchTo(frameBuffer);
                frameBuffer.addKeyListener(new AWTKeyboardDriver(keyboard, keyboardBuffer));
                frameBuffer.addKeyListener(new NumpadJoystickDriver(joystickLeft));
                UserInterface.show(frameBuffer, List.of(
                        Actions.reset(),
                        SEPARATOR,
                        Actions.rewindCassette(home, cassette),
                        Actions.insertCassette(home, cassette),
                        SEPARATOR,
                        Actions.keyboard(keyboardBuffer),
                        SEPARATOR,
                        Actions.presentation()
                ));
                break;
        }

        var services = new Threads();

        services.declare(new LinuxJoystickDriver(Path.of("/dev/input/js0"), joystickLeft));
        services.declare(new LinuxJoystickDriver(Path.of("/dev/input/js1"), joystickRight));

        CoCoII.emulate(services, clock, display, keyboard, joystickLeft, joystickRight, cassette, script, playback, recording, home);
    }

    public static Map<String, String> parse(String[] args) {
        Map<String, String> params = new HashMap<>();

        String key = null;
        for (var arg : args) {
            if (arg.startsWith("--")) {
                key = arg.substring(2);
                params.put(key, null);
            } else if (key != null) {
                params.put(key, arg);
                key = null;
            }
        }

        return params;
    }

}
