package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.Reference;
import com.joprovost.r8bemu.data.Size;
import com.joprovost.r8bemu.memory.Addressing;
import com.joprovost.r8bemu.memory.MemoryMapped;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.joprovost.r8bemu.mc6809.Mnemonic.*;
import static com.joprovost.r8bemu.memory.Addressing.DIRECT_ADDRESS;
import static com.joprovost.r8bemu.memory.Addressing.DIRECT_DATA_16;
import static com.joprovost.r8bemu.memory.Addressing.DIRECT_DATA_8;
import static com.joprovost.r8bemu.memory.Addressing.EXTENDED_ADDRESS;
import static com.joprovost.r8bemu.memory.Addressing.EXTENDED_DATA_16;
import static com.joprovost.r8bemu.memory.Addressing.EXTENDED_DATA_8;
import static com.joprovost.r8bemu.memory.Addressing.IMMEDIATE_VALUE_16;
import static com.joprovost.r8bemu.memory.Addressing.IMMEDIATE_VALUE_8;
import static com.joprovost.r8bemu.memory.Addressing.INDEXED_ADDRESS;
import static com.joprovost.r8bemu.memory.Addressing.INDEXED_DATA_16;
import static com.joprovost.r8bemu.memory.Addressing.INDEXED_DATA_8;
import static com.joprovost.r8bemu.memory.Addressing.INHERENT;
import static com.joprovost.r8bemu.memory.Addressing.RELATIVE_ADDRESS_16;
import static com.joprovost.r8bemu.memory.Addressing.RELATIVE_ADDRESS_8;

public class Op {
    private static final Map<Integer, Op> ops;
    private static final Set<Integer> extended = new HashSet<>();

