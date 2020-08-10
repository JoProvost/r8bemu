package com.joprovost.r8bemu;

import com.joprovost.r8bemu.clock.EmulatorContext;
import com.joprovost.r8bemu.data.Flag;
import com.joprovost.r8bemu.io.CassetteRecorder;
import com.joprovost.r8bemu.io.DiskSlot;
import com.joprovost.r8bemu.io.Display;
import com.joprovost.r8bemu.io.DisplayPage;
import com.joprovost.r8bemu.io.Joystick;
import com.joprovost.r8bemu.io.Keyboard;
import com.joprovost.r8bemu.io.awt.AWTKeyboardDriver;
import com.joprovost.r8bemu.io.awt.ArrowsJoystickDriver;
import com.joprovost.r8bemu.io.awt.FrameBuffer;
import com.joprovost.r8bemu.io.awt.MouseJoystickDriver;
import com.joprovost.r8bemu.io.awt.NumpadJoystickDriver;
import com.joprovost.r8bemu.io.awt.UserInterface;
import com.joprovost.r8bemu.io.linux.LinuxJoystickDriver;
import com.joprovost.r8bemu.io.sound.Mixer;
import com.joprovost.r8bemu.io.terminal.InputStreamKeyboard;
import com.joprovost.r8bemu.io.terminal.Terminal;
import com.joprovost.r8bemu.mc6809.Register;
import com.joprovost.r8bemu.mc6809.Signal;
import com.joprovost.r8bemu.memory.Memory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import static com.joprovost.r8bemu.io.awt.UserInterface.SEPARATOR;

public class R8BEmu {

    public static void main(String[] args) throws IOException, URISyntaxException {
        Settings settings = Settings.parse(args);
        String ui = settings.string("interface", "awt");
        Path home = settings.path("home", System.getProperty("user.home") + "/.r8bemu");
        Path script = settings.path("script", home + "/autorun.bas");
        Path playback = settings.path("playback", home + "/playback.wav");
        Path recording = settings.path("recording", home + "/recording.wav");
        Flag keyboardBuffer = settings.flag("keyboard-buffer", true);
        Flag keyboardGamepad = settings.flag("keyboard-gamepad", false);
        Flag mouse = settings.flag("mouse", false);
        Flag disableRg6Color = settings.flag("disable-rg6-color", false);
        Flag disassembler = settings.flag("disassembler", false);

        var context = new EmulatorContext();
        var ram = new Memory(0x7fff);
        var keyboard = Keyboard.dispatcher(context);
        var joystickLeft = Joystick.dispatcher(context);
        var joystickRight = Joystick.dispatcher(context);
        var display = Display.dispatcher();
        var cassette = CassetteRecorder.dispatcher(context);
        var drive = DiskSlot.dispatcher(context);
        var mixer = Mixer.dispatcher(context);
        var displayPage = DisplayPage.dispatcher(context);

        switch (ui) {
            case "terminal":
                context.aware(new InputStreamKeyboard(System.in, keyboard));
                display.dispatchTo(new Terminal(System.out));
                break;

            case "awt":
                var frameBuffer = new FrameBuffer();
                display.dispatchTo(frameBuffer);
                frameBuffer.addKeyListener(new AWTKeyboardDriver(keyboard, keyboardBuffer, keyboardGamepad));
                frameBuffer.addKeyListener(new NumpadJoystickDriver(joystickLeft));
                frameBuffer.addKeyListener(new ArrowsJoystickDriver(joystickLeft, keyboardGamepad));
                var mouseJoystickDriver = new MouseJoystickDriver(joystickLeft, mouse);
                frameBuffer.addMouseMotionListener(mouseJoystickDriver);
                frameBuffer.addMouseListener(mouseJoystickDriver);
                UserInterface.show(frameBuffer, List.of(
                        Actions.reboot(() -> context.execute(() -> {
                            Signal.RESET.pulse();
                            Register.reset();
                            ram.clear();
                        })),
                        Actions.reset(() -> context.execute(Signal.RESET::pulse)),
                        Actions.halt(Signal.HALT, context),
                        SEPARATOR,
                        Actions.rewindCassette(cassette),
                        Actions.insertCassette(home, cassette),
                        SEPARATOR,
                        Actions.insertDisk(home, drive),
                        SEPARATOR,
                        Actions.keyboardBuffered(keyboardBuffer),
                        Actions.keyboardGamepad(keyboardGamepad),
                        Actions.mouse(mouse),
                        SEPARATOR,
                        Actions.disableRg6Color(disableRg6Color),
                        Actions.previousPage(displayPage),
                        Actions.nextPage(displayPage),
                        SEPARATOR,
                        Actions.mute(mixer),
                        SEPARATOR,
                        Actions.presentation()
                ), mouse);
                break;
        }

        var services = new Threads();

        services.declare(new LinuxJoystickDriver(Path.of("/dev/input/js0"), joystickLeft));
        services.declare(new LinuxJoystickDriver(Path.of("/dev/input/js1"), joystickRight));

        Configuration.prepare(home);
        Debugger debugger = disassembler.isSet() ? new Disassembler(home.resolve("disassembler.asm")) : Debugger.none();
        ColorComputer2.emulate(context, ram, display, disableRg6Color, displayPage, keyboard, cassette, drive, services,
                               joystickLeft, joystickRight, mixer, script, playback, recording, home, debugger);
    }

}
