package com.joprovost.r8bemu;

import com.joprovost.r8bemu.clock.ClockFrequency;
import com.joprovost.r8bemu.clock.EmulatorContext;
import com.joprovost.r8bemu.data.MemoryDataReference;
import com.joprovost.r8bemu.data.Size;
import com.joprovost.r8bemu.data.link.ParallelInputProvider;
import com.joprovost.r8bemu.devices.KeyboardAdapter;
import com.joprovost.r8bemu.devices.MC6821;
import com.joprovost.r8bemu.devices.MC6821Port;
import com.joprovost.r8bemu.devices.MC6847;
import com.joprovost.r8bemu.devices.MC6883;
import com.joprovost.r8bemu.devices.SC77526;
import com.joprovost.r8bemu.devices.disk.DiskDrive;
import com.joprovost.r8bemu.devices.disk.FD197x;
import com.joprovost.r8bemu.io.AudioSink;
import com.joprovost.r8bemu.io.Button;
import com.joprovost.r8bemu.io.CassetteRecorderDispatcher;
import com.joprovost.r8bemu.io.Disk;
import com.joprovost.r8bemu.io.DiskSlotDispatcher;
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

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.joprovost.r8bemu.DemoROM.demo;
import static com.joprovost.r8bemu.data.link.ParallelPort.P0;
import static com.joprovost.r8bemu.data.link.ParallelPort.P1;
import static com.joprovost.r8bemu.data.link.ParallelPort.P2;
import static com.joprovost.r8bemu.data.link.ParallelPort.P3;
import static com.joprovost.r8bemu.data.link.ParallelPort.P4;
import static com.joprovost.r8bemu.data.link.ParallelPort.P5;
import static com.joprovost.r8bemu.data.link.ParallelPort.P6;
import static com.joprovost.r8bemu.data.link.ParallelPort.P7;
import static com.joprovost.r8bemu.mc6809.MC6809E.FIRQ_VECTOR;
import static com.joprovost.r8bemu.mc6809.MC6809E.IRQ_VECTOR;
import static com.joprovost.r8bemu.mc6809.MC6809E.NMI_VECTOR;
import static com.joprovost.r8bemu.mc6809.MC6809E.RESET_VECTOR;
import static com.joprovost.r8bemu.memory.AddressRange.range;

public class ColorComputer2 {
    public static void emulate(EmulatorContext context,
                               Memory ram,
                               Display display,
                               KeyboardDispatcher keyboard,
                               CassetteRecorderDispatcher cassette,
                               DiskSlotDispatcher slot,
                               Services services,
                               JoystickDispatcher joystickLeft,
                               JoystickDispatcher joystickRight,
                               Path script,
                               Path playbackFile,
                               Path recordingFile,
                               Path home) throws IOException {

        var uptime = context.aware(new ClockFrequency(900, context));

        var sam = new MC6883();
        var rom0 = rom(home.resolve("extbas11.rom"));
        var rom1 = rom(home.resolve("bas13.rom"));
        var cart = rom(home.resolve("disk11.rom"));
        var drive = new DiskDrive();
        var fd179x = context.aware(new FD197x(drive));
        fd179x.irq().to(Signal.NMI);

        drive.insert(Disk.blank());
        slot.dispatchTo(drive);

        var s4a = new MC6821Port(Signal.IRQ);
        var s4b = new MC6821Port(Signal.IRQ);
        var s5a = new MC6821Port(Signal.FIRQ);
        var s5b = new MC6821Port(Signal.FIRQ);

        var bus = MemoryDevice.bus(
                sam, // S7
                MemoryDevice.map(range(0x0000, 0x7fff), ram),
                MemoryDevice.map(range(0x8000, 0x9fff), rom0),  // S=1
                MemoryDevice.map(range(0xa000, 0xbfff), rom1),  // S=2
                MemoryDevice.map(range(0xc000, 0xfeff), cart),  // S=3
                MemoryDevice.map(range(0xff00, 0xff1f), new MC6821(s4a, s4b)),  // S=4
                MemoryDevice.map(range(0xff20, 0xff3f), new MC6821(s5a, s5b)),  // S=5
                MemoryDevice.map(range(0xff40, 0xff5f), fd179x),  // S=6
                MemoryDevice.map(range(0xffe0, 0xffff), rom1)   // S=2
        );

        var vdg = context.aware(new MC6847(display, s4a::interrupt, s4b::interrupt, sam.videoMemory(ram)));
        s5b.port().to(vdg.mode());
        Signal.RESET.to(vdg.reset());

        keyboard.dispatchTo(context.aware(new KeyboardAdapter(s4a.port(), s4b.port())));

        var playback = new TapePlayback(uptime, playbackFile);
        cassette.dispatchTo(playback);
        var recorder = new TapeRecorder(uptime, recordingFile);
        s5a.port().from(playback.output(P0));
        s5a.control().to(playback.motor());
        s5a.control().to(recorder.motor());

        var speaker = services.declare(new Speaker(new AudioFormat(44100, 16, 1, true, true), uptime));
        var sc77526 = new SC77526(AudioSink.broadcast(speaker.input(), recorder.input()));
        s5a.port().to(sc77526.dac(P7 | P6 | P5 | P4 | P3 | P2));
        s5b.control().to(sc77526.soundOutput());
        s4a.port().from(sc77526.joystick(P7));
        s4a.control().to(sc77526.selA());
        s4b.control().to(sc77526.selB());

        var leftButton = new PushButton();
        s4a.port().from(leftButton.clear(P0));
        joystickLeft.dispatchTo(Joystick.button(leftButton));
        joystickLeft.dispatchTo(sc77526.left());

        var rightButton = new PushButton();
        s4a.port().from(rightButton.clear(P1));
        joystickRight.dispatchTo(Joystick.button(rightButton));
        joystickRight.dispatchTo(sc77526.right());

        var debug = new Disassembler(home.resolve("disassembler.asm"));
        debug.label("RESET", MemoryDataReference.of(bus, RESET_VECTOR, Size.WORD_16).value());
        debug.label("IRQ", MemoryDataReference.of(bus, IRQ_VECTOR, Size.WORD_16).value());
        debug.label("FIRQ", MemoryDataReference.of(bus, FIRQ_VECTOR, Size.WORD_16).value());
        debug.label("NMI", MemoryDataReference.of(bus, NMI_VECTOR, Size.WORD_16).value());

        var cpu = context.aware(new MC6809E(bus, debug, context));
        Signal.RESET.to(cpu.reset());
        Signal.IRQ.to(cpu.irq());
        Signal.FIRQ.to(cpu.firq());
        Signal.NMI.to(cpu.nmi());
        Signal.HALT.to(cpu.halt());

        Signal.RESET.pulse();

        if (Files.exists(script)) keyboard.script(Files.readString(script));

        try {
            services.start();
            context.run();
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

        public ParallelInputProvider clear(int mask) {
            return input -> {
                if (pressed) input.clear(mask);
            };
        }
    }
}
