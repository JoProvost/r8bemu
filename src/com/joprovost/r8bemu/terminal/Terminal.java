package com.joprovost.r8bemu.terminal;

import com.joprovost.r8bemu.Display;

import java.io.PrintStream;


public class Terminal implements Display {
    private static final int MARGIN_TOP = 1;
    private static final int MARGIN_LEFT = 4;
    private static final int HEIGHT = 16;

    private final PrintStream printStream;

    public Terminal(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void character(int row, int column, Color fg, Color bg, char character) {
        begin();
        move(row, column);
        color(fg, bg);
        printStream.print(character);
        end();
    }

    @Override
    public void pixel(int x, int y, Color color) {
        throw new UnsupportedOperationException();
    }

    public void begin() {
        printStream.print("\u001b[?25l");
    }

    public void end() {
        printStream.print("\u001b[" + (1 + HEIGHT + (MARGIN_TOP * 2)) + ";1f");
        printStream.print("\u001b[0m");
        printStream.print("\u001b[?25h");
    }

    public void move(int row, int column) {
        printStream.print("\u001b[" + (MARGIN_TOP + row) + ";" + (MARGIN_LEFT + column) + "f");
    }

    public void color(Color fg, Color bg) {
        printStream.print("\u001b[38;5;" + ansi256(fg) + "m");
        printStream.print("\u001b[48;5;" + ansi256(bg) + "m");
    }

    private int ansi256(Color color){
        switch(color) {
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
