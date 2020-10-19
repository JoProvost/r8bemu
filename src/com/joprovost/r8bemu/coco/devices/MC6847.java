package com.joprovost.r8bemu.coco.devices;

import com.joprovost.r8bemu.data.binary.BinaryOutput;
import com.joprovost.r8bemu.data.binary.BinaryOutputHandler;
import com.joprovost.r8bemu.data.binary.BinaryOutputRedirect;
import com.joprovost.r8bemu.data.discrete.DiscreteLineInput;
import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.data.discrete.DiscteteOutputHandler;
import com.joprovost.r8bemu.data.transform.BinaryOutputSubset;
import com.joprovost.r8bemu.devices.RasterGraphicDecoder;
import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.graphic.Colors;
import com.joprovost.r8bemu.graphic.Font;
import com.joprovost.r8bemu.graphic.Screen;

import java.awt.*;

// VideoDisplayGenerator
public class MC6847 {
    public static final String CHARACTERS = "" +
            "@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]↑← !\"#$%&'()*+,-./0123456789:;<=>?" +
            "@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]↑← !\"#$%&'()*+,-./0123456789:;<=>?" +
            " ▗▖▄▝▐▞▟▘▚▌▙▀▜▛█ ▗▖▄▝▐▞▟▘▚▌▙▀▜▛█ ▗▖▄▝▐▞▟▘▚▌▙▀▜▛█ ▗▖▄▝▐▞▟▘▚▌▙▀▜▛█" +
            " ▗▖▄▝▐▞▟▘▚▌▙▀▜▛█ ▗▖▄▝▐▞▟▘▚▌▙▀▜▛█ ▗▖▄▝▐▞▟▘▚▌▙▀▜▛█ ▗▖▄▝▐▞▟▘▚▌▙▀▜▛█";

    private static final int CG1 = 0;
    private static final int RG1 = 1;
    private static final int CG2 = 2;
    private static final int RG2 = 3;
    private static final int CG3 = 4;
    private static final int RG3 = 5;
    private static final int CG6 = 6;
    private static final int RG6 = 7;

    private static final int AS = 7;
    private static final int INV = 6;

    private final BinaryOutputRedirect port = BinaryOutputRedirect.empty();
    private final BinaryOutput graphic = BinaryOutputSubset.bit(port, 7);
    private final BinaryOutput mode = BinaryOutputSubset.of(port, 0b1110000);
    private final BinaryOutput css = BinaryOutputSubset.bit(port, 3);

    private final Screen screen;
    private final Addressable ram;
    private final DiscreteOutput composite;
    private final Font font;
    private final DiscreteOutput enabled;
    private final Colors colors;
    private final RasterGraphicDecoder raster;

    private int rg6ColorOffset = 0;
    private int line;

    public MC6847(Screen screen, Addressable ram, DiscreteOutput composite, Font font, DiscreteOutput enabled, Colors colors) {
        this.screen = screen;
        this.ram = ram;
        this.composite = composite;
        this.font = font;
        this.enabled = enabled;
        this.colors = colors;
        raster = RasterGraphicDecoder.of(ram, this::width, this::bits, this::pixelColor);
    }

    public BinaryOutputHandler mode() {
        return port::referTo;
    }

    public DiscteteOutputHandler reset() {
        return it -> { if (it.isSet()) rg6ColorOffset ^= 1; };
    }

    public DiscreteLineInput sync() {
        return value -> {
            if (!value) return;
            if (enabled.isClear()) return;
            line = 0;
        };
    }

    public DiscreteLineInput scan() {
        return value -> {
            if (!value) return;
            if (enabled.isClear()) return;
            if (line >= height()) return;
            if (graphic.isClear()) {
                for (int col = 0; col < 32; col++)
                    characterScan(16 * line / height(), col);
            } else {
                lineScan();
            }
            line++;
        };
    }

    public int width() {
        if (graphic.isClear()) return 256;
        switch (mode.value()) {
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

    public int height() {
        return 192;
    }

    private int bits() {
        switch (mode.value()) {
            case CG1: // 0b000
            case CG2: // 0b010
            case CG3: // 0b100
            case CG6: // 0b110
                return 2;

            case RG1: // 0b001
            case RG2: // 0b011
            case RG3: // 0b101
            case RG6: // 0b111
            default:
                return 1;
        }
    }

    private void characterScan(int row, int col) {
        int spriteLine = line % 12;
        int data = ram.read(row * 32 * 12 + (spriteLine * 32) + col);
        char utf8 = CHARACTERS.charAt(data % CHARACTERS.length());
        if (BinaryOutput.bit(data, AS)) {
            character(utf8, row, col, spriteLine, colors.color(BinaryOutput.subset(data, 0b01110000)), black());
        } else {
            Color color = css.isSet() ? buff() : green();
            if (BinaryOutput.bit(data, INV)) {
                character(utf8, row, col, spriteLine, black(), color);
            } else {
                character(utf8, row, col, spriteLine, color, black());
            }
        }
    }

    private void character(char utf8, int row, int column, int line, Color fg, Color bg) {
        screen.character(utf8, row, column, fg, bg);
        font.sprite(utf8).ifPresent(sprite -> {
            for (int x = 0; x < 8; x++) {
                screen.pixel(column * 8 + x, row * 12 + line, sprite.pixel(x, line) ? fg : bg, width(), height());
            }
        });
    }

    private void lineScan() {
        Color[] pixels = raster.line(line);
        if (mode.value() == RG6 && composite.isSet()) composite(pixels);
        screen.pixels(0, line, pixels, width(), height());
    }

    private Color pixelColor(int value) {
        if (bits() == 2) return colors.color(css.isSet() ? value + 4 : value);
        if (value == 0) return black();
        return css.isSet() ? buff() : green();
    }

    private void composite(Color[] pixels) {
        for (int x = 0; x < width(); x++) {
            var left = (x > 0) ? pixels[x - 1] : black();
            var right = (x < width() - 1) ? pixels[x + 1] : black();
            var after = (x < width() - 2) ? pixels[x + 2] : buff();

            if (pixels[x] == buff() && left != buff() && right == black()) {
                var color = ((x + rg6ColorOffset) % 2 == 0) ? blue() : red();
                pixels[x] = color;
                if (after != black() && (x + 1) < pixels.length) pixels[x + 1] = color;
            }
        }
    }

    private Color green() {
        return colors.color(0);
    }

    private Color blue() {
        return colors.color(2);
    }

    private Color red() {
        return colors.color(3);
    }

    private Color buff() {
        return colors.color(4);
    }

    private Color black() {
        return colors.color(8);
    }
}
