package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.clock.ClockAwareBusyState;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.DataOutputRedirect;
import com.joprovost.r8bemu.data.Flag;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.data.link.LineOutputHandler;
import com.joprovost.r8bemu.data.link.ParallelOutputHandler;
import com.joprovost.r8bemu.data.transform.DataAccessSubset;
import com.joprovost.r8bemu.data.transform.DataOutputSubset;
import com.joprovost.r8bemu.font.Font;
import com.joprovost.r8bemu.io.Screen;
import com.joprovost.r8bemu.memory.MemoryDevice;

import static com.joprovost.r8bemu.io.Screen.Color.BLACK;
import static com.joprovost.r8bemu.io.Screen.Color.BLUE;
import static com.joprovost.r8bemu.io.Screen.Color.BUFF;
import static com.joprovost.r8bemu.io.Screen.Color.CYAN;
import static com.joprovost.r8bemu.io.Screen.Color.GREEN;
import static com.joprovost.r8bemu.io.Screen.Color.MAGENTA;
import static com.joprovost.r8bemu.io.Screen.Color.ORANGE;
import static com.joprovost.r8bemu.io.Screen.Color.RED;
import static com.joprovost.r8bemu.io.Screen.Color.YELLOW;

// VideoDisplayGenerator
public class MC6847 implements ClockAware {
    public static final int LINES = 250;
    public static final int WIDTH = 256;
    public static final int HEIGHT = 192;

    private static final String ASCII = "@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]↑← !\"#$%&'()*+,-./0123456789:;<=>?";
    private static final String GRAPHICS4 = " ▗▖▄▝▐▞▟▘▚▌▙▀▜▛█";

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

    private final DataOutputRedirect PORT = DataOutputRedirect.empty();
    private final DataOutput A_G = DataOutputSubset.bit(PORT, 7);
    private final DataOutput GM0_2 = DataOutputSubset.of(PORT, 0b1110000);
    private final DataOutput CSS = DataOutputSubset.bit(PORT, 3);

    private final ClockAwareBusyState hClock = new ClockAwareBusyState();
    private final ClockAwareBusyState vClock = new ClockAwareBusyState();

    private final Screen screen;
    private final Runnable hsync;
    private final Runnable vsync;
    private final MemoryDevice ram;
    private final Flag disableRg6Color;
    private final Font font;
    private int rg6ColorOffset = 0;

    public MC6847(Screen screen, Runnable hsync, Runnable vsync, MemoryDevice ram, Flag disableRg6Color, Font font) {
        this.screen = screen;
        this.hsync = hsync;
        this.vsync = vsync;
        this.ram = ram;
        this.disableRg6Color = disableRg6Color;
        this.font = font;
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

    public ParallelOutputHandler mode() {
        return PORT::referTo;
    }

    public LineOutputHandler reset() {
        return it -> { if (it.isSet()) rg6ColorOffset ^= 1; };
    }

    private void verticalScan() {
        if (A_G.isClear()) {
            for (int row = 0; row < 16; row++)
                for (int col = 0; col < 32; col++)
                    characterScan(row, col);
        } else {
            for (int line = 0; line < HEIGHT; line++)
                horizontalScan(line);
        }
    }

    private void characterScan(int row, int col) {
        for (int line = 0; line < 12; line ++) {
            VDG_DATA_BUS.value(ram.read(row * 32 * 12 + (line * 32) + col));
            if (AS.isSet()) {
                var utf8 = GRAPHICS4.charAt(SGM4_LUMA.value() % GRAPHICS4.length());
                character(utf8, row, col, line, color(SGM4_CHROMA.value()), BLACK);
            } else {
                var utf8 = ASCII.charAt(ASCII_CODE.value() % ASCII.length());
                Screen.Color color = CSS.isSet() ? ORANGE : GREEN;
                if (INV.isSet()) {
                    character(utf8, row, col, line, BLACK, color);
                } else {
                    character(utf8, row, col, line, color, BLACK);
                }
            }
        }
    }

    private void character(char utf8, int row, int column, int line, Screen.Color fg, Screen.Color bg) {
        screen.character(utf8, row, column, fg, bg);
        font.sprite(utf8).ifPresent(sprite -> {
            for (int x = 0; x < 8; x++) {
                screen.pixel(column * 8 + x, row * 12 + line, sprite.pixel(x, line) ? fg : bg);
            }
        });
    }

    private void horizontalScan(int line) {
        var pixelsPerByte = pixelsPerByte();
        int bytes = lineWidth() / pixelsPerByte;
        int address = bytes * line;

        Screen.Color foreground = CSS.isSet() ? BUFF : GREEN;
        Screen.Color background = BLACK;

        Screen.Color[] pixels = new Screen.Color[bytes * pixelsPerByte];
        for (int i = 0; i < bytes; i++) {
            VDG_DATA_BUS.value(ram.read(address + i));
            if (pixelsPerByte == 4) {
                pixels[i * 4]     = color(CSS.isSet(), E3.value());
                pixels[i * 4 + 1] = color(CSS.isSet(), E2.value());
                pixels[i * 4 + 2] = color(CSS.isSet(), E1.value());
                pixels[i * 4 + 3] = color(CSS.isSet(), E0.value());
            } else if (pixelsPerByte == 8) {
                pixels[i * 8]     = L7.isSet() ? foreground : background;
                pixels[i * 8 + 1] = L6.isSet() ? foreground : background;
                pixels[i * 8 + 2] = L5.isSet() ? foreground : background;
                pixels[i * 8 + 3] = L4.isSet() ? foreground : background;
                pixels[i * 8 + 4] = L3.isSet() ? foreground : background;
                pixels[i * 8 + 5] = L2.isSet() ? foreground : background;
                pixels[i * 8 + 6] = L1.isSet() ? foreground : background;
                pixels[i * 8 + 7] = L0.isSet() ? foreground : background;
            }
        }

        if (GM0_2.value() == RG6 && disableRg6Color.isClear()) rg6Colors(pixels);

        var pixelWidth = WIDTH / pixels.length;
        for (int x = 0; x < WIDTH; x++) {
            screen.pixel(x, line, pixels[x / pixelWidth]);
        }
    }

    private void rg6Colors(Screen.Color[] pixels) {
        for (int x = 0; x < WIDTH; x++) {
            var left = (x > 0) ? pixels[x - 1] : BLACK;
            var right = (x < WIDTH - 1) ? pixels[x + 1] : BLACK;
            var after = (x < WIDTH - 2) ? pixels[x + 2] : BUFF;

            if (pixels[x] == BUFF && left != BUFF && right == BLACK) {
                var color = ((x + rg6ColorOffset) % 2 == 0) ? BLUE : RED;
                pixels[x] = color;
                if (after != BLACK && (x + 1) < pixels.length) pixels[x + 1] = color;
            }
        }
    }

    private int lineWidth() {
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

    private int pixelsPerByte() {
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

    private Screen.Color color(int chroma) {
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

    private Screen.Color color(boolean css, int chroma) {
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
}
