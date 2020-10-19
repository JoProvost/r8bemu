package com.joprovost.r8bemu.coco;

import com.joprovost.r8bemu.Services;
import com.joprovost.r8bemu.clock.ClockFrequency;
import com.joprovost.r8bemu.clock.EmulatorContext;
import com.joprovost.r8bemu.coco.devices.MC6847;
import com.joprovost.r8bemu.coco.devices.gime.ColorPalette;
import com.joprovost.r8bemu.coco.devices.gime.CompositeDetection;
import com.joprovost.r8bemu.coco.devices.gime.DisplayProcessor;
import com.joprovost.r8bemu.coco.devices.gime.MMU;
import com.joprovost.r8bemu.coco.font.HighResFont;
import com.joprovost.r8bemu.coco.font.LegacyFont3;
import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.data.discrete.DiscretePort;
import com.joprovost.r8bemu.devices.DiskDrive;
import com.joprovost.r8bemu.devices.MC6821;
import com.joprovost.r8bemu.devices.MC6821Port;
import com.joprovost.r8bemu.devices.VideoTimer;
import com.joprovost.r8bemu.devices.mc6809.Debugger;
import com.joprovost.r8bemu.devices.mc6809.Register;
import com.joprovost.r8bemu.devices.mc6809.Signal;
import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.devices.memory.Memory;
import com.joprovost.r8bemu.graphic.Screen;
import com.joprovost.r8bemu.io.CassetteRecorderDispatcher;
import com.joprovost.r8bemu.io.JoystickDispatcher;
import com.joprovost.r8bemu.io.KeyboardDispatcher;
import com.joprovost.r8bemu.io.sound.MixerDispatcher;
import com.joprovost.r8bemu.storage.DiskSlotDispatcher;

import java.nio.file.Path;

import static com.joprovost.r8bemu.coco.DemoROM.demo;
import static com.joprovost.r8bemu.devices.memory.Addressable.bus;
import static com.joprovost.r8bemu.devices.memory.Addressable.none;
import static com.joprovost.r8bemu.devices.memory.Addressable.readOnly;
import static com.joprovost.r8bemu.devices.memory.Addressable.rom;
import static com.joprovost.r8bemu.devices.memory.Addressable.select;
import static com.joprovost.r8bemu.devices.memory.Addressable.when;

public class CoCo3 {
    public static void emulate(EmulatorContext context,
                               Screen screen,
                               DiscretePort composite,
                               KeyboardDispatcher keyboard,
                               CassetteRecorderDispatcher cassette,
                               DiskSlotDispatcher slot,
                               Services services,
                               JoystickDispatcher left,
                               JoystickDispatcher right,
                               MixerDispatcher mixer,
                               DiscreteOutput mute,
                               Path recordingFile,
                               Path home,
                               Debugger debugger) {

        var uptime = context.aware(new ClockFrequency(900, context));

        Memory ram = new Memory(0x7ffff);

        var rom = rom(home.resolve("coco3.rom"))
                .or(() -> rom(home.resolve("BASIC3.ROM")))
                .orElse(demo());
        var cart = rom(home.resolve("disk12.rom"))
                .or(() -> rom(home.resolve("disk11.rom")))
                .orElse(none());
        var drive = context.aware(new DiskDrive());
        drive.irq().to(Signal.NMI);
        slot.dispatchTo(drive);

        var pia0a = new MC6821Port(Signal.IRQ);
        var pia0b = new MC6821Port(Signal.IRQ);
        var pia1a = new MC6821Port(Signal.FIRQ);
        var pia1b = new MC6821Port(Signal.FIRQ);

        var videoTiming = context.aware(new VideoTimer());
        videoTiming.horizontalSync().to(pia0a.interrupt());
        videoTiming.verticalSync().to(pia0b.interrupt());

        MMU mmu = new MMU(ram);

        ColorPalette palette = new ColorPalette(StandardColors.select(composite), new CompositeDetection(composite));
        DisplayProcessor displayProcessor = new DisplayProcessor(screen, mmu.video(), new HighResFont(), palette, DiscreteOutput.not(mmu.legacy()));
        videoTiming.verticalSync().to(displayProcessor.sync());
        videoTiming.horizontalSync().to(displayProcessor.scan());

        Addressable bus = bus(
                mmu,
                displayProcessor,
                palette,
                when(mmu.rom(), readOnly(rom)),
                when(mmu.cts(), readOnly(cart)),
                when(mmu.pia(),
                     select(0x00, 0x20, new MC6821(pia0a, pia0b)),
                     select(0x20, 0x20, new MC6821(pia1a, pia1b))),
                when(mmu.scs(), drive));

        Hardware.legacyVideo(
                new MC6847(screen, mmu.lowResVideo(), composite, new LegacyFont3(), mmu.legacy(), palette),
                pia1b, videoTiming);

        Hardware.base(context, uptime, services, bus, pia0a, pia0b, pia1a, pia1b, keyboard, cassette,
                      left, right, mixer, mute, recordingFile, debugger);

        Signal.REBOOT.to(line -> {
            if (line.isClear()) return;
            Signal.RESET.pulse();
            Register.reset();
            ram.clear();
            mmu.clear();
        });

        try {
            services.start();
            context.run();
        } finally {
            services.stop();
        }
    }
}
