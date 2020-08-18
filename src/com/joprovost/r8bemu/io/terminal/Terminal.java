package com.joprovost.r8bemu.io.terminal;

import com.joprovost.r8bemu.io.Screen;

import java.io.PrintStream;

public class Terminal implements Screen {
    private static final int WIDTH = 32;
    private static final int HEIGHT = 16;

    private final Color[] fgColors = new Color[WIDTH * HEIGHT];
    private final Color[] bgColors = new Color[WIDTH * HEIGHT];
    private final char[] characters = "\0".repeat(WIDTH * HEIGHT).toCharArray();

    private final PrintStream printStream;

    public Terminal(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void glyph(int row, int column, Color fg, Color bg, char glyph, int line) {
        if (alreadyDisplayed(row, column, fg, bg, glyph))
            return;

        begin();
        move(row, column);
        color(fg, bg);
        printStream.print(glyph);
        end();
    }

    public boolean alreadyDisplayed(int row, int column, Color fg, Color bg, char character) {
        int pos = (row) * 32 + column;
        if (fgColors[pos] == fg && bgColors[pos] == bg && characters[pos] == character)
            return true;

        fgColors[pos] = fg;
        bgColors[pos] = bg;
        characters[pos] = character;
        return false;
    }

    @Override
    public void pixel(int x, int y, Color color) {
        throw new UnsupportedOperationException();
    }

    public void begin() {
        printStream.print("\u001b[?25l");
    }

    public void end() {
        printStream.print("\u001b[" + (1 + HEIGHT) + ";1f");
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
