package com.joprovost.r8bemu.memory;

public class Demo implements MemoryMapped {

    private final Memory memory = new Memory(0x1fff);

    public Demo() {
        memory.write(0xbffe, 0xa0, 0x00); // Boot vector at $a000

        memory.write(
                0xa000,           //        ORG   $a000

                0x8e, 0x04, 0x00, // a000:  LDX   #$0400
                0xce, 0xb0, 0x00, //        LDU   #$b000
                0xc6, 0x00,       //        LDB   #$0

                0xa6, 0b11000000, // a008:  LDA   ,U+
                0xa7, 0b10000000, //        STA   ,X+
                0x5a,             //        DECB
                0x26, -7,         //        BNE   $a008

                0x8e, 0x05, 0x00, // a00f:  LDX   #$0400
                0xce, 0xb1, 0x00, //        LDU   #$b000
                0xc6, 0x00,       //        LDB   #$0

                0xa6, 0b11000000, // a017:  LDA   ,U+
                0xa7, 0b10000000, //        STA   ,X+
                0x5a,             //        DECB
                0x26, -7,         //        BNE   $a01u

                0x13              //        SYNC
        );

        for (var i = 0xb000; i < 0xb200; i++) {
            memory.write(i, 96);
        }

        memory.write(0xb0ad, "WELCOME".getBytes());
        memory.write(0xb0ef, "TO".getBytes());
        memory.write(0xb12d, "R8BEMU".getBytes());
    }

    @Override
    public int read(int address) {
        return memory.read(address);
    }
}
