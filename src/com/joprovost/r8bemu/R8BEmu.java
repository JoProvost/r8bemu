package com.joprovost.r8bemu;

import com.joprovost.r8bemu.clock.EmulatorContext;
import com.joprovost.r8bemu.data.BitOutput;
import com.joprovost.r8bemu.data.Flag;
import com.joprovost.r8bemu.io.CassetteRecorder;
import com.joprovost.r8bemu.io.DiskSlot;
import com.joprovost.r8bemu.io.DisplayPage;
import com.joprovost.r8bemu.io.Joystick;
import com.joprovost.r8bemu.io.Keyboard;
import com.joprovost.r8bemu.io.Screen;
import com.joprovost.r8bemu.io.awt.AWTKeyboardDriver;
import com.joprovost.r8bemu.io.awt.ActionIcon;
import com.joprovost.r8bemu.io.awt.Actions;
import com.joprovost.r8bemu.io.awt.ArrowsJoystickDriver;
import com.joprovost.r8bemu.io.awt.FrameBuffer;
import com.joprovost.r8bemu.io.awt.MouseJoystickDriver;
import com.joprovost.r8bemu.io.awt.NumpadJoystickDriver;
import com.joprovost.r8bemu.io.awt.UserInterface;
import com.joprovost.r8bemu.io.linux.LinuxJoystickDriver;
import com.joprovost.r8bemu.io.sound.Mixer;
import com.joprovost.r8bemu.io.terminal.InputStreamKeyboard;
import com.joprovost.r8bemu.io.terminal.Terminal;
import com.joprovost.r8bemu.mc6809.Signal;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.joprovost.r8bemu.io.awt.ActionIcon.HALT;
import static com.joprovost.r8bemu.io.awt.ActionIcon.KEYBOARD_BUFFER;
import static com.joprovost.r8bemu.io.awt.ActionIcon.KEYBOARD_DPAD_LEFT;
import static com.joprovost.r8bemu.io.awt.ActionIcon.KEYBOARD_DPAD_RIGHT;
import static com.joprovost.r8bemu.io.awt.ActionIcon.MOUSE;
import static com.joprovost.r8bemu.io.awt.ActionIcon.MUTE;
import static com.joprovost.r8bemu.io.awt.ActionIcon.RG6_BW;
import static com.joprovost.r8bemu.io.awt.UserInterface.SEPARATOR;

public class R8BEmu {

    public static void main(String[] args) throws IOException, URISyntaxException {
        Settings settings = Settings.parse(args);
        String ui = settings.string("interface", "awt");
        Path home = settings.path("home", System.getProperty("user.home") + "/.r8bemu");
        Path script = settings.path("script", home + "/autorun.bas");
        String scriptText = settings.string("script-text", null);
        Path playback = settings.path("playback", home + "/playback.wav");
        Path recording = settings.path("recording", home + "/recording.wav");
        Path disk = settings.path("disk", home + "/disk.dsk");
        Flag keyboardBuffer = settings.flag("keyboard-buffer", true);
        Flag dpadRight = settings.flag("dpad-right", false);
        Flag dpadLeft = settings.flag("dpad-left", false);
        Flag mouse = settings.flag("mouse", false);
        Flag disableRg6Color = settings.flag("disable-rg6-color", false);
        Flag disassembler = settings.flag("disassembler", false);
        Flag mute = settings.flag("mute", false);

        var context = new EmulatorContext();
        var keyboard = Keyboard.dispatcher(context);
        var joystickLeft = Joystick.dispatcher(context);
        var joystickRight = Joystick.dispatcher(context);
        var screen = Screen.dispatcher();
        var cassette = CassetteRecorder.dispatcher(context);
        var drive = DiskSlot.dispatcher(context);
        var mixer = Mixer.dispatcher(context);
        var displayPage = DisplayPage.dispatcher(context);

        if (Files.exists(playback)) cassette.insert(playback.toFile());
        if (Files.exists(disk)) drive.insert(disk.toFile());
        if (Files.exists(script)) keyboard.script(Files.readString(script));
        if (scriptText != null) keyboard.script(scriptText);

        switch (ui) {
            case "terminal":
                context.aware(new InputStreamKeyboard(System.in, keyboard));
                screen.dispatchTo(new Terminal(System.out));
                break;

            case "awt":
                var frameBuffer = new FrameBuffer();
                screen.dispatchTo(frameBuffer);

                frameBuffer.addKeyListener(new AWTKeyboardDriver(keyboard, keyboardBuffer, BitOutput.or(dpadLeft, dpadRight)));

                frameBuffer.addKeyListener(new NumpadJoystickDriver(joystickLeft, BitOutput.not(dpadRight)));
                frameBuffer.addKeyListener(new NumpadJoystickDriver(joystickRight, BitOutput.not(dpadLeft)));
                frameBuffer.addKeyListener(new ArrowsJoystickDriver(joystickLeft, dpadLeft));
                frameBuffer.addKeyListener(new ArrowsJoystickDriver(joystickRight, dpadRight));

                var mouseJoystickDriver = new MouseJoystickDriver(joystickLeft, mouse);
                frameBuffer.addMouseMotionListener(mouseJoystickDriver);
                frameBuffer.addMouseListener(mouseJoystickDriver);

                UserInterface.show(frameBuffer, List.of(
                        Actions.action(ActionIcon.REBOOT, context.aware(Signal.REBOOT)::pulse),
                        Actions.action(ActionIcon.RESET, context.aware(Signal.RESET)::pulse),
                        Actions.toggle(HALT, context.aware(Signal.HALT)),
                        SEPARATOR,
                        Actions.action(ActionIcon.CASSETTE_REWIND, cassette::rewind),
                        Actions.file(ActionIcon.CASSETTE, cassette::insert, home, new FileNameExtensionFilter("Audio file", "wav")),
                        SEPARATOR,
                        Actions.file(ActionIcon.DISK, drive::insert, home, new FileNameExtensionFilter("Disk image", "dsk")),
                        SEPARATOR,
                        Actions.toggle(KEYBOARD_BUFFER, keyboardBuffer),
                        Actions.toggle(KEYBOARD_DPAD_LEFT, dpadLeft),
                        Actions.toggle(KEYBOARD_DPAD_RIGHT, dpadRight),
                        Actions.toggle(MOUSE, mouse),
                        SEPARATOR,
                        Actions.toggle(RG6_BW, disableRg6Color),
                        Actions.action(ActionIcon.DISPLAY_PREVIOUS, ((DisplayPage) displayPage)::previous),
                        Actions.action(ActionIcon.DISPLAY_NEXT, ((DisplayPage) displayPage)::next),
                        SEPARATOR,
                        Actions.toggle(MUTE, mute),
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
        ColorComputer2.emulate(context, screen, disableRg6Color, displayPage, keyboard, cassette, drive, services,
                               joystickLeft, joystickRight, mixer, mute, recording, home, debugger);
    }

}
