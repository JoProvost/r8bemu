package com.joprovost.r8bemu.coco;

import com.joprovost.r8bemu.Configuration;
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
import com.joprovost.r8bemu.devices.DiskController;
import com.joprovost.r8bemu.devices.DiskDrive;
import com.joprovost.r8bemu.devices.MC6821;
import com.joprovost.r8bemu.devices.MC6821Port;
import com.joprovost.r8bemu.devices.VideoTimer;
import com.joprovost.r8bemu.devices.mc6809.Debugger;
import com.joprovost.r8bemu.devices.mc6809.Register;
import com.joprovost.r8bemu.devices.mc6809.Signal;
import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.devices.memory.Memory;
import com.joprovost.r8bemu.graphic.Colors;
import com.joprovost.r8bemu.graphic.Screen;
import com.joprovost.r8bemu.io.CassetteRecorderDispatcher;
import com.joprovost.r8bemu.io.JoystickDispatcher;
import com.joprovost.r8bemu.io.KeyboardDispatcher;
import com.joprovost.r8bemu.io.sound.MixerDispatcher;
import com.joprovost.r8bemu.storage.DiskSlotDispatcher;

import java.nio.file.Path;

import static com.joprovost.r8bemu.Configuration.rom;
import static com.joprovost.r8bemu.coco.DemoROM.demo;
import static com.joprovost.r8bemu.devices.memory.Addressable.bus;
import static com.joprovost.r8bemu.devices.memory.Addressable.none;
import static com.joprovost.r8bemu.devices.memory.Addressable.readOnly;
import static com.joprovost.r8bemu.devices.memory.Addressable.select;
import static com.joprovost.r8bemu.devices.memory.Addressable.when;

public class CoCo3 {
    public static boolean isConfigured(Path home) {
        return Configuration.file(home, "coco3.rom", "BASIC3.ROM").isPresent();
    }

    public static void emulate(EmulatorContext context,
                               Screen screen,
                               DiscretePort composite,
                               KeyboardDispatcher keyboard,
                               CassetteRecorderDispatcher cassette,
                               DiskSlotDispatcher drive0,
                               DiskSlotDispatcher drive1,
                               DiskSlotDispatcher drive2,
                               DiskSlotDispatcher drive3,
                               Services services,
                               JoystickDispatcher left,
                               JoystickDispatcher right,
                               MixerDispatcher mixer,
                               DiscreteOutput mute,
                               Path recordingFile,
                               Path home,
                               Debugger debugger) {

        ClockFrequency uptime = context.aware(new ClockFrequency(1780, context));

        Memory ram = new Memory(0x7ffff);

        Addressable rom = rom(home, "coco3.rom", "BASIC3.ROM").orElse(demo());
        Addressable cart = rom(home, "disk12.rom", "disk11.rom").orElse(none());

        DiskDrive diskDrive = new DiskDrive();
        drive0.dispatchTo(diskDrive.slot0());
        drive1.dispatchTo(diskDrive.slot1());
        drive2.dispatchTo(diskDrive.slot2());
        drive3.dispatchTo(diskDrive.slot3());

        DiskController diskController = context.aware(new DiskController(diskDrive));
        diskController.irq().to(Signal.NMI);

        var pia0a = new MC6821Port(Signal.IRQ);
        var pia0b = new MC6821Port(Signal.IRQ);
        var pia1a = new MC6821Port(Signal.FIRQ);
        var pia1b = new MC6821Port(Signal.FIRQ);

        EmulatorContext video = services.declare(new EmulatorContext());
        video.aware(new ClockFrequency(15, video));
        VideoTimer videoTiming = video.aware(new VideoTimer());
        videoTiming.horizontalSync().to(context.aware(pia0a.interrupt()));
        videoTiming.verticalSync().to(context.aware(pia0b.interrupt()));

        MMU mmu = new MMU(ram, context);

        Colors colors = StandardColors.select(composite);
        ColorPalette palette = new ColorPalette(colors, new CompositeDetection(composite));
        DisplayProcessor displayProcessor = new DisplayProcessor(screen, mmu.video(), new HighResFont(), palette, colors, DiscreteOutput.not(mmu.legacy()), 225);
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
                when(mmu.scs(), diskController),
                when(mmu.scs(), diskDrive)
        );

        Hardware.legacyVideo(
                new MC6847(screen, mmu.lowResVideo(), composite, new LegacyFont3(), mmu.legacy(), palette, 320, 225),
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
