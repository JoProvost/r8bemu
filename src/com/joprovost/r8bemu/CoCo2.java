package com.joprovost.r8bemu;

import com.joprovost.r8bemu.clock.ClockFrequency;
import com.joprovost.r8bemu.clock.EmulatorContext;
import com.joprovost.r8bemu.data.BitOutput;
import com.joprovost.r8bemu.data.Flag;
import com.joprovost.r8bemu.devices.DiskDrive;
import com.joprovost.r8bemu.devices.KeyboardAdapter;
import com.joprovost.r8bemu.devices.MC6821;
import com.joprovost.r8bemu.devices.MC6821Port;
import com.joprovost.r8bemu.devices.MC6847;
import com.joprovost.r8bemu.devices.SC77526;
import com.joprovost.r8bemu.devices.sam.MC6883;
import com.joprovost.r8bemu.devices.sam.VideoMemory;
import com.joprovost.r8bemu.font.Model2Font;
import com.joprovost.r8bemu.io.AudioSink;
import com.joprovost.r8bemu.io.CassetteRecorderDispatcher;
import com.joprovost.r8bemu.io.DiskSlotDispatcher;
import com.joprovost.r8bemu.io.DisplayPageDispatcher;
import com.joprovost.r8bemu.io.JoystickDispatcher;
import com.joprovost.r8bemu.io.KeyboardDispatcher;
import com.joprovost.r8bemu.io.Screen;
import com.joprovost.r8bemu.io.sound.MixerDispatcher;
import com.joprovost.r8bemu.io.sound.Speaker;
import com.joprovost.r8bemu.io.sound.TapePlayback;
import com.joprovost.r8bemu.io.sound.TapeRecorder;
import com.joprovost.r8bemu.mc6809.Register;
import com.joprovost.r8bemu.mc6809.Signal;
import com.joprovost.r8bemu.memory.Memory;
import com.joprovost.r8bemu.memory.MemoryDevice;

import javax.sound.sampled.AudioFormat;
import java.nio.file.Path;

import static com.joprovost.r8bemu.memory.MemoryDevice.none;
import static com.joprovost.r8bemu.memory.MemoryDevice.rom;

public class CoCo2 {
    public static void emulate(EmulatorContext context,
                               Screen screen,
                               Flag disableRg6Color,
                               DisplayPageDispatcher displayPage,
                               KeyboardDispatcher keyboard,
                               CassetteRecorderDispatcher cassette,
                               DiskSlotDispatcher slot,
                               Services services,
                               JoystickDispatcher joystickLeft,
                               JoystickDispatcher joystickRight,
                               MixerDispatcher mixer,
                               BitOutput mute,
                               Path recordingFile,
                               Path home,
                               Debugger debugger) {

        var uptime = context.aware(new ClockFrequency(900, context));

        Memory ram = new Memory(0xffff);

        var sam = new MC6883(ram);
        Signal.RESET.to(sam.reset());
        var rom0 = rom(home.resolve("extbas11.rom"))
                .or(() -> rom(home.resolve("EXTBASIC.ROM")))
                .orElse(none());
        var rom1 = rom(home.resolve("bas13.rom"))
                .or(() -> rom(home.resolve("BASIC.ROM")))
                .orElse(DemoROM.demo());
        var cart = rom(home.resolve("disk12.rom"))
                .or(() -> rom(home.resolve("disk11.rom")))
                .or(() -> rom(home.resolve("DSKBASIC.ROM")))
                .orElse(none());
        var drive = context.aware(new DiskDrive());
        drive.irq().to(Signal.NMI);
        slot.dispatchTo(drive);

        var s4a = new MC6821Port(Signal.IRQ);
        var s4b = new MC6821Port(Signal.IRQ);
        var s5a = new MC6821Port(Signal.FIRQ);
        var s5b = new MC6821Port(Signal.FIRQ);

        var bus = MemoryDevice.bus(
                sam, // S7
                MemoryDevice.when(sam.select(1), rom0),  // S=1
                MemoryDevice.when(sam.select(2), rom1),  // S=2
                MemoryDevice.when(sam.select(3), cart),  // S=3
                MemoryDevice.when(sam.select(4), new MC6821(s4a, s4b)),  // S=4
                MemoryDevice.when(sam.select(5), new MC6821(s5a, s5b)),  // S=5
                MemoryDevice.when(sam.select(6), drive)  // S=6
        );

        VideoMemory video = sam.video();
        displayPage.dispatchTo(video);

        var vdg = context.aware(new MC6847(screen, s4a::interrupt, s4b::interrupt, video, disableRg6Color, new Model2Font()));
        s5b.port().to(vdg.mode());
        Signal.RESET.to(vdg.reset());

        keyboard.dispatchTo(context.aware(new KeyboardAdapter(s4a.port(), s4b.port())));

        var playback = new TapePlayback(uptime);
        cassette.dispatchTo(playback);
        var recorder = new TapeRecorder(uptime, recordingFile);
        CoCo.tape(playback, recorder, s5a);

        Speaker speaker = services.declare(new Speaker(new AudioFormat(44100, 16, 1, true, true), uptime, mute));
        mixer.dispatchTo(speaker);

        var sc77526 = new SC77526(AudioSink.broadcast(speaker.input(), recorder.input()));
        CoCo.dac(sc77526, s4a, s4b, s5a, s5b);
        CoCo.joysticks(joystickLeft, joystickRight, s4a, sc77526);
        CoCo.jumper64k(s4b, s5b);

        CoCo.cpu(context, debugger, bus);

        Signal.RESET.to(Signal.HALT); // clear HALT when reset is released
        context.onError(Throwable::printStackTrace);
        context.onError(e -> System.err.println(Register.dump()));
        context.onError(e -> Signal.HALT.set());

        Signal.REBOOT.to(line -> {
            if (line.isClear()) return;
            Signal.RESET.pulse();
            Register.reset();
            ram.clear();
        });

        try {
            services.start();
            context.run();
        } finally {
            services.stop();
        }
    }

}
