package com.joprovost.r8bemu;

import com.joprovost.r8bemu.memory.Memory;
import com.joprovost.r8bemu.memory.MemoryDevice;

public class DemoROM {
    static MemoryDevice demo() {
        Memory memory = new Memory(0x1fff);

        memory.write(0xbffe, 0xa0, 0x00); // Boot vector at $a000

        memory.write(
                0xa000,           //        ORG   $a000

                0x8e, 0x00, 0x00, // a000:  LDX   #$0000
                0xce, 0xb0, 0x00, //        LDU   #$b000
                0xc6, 0x00,       //        LDB   #$0

                0xa6, 0b11000000, // a008:  LDA   ,U+
                0xa7, 0b10000000, //        STA   ,X+
                0x5a,             //        DECB
                0x26, -7,         //        BNE   $a008

                0x8e, 0x01, 0x00, // a00f:  LDX   #$0100
                0xce, 0xb1, 0x00, //        LDU   #$b100
                0xc6, 0x00,       //        LDB   #$0

                0xa6, 0b11000000, // a017:  LDA   ,U+
                0xa7, 0b10000000, //        STA   ,X+
                0x5a,             //        DECB
                0x26, -7,         //        BNE   $a017

                0x20, -2          // a01e:  BRA   $a01e
        );

        memory.write(0xb000, strings(
                black("                                ",
                      "                                ",
                      " ###   ##  ###   ###            ",
                      " #  # #  # #  #  #              ",
                      " ###   ##  ###   ##  ## #  #  # ",
                      " #  # #  # #  #  #   # # # #  # ",
                      " #  #  ##  ###   ### #   #  ### ",
                      "                                "),
                green("                                ",
                      " ROM FILES :                    ",
                      "  .R8BEMU/BAS13.ROM    "),
                black("REQUIRED"),
                green(" ",
                      "  .R8BEMU/EXTBAS11.ROM          ",
                      "  .R8BEMU/DISK11.ROM            ",
                      "                                ",
                      " SEE README.MD FOR DETAILS      ",
                      "                                ")
        ));
        return MemoryDevice.readOnly(memory);
    }

    static byte[] strings(String... strings) {
        return String.join("", strings).getBytes();
    }

    static String black(String... strings) {
        String string = String.join("", strings);
        StringBuilder sb = new StringBuilder();
        string.codePoints().map(x -> x & 0x3f).forEach(sb::appendCodePoint);
        return sb.toString();
    }

    static String green(String... strings) {
        String string = String.join("", strings);
        StringBuilder sb = new StringBuilder();
        string.codePoints().map(x -> x | 0x40).forEach(sb::appendCodePoint);
        return sb.toString();
    }
}
