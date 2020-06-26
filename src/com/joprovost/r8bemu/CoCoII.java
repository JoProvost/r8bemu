package com.joprovost.r8bemu;

import com.joprovost.r8bemu.audio.AudioSink;
import com.joprovost.r8bemu.audio.Speaker;
import com.joprovost.r8bemu.audio.TapePlayback;
import com.joprovost.r8bemu.audio.TapeRecorder;
import com.joprovost.r8bemu.clock.ClockFrequency;
import com.joprovost.r8bemu.clock.ClockGenerator;
import com.joprovost.r8bemu.data.Reference;
import com.joprovost.r8bemu.data.Size;
import com.joprovost.r8bemu.devices.MC6821;
import com.joprovost.r8bemu.devices.MC6847;
import com.joprovost.r8bemu.devices.MC6883;
import com.joprovost.r8bemu.devices.SC77526;
import com.joprovost.r8bemu.devices.keyboard.KeyboardAdapter;
import com.joprovost.r8bemu.devices.keyboard.KeyboardDispatcher;
import com.joprovost.r8bemu.mc6809.MC6809E;
import com.joprovost.r8bemu.mc6809.Signal;
import com.joprovost.r8bemu.memory.Demo;
import com.joprovost.r8bemu.memory.Memory;
import com.joprovost.r8bemu.memory.MemoryMapped;
import com.joprovost.r8bemu.memory.ReadOnly;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.joprovost.r8bemu.mc6809.MC6809E.FIRQ_VECTOR;
import static com.joprovost.r8bemu.mc6809.MC6809E.IRQ_VECTOR;
import static com.joprovost.r8bemu.mc6809.MC6809E.NMI_VECTOR;
import static com.joprovost.r8bemu.mc6809.MC6809E.RESET_VECTOR;

public class CoCoII {
    private static final String OS = System.getProperty("os.name");
    public static final boolean LINUX = OS.toLowerCase().contains("linux");

    public static void emulate(ClockGenerator clock,
                               Display display,
                               KeyboardDispatcher keyboard,
                               Path script,
                               Path playbackFile,
                               Path recordingFile,
                               String home) throws IOException {

        var uptime = clock.aware(new ClockFrequency(895, clock));

        var cs4 = new MC6821(Signal.IRQ, Signal.IRQ);
        keyboard.dispatchTo(clock.aware(new KeyboardAdapter(cs4)));
        var vdg = clock.aware(new MC6847(display, cs4.portA()::interrupt, cs4.portB()::interrupt));
        var cs5 = new MC6821(Signal.FIRQ, Signal.FIRQ);

        TapeRecorder recorder = new TapeRecorder(uptime, recordingFile);
        TapePlayback playback = new TapePlayback(uptime, playbackFile);
        Speaker speaker = new Speaker(new AudioFormat(44100, 8, 1, LINUX, false), uptime);
        Thread speakerThread = new Thread(speaker);

        cs5.portA().inputFrom(playback.output(0));
        cs5.portA().outputTo(new SC77526(AudioSink.broadcast(speaker.input(), recorder.input())));
        cs5.portA().controlTo(recorder.motor());
        cs5.portA().controlTo(playback.motor());

        var sam = MC6883.ofRam(new Memory(0xffff))
                        .withRom0(rom(home + "/rom/extbas.rom"))
                        .withRom1(rom(home + "/rom/bas.rom"))
                        .withDisplay(vdg)
                        .withCS4(cs4)
                        .withCS5(cs5)
                        .withCS6(new MC6821(Signal.none(), Signal.none()))
                        .withRom2(new Memory(0x1f));

        Debugger debugger = new Disassembler(Path.of(home, "doc/rom.asm"),
                                             Reference.of(sam, RESET_VECTOR, Size.WORD_16).value(),
                                             Reference.of(sam, IRQ_VECTOR, Size.WORD_16).value(),
                                             Reference.of(sam, FIRQ_VECTOR, Size.WORD_16).value(),
                                             Reference.of(sam, NMI_VECTOR, Size.WORD_16).value());

        MC6809E cpu = clock.aware(new MC6809E(sam, debugger, clock));
        cpu.reset();

        if (Files.exists(script)) keyboard.script(Files.readString(script));

        try {
            speakerThread.start();
            clock.run();
        } finally {
            speakerThread.interrupt();
        }
    }

    public static MemoryMapped rom(String rom) throws IOException {
        var path = Path.of(rom);
        if (Files.exists(path)) return ReadOnly.file(path);
        return new Demo();
    }
}
