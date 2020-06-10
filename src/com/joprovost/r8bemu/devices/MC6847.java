package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.clock.ClockAwareBusyState;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataAccessSubset;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.memory.MemoryMapped;
import com.joprovost.r8bemu.terminal.Color;
import com.joprovost.r8bemu.terminal.Display;

import static com.joprovost.r8bemu.terminal.Color.BLACK;
import static com.joprovost.r8bemu.terminal.Color.BLUE;
import static com.joprovost.r8bemu.terminal.Color.BUFF;
import static com.joprovost.r8bemu.terminal.Color.CYAN;
import static com.joprovost.r8bemu.terminal.Color.GREEN;
import static com.joprovost.r8bemu.terminal.Color.MAGENTA;
import static com.joprovost.r8bemu.terminal.Color.ORANGE;
import static com.joprovost.r8bemu.terminal.Color.RED;
import static com.joprovost.r8bemu.terminal.Color.YELLOW;

// VideoDisplayGenerator
public class MC6847 implements MemoryMapped, ClockAware {
    public static final int WIDTH = 32;
    public static final int MASK = 0x01ff;

    private final Variable VDG_DATA_BUS = Variable.ofMask(0xff);

    private final DataAccess DD0 = DataAccessSubset.bit(VDG_DATA_BUS, 0);
    private final DataAccess DD1 = DataAccessSubset.bit(VDG_DATA_BUS, 1);
    private final DataAccess DD2 = DataAccessSubset.bit(VDG_DATA_BUS, 2);
    private final DataAccess DD3 = DataAccessSubset.bit(VDG_DATA_BUS, 3);
    private final DataAccess DD4 = DataAccessSubset.bit(VDG_DATA_BUS, 4);
    private final DataAccess DD5 = DataAccessSubset.bit(VDG_DATA_BUS, 5);
    private final DataAccess DD6 = DataAccessSubset.bit(VDG_DATA_BUS, 6);
    private final DataAccess DD7 = DataAccessSubset.bit(VDG_DATA_BUS, 7);
    private final DataAccess INV = DD6;
    private final DataAccess AS = DD7;

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
        VDG_DATA_BUS.set(data);

        address &= MASK;
        var row = address / WIDTH + 1;
        var column = address % WIDTH + 1;

        if (AS.isSet()) {
            display.sgm4(row, column, color(SGM4_CHROMA.unsigned()), BLACK, SGM4_LUMA.unsigned());
        } else {
            if (INV.isSet()) display.ascii(row, column, BLACK, GREEN, ASCII_CODE.unsigned());
            else display.ascii(row, column, GREEN, BLACK, ASCII_CODE.unsigned());
        }
    }

    private static Color color(int chroma) {
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
    public void tick(long tick) {
        if (!hClock.at(tick).isBusy()) {
            hClock.busy(60); // 15 kHz @ 900 kHz
            hsync.run();
        }
        if (!vClock.at(tick).isBusy()) {
            vClock.busy(15000); // 60 Hz @ 900 kHz
            vsync.run();
        }
    }
}
