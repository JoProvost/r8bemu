package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.Display;
import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.clock.ClockAwareBusyState;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataAccessSubset;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.memory.MemoryMapped;

import static com.joprovost.r8bemu.Display.Color.BLACK;
import static com.joprovost.r8bemu.Display.Color.BLUE;
import static com.joprovost.r8bemu.Display.Color.BUFF;
import static com.joprovost.r8bemu.Display.Color.CYAN;
import static com.joprovost.r8bemu.Display.Color.GREEN;
import static com.joprovost.r8bemu.Display.Color.MAGENTA;
import static com.joprovost.r8bemu.Display.Color.ORANGE;
import static com.joprovost.r8bemu.Display.Color.RED;
import static com.joprovost.r8bemu.Display.Color.YELLOW;

// VideoDisplayGenerator
public class MC6847 implements MemoryMapped, ClockAware {
    public static final int WIDTH = 32;
    public static final int MASK = 0x01ff;
    public static final int LINES = 250;

    private final Variable VDG_DATA_BUS = Variable.ofMask(0xff);

    private final DataAccess INV = DataAccessSubset.bit(VDG_DATA_BUS, 6);
    private final DataAccess AS = DataAccessSubset.bit(VDG_DATA_BUS, 7);

    private final DataAccess ASCII_CODE = DataAccessSubset.of(VDG_DATA_BUS, 0b00111111);
    private final DataAccess SGM4_CHROMA = DataAccessSubset.of(VDG_DATA_BUS, 0b01110000);
    private final DataAccess SGM4_LUMA = DataAccessSubset.of(VDG_DATA_BUS, 0b00001111);

    private final ClockAwareBusyState hClock = new ClockAwareBusyState();
    private final ClockAwareBusyState vClock = new ClockAwareBusyState();

    private final Display display;
    private final Runnable hsync;
    private final Runnable vsync;

    public MC6847(Display display, Runnable hsync, Runnable vsync) {
        this.display = display;
        this.hsync = hsync;
        this.vsync = vsync;
    }

    @Override
    public void write(int address, int data) {
        VDG_DATA_BUS.value(data);

        address &= MASK;
        var row = address / WIDTH + 1;
        var column = address % WIDTH + 1;

        if (AS.isSet()) {
            display.graphics4(row, column, color(SGM4_CHROMA.value()), BLACK, SGM4_LUMA.value());
        } else {
            if (INV.isSet()) display.character(row, column, BLACK, GREEN, ASCII_CODE.value());
            else display.character(row, column, GREEN, BLACK, ASCII_CODE.value());
        }
    }

    private static Display.Color color(int chroma) {
        switch (chroma) {
            case 0: return GREEN;
            case 1: return YELLOW;
            case 2: return BLUE;
            case 3: return RED;
            case 4: return BUFF;
            case 5: return CYAN;
            case 6: return MAGENTA;
            case 7: return ORANGE;
            default: return BLACK;
        }
    }

    @Override
    public void tick(Clock clock) {
        if (!hClock.at(clock).isBusy()) {
            hClock.busy(14917 / LINES); // @ 895 kHz
            hsync.run();
        }
        if (!vClock.at(clock).isBusy()) {
            vClock.busy(14917); // 60 Hz @ 895 kHz => 14916,6666
            vsync.run();
        }
    }
}
