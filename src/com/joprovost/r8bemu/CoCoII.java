package com.joprovost.r8bemu;

import com.joprovost.r8bemu.clock.ClockFrequency;
import com.joprovost.r8bemu.clock.ClockGenerator;
import com.joprovost.r8bemu.devices.Sound;
import com.joprovost.r8bemu.devices.MC6821;
import com.joprovost.r8bemu.devices.MC6847;
import com.joprovost.r8bemu.devices.MC6883;
import com.joprovost.r8bemu.devices.keyboard.KeyboardAdapter;
import com.joprovost.r8bemu.mc6809.MC6809E;
import com.joprovost.r8bemu.mc6809.Signal;
import com.joprovost.r8bemu.memory.Demo;
import com.joprovost.r8bemu.memory.Memory;
import com.joprovost.r8bemu.memory.MemoryMapped;
import com.joprovost.r8bemu.memory.ReadOnly;
import com.joprovost.r8bemu.terminal.Keyboard;
import com.joprovost.r8bemu.terminal.Terminal;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CoCoII {

    public static void main(String[] args) throws IOException, LineUnavailableException {
        var clock = new ClockGenerator();

        var uptime = clock.aware(new ClockFrequency(900, clock));

        var cs4 = new MC6821(Signal.IRQ, Signal.IRQ);
        var keyboard = clock.aware(new Keyboard(System.in, clock.aware(new KeyboardAdapter(cs4))));
        var display = new Terminal(System.out);
        var vdg = clock.aware(new MC6847(display, cs4.portA()::control, cs4.portB()::control));
        var cs5 = new MC6821(Signal.FIRQ, Signal.FIRQ);
        cs5.portA().consumer(clock.aware(new Sound(uptime)));

        var sam = MC6883.ofRam(new Memory(0x7fff))
                        .withRom0(rom("rom/extbas.rom"))
                        .withRom1(rom("rom/bas.rom"))
                        .withDisplay(vdg)
                        .withCS4(cs4)
                        .withCS5(cs5)
                        .withCS6(new MC6821(Signal.none(), Signal.none()))
                        .withRom2(new Memory(0x1f));

        Debugger debugger = Debugger.disassembler(Path.of("./doc/rom.asm"));

        MC6809E cpu = clock.aware(new MC6809E(sam, debugger, clock));
        cpu.reset();

        var path = Paths.get("./autorun.bas");
        if (Files.exists(path)) keyboard.script(path);

        clock.run();
    }

    public static MemoryMapped rom(String rom) throws IOException {
        var path = Path.of(rom);
        if (Files.exists(path)) return ReadOnly.file(path);
        return new Demo();
    }
}
