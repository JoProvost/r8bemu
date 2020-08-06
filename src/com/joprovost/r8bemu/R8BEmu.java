package com.joprovost.r8bemu;

import com.joprovost.r8bemu.clock.EmulatorContext;
import com.joprovost.r8bemu.data.Flag;
import com.joprovost.r8bemu.io.CassetteRecorder;
import com.joprovost.r8bemu.io.Display;
import com.joprovost.r8bemu.io.Joystick;
import com.joprovost.r8bemu.io.Keyboard;
import com.joprovost.r8bemu.io.awt.AWTKeyboardDriver;
import com.joprovost.r8bemu.io.awt.FrameBuffer;
import com.joprovost.r8bemu.io.awt.NumpadJoystickDriver;
import com.joprovost.r8bemu.io.awt.UserInterface;
import com.joprovost.r8bemu.io.linux.LinuxJoystickDriver;
import com.joprovost.r8bemu.io.terminal.InputStreamKeyboard;
import com.joprovost.r8bemu.io.terminal.Terminal;
import com.joprovost.r8bemu.mc6809.Register;
import com.joprovost.r8bemu.mc6809.Signal;
import com.joprovost.r8bemu.memory.Memory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.joprovost.r8bemu.io.awt.UserInterface.SEPARATOR;

public class R8BEmu {

    public static void main(String[] args) throws IOException, URISyntaxException {
        var options = parse(args);
        var ui = options.getOrDefault("interface", "awt");
        var home = Path.of(options.getOrDefault("home", System.getProperty("user.home") + "/.r8bemu"));
        var script = Path.of(options.getOrDefault("script", home + "/autorun.bas"));
        var playback = Path.of(options.getOrDefault("playback", home + "/playback.wav"));
        var recording = Path.of(options.getOrDefault("recording", home + "/recording.wav"));

        var keyboardBuffer = Flag.value(Boolean.parseBoolean(options.getOrDefault("keyboard-buffer", "true")));

        var context = new EmulatorContext();
        var ram = new Memory(0x7fff);
        var keyboard = Keyboard.dispatcher(context);
        var joystickLeft = Joystick.dispatcher(context);
        var joystickRight = Joystick.dispatcher(context);
        var display = Display.dispatcher();
        var cassette = CassetteRecorder.dispatcher(context);

        switch (ui) {
            case "terminal":
                context.aware(new InputStreamKeyboard(System.in, keyboard));
                display.dispatchTo(new Terminal(System.out));
                break;

            case "awt":
                var frameBuffer = new FrameBuffer();
                display.dispatchTo(frameBuffer);
                frameBuffer.addKeyListener(new AWTKeyboardDriver(keyboard, keyboardBuffer));
                frameBuffer.addKeyListener(new NumpadJoystickDriver(joystickLeft));
                UserInterface.show(frameBuffer, List.of(
                        Actions.reboot(() -> context.execute(() -> {
                            Signal.RESET.pulse();
                            Register.reset();
                            ram.clear();
                        })),
                        Actions.reset(() -> context.execute(Signal.RESET::pulse)),
                        SEPARATOR,
                        Actions.rewindCassette(cassette),
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

        Configuration.prepare(home);
        ColorComputer2.emulate(context, ram, display, keyboard, cassette, services, joystickLeft, joystickRight, script, playback, recording, home);
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
