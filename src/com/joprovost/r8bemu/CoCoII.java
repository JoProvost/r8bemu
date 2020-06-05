package com.joprovost.r8bemu;

import com.joprovost.r8bemu.clock.ClockFrequency;
import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.devices.MC6821;
import com.joprovost.r8bemu.devices.MC6847;
import com.joprovost.r8bemu.devices.MC6883;
import com.joprovost.r8bemu.devices.keyboard.Keyboard;
import com.joprovost.r8bemu.devices.keyboard.KeyboardBuffer;
import com.joprovost.r8bemu.mc6809.MC6809E;
import com.joprovost.r8bemu.memory.Demo;
import com.joprovost.r8bemu.memory.Memory;
import com.joprovost.r8bemu.memory.MemoryMapped;
import com.joprovost.r8bemu.memory.ReadOnly;
import com.joprovost.r8bemu.terminal.Terminal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CoCoII {

    public static void main(String[] args) throws IOException {
        var clock = new Clock();

        clock.aware(new ClockFrequency(900));

        var cs4 = new MC6821();
        var keyboard = clock.aware(new Keyboard(cs4));
        var buffer = clock.aware(new KeyboardBuffer(keyboard));
        var terminal = clock.aware(new Terminal(System.in, System.out, buffer));

        var sam = MC6883.ofRam(new Memory(0x7fff))
                        .withRom0(rom("rom/extbas.rom"))
                        .withRom1(rom("rom/bas.rom"))
                        .withDisplay(new MC6847(terminal))
                        .withCS4(cs4)
                        .withCS5(new MC6821())
                        .withCS6(new MC6821())
                        .withRom2(new Memory(0x1f));

        Debugger debugger = Debugger.disassembler(Path.of("./doc/rom.asm"));

        MC6809E cpu = clock.aware(new MC6809E(sam, debugger, clock));
        cpu.reset();

        var path = Paths.get("./autorun.bas");
        if (Files.exists(path)) buffer.script(path);

        clock.run();
    }

    public static MemoryMapped rom(String rom) throws IOException {
        var path = Path.of(rom);
        if (Files.exists(path)) return ReadOnly.file(path);
        return new Demo();
    }
}
