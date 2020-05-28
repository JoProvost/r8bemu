package com.joprovost.r8bemu.terminal;

public enum Color {
    GREEN(2),
    YELLOW(3),
    BLUE(4),
    RED(1),
    BUFF(236),
    CYAN(6),
    MAGENTA(5),
    ORANGE(214),
    BLACK(0);

    public final int ansi256;

    Color(int ansi256) {
        this.ansi256 = ansi256;
    }

}
