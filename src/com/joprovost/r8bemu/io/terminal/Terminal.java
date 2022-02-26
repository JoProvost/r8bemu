package com.joprovost.r8bemu.io.terminal;

import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.graphic.Color;
import com.joprovost.r8bemu.graphic.Screen;

import java.io.PrintStream;

public class Terminal implements Screen {
    private static final int COLUMNS = 80;
    private static final int ROWS = 24;

    private final Color[] fgColors = new Color[COLUMNS * ROWS];
    private final Color[] bgColors = new Color[COLUMNS * ROWS];
    private final char[] characters = "\0".repeat(COLUMNS * ROWS).toCharArray();
    private Color[] pixels = new Color[]{};

    private final PrintStream printStream;
    private final DiscreteOutput graphic;

    public Terminal(PrintStream printStream, DiscreteOutput graphic) {
        this.printStream = printStream;
        this.graphic = graphic;
    }

    @Override
    public void character(char utf8, int row, int column, Color fg, Color bg) {
        if (graphic.isClear()) {
            if (alreadyDisplayed(utf8, row, column, fg, bg)) return;
            begin();
            move(row, column);
            color(fg, bg);
            printStream.print(utf8);
            end();
        }
    }

    public boolean alreadyDisplayed(char utf8, int row, int column, Color fg, Color bg) {
        int pos = row * COLUMNS + column;
        if (fgColors[pos] == fg && bgColors[pos] == bg && characters[pos] == utf8)
            return true;

        fgColors[pos] = fg;
        bgColors[pos] = bg;
        characters[pos] = utf8;
        return false;
    }

    public boolean pixelAlreadyDisplayed(int x, int y, Color color, int width, int height) {
        int pos = y * width + x;
        if (pixels.length != width * height) pixels = new Color[width*height];
        if (pixels[pos] == color)
            return true;

        pixels[pos] = color;
        return false;
    }

    @Override
    public void pixel(int x, int y, Color color, int width, int height) {
        if (graphic.isClear()) return;
        if (pixelAlreadyDisplayed(x, y, color, width, height)) return;
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
        printStream.print("\u001b[38;" + trueColor(fg) + "m");
        printStream.print("\u001b[48;" + trueColor(bg) + "m");
    }

    private String trueColor(Color color) {
        return "2;" + color.red + ";" + color.green + ";" + color.blue;
    }
}
