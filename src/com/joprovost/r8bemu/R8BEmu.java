package com.joprovost.r8bemu;

import com.joprovost.r8bemu.clock.ClockGenerator;
import com.joprovost.r8bemu.devices.keyboard.Keyboard;
import com.joprovost.r8bemu.terminal.InputStreamKeyboard;
import com.joprovost.r8bemu.terminal.Terminal;
import com.joprovost.r8bemu.awt.AWTKeyboard;
import com.joprovost.r8bemu.awt.FrameBuffer;
import com.joprovost.r8bemu.awt.UserInterface;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class R8BEmu {

    public static void main(String[] args) throws IOException {
        var options = parse(args);
        var ui = options.getOrDefault("interface", "awt");
        var home = options.getOrDefault("home", ".");
        var script = Path.of(options.getOrDefault("script", home + "/script/autorun.bas"));
        var playback = Path.of(options.getOrDefault("playback", home + "/cassette/playback.wav"));
        var recording = Path.of(options.getOrDefault("recording", home + "/cassette/recording.wav"));

        var clock = new ClockGenerator();
        var keyboard = Keyboard.dispatcher();

        switch (ui) {
            case "terminal":
                clock.aware(new InputStreamKeyboard(System.in, keyboard));
                CoCoII.emulate(clock, new Terminal(System.out), keyboard, script, playback, recording, home);
                break;

            case "awt":
                var frameBuffer = new FrameBuffer();
                UserInterface.show(frameBuffer).addKeyListener(new AWTKeyboard(keyboard));
                CoCoII.emulate(clock, frameBuffer, keyboard, script, playback, recording, home);
                break;
        }
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
