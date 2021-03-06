package com.joprovost.r8bemu.coco;

import com.joprovost.r8bemu.Services;
import com.joprovost.r8bemu.clock.ClockFrequency;
import com.joprovost.r8bemu.clock.EmulatorContext;
import com.joprovost.r8bemu.coco.devices.MC6847;
import com.joprovost.r8bemu.coco.devices.sam.MC6883;
import com.joprovost.r8bemu.coco.font.LegacyFont;
import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.data.discrete.Flag;
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
import com.joprovost.r8bemu.graphic.Screen;
import com.joprovost.r8bemu.io.CassetteRecorderDispatcher;
import com.joprovost.r8bemu.io.JoystickDispatcher;
import com.joprovost.r8bemu.io.KeyboardDispatcher;
import com.joprovost.r8bemu.io.sound.MixerDispatcher;
import com.joprovost.r8bemu.storage.DiskSlotDispatcher;

import java.nio.file.Path;

import static com.joprovost.r8bemu.Configuration.rom;
import static com.joprovost.r8bemu.devices.memory.Addressable.bus;
import static com.joprovost.r8bemu.devices.memory.Addressable.none;
import static com.joprovost.r8bemu.devices.memory.Addressable.when;

public class CoCo2 {
    public static void emulate(EmulatorContext context,
                               Screen screen,
                               DiscreteOutput composite,
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

        Memory ram = new Memory(0xffff);

        MC6883 sam = new MC6883(ram, context);
        Signal.RESET.to(sam.reset());

        Addressable rom0 = rom(home, "extbas11.rom", "EXTBASIC.ROM").orElse(none());
        Addressable rom1 = rom(home, "bas13.rom", "BASIC.ROM").orElse(DemoROM.demo());
        Addressable cart = rom(home, "disk12.rom", "disk11.rom", "DSKBASIC.ROM").orElse(none());

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

        Addressable bus = bus(
                sam, // S7
                when(sam.select(1), rom0),  // S=1
                when(sam.select(2), rom1),  // S=2
                when(sam.select(3), cart),  // S=3
                when(sam.select(4), new MC6821(pia0a, pia0b)),  // S=4
                when(sam.select(5), new MC6821(pia1a, pia1b)),  // S=5
                when(sam.select(6), diskController),  // S=6
                when(sam.select(6), diskDrive)  // S=6
        );

        EmulatorContext video = services.declare(new EmulatorContext());
        video.aware(new ClockFrequency(15, video));
        var videoTiming = video.aware(new VideoTimer());
        videoTiming.horizontalSync().to(context.aware(pia0a.interrupt()));
        videoTiming.verticalSync().to(context.aware(pia0b.interrupt()));

        Hardware.legacyVideo(
                new MC6847(screen, sam.video(), composite, new LegacyFont(), Flag.value(true), StandardColors.legacy(), 320, 225),
                pia1b, videoTiming);
        Hardware.base(context, uptime, services, bus, pia0a, pia0b, pia1a, pia1b, keyboard, cassette,
                      left, right, mixer, mute, recordingFile, debugger);

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
