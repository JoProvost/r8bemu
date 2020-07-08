package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.io.Display;
import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.clock.ClockAwareBusyState;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataAccessSubset;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.DataOutputReference;
import com.joprovost.r8bemu.data.DataOutputSubset;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.memory.MemoryDevice;
import com.joprovost.r8bemu.port.DataOutputHandler;

import static com.joprovost.r8bemu.io.Display.Color.BLACK;
import static com.joprovost.r8bemu.io.Display.Color.BLUE;
import static com.joprovost.r8bemu.io.Display.Color.BUFF;
import static com.joprovost.r8bemu.io.Display.Color.CYAN;
import static com.joprovost.r8bemu.io.Display.Color.GREEN;
import static com.joprovost.r8bemu.io.Display.Color.MAGENTA;
import static com.joprovost.r8bemu.io.Display.Color.ORANGE;
import static com.joprovost.r8bemu.io.Display.Color.RED;
import static com.joprovost.r8bemu.io.Display.Color.YELLOW;

// VideoDisplayGenerator
public class MC6847 implements ClockAware {
    public static final int LINES = 250;

    private static final int CG1 = 0;
    private static final int RG1 = 1;
    private static final int CG2 = 2;
    private static final int RG2 = 3;
    private static final int CG3 = 4;
    private static final int RG3 = 5;
    private static final int CG6 = 6;
    private static final int RG6 = 7;

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
    private final MemoryDevice ram;

    public MC6847(Display display, Runnable hsync, Runnable vsync, MemoryDevice ram) {
        this.display = display;
        this.hsync = hsync;
        this.vsync = vsync;
        this.ram = ram;
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

    public void verticalScan() {
        if (A_G.isClear()) {
            for (int row = 0; row < 16; row++)
                for (int col = 0; col < 32; col++) {
                    scanChar(row, col);
                }
        } else {
            for (int line = 0; line < 192; line++)
                horizontalScan(line);
        }
    }

    public void scanChar(int row, int col) {
        VDG_DATA_BUS.value(ram.read(row * 32 * 24 + col));
        if (AS.isSet()) {
            display.graphics4(row + 1, col + 1, color(SGM4_CHROMA.value()), BLACK, SGM4_LUMA.value());
        } else {
            if (INV.isSet())
                display.character(row + 1, col + 1, BLACK, CSS.isSet() ? ORANGE : GREEN, ASCII_CODE.value());
            else display.character(row + 1, col + 1, CSS.isSet() ? ORANGE : GREEN, BLACK, ASCII_CODE.value());
        }
    }

    public void horizontalScan(int line) {
        int bytes = bytesPerLine();
        int address = bytes * line;
        for (int i = 0; i <= bytes; i++) {
            drawByte(address + i);
        }
    }

    public void drawByte(int address) {
        VDG_DATA_BUS.value(ram.read(address));

        Display.Color foreground = CSS.isSet() ? BUFF : GREEN;
        Display.Color background = BLACK;

        if (pixelsPerByte() == 4)
            draw(address, lineWidth(),
                 color(CSS.isSet(), E3.value()),
                 color(CSS.isSet(), E2.value()),
                 color(CSS.isSet(), E1.value()),
                 color(CSS.isSet(), E0.value()));
        else if (pixelsPerByte() == 8)
            draw(address, lineWidth(),
                 L7.isSet() ? foreground : background,
                 L6.isSet() ? foreground : background,
                 L5.isSet() ? foreground : background,
                 L4.isSet() ? foreground : background,
                 L3.isSet() ? foreground : background,
                 L2.isSet() ? foreground : background,
                 L1.isSet() ? foreground : background,
                 L0.isSet() ? foreground : background);
    }

    int bytesPerLine() {
        return lineWidth() / pixelsPerByte();
    }

    int lineWidth() {
        switch (GM0_2.value()) {
            case CG1: // 0b000
                return 64;

            case RG1: // 0b001
            case CG2: // 0b010
            case RG2: // 0b011 PMODE 0
            case CG3: // 0b100 PMODE 1
            case RG3: // 0b101 PMODE 2
            case CG6: // 0b110 PMODE 3
                return 128;

            case RG6: // 0b111 PMODE 4
            default:
                return 256;
        }
    }

    int pixelsPerByte() {
        switch (GM0_2.value()) {
            case CG1: // 0b000
            case CG2: // 0b010
            case CG3: // 0b100
            case CG6: // 0b110
                return 4;

            case RG1: // 0b001
            case RG2: // 0b011
            case RG3: // 0b101
            case RG6: // 0b111
            default:
                return 8;
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
            verticalScan();
            vsync.run();
        }
    }

    public DataOutputHandler mode() {
        return PORT::referTo;
    }

    private void draw(int index, int width, Display.Color color) {
        int pxWidth = 256 / width;
        int x = (index % width) * pxWidth;
        int y = (index / width);
        display.square(x, y, pxWidth, 1, color);
    }

    private void draw(int index, int width, Display.Color... color) {
        for (int offset = 0; offset < color.length; offset++) {
            draw(index * color.length + offset, width, color[offset]);
        }
    }
}
