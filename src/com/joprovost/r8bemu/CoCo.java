package com.joprovost.r8bemu;

import com.joprovost.r8bemu.clock.EmulatorContext;
import com.joprovost.r8bemu.data.Flag;
import com.joprovost.r8bemu.data.MemoryDataReference;
import com.joprovost.r8bemu.data.Size;
import com.joprovost.r8bemu.data.link.PushButton;
import com.joprovost.r8bemu.devices.MC6821Port;
import com.joprovost.r8bemu.devices.SC77526;
import com.joprovost.r8bemu.io.Joystick;
import com.joprovost.r8bemu.io.JoystickDispatcher;
import com.joprovost.r8bemu.io.sound.TapePlayback;
import com.joprovost.r8bemu.io.sound.TapeRecorder;
import com.joprovost.r8bemu.mc6809.MC6809E;
import com.joprovost.r8bemu.mc6809.Signal;
import com.joprovost.r8bemu.memory.MemoryDevice;

import static com.joprovost.r8bemu.data.link.ParallelInputProvider.pin;
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

public class CoCo {
    public static void jumper64k(MC6821Port s4b, MC6821Port s5b) {
        s5b.port().from(pin(P2, s4b.port().output(P6)));
    }

    public static void joystick(JoystickDispatcher joystick, Joystick dac, MC6821Port s4a, int connector) {
        var leftButton = new PushButton();
        s4a.port().from(leftButton.clear(connector));
        joystick.dispatchTo(Joystick.button(leftButton));
        joystick.dispatchTo(dac);
    }

    public static void cpu(EmulatorContext context, Debugger debugger, MemoryDevice bus) {
        debugger.label("RESET", MemoryDataReference.of(bus, RESET_VECTOR, Size.WORD_16).value());
        debugger.label("IRQ", MemoryDataReference.of(bus, IRQ_VECTOR, Size.WORD_16).value());
        debugger.label("FIRQ", MemoryDataReference.of(bus, FIRQ_VECTOR, Size.WORD_16).value());
        debugger.label("NMI", MemoryDataReference.of(bus, NMI_VECTOR, Size.WORD_16).value());

        var cpu = context.aware(new MC6809E(bus, debugger, context));
        cpu.reset().handle(Flag.value(true));

        Signal.RESET.to(cpu.reset());
        Signal.IRQ.to(cpu.irq());
        Signal.FIRQ.to(cpu.firq());
        Signal.NMI.to(cpu.nmi());
        Signal.HALT.to(cpu.halt());
    }

    public static void dac(SC77526 sc77526, MC6821Port s4a, MC6821Port s4b, MC6821Port s5a, MC6821Port s5b) {
        s5a.port().to(sc77526.dac(P7 | P6 | P5 | P4 | P3 | P2));
        s5b.control().to(sc77526.soundOutput());
        s4a.port().from(sc77526.joystick(P7));
        s4a.control().to(sc77526.selA());
        s4b.control().to(sc77526.selB());
    }

    public static TapeRecorder tape(TapePlayback playback, TapeRecorder recorder, MC6821Port s5a) {
        s5a.port().from(playback.output(P0));
        s5a.control().to(playback.motor());
        s5a.control().to(recorder.motor());
        return recorder;
    }

    public static void joysticks(JoystickDispatcher joystickLeft, JoystickDispatcher joystickRight, MC6821Port s4a, SC77526 sc77526) {
        joystick(joystickLeft, sc77526.left(), s4a, P0);
        joystick(joystickRight, sc77526.right(), s4a, P1);
    }
}
