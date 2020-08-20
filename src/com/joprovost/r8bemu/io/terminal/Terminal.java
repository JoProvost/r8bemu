package com.joprovost.r8bemu.io.terminal;

import com.joprovost.r8bemu.data.BitOutput;
import com.joprovost.r8bemu.io.Screen;

import java.io.PrintStream;

public class Terminal implements Screen {
    private static final int COLUMNS = 32;
    private static final int ROWS = 16;
    private static final int WIDTH = 256;
    private static final int HEIGHT = 192;

    private final Color[] fgColors = new Color[COLUMNS * ROWS];
    private final Color[] bgColors = new Color[COLUMNS * ROWS];
    private final char[] characters = "\0".repeat(COLUMNS * ROWS).toCharArray();
    private final Color[] pixels = new Color[WIDTH * HEIGHT];

    private final PrintStream printStream;
    private final BitOutput graphic;

    public Terminal(PrintStream printStream, BitOutput graphic) {
        this.printStream = printStream;
        this.graphic = graphic;
    }

    @Override
    public void glyph(int row, int column, Color fg, Color bg, char glyph, int line) {
        if (graphic.isSet()) {
            for (int x = 0; x < 8; x++) {
                pixel(column * 8 + x, row * 12 + line, FONT.pixel(glyph, line, x) ? fg : bg);
            }
        } else {
            if (alreadyDisplayed(row, column, fg, bg, glyph)) return;
            begin();
            move(row, column);
            color(fg, bg);
            printStream.print(glyph);
            end();
        }
    }

    public boolean alreadyDisplayed(int row, int column, Color fg, Color bg, char character) {
        int pos = row * 32 + column;
        if (fgColors[pos] == fg && bgColors[pos] == bg && characters[pos] == character)
            return true;

        fgColors[pos] = fg;
        bgColors[pos] = bg;
        characters[pos] = character;
        return false;
    }

    public boolean pixelAlreadyDisplayed(int x, int y, Color color) {
        int pos = y * WIDTH + x;
        if (pixels[pos] == color)
            return true;

        pixels[pos] = color;
        return false;
    }

    @Override
    public void pixel(int x, int y, Color color) {
        if (graphic.isClear()) return;
        if (pixelAlreadyDisplayed(x, y, color)) return;
        move(y, x * 2);
        color(color, color);
        printStream.print("  ");
    }

    public void begin() {
        printStream.print("\u001b[?25l");
    }

    public void end() {
        printStream.print("\u001b[" + (1 + ROWS) + ";1f");
        printStream.print("\u001b[0m");
        printStream.print("\u001b[?25h");
    }

    public void move(int row, int column) {
        printStream.print("\u001b[" + (row + 1) + ";" + (column + 1) + "f");
    }

    public void color(Color fg, Color bg) {
        printStream.print("\u001b[38;5;" + ansi256(fg) + "m");
        printStream.print("\u001b[48;5;" + ansi256(bg) + "m");
    }

    private int ansi256(Color color) {
        switch (color) {
            case GREEN: return 2;
            case YELLOW: return 3;
            case BLUE: return 4;
            case RED: return 1;
            case BUFF: return 236;
            case CYAN: return 6;
            case MAGENTA: return 5;
            case ORANGE: return 214;
            case BLACK: return 0;
            default: return 0;
        }
    }
}
