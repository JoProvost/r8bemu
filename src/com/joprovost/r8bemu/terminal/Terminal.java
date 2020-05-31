package com.joprovost.r8bemu.terminal;

import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.devices.keyboard.KeyboardBuffer;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Optional;


public class Terminal implements Display, ClockAware {
    public static final String ASCII_CHARSET = "@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]↑← !\"#$%&'()*+,-./0123456789:;<=>?";
    public static final String SGM4_CHARSET = " ▗▖▄▝▐▞▟▘▚▌▙▀▜▛█";

    private static final int MARGIN_TOP = 1;
    private static final int MARGIN_LEFT = 4;
    private static final int HEIGHT = 16;
    private final InputStream inputStream;
    private final PrintStream printStream;
    private final KeyboardBuffer keyboard;

    public Terminal(InputStream inputStream, PrintStream printStream, KeyboardBuffer keyboard) {
        this.inputStream = inputStream;
        this.printStream = printStream;
        this.keyboard = keyboard;
    }

    @Override
    public void ascii(int row, int column, Color fg, Color bg, int code) {
        begin();
        move(row, column);
        color(fg, bg);
        printStream.print(ASCII_CHARSET.charAt(code));
        end();
    }

    @Override
    public void sgm4(int row, int column, Color fg, Color bg, int luma) {
        begin();
        move(row, column);
        color(fg, bg);
        printStream.print(SGM4_CHARSET.charAt(luma));
        end();
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
        printStream.print("\u001b[38;5;" + fg.ansi256 + "m");
        printStream.print("\u001b[48;5;" + bg.ansi256 + "m");
    }

    @Override
    public void tick(long tick) throws IOException {
        var terminalKey = readKey();
        if (terminalKey.filter(x -> x.charAt(0) == 3).isPresent()) {
            throw new EOFException("Closed by user");
        }

        terminalKey.ifPresent(keyboard::type);
    }

    private Optional<String> readKey() throws IOException {
        byte[] buffer = new byte[16];
        if (inputStream.available() > 0) {
            int len = inputStream.read(buffer);
            return Optional.of(new String(buffer).substring(0, len));
        }
        return Optional.empty();
    }
}
