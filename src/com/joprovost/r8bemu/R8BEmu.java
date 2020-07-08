package com.joprovost.r8bemu;

import com.joprovost.r8bemu.io.Button;
import com.joprovost.r8bemu.io.awt.NumpadJoystick;
import com.joprovost.r8bemu.clock.ClockGenerator;
import com.joprovost.r8bemu.io.Joystick;
import com.joprovost.r8bemu.io.Keyboard;
import com.joprovost.r8bemu.io.Display;
import com.joprovost.r8bemu.mc6809.Signal;
import com.joprovost.r8bemu.io.terminal.InputStreamKeyboard;
import com.joprovost.r8bemu.io.terminal.Terminal;
import com.joprovost.r8bemu.io.awt.AWTKeyboard;
import com.joprovost.r8bemu.io.awt.FrameBuffer;
import com.joprovost.r8bemu.io.awt.UserInterface;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
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
        var joystick = Joystick.dispatcher();
        var button = Button.dispatcher();
        var display = Display.dispatcher();

        switch (ui) {
            case "terminal":
                clock.aware(new InputStreamKeyboard(System.in, keyboard));
                display.dispatchTo(new Terminal(System.out));
                break;

            case "awt":
                var frameBuffer = new FrameBuffer();
                display.dispatchTo(frameBuffer);
                frameBuffer.addKeyListener(new AWTKeyboard(keyboard));
                frameBuffer.addKeyListener(new NumpadJoystick(joystick, button));
                UserInterface.show(frameBuffer, List.of(new AbstractAction("Reset") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Signal.RESET.set();
                    }
                }));
                break;
        }

        CoCoII.emulate(clock, display, keyboard, joystick, button, script, playback, recording, home);
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
