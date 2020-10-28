package com.joprovost.r8bemu.coco;

import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.devices.memory.Memory;

public class DemoROM {
    static Addressable demo() {
        Memory memory = new Memory(0x1fff);

        memory.write(0xbffe, 0xa0, 0x00); // Boot vector at $a000
        memory.write(0x8c1b, 0x7e, 0xa0, 0x00); // Jump to $a000

        memory.write(
                0xa000,           //        ORG   $a000

                0x86, 0x80,       //        LDA   #$80      Legacy mode
                0xb7, 0xff, 0x90, //        STA   $ff90     Stored in GIME init0
                0x86, 0x12,       //        LDA   #$12      Green color
                0xb7, 0xff, 0xb0, //        STA   $ffb0     Stored in GIME palette 0

                0x8e, 0x00, 0x00, //        LDX   #$0000
                0xce, 0xb0, 0x00, //        LDU   #$b000
                0xc6, 0x00,       //        LDB   #$0
                0xa6, 0b11000000, // cpy1:  LDA   ,U+
                0xa7, 0b10000000, //        STA   ,X+
                0x5a,             //        DECB
                0x26, -7,         //        BNE   cpy1

                0x8e, 0x01, 0x00, //        LDX   #$0100
                0xce, 0xb1, 0x00, //        LDU   #$b100
                0xc6, 0x00,       //        LDB   #$0
                0xa6, 0b11000000, // cpy2:  LDA   ,U+
                0xa7, 0b10000000, //        STA   ,X+
                0x5a,             //        DECB
                0x26, -7,         //        BNE   cpy1

                0x20, -2          // loop:  BRA   loop
        );

        memory.write(0xb000, strings(
                text("                                ",
                     " ###   ##  ###   ###            ",
                     " #  # #  # #  #  #              ",
                     " ###   ##  ###   ##  ## #  #  # ",
                     " #  # #  # #  #  #   # # # #  # ",
                     " #  #  ##  ###   ### #   #  ### ",
                     "                                "),
                text("                                ",
                     " ROM FILES :                    ",
                     "  .R8BEMU/COCO3.ROM  "),
                inv("REQ"),
                text(" COCO 3 ",
                     "  .R8BEMU/BAS13.ROM  "),
                inv("REQ"),
                text(" COCO 2 ",
                     "  .R8BEMU/EXTBAS11.ROM   COCO 2 ",
                     "  .R8BEMU/DISK11.ROM            ",
                     "                                ",
                     " SEE README.MD FOR DETAILS      ",
                     "                                ")
        ));
        return Addressable.readOnly(memory);
    }

    static byte[] strings(String... strings) {
        return String.join("", strings).getBytes();
    }

    static String text(String... strings) {
        String string = String.join("", strings);
        StringBuilder sb = new StringBuilder();
        string.codePoints().map(x -> x & 0x3f).forEach(sb::appendCodePoint);
        return sb.toString();
    }

    static String inv(String... strings) {
        String string = String.join("", strings);
        StringBuilder sb = new StringBuilder();
        string.codePoints().map(x -> x | 0x40).forEach(sb::appendCodePoint);
        return sb.toString();
    }
}
