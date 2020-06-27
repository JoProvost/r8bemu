package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.Display;
import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.clock.ClockAwareBusyState;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataAccessSubset;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.DataOutputReference;
import com.joprovost.r8bemu.data.DataOutputSubset;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.memory.MemoryMapped;
import com.joprovost.r8bemu.port.DataOutputHandler;

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
    public static final int LINES = 250;

    private static final int CG1 = 0;
    private static final int RG1 = 1;
    private static final int CG2 = 2;
    private static final int PMODE_0 = 3;
    private static final int PMODE_1 = 4;
    private static final int PMODE_2 = 5;
    private static final int PMODE_3 = 6;
    private static final int PMODE_4 = 7;

    private final Variable VDG_DATA_BUS = Variable.ofMask(0xff);

    private final DataAccess INV = DataAccessSubset.bit(VDG_DATA_BUS, 6);
    private final DataAccess AS = DataAccessSubset.bit(VDG_DATA_BUS, 7);

    private final DataAccess L0 = DataAccessSubset.bit(VDG_DATA_BUS, 0);
    private final DataAccess L1 = DataAccessSubset.bit(VDG_DATA_BUS, 1);
    private final DataAccess L2 = DataAccessSubset.bit(VDG_DATA_BUS, 2);
    private final DataAccess L3 = DataAccessSubset.bit(VDG_DATA_BUS, 3);
    private final DataAccess L4 = DataAccessSubset.bit(VDG_DATA_BUS, 4);
    private final DataAccess L5 = DataAccessSubset.bit(VDG_DATA_BUS, 5);
    private final DataAccess L6 = DataAccessSubset.bit(VDG_DATA_BUS, 6);
    private final DataAccess L7 = DataAccessSubset.bit(VDG_DATA_BUS, 7);

    private final DataAccess E0 = DataAccessSubset.of(VDG_DATA_BUS, 0b00000011);
    private final DataAccess E1 = DataAccessSubset.of(VDG_DATA_BUS, 0b00001100);
    private final DataAccess E2 = DataAccessSubset.of(VDG_DATA_BUS, 0b00110000);
    private final DataAccess E3 = DataAccessSubset.of(VDG_DATA_BUS, 0b11000000);

    private final DataAccess ASCII_CODE = DataAccessSubset.of(VDG_DATA_BUS, 0b00111111);
    private final DataAccess SGM4_CHROMA = DataAccessSubset.of(VDG_DATA_BUS, 0b01110000);
    private final DataAccess SGM4_LUMA = DataAccessSubset.of(VDG_DATA_BUS, 0b00001111);

    private final DataOutputReference PORT = DataOutputReference.empty();
    private final DataOutput A_G = DataOutputSubset.bit(PORT, 7);
    private final DataOutput GM0_2 = DataOutputSubset.of(PORT, 0b1110000);
    private final DataOutput CSS = DataOutputSubset.bit(PORT, 3);

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

    private static Display.Color color(boolean css, int chroma) {
        if (css) {
            switch (chroma) {
                case 0: return BUFF;
                case 1: return CYAN;
                case 2: return MAGENTA;
                case 3: return ORANGE;
                default: return BLACK;
            }
        } else {
            switch (chroma) {
                case 0: return GREEN;
                case 1: return YELLOW;
                case 2: return BLUE;
                case 3: return RED;
                default: return BLACK;
            }
        }
    }

    @Override
    public void write(int address, int data) {
        VDG_DATA_BUS.value(data);

        if (A_G.isClear()) {
            var row = address / WIDTH + 1;
            var column = address % WIDTH + 1;
            if (AS.isSet()) {
                display.graphics4(row, column, color(SGM4_CHROMA.value()), BLACK, SGM4_LUMA.value());
            } else {
                if (INV.isSet())
                    display.character(row, column, BLACK, CSS.isSet() ? ORANGE : GREEN, ASCII_CODE.value());
                else display.character(row, column, CSS.isSet() ? ORANGE : GREEN, BLACK, ASCII_CODE.value());
            }
        } else {
            Display.Color foreground = CSS.isSet() ? BUFF : GREEN;
            Display.Color background = BLACK;

            int index = address;
            switch (GM0_2.value()) {
                case CG1:
                    draw(index, 4, 3,
                         // TODO: Test this mode with an application
                         //       There is no mapping in Color BASIC
                         color(CSS.isSet(), E3.value()),
                         color(CSS.isSet(), E2.value()),
                         color(CSS.isSet(), E1.value()),
                         color(CSS.isSet(), E0.value()));
                    break;

                case RG1:
                    // TODO: Test this mode with an application
                    //       There is no mapping in Color BASIC
                    draw(index, 3, 3,
                         L7.isSet() ? foreground : background,
                         L6.isSet() ? foreground : background,
                         L5.isSet() ? foreground : background,
                         L4.isSet() ? foreground : background,
                         L3.isSet() ? foreground : background,
                         L2.isSet() ? foreground : background,
                         L1.isSet() ? foreground : background,
                         L0.isSet() ? foreground : background);
                    break;

                case CG2:
                    // TODO See why "microchess" uses this mode but puts SAM into VDG MODE 4 and expects pixels of
                    //      2 x 2.  It should be 3 x 3 according to the chip specs.
                    draw(index, 2, 2,
                         color(CSS.isSet(), E3.value()),
                         color(CSS.isSet(), E2.value()),
                         color(CSS.isSet(), E1.value()),
                         color(CSS.isSet(), E0.value()));
                    break;

                case PMODE_0:
                    draw(index, 2, 2,
                         L7.isSet() ? foreground : background,
                         L6.isSet() ? foreground : background,
                         L5.isSet() ? foreground : background,
                         L4.isSet() ? foreground : background,
                         L3.isSet() ? foreground : background,
                         L2.isSet() ? foreground : background,
                         L1.isSet() ? foreground : background,
                         L0.isSet() ? foreground : background);
                    break;

                case PMODE_1:
                    draw(index, 2, 2,
                         color(CSS.isSet(), E3.value()),
                         color(CSS.isSet(), E2.value()),
                         color(CSS.isSet(), E1.value()),
                         color(CSS.isSet(), E0.value()));
                    break;

                case PMODE_2:
                    draw(index, 2, 1,
                         L7.isSet() ? foreground : background,
                         L6.isSet() ? foreground : background,
                         L5.isSet() ? foreground : background,
                         L4.isSet() ? foreground : background,
                         L3.isSet() ? foreground : background,
                         L2.isSet() ? foreground : background,
                         L1.isSet() ? foreground : background,
                         L0.isSet() ? foreground : background);
                    break;

                case PMODE_3:
                    draw(index, 2, 1,
                         color(CSS.isSet(), E3.value()),
                         color(CSS.isSet(), E2.value()),
                         color(CSS.isSet(), E1.value()),
                         color(CSS.isSet(), E0.value()));
                    break;

                case PMODE_4:
                    draw(index, 1, 1,
                         L7.isSet() ? foreground : background,
                         L6.isSet() ? foreground : background,
                         L5.isSet() ? foreground : background,
                         L4.isSet() ? foreground : background,
                         L3.isSet() ? foreground : background,
                         L2.isSet() ? foreground : background,
                         L1.isSet() ? foreground : background,
                         L0.isSet() ? foreground : background);
                    break;
            }
        }
    }

    @Override
    public void tick(Clock clock) {
        if (!hClock.at(clock).isBusy()) {
            hClock.busy(15000 / LINES); // 15 kHz @ 900 kHz
            hsync.run();
        }
        if (!vClock.at(clock).isBusy()) {
            vClock.busy(15000); // 60 Hz @ 900 kHz
            vsync.run();
        }
    }

    public DataOutputHandler mode() {
        return PORT::referTo;
    }

    private void draw(int index, int width, int height, Display.Color color) {
        int rx = 256 / width;
        int x = (index % rx) * width;
        int y = (index / rx) * height;
        display.square(x, y, width, height, color);
    }

    private void draw(int index, int width, int height, Display.Color... color) {
        for (int offset = 0; offset < color.length; offset++) {
            draw(index * color.length + offset, width, height, color[offset]);
        }
    }
}
