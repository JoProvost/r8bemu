package com.joprovost.r8bemu;

import com.joprovost.r8bemu.clock.EmulatorContext;
import com.joprovost.r8bemu.coco.CoCo2;
import com.joprovost.r8bemu.coco.CoCo3;
import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.data.discrete.Flag;
import com.joprovost.r8bemu.devices.mc6809.Debugger;
import com.joprovost.r8bemu.devices.mc6809.Signal;
import com.joprovost.r8bemu.graphic.Screen;
import com.joprovost.r8bemu.io.CassetteRecorder;
import com.joprovost.r8bemu.io.JoystickInput;
import com.joprovost.r8bemu.io.Keyboard;
import com.joprovost.r8bemu.io.awt.AWTKeyboardDriver;
import com.joprovost.r8bemu.io.awt.ActionIcon;
import com.joprovost.r8bemu.io.awt.Actions;
import com.joprovost.r8bemu.io.awt.ArrowsJoystickDriver;
import com.joprovost.r8bemu.io.awt.Display;
import com.joprovost.r8bemu.io.awt.MouseJoystickDriver;
import com.joprovost.r8bemu.io.awt.NumpadJoystickDriver;
import com.joprovost.r8bemu.io.awt.UserInterface;
import com.joprovost.r8bemu.io.linux.LinuxJoystickDriver;
import com.joprovost.r8bemu.io.sound.Mixer;
import com.joprovost.r8bemu.io.terminal.InputStreamKeyboard;
import com.joprovost.r8bemu.io.terminal.Terminal;
import com.joprovost.r8bemu.storage.DiskSlot;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.joprovost.r8bemu.io.awt.ActionIcon.COMPOSITE;
import static com.joprovost.r8bemu.io.awt.ActionIcon.HALT;
import static com.joprovost.r8bemu.io.awt.ActionIcon.KEYBOARD_BUFFER;
import static com.joprovost.r8bemu.io.awt.ActionIcon.KEYBOARD_DPAD_LEFT;
import static com.joprovost.r8bemu.io.awt.ActionIcon.MOUSE;
import static com.joprovost.r8bemu.io.awt.ActionIcon.MUTE;
import static com.joprovost.r8bemu.io.awt.UserInterface.SEPARATOR;

public class R8BEmu {

    public static void main(String[] args) throws IOException, URISyntaxException {
        Settings settings = Settings.parse(args);
        Flag coco2 = settings.flag("coco2", true, "Use Color Computer Model 2 hardware emulation");
        Flag coco3 = settings.flag("coco3", coco2.isClear(), "Use Color Computer Model 3 hardware emulation");

        Flag terminalGraphic = settings.flag("terminal-graphic", false, "Activate the terminal interface in graphic mode");
        Flag terminal = settings.flag("terminal", terminalGraphic.isSet(), "Activate the terminal interface in text mode");
        Flag window = settings.flag("window", terminal.isClear(), "Activate the windowed graphic interface");
        Flag composite = settings.flag("composite", true, "Composite blue/red color emulation");

        Path home = settings.path("home", System.getProperty("user.home") + "/.r8bemu", "Home directory (location of ROM files)");
        Path disk = settings.path("disk", home + "/disk.dsk", "Insert the diskette image file in drive 0");
        Path playback = settings.path("playback", home + "/playback.wav", "Define the audio file used for playback");
        Path recording = settings.path("recording", home + "/recording.wav", "Define the audio file used for recording");
        Path script = settings.path("script", home + "/autorun.bas", "Load a script file at boot");
        String scriptText = settings.string("script-text", null, "Type the following keys at boot");
        Flag mouse = settings.flag("mouse", false, "Use the mouse as the left joystick");
        Flag dpadLeft = settings.flag("dpad-left", false, "Use the keyboard arrow keys as the left joystick");
        Flag dpadRight = settings.flag("dpad-right", false, "Use the keyboard arrow keys as the right joystick");
        Flag keyboardBuffer = settings.flag("keyboard-buffer", true, "Enable keyboard input buffering");
        Flag disassembler = settings.flag("disassembler", false, "Enable the disassembler");
        Flag mute = settings.flag("mute", false, "Mute the speaker");

        int width = settings.integer("window-width", 640, "Width of the windowed graphic interface");
        int height = settings.integer("window-height", 450, "Height of the windowed graphic interface");

        if (settings.flag("help", false, "Show help").isSet()) {
            settings.help();
            return;
        }

        var context = new EmulatorContext();
        var keyboard = Keyboard.dispatcher(context);
        var joystickLeft = JoystickInput.dispatcher(context);
        var joystickRight = JoystickInput.dispatcher(context);
        var screen = Screen.dispatcher();
        var cassette = CassetteRecorder.dispatcher(context);
        var drive = DiskSlot.dispatcher(context);
        var mixer = Mixer.dispatcher(context);

        if (Files.exists(playback)) cassette.insert(playback.toFile());
        if (Files.exists(disk)) drive.insert(disk.toFile());
        if (Files.exists(script)) keyboard.script(Files.readString(script));
        if (scriptText != null) keyboard.script(scriptText);

        if (terminal.isSet()) {
            context.aware(new InputStreamKeyboard(System.in, keyboard));
            screen.dispatchTo(new Terminal(System.out, terminalGraphic));
        }

        if (window.isSet()) {
            var display = new Display(new Dimension(width, height));
            screen.dispatchTo(display.screen());

            display.addKeyListener(new AWTKeyboardDriver(keyboard, keyboardBuffer, DiscreteOutput.or(dpadLeft, dpadRight)));
            display.addKeyListener(new NumpadJoystickDriver(joystickLeft, DiscreteOutput.not(dpadRight)));
            display.addKeyListener(new NumpadJoystickDriver(joystickRight, DiscreteOutput.not(dpadLeft)));
            display.addKeyListener(new ArrowsJoystickDriver(joystickLeft, dpadLeft));
            display.addKeyListener(new ArrowsJoystickDriver(joystickRight, dpadRight));

            var mouseJoystickDriver = new MouseJoystickDriver(joystickLeft, mouse);
            display.addMouseMotionListener(mouseJoystickDriver);
            display.addMouseListener(mouseJoystickDriver);

            var mouseRightJoystickDriver = new MouseJoystickDriver(joystickRight, mouse);
            display.addMouseMotionListener(mouseRightJoystickDriver);
            display.addMouseListener(mouseRightJoystickDriver);

            UserInterface.show(display, List.of(
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
                    Actions.toggle(MOUSE, mouse),
                    SEPARATOR,
                    Actions.toggle(COMPOSITE, composite),
                    SEPARATOR,
                    Actions.toggle(MUTE, mute),
                    SEPARATOR,
                    Actions.presentation()
            ), mouse);
        }

        var services = new Threads();

        services.declare(new LinuxJoystickDriver(Path.of("/dev/input/js0"), joystickLeft));
        services.declare(new LinuxJoystickDriver(Path.of("/dev/input/js1"), joystickRight));

        Configuration.prepare(home);
        Debugger debugger = disassembler.isSet() ? new Disassembler(home.resolve("disassembler.asm")) : Debugger.none();

        if (coco3.isSet())
            CoCo3.emulate(context, screen, composite, keyboard, cassette, drive, services,
                          joystickLeft, joystickRight, mixer, mute, recording, home, debugger);
        else
            CoCo2.emulate(context, screen, composite, keyboard, cassette, drive, services,
                          joystickLeft, joystickRight, mixer, mute, recording, home, debugger);
    }
}
