package com.joprovost.r8bemu.coco;

import com.joprovost.r8bemu.Services;
import com.joprovost.r8bemu.clock.ClockFrequency;
import com.joprovost.r8bemu.clock.EmulatorContext;
import com.joprovost.r8bemu.coco.devices.JoystickConnector;
import com.joprovost.r8bemu.coco.devices.KeyboardAdapter;
import com.joprovost.r8bemu.coco.devices.MC6847;
import com.joprovost.r8bemu.coco.devices.SC77526;
import com.joprovost.r8bemu.data.analog.AnalogInput;
import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.data.discrete.Flag;
import com.joprovost.r8bemu.devices.MC6821Port;
import com.joprovost.r8bemu.devices.VideoTimer;
import com.joprovost.r8bemu.devices.mc6809.Debugger;
import com.joprovost.r8bemu.devices.mc6809.MC6809E;
import com.joprovost.r8bemu.devices.mc6809.Register;
import com.joprovost.r8bemu.devices.mc6809.Signal;
import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.devices.memory.BinaryReference;
import com.joprovost.r8bemu.devices.memory.Size;
import com.joprovost.r8bemu.io.CassetteRecorderDispatcher;
import com.joprovost.r8bemu.io.JoystickDispatcher;
import com.joprovost.r8bemu.io.KeyboardDispatcher;
import com.joprovost.r8bemu.io.sound.MixerDispatcher;
import com.joprovost.r8bemu.io.sound.Speaker;
import com.joprovost.r8bemu.io.sound.TapePlayback;
import com.joprovost.r8bemu.io.sound.TapeRecorder;

import javax.sound.sampled.AudioFormat;
import java.nio.file.Path;

import static com.joprovost.r8bemu.data.binary.BinaryPort.P0;
import static com.joprovost.r8bemu.data.binary.BinaryPort.P1;
import static com.joprovost.r8bemu.data.binary.BinaryPort.P2;
import static com.joprovost.r8bemu.data.binary.BinaryPort.P3;
import static com.joprovost.r8bemu.data.binary.BinaryPort.P4;
import static com.joprovost.r8bemu.data.binary.BinaryPort.P5;
import static com.joprovost.r8bemu.data.binary.BinaryPort.P6;
import static com.joprovost.r8bemu.data.binary.BinaryPort.P7;
import static com.joprovost.r8bemu.devices.mc6809.MC6809E.FIRQ_VECTOR;
import static com.joprovost.r8bemu.devices.mc6809.MC6809E.IRQ_VECTOR;
import static com.joprovost.r8bemu.devices.mc6809.MC6809E.NMI_VECTOR;
import static com.joprovost.r8bemu.devices.mc6809.MC6809E.RESET_VECTOR;

public class Hardware {

    public static void base(EmulatorContext context, ClockFrequency uptime, Services services, Addressable bus,
                            MC6821Port pia0a, MC6821Port pia0b, MC6821Port pia1a, MC6821Port pia1b,
                            KeyboardDispatcher keyboard, CassetteRecorderDispatcher cassette,
                            JoystickDispatcher jsLeft, JoystickDispatcher jsRight, MixerDispatcher mixer,
                            DiscreteOutput mute, Path recordingFile, Debugger debugger) {

        keyboard.dispatchTo(context.aware(new KeyboardAdapter(pia0a.port(), pia0b.port())));

        var playback = new TapePlayback(uptime);
        cassette.dispatchTo(playback);
        var recorder = new TapeRecorder(uptime, recordingFile);
        pia1a.port(P0).from(playback.output());
        pia1a.control().to(playback.motor());
        pia1a.control().to(recorder.motor());

        Speaker speaker = services.declare(new Speaker(new AudioFormat(44100, 16, 1, true, true), uptime, mute));
        mixer.dispatchTo(speaker);

        AnalogInput audio = AnalogInput.broadcast(speaker.input(), recorder.input());
        var sc77526 = new SC77526(audio);
        pia0a.port(P7).from(sc77526.cmp());
        pia1a.port(P7 | P6 | P5 | P4 | P3 | P2).to(sc77526.dac());
        pia0a.control().to(sc77526.selA());
        pia0b.control().to(sc77526.selB());
        pia1b.control().to(sc77526.sndEn());

        var left = new JoystickConnector(sc77526.joy(0), sc77526.joy(1));
        pia0a.port(P0).from(left.button());
        jsLeft.dispatchTo(left);

        var right = new JoystickConnector(sc77526.joy(2), sc77526.joy(3));
        pia0a.port(P1).from(right.button());
        jsRight.dispatchTo(right);

        // jumper64k
        pia1b.port(P2).from(pia0b.port(P6));

        debugger.label("RESET", BinaryReference.of(bus, RESET_VECTOR, Size.WORD_16).value());
        debugger.label("IRQ", BinaryReference.of(bus, IRQ_VECTOR, Size.WORD_16).value());
        debugger.label("FIRQ", BinaryReference.of(bus, FIRQ_VECTOR, Size.WORD_16).value());
        debugger.label("NMI", BinaryReference.of(bus, NMI_VECTOR, Size.WORD_16).value());

        var cpu = context.aware(new MC6809E(bus, debugger, context));
        cpu.reset().handle(Flag.value(true));

        Signal.RESET.to(cpu.reset());
        Signal.IRQ.to(cpu.irq());
        Signal.FIRQ.to(cpu.firq());
        Signal.NMI.to(cpu.nmi());
        Signal.HALT.to(cpu.halt());

        Signal.RESET.to(Signal.HALT); // clear HALT when reset is released
        context.onError(debugger::onError);
        context.onError(e -> System.err.println(Register.dump()));
        context.onError(e -> Signal.HALT.set());
    }

    public static void legacyVideo(MC6847 vdg, MC6821Port pia1b, VideoTimer videoTiming) {
        videoTiming.verticalSync().to(vdg.sync());
        videoTiming.horizontalSync().to(vdg.scan());
        pia1b.port().to(vdg.mode());
        Signal.RESET.to(vdg.reset());
    }
}
