package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.data.Subset;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.memory.MemoryMapped;
import com.joprovost.r8bemu.terminal.Color;
import com.joprovost.r8bemu.terminal.Display;

import static com.joprovost.r8bemu.terminal.Color.BLACK;
import static com.joprovost.r8bemu.terminal.Color.BLUE;
import static com.joprovost.r8bemu.terminal.Color.BUFF;
import static com.joprovost.r8bemu.terminal.Color.CYAN;
import static com.joprovost.r8bemu.terminal.Color.GREEN;
import static com.joprovost.r8bemu.terminal.Color.MAGENTA;
import static com.joprovost.r8bemu.terminal.Color.ORANGE;
import static com.joprovost.r8bemu.terminal.Color.RED;
import static com.joprovost.r8bemu.terminal.Color.YELLOW;

// VideoDisplayGenerator
public class MC6847 implements MemoryMapped {
    public static final int WIDTH = 32;
    public static final int MASK = 0x01ff;

    private final Variable VDG_DATA_BUS = Variable.ofMask(0xff);

    private final Subset DD0 = Subset.bit(VDG_DATA_BUS, 0);
    private final Subset DD1 = Subset.bit(VDG_DATA_BUS, 1);
    private final Subset DD2 = Subset.bit(VDG_DATA_BUS, 2);
    private final Subset DD3 = Subset.bit(VDG_DATA_BUS, 3);
    private final Subset DD4 = Subset.bit(VDG_DATA_BUS, 4);
    private final Subset DD5 = Subset.bit(VDG_DATA_BUS, 5);
    private final Subset DD6 = Subset.bit(VDG_DATA_BUS, 6);
    private final Subset DD7 = Subset.bit(VDG_DATA_BUS, 7);
    private final Subset INV = DD6;
    private final Subset AS = DD7;

    private final Subset ASCII_CODE = Subset.of(VDG_DATA_BUS, 0b00111111);
    private final Subset SGM4_CHROMA = Subset.of(VDG_DATA_BUS, 0b01110000);
    private final Subset SGM4_LUMA = Subset.of(VDG_DATA_BUS, 0b00001111);

    private final Display terminal;

    public MC6847(Display terminal) {
        this.terminal = terminal;
    }

    @Override
    public void write(int address, int data) {
        VDG_DATA_BUS.set(data);

        address &= MASK;
        var row = address / WIDTH + 1;
        var column = address % WIDTH + 1;

        if (AS.isSet()) {
            terminal.sgm4(row, column, color(SGM4_CHROMA.unsigned()), BLACK, SGM4_LUMA.unsigned());
        } else {
            if (INV.isSet()) terminal.ascii(row, column, BLACK, GREEN, ASCII_CODE.unsigned());
            else terminal.ascii(row, column, GREEN, BLACK, ASCII_CODE.unsigned());
        }
    }

    private static Color color(int chroma) {
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
}
