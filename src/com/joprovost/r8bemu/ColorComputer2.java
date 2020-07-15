package com.joprovost.r8bemu;

import com.joprovost.r8bemu.clock.ClockFrequency;
import com.joprovost.r8bemu.clock.ClockGenerator;
import com.joprovost.r8bemu.devices.KeyboardAdapter;
import com.joprovost.r8bemu.devices.MC6821;
import com.joprovost.r8bemu.devices.MC6847;
import com.joprovost.r8bemu.devices.MC6883;
import com.joprovost.r8bemu.devices.SC77526;
import com.joprovost.r8bemu.io.AudioSink;
import com.joprovost.r8bemu.io.Button;
import com.joprovost.r8bemu.io.CassetteRecorderDispatcher;
import com.joprovost.r8bemu.io.Display;
import com.joprovost.r8bemu.io.Joystick;
import com.joprovost.r8bemu.io.JoystickDispatcher;
import com.joprovost.r8bemu.io.KeyboardDispatcher;
import com.joprovost.r8bemu.io.sound.Speaker;
import com.joprovost.r8bemu.io.sound.TapePlayback;
import com.joprovost.r8bemu.io.sound.TapeRecorder;
import com.joprovost.r8bemu.mc6809.MC6809E;
import com.joprovost.r8bemu.mc6809.Signal;
import com.joprovost.r8bemu.memory.Memory;
import com.joprovost.r8bemu.memory.MemoryDevice;
import com.joprovost.r8bemu.port.DataInputProvider;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.joprovost.r8bemu.DemoROM.demo;
import static com.joprovost.r8bemu.memory.AddressRange.range;
import static com.joprovost.r8bemu.port.DataPort.P0;
import static com.joprovost.r8bemu.port.DataPort.P1;
import static com.joprovost.r8bemu.port.DataPort.P2;
import static com.joprovost.r8bemu.port.DataPort.P3;
import static com.joprovost.r8bemu.port.DataPort.P4;
import static com.joprovost.r8bemu.port.DataPort.P5;
import static com.joprovost.r8bemu.port.DataPort.P6;
import static com.joprovost.r8bemu.port.DataPort.P7;

public class ColorComputer2 {
    public static void emulate(Services services,
                               ClockGenerator clock,
                               Display display,
                               KeyboardDispatcher keyboard,
                               JoystickDispatcher joystickLeft,
                               JoystickDispatcher joystickRight,
                               CassetteRecorderDispatcher cassette,
                               Path script,
                               Path playbackFile,
                               Path recordingFile,
                               Path home) throws IOException {

        var uptime = clock.aware(new ClockFrequency(900, clock));

        var ram = new Memory(0x7fff);
        var rom0 = rom(home.resolve("extbas11.rom"));
        var rom1 = rom(home.resolve("bas13.rom"));
        var pia0 = new MC6821(Signal.IRQ, Signal.IRQ);
        var pia1 = new MC6821(Signal.FIRQ, Signal.FIRQ);
        var pia2 = new MC6821(Signal.none(), Signal.none());
        var sam = new MC6883();

        var bus = MemoryDevice.bus(
                sam, // S7
                MemoryDevice.map(range(0xffe0, 0xffff), rom1),  // S=2
                MemoryDevice.map(range(0xff40, 0xff5f), pia2),  // S=6
                MemoryDevice.map(range(0xff20, 0xff3f), pia1),  // S=5
                MemoryDevice.map(range(0xff00, 0xff1f), pia0),  // S=4
                //MemoryDevice.map(range(0xc000, 0xfeff), rom2),  // S=3
                MemoryDevice.map(range(0xa000, 0xbfff), rom1),  // S=2
                MemoryDevice.map(range(0x8000, 0x9fff), rom0),  // S=1
                MemoryDevice.map(range(0x0000, 0x7fff), ram)
        );

        keyboard.dispatchTo(clock.aware(new KeyboardAdapter(pia0)));

        var playback = new TapePlayback(uptime, playbackFile);
        cassette.dispatchTo(playback);
        var recorder = new TapeRecorder(uptime, recordingFile);
        pia1.portA().inputFrom(playback.output(P0));
        pia1.portA().controlTo(playback.motor());
        pia1.portA().controlTo(recorder.motor());

        var speaker = services.declare(new Speaker(new AudioFormat(44100, 8, 1, true, false), uptime));
        var sc77526 = new SC77526(AudioSink.broadcast(speaker.input(), recorder.input()));
        pia1.portA().outputTo(sc77526.dac(P7 | P6 | P5 | P4 | P3 | P2));
        pia1.portB().controlTo(sc77526.soundOutput());
        pia0.portA().inputFrom(sc77526.joystick(P7));
        pia0.portA().controlTo(sc77526.selA());
        pia0.portB().controlTo(sc77526.selB());

        var leftButton = new PushButton();
        pia0.portA().inputFrom(leftButton.clear(P0));
        joystickLeft.dispatchTo(Joystick.button(leftButton));
        joystickLeft.dispatchTo(sc77526.left());

        var rightButton = new PushButton();
        pia0.portA().inputFrom(rightButton.clear(P1));
        joystickRight.dispatchTo(Joystick.button(rightButton));
        joystickRight.dispatchTo(sc77526.right());

        var vdg = clock.aware(new MC6847(display, pia0.portA()::interrupt, pia0.portB()::interrupt, sam.videoMemory(ram)));
        pia1.portB().outputTo(vdg.mode());

        clock.aware(new MC6809E(bus, Debugger.none(), clock));
        Signal.RESET.set();

        if (Files.exists(script)) keyboard.script(Files.readString(script));

        try {
            services.start();
            clock.run();
        } finally {
            services.stop();
        }
    }

    public static MemoryDevice rom(Path path) throws IOException {
        if (Files.exists(path)) return MemoryDevice.readOnly(Memory.file(path));
        return demo();
    }

    private static class PushButton implements Button {
        boolean pressed = false;

        @Override
        public void press() {
            pressed = true;
        }

        @Override
        public void release() {
            pressed = false;
        }

        public DataInputProvider clear(int mask) {
            return input -> {
                if (pressed) input.clear(mask);
            };
        }
    }
}
