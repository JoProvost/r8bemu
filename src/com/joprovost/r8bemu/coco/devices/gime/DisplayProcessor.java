package com.joprovost.r8bemu.coco.devices.gime;

import com.joprovost.r8bemu.data.binary.BinaryOutput;
import com.joprovost.r8bemu.data.binary.BinaryRegister;
import com.joprovost.r8bemu.data.discrete.DiscreteLineInput;
import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.data.transform.BinaryAccessSubset;
import com.joprovost.r8bemu.devices.RasterGraphicDecoder;
import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.graphic.Colors;
import com.joprovost.r8bemu.graphic.Font;
import com.joprovost.r8bemu.graphic.Screen;

import java.awt.*;

import static com.joprovost.r8bemu.coco.StandardColors.BLACK;
import static com.joprovost.r8bemu.coco.StandardColors.GREEN;

public class DisplayProcessor implements Addressable {
    public static final String CHARACTERS = "" +
            "ÇüéâäàåçêëèïîßÄÅóæÆôöøûùØÖÜ§£±°ƒ !\"#$%&'()*+,-./0123456789:;<=>?" +
            "@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]↑←^abcdefghijklmnopqrstuvwxyz{¦}~_";

    private final Screen screen;
    private final Addressable ram;
    private final Font font;
    private int line;

    private final DiscreteOutput enabled;

    // Video mode register
    private final BinaryRegister mode = BinaryRegister.ofMask(0xff);
    // BP 0=alphanumeric (text modes), 1=bit plane (graphics modes)
    private final DiscreteOutput graphic = BinaryAccessSubset.bit(mode, 7);

    // Video resolution register
    private final BinaryRegister res = BinaryRegister.ofMask(0xff);
    private final BinaryOutput lpf = BinaryAccessSubset.of(res, 0b01100000);
    private final BinaryOutput hres = BinaryAccessSubset.of(res, 0b00011100);
    private final BinaryOutput cres = BinaryAccessSubset.of(res, 0b00000011);
    // NOTE: Text modes x0=No color attributes x1=Color attributes enabled
    private final BinaryOutput cattr = BinaryAccessSubset.of(res, 0b00000001);
    private final RasterGraphicDecoder raster;


    public DisplayProcessor(Screen screen, Addressable ram, Font font, Colors colors, DiscreteOutput enabled) {
        this.screen = screen;
        this.ram = ram;
        this.font = font;
        this.enabled = enabled;
        raster = RasterGraphicDecoder.of(ram, this::width, this::bits, colors);
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
            if (graphic.isSet()) lineScan();
            else characterScan();
            line++;
        };
    }

    private void lineScan() {
        screen.pixels(0, line, raster.line(line), width(), height());
    }

    private void characterScan() {
        int spriteLine = line % 8;
        int row = line / 8;
        for (int col = 0; col < columns(); col++) {
            char utf8 = CHARACTERS.charAt(ram.read((row * columns() + col) << cattr.value()) % CHARACTERS.length());
            character(utf8, row, col, spriteLine, BLACK, GREEN);
        }
    }

    private void character(char utf8, int row, int column, int line, Color fg, Color bg) {
        screen.character(utf8, row, column, fg, bg);
        font.sprite(utf8).ifPresent(sprite -> {
            for (int x = 0; x < 8; x++) {
                screen.pixel(column * 8 + x, row * 8 + line, sprite.pixel(x, line) ? fg : bg, width(), height());
            }
        });
    }

    @Override
    public int read(int address) {
        switch (address) {
            case 0xff98: return mode.value();
            case 0xff99: return res.value();
            default: return 0;
        }
    }

    @Override
    public void write(int address, int data) {
        switch (address) {
            case 0xff98: mode.value(data); break;
            case 0xff99: res.value(data); break;
        }
    }

    public int width() {
        if (graphic.isSet())
            return bytesPerRow() * 8 / bits();
        return columns() * 8;
    }

    public int height() {
        switch (lpf.value()) {
            case 0b00: return 192;
            case 0b01: return 200;
            case 0b11: return 225;
            default: throw new UnsupportedOperationException();
        }
    }

    private int bytesPerRow() {
        switch (hres.value()) {
            case 0b000: return 16;
            case 0b001: return 20;
            case 0b010: return 32;
            case 0b011: return 40;
            case 0b100: return 64;
            case 0b101: return 80;
            case 0b110: return 128;
            case 0b111: return 160;
            default: throw new UnsupportedOperationException();
        }
    }

    private int bits() {
        return 1 << cres.value();
    }

    private int columns() {
        switch (hres.value() & 0b101) {
            case 0b000: return 32;
            case 0b001: return 40;
            case 0b100: return 64;
            case 0b101: return 80;
            default: throw new UnsupportedOperationException();
        }
    }
}