    static {
        ops = new HashMap<>();
        ops.put(0x00, new Op(6, NEG, DIRECT_DATA_8));
        ops.put(0x03, new Op(6, COM, DIRECT_DATA_8));
        ops.put(0x04, new Op(6, LSR, DIRECT_DATA_8));
        ops.put(0x06, new Op(6, ROR, DIRECT_DATA_8));
        ops.put(0x07, new Op(6, ASR, DIRECT_DATA_8));
        ops.put(0x08, new Op(6, LSL, DIRECT_DATA_8));
        ops.put(0x09, new Op(6, ROL, DIRECT_DATA_8));
        ops.put(0x0a, new Op(6, DEC, DIRECT_DATA_8));
        ops.put(0x0c, new Op(6, INC, DIRECT_DATA_8));
        ops.put(0x0d, new Op(6, TST, DIRECT_DATA_8));
        ops.put(0x0e, new Op(3, JMP, DIRECT_ADDRESS));
        ops.put(0x0f, new Op(6, CLR, DIRECT_DATA_8));

        ops.put(0x12, new Op(2, NOP, INHERENT));
        ops.put(0x13, new Op(4, SYNC, INHERENT));
        ops.put(0x16, new Op(5, LBRA, RELATIVE_ADDRESS_16));
        ops.put(0x17, new Op(7, LBSR, RELATIVE_ADDRESS_16));
        ops.put(0x19, new Op(2, DAA, INHERENT));
        ops.put(0x1a, new Op(3, ORCC, IMMEDIATE_VALUE_8));
        ops.put(0x1c, new Op(3, ANDCC, IMMEDIATE_VALUE_8));
        ops.put(0x1d, new Op(2, SEX, INHERENT));
        ops.put(0x1e, new Op(8, EXG, IMMEDIATE_VALUE_8));
        ops.put(0x1f, new Op(6, TFR, IMMEDIATE_VALUE_8));

        ops.put(0x20, new Op(3, BRA, RELATIVE_ADDRESS_8));
        ops.put(0x21, new Op(3, BRN, RELATIVE_ADDRESS_8));
        ops.put(0x22, new Op(3, BHI, RELATIVE_ADDRESS_8));
        ops.put(0x23, new Op(3, BLS, RELATIVE_ADDRESS_8));
        ops.put(0x24, new Op(3, BCC, RELATIVE_ADDRESS_8));
        ops.put(0x25, new Op(3, BCS, RELATIVE_ADDRESS_8));
        ops.put(0x26, new Op(3, BNE, RELATIVE_ADDRESS_8));
        ops.put(0x27, new Op(3, BEQ, RELATIVE_ADDRESS_8));
        ops.put(0x28, new Op(3, BVC, RELATIVE_ADDRESS_8));
        ops.put(0x29, new Op(3, BVS, RELATIVE_ADDRESS_8));
        ops.put(0x2a, new Op(3, BPL, RELATIVE_ADDRESS_8));
        ops.put(0x2b, new Op(3, BMI, RELATIVE_ADDRESS_8));
        ops.put(0x2c, new Op(3, BGE, RELATIVE_ADDRESS_8));
        ops.put(0x2d, new Op(3, BLT, RELATIVE_ADDRESS_8));
        ops.put(0x2e, new Op(3, BGT, RELATIVE_ADDRESS_8));
        ops.put(0x2f, new Op(3, BLE, RELATIVE_ADDRESS_8));

        ops.put(0x30, new Op(4, LEAX, INDEXED_ADDRESS));
        ops.put(0x31, new Op(4, LEAY, INDEXED_ADDRESS));
        ops.put(0x32, new Op(4, LEAS, INDEXED_ADDRESS));
        ops.put(0x33, new Op(4, LEAU, INDEXED_ADDRESS));
        ops.put(0x34, new Op(1, PSHS, IMMEDIATE_VALUE_8));
        ops.put(0x35, new Op(1, PULS, IMMEDIATE_VALUE_8));
        ops.put(0x36, new Op(1, PSHU, IMMEDIATE_VALUE_8));
        ops.put(0x37, new Op(1, PULU, IMMEDIATE_VALUE_8));
        ops.put(0x39, new Op(1, RTS, INHERENT));
        ops.put(0x3a, new Op(3, ABX, INHERENT));
        ops.put(0x3b, new Op(2, RTI, INHERENT));
        ops.put(0x3c, new Op(20, CWAI, INHERENT));
        ops.put(0x3d, new Op(11, MUL, INHERENT));
        ops.put(0x3f, new Op(19, SWI, INHERENT));  // TODO remove cycles of stack operation when implemented

        ops.put(0x40, new Op(2, NEGA, INHERENT));
        ops.put(0x43, new Op(2, COMA, INHERENT));
        ops.put(0x44, new Op(2, LSRA, INHERENT));
        ops.put(0x46, new Op(2, RORA, INHERENT));
        ops.put(0x47, new Op(2, ASRA, INHERENT));
        ops.put(0x48, new Op(2, LSLA, INHERENT));
        ops.put(0x49, new Op(2, ROLA, INHERENT));
        ops.put(0x4a, new Op(2, DECA, INHERENT));
        ops.put(0x4c, new Op(2, INCA, INHERENT));
        ops.put(0x4d, new Op(2, TSTA, INHERENT));
        ops.put(0x4f, new Op(2, CLRA, INHERENT));

        ops.put(0x50, new Op(2, NEGB, INHERENT));
        ops.put(0x53, new Op(2, COMB, INHERENT));
        ops.put(0x54, new Op(2, LSRB, INHERENT));
        ops.put(0x56, new Op(2, RORB, INHERENT));
        ops.put(0x57, new Op(2, ASRB, INHERENT));
        ops.put(0x58, new Op(2, LSLB, INHERENT));
        ops.put(0x59, new Op(2, ROLB, INHERENT));
        ops.put(0x5a, new Op(2, DECB, INHERENT));
        ops.put(0x5c, new Op(2, INCB, INHERENT));
        ops.put(0x5d, new Op(2, TSTB, INHERENT));
        ops.put(0x5f, new Op(2, CLRB, INHERENT));

        ops.put(0x60, new Op(6, NEG, INDEXED_DATA_8));
        ops.put(0x63, new Op(6, COM, INDEXED_DATA_8));
        ops.put(0x64, new Op(6, LSR, INDEXED_DATA_8));
        ops.put(0x66, new Op(6, ROR, INDEXED_DATA_8));
        ops.put(0x67, new Op(6, ASR, INDEXED_DATA_8));
        ops.put(0x68, new Op(6, LSL, INDEXED_DATA_8));
        ops.put(0x69, new Op(6, ROL, INDEXED_DATA_8));
        ops.put(0x6a, new Op(6, DEC, INDEXED_DATA_8));
        ops.put(0x6c, new Op(6, INC, INDEXED_DATA_8));
        ops.put(0x6d, new Op(6, TST, INDEXED_DATA_8));
        ops.put(0x6e, new Op(6, JMP, INDEXED_ADDRESS));
        ops.put(0x6f, new Op(6, CLR, INDEXED_DATA_8));

        ops.put(0x70, new Op(5, NEG, EXTENDED_DATA_8));
        ops.put(0x73, new Op(5, COM, EXTENDED_DATA_8));
        ops.put(0x74, new Op(5, LSR, EXTENDED_DATA_8));
        ops.put(0x76, new Op(5, ROR, EXTENDED_DATA_8));
        ops.put(0x77, new Op(5, ASR, EXTENDED_DATA_8));
        ops.put(0x78, new Op(5, LSL, EXTENDED_DATA_8));
        ops.put(0x79, new Op(5, ROL, EXTENDED_DATA_8));
        ops.put(0x7a, new Op(5, DEC, EXTENDED_DATA_8));
        ops.put(0x7c, new Op(5, INC, EXTENDED_DATA_8));
        ops.put(0x7d, new Op(5, TST, EXTENDED_DATA_8));
        ops.put(0x7e, new Op(5, JMP, EXTENDED_ADDRESS));
        ops.put(0x7f, new Op(5, CLR, EXTENDED_DATA_8));

        ops.put(0x80, new Op(2, SUBA, IMMEDIATE_VALUE_8));
        ops.put(0x81, new Op(2, CMPA, IMMEDIATE_VALUE_8));
        ops.put(0x82, new Op(2, SBCA, IMMEDIATE_VALUE_8));
        ops.put(0x83, new Op(4, SUBD, IMMEDIATE_VALUE_16));
        ops.put(0x84, new Op(2, ANDA, IMMEDIATE_VALUE_8));
        ops.put(0x85, new Op(2, BITA, IMMEDIATE_VALUE_8));
        ops.put(0x86, new Op(2, LDA, IMMEDIATE_VALUE_8));
        ops.put(0x88, new Op(2, EORA, IMMEDIATE_VALUE_8));
        ops.put(0x89, new Op(2, ADCA, IMMEDIATE_VALUE_8));
        ops.put(0x8a, new Op(2, ORA, IMMEDIATE_VALUE_8));
        ops.put(0x8b, new Op(2, ADDA, IMMEDIATE_VALUE_8));
        ops.put(0x8c, new Op(4, CMPX, IMMEDIATE_VALUE_16));
        ops.put(0x8d, new Op(5, BSR, RELATIVE_ADDRESS_8));
        ops.put(0x8e, new Op(3, LDX, IMMEDIATE_VALUE_16));

        ops.put(0x90, new Op(4, SUBA, DIRECT_DATA_8));
        ops.put(0x91, new Op(4, CMPA, DIRECT_DATA_8));
        ops.put(0x92, new Op(4, SBCA, DIRECT_DATA_8));
        ops.put(0x93, new Op(6, SUBD, DIRECT_DATA_16));
        ops.put(0x94, new Op(4, ANDA, DIRECT_DATA_8));
        ops.put(0x95, new Op(4, BITA, DIRECT_DATA_8));
        ops.put(0x96, new Op(4, LDA, DIRECT_DATA_8));
        ops.put(0x97, new Op(4, STA, DIRECT_DATA_8));
        ops.put(0x98, new Op(4, EORA, DIRECT_DATA_8));
        ops.put(0x99, new Op(4, ADCA, DIRECT_DATA_8));
        ops.put(0x9a, new Op(4, ORA, DIRECT_DATA_8));
        ops.put(0x9b, new Op(4, ADDA, DIRECT_DATA_8));
        ops.put(0x9c, new Op(6, CMPX, DIRECT_DATA_16));
        ops.put(0x9d, new Op(3, JSR, DIRECT_ADDRESS));
        ops.put(0x9e, new Op(5, LDX, DIRECT_DATA_16));
        ops.put(0x9f, new Op(5, STX, DIRECT_DATA_16));

        ops.put(0xa0, new Op(4, SUBA, INDEXED_DATA_8));
        ops.put(0xa1, new Op(4, CMPA, INDEXED_DATA_8));
        ops.put(0xa2, new Op(4, SBCA, INDEXED_DATA_8));
        ops.put(0xa3, new Op(6, SUBD, INDEXED_DATA_16));
        ops.put(0xa4, new Op(4, ANDA, INDEXED_DATA_8));
        ops.put(0xa5, new Op(4, BITA, INDEXED_DATA_8));
        ops.put(0xa6, new Op(4, LDA, INDEXED_DATA_8));
        ops.put(0xa7, new Op(4, STA, INDEXED_DATA_8));
        ops.put(0xa8, new Op(4, EORA, INDEXED_DATA_8));
        ops.put(0xa9, new Op(4, ADCA, INDEXED_DATA_8));
        ops.put(0xaa, new Op(4, ORA, INDEXED_DATA_8));
        ops.put(0xab, new Op(4, ADDA, INDEXED_DATA_8));
        ops.put(0xac, new Op(6, CMPX, INDEXED_DATA_16));
        ops.put(0xad, new Op(3, JSR, INDEXED_ADDRESS));
        ops.put(0xae, new Op(5, LDX, INDEXED_DATA_16));
        ops.put(0xaf, new Op(5, STX, INDEXED_DATA_16));

        ops.put(0xb0, new Op(5, SUBA, EXTENDED_DATA_8));
        ops.put(0xb1, new Op(5, CMPA, EXTENDED_DATA_8));
        ops.put(0xb2, new Op(5, SBCA, EXTENDED_DATA_8));
        ops.put(0xb3, new Op(7, SUBD, EXTENDED_DATA_8));
        ops.put(0xb4, new Op(5, ANDA, EXTENDED_DATA_8));
        ops.put(0xb5, new Op(5, BITA, EXTENDED_DATA_8));
        ops.put(0xb6, new Op(5, LDA, EXTENDED_DATA_8));
        ops.put(0xb7, new Op(5, STA, EXTENDED_DATA_8));
        ops.put(0xb8, new Op(5, EORA, EXTENDED_DATA_8));
        ops.put(0xb9, new Op(5, ADCA, EXTENDED_DATA_8));
        ops.put(0xba, new Op(5, ORA, EXTENDED_DATA_8));
        ops.put(0xbb, new Op(5, ADDA, EXTENDED_DATA_8));
        ops.put(0xbc, new Op(7, CMPX, EXTENDED_DATA_16));
        ops.put(0xbd, new Op(4, JSR, EXTENDED_ADDRESS));
        ops.put(0xbe, new Op(6, LDX, EXTENDED_DATA_16));
        ops.put(0xbf, new Op(6, STX, EXTENDED_DATA_16));

        ops.put(0xc0, new Op(2, SUBB, IMMEDIATE_VALUE_8));
        ops.put(0xc1, new Op(2, CMPB, IMMEDIATE_VALUE_8));
        ops.put(0xc2, new Op(2, SBCB, IMMEDIATE_VALUE_8));
        ops.put(0xc3, new Op(4, ADDD, IMMEDIATE_VALUE_16));
        ops.put(0xc4, new Op(2, ANDB, IMMEDIATE_VALUE_8));
        ops.put(0xc5, new Op(2, BITB, IMMEDIATE_VALUE_8));
        ops.put(0xc6, new Op(2, LDB, IMMEDIATE_VALUE_8));
        ops.put(0xc8, new Op(2, EORB, IMMEDIATE_VALUE_8));
        ops.put(0xc9, new Op(2, ADCB, IMMEDIATE_VALUE_8));
        ops.put(0xca, new Op(2, ORB, IMMEDIATE_VALUE_8));
        ops.put(0xcb, new Op(2, ADDB, IMMEDIATE_VALUE_8));
        ops.put(0xcc, new Op(3, LDD, IMMEDIATE_VALUE_16));
        ops.put(0xce, new Op(3, LDU, IMMEDIATE_VALUE_16));

        ops.put(0xd0, new Op(4, SUBB, DIRECT_DATA_8));
        ops.put(0xd1, new Op(4, CMPB, DIRECT_DATA_8));
        ops.put(0xd2, new Op(4, SBCB, DIRECT_DATA_8));
        ops.put(0xd3, new Op(6, ADDD, DIRECT_DATA_16));
        ops.put(0xd4, new Op(4, ANDB, DIRECT_DATA_8));
        ops.put(0xd5, new Op(4, BITB, DIRECT_DATA_8));
        ops.put(0xd6, new Op(4, LDB, DIRECT_DATA_8));
        ops.put(0xd7, new Op(4, STB, DIRECT_DATA_8));
        ops.put(0xd8, new Op(4, EORB, DIRECT_DATA_8));
        ops.put(0xd9, new Op(4, ADCB, DIRECT_DATA_8));
        ops.put(0xda, new Op(4, ORB, DIRECT_DATA_8));
        ops.put(0xdb, new Op(4, ADDB, DIRECT_DATA_8));
        ops.put(0xdc, new Op(5, LDD, DIRECT_DATA_16));
        ops.put(0xdd, new Op(5, STD, DIRECT_DATA_16));
        ops.put(0xde, new Op(5, LDU, DIRECT_DATA_16));
        ops.put(0xdf, new Op(5, STU, DIRECT_DATA_16));

        ops.put(0xe0, new Op(4, SUBB, INDEXED_DATA_8));
        ops.put(0xe1, new Op(4, CMPB, INDEXED_DATA_8));
        ops.put(0xe2, new Op(4, SBCB, INDEXED_DATA_8));
        ops.put(0xe3, new Op(6, ADDD, INDEXED_DATA_16));
        ops.put(0xe4, new Op(4, ANDB, INDEXED_DATA_8));
        ops.put(0xe5, new Op(4, BITB, INDEXED_DATA_8));
        ops.put(0xe6, new Op(4, LDB, INDEXED_DATA_8));
        ops.put(0xe7, new Op(4, STB, INDEXED_DATA_8));
        ops.put(0xe8, new Op(4, EORB, INDEXED_DATA_8));
        ops.put(0xe9, new Op(4, ADCB, INDEXED_DATA_8));
        ops.put(0xea, new Op(4, ORB, INDEXED_DATA_8));
        ops.put(0xeb, new Op(4, ADDB, INDEXED_DATA_8));
        ops.put(0xec, new Op(5, LDD, INDEXED_DATA_16));
        ops.put(0xed, new Op(5, STD, INDEXED_DATA_16));
        ops.put(0xee, new Op(5, LDU, INDEXED_DATA_16));
        ops.put(0xef, new Op(5, STU, INDEXED_DATA_16));

        ops.put(0xf0, new Op(5, SUBB, EXTENDED_DATA_8));
        ops.put(0xf1, new Op(5, CMPB, EXTENDED_DATA_8));
        ops.put(0xf2, new Op(5, SBCB, EXTENDED_DATA_8));
        ops.put(0xf3, new Op(7, ADDD, EXTENDED_DATA_16));
        ops.put(0xf4, new Op(5, ANDB, EXTENDED_DATA_8));
        ops.put(0xf5, new Op(5, BITB, EXTENDED_DATA_8));
        ops.put(0xf6, new Op(5, LDB, EXTENDED_DATA_8));
        ops.put(0xf7, new Op(5, STB, EXTENDED_DATA_8));
        ops.put(0xf8, new Op(5, EORB, EXTENDED_DATA_8));
        ops.put(0xf9, new Op(5, ADCB, EXTENDED_DATA_8));
        ops.put(0xfa, new Op(5, ORB, EXTENDED_DATA_8));
        ops.put(0xfb, new Op(5, ADDB, EXTENDED_DATA_8));
        ops.put(0xfc, new Op(6, LDD, EXTENDED_DATA_16));
        ops.put(0xfd, new Op(6, STD, EXTENDED_DATA_16));
        ops.put(0xfe, new Op(6, LDU, EXTENDED_DATA_16));
        ops.put(0xff, new Op(6, STU, EXTENDED_DATA_16));

        ops.put(0x1021, new Op(5, LBRN, RELATIVE_ADDRESS_16));
        ops.put(0x1022, new Op(5, LBHI, RELATIVE_ADDRESS_16)); // TODO 6 cycles if branching
        ops.put(0x1023, new Op(5, LBLS, RELATIVE_ADDRESS_16)); // TODO 6 cycles if branching
        ops.put(0x1024, new Op(5, LBCC, RELATIVE_ADDRESS_16)); // TODO 6 cycles if branching
        ops.put(0x1025, new Op(5, LBCS, RELATIVE_ADDRESS_16)); // TODO 6 cycles if branching
        ops.put(0x1026, new Op(5, LBNE, RELATIVE_ADDRESS_16)); // TODO 6 cycles if branching
        ops.put(0x1027, new Op(5, LBEQ, RELATIVE_ADDRESS_16)); // TODO 6 cycles if branching
        ops.put(0x1028, new Op(5, LBVC, RELATIVE_ADDRESS_16)); // TODO 6 cycles if branching
        ops.put(0x1029, new Op(5, LBVS, RELATIVE_ADDRESS_16)); // TODO 6 cycles if branching
        ops.put(0x102a, new Op(5, LBPL, RELATIVE_ADDRESS_16)); // TODO 6 cycles if branching
        ops.put(0x102b, new Op(5, LBMI, RELATIVE_ADDRESS_16)); // TODO 6 cycles if branching
        ops.put(0x102c, new Op(5, LBGE, RELATIVE_ADDRESS_16)); // TODO 6 cycles if branching
        ops.put(0x102d, new Op(5, LBLT, RELATIVE_ADDRESS_16)); // TODO 6 cycles if branching
        ops.put(0x102e, new Op(5, LBGT, RELATIVE_ADDRESS_16)); // TODO 6 cycles if branching
        ops.put(0x102f, new Op(5, LBLE, RELATIVE_ADDRESS_16)); // TODO 6 cycles if branching

        ops.put(0x103f, new Op(20, SWI2, INHERENT)); // TODO remove cycles of stack operation when implemented
        ops.put(0x1083, new Op(5, CMPD, IMMEDIATE_VALUE_16));
        ops.put(0x108c, new Op(5, CMPY, IMMEDIATE_VALUE_16));
        ops.put(0x108e, new Op(4, LDY, IMMEDIATE_VALUE_16));
        ops.put(0x1093, new Op(7, CMPD, DIRECT_DATA_16));
        ops.put(0x109c, new Op(7, CMPY, DIRECT_DATA_16));
        ops.put(0x109e, new Op(6, LDY, DIRECT_DATA_16));
        ops.put(0x109f, new Op(6, STY, DIRECT_DATA_16));

        ops.put(0x10a3, new Op(7, CMPD, INDEXED_DATA_16));
        ops.put(0x10ac, new Op(7, CMPY, INDEXED_DATA_16));
        ops.put(0x10ae, new Op(6, LDY, INDEXED_DATA_16));
        ops.put(0x10af, new Op(6, STY, INDEXED_DATA_16));
        ops.put(0x10b3, new Op(8, CMPD, EXTENDED_DATA_16));
        ops.put(0x10bc, new Op(8, CMPY, EXTENDED_DATA_16));
        ops.put(0x10be, new Op(7, LDY, EXTENDED_DATA_16));
        ops.put(0x10bf, new Op(7, STY, EXTENDED_DATA_16));
        ops.put(0x10ce, new Op(4, LDS, IMMEDIATE_VALUE_16));
        ops.put(0x10de, new Op(6, LDS, DIRECT_DATA_16));
        ops.put(0x10df, new Op(6, STS, DIRECT_DATA_16));
        ops.put(0x10ee, new Op(6, LDS, INDEXED_DATA_16));
        ops.put(0x10ef, new Op(6, STS, INDEXED_DATA_16));
        ops.put(0x10fe, new Op(7, LDS, EXTENDED_DATA_16));
        ops.put(0x10ff, new Op(7, STS, EXTENDED_DATA_16));

        ops.put(0x113f, new Op(20, SWI3, INHERENT)); // TODO remove cycles of stack operation when implemented
        ops.put(0x1183, new Op(5, CMPU, IMMEDIATE_VALUE_16));
        ops.put(0x118c, new Op(5, CMPS, IMMEDIATE_VALUE_16));
        ops.put(0x1193, new Op(7, CMPU, DIRECT_DATA_16));
        ops.put(0x119c, new Op(7, CMPS, DIRECT_DATA_16));
        ops.put(0x11a3, new Op(7, CMPU, INDEXED_DATA_16));
        ops.put(0x11ac, new Op(7, CMPS, INDEXED_DATA_16));
        ops.put(0x11b3, new Op(8, CMPU, EXTENDED_DATA_16));
        ops.put(0x11bc, new Op(8, CMPS, EXTENDED_DATA_16));
    }

    static {
        for (var code : ops.keySet()) {
            if (code > 0xff) extended.add(code >> 8);
        }
    }

    private final Mnemonic mnemonic;
    private final Addressing addressing;
    private final int cycles;

    private Op(int cycles, Mnemonic mnemonic, Addressing addressing) {
        this.cycles = cycles;
        this.mnemonic = mnemonic;
        this.addressing = addressing;
    }

    public static Op next(MemoryMapped memory, DataAccess programCounter) {
        final int code = nextCode(memory, programCounter);
        var instruction = ops.get(code);
        if (instruction == null)
            throw new UnsupportedOperationException("Invalid Op code : 0x" + Integer.toHexString(code));
        return instruction;
    }

    private static int nextCode(MemoryMapped memory, DataAccess programCounter) {
        int op = Reference.next(memory, Size.WORD_8, programCounter).unsigned();
        if (extended.contains(op)) op = op << 8 | Reference.next(memory, Size.WORD_8, programCounter).unsigned();
        return op;
    }

    public Mnemonic mnemonic() {
        return mnemonic;
    }

    public Addressing addressing() {
        return addressing;
    }

    public int cycles() {
        return cycles;
    }

    @Override
    public String toString() {
        return "" + mnemonic + ' ' + addressing;
    }
}
