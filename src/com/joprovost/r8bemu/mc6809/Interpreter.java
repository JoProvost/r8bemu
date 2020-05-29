package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.Instruction;
import com.joprovost.r8bemu.data.Reference;
import com.joprovost.r8bemu.data.Size;
import com.joprovost.r8bemu.memory.MemoryMapped;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.joprovost.r8bemu.Instruction.op;
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

public class Interpreter {

    private static final List<Instruction<Mnemonic>> instructions;

    static {
        instructions = new ArrayList<>();
        instructions.add(op(0x00, NEG, DIRECT_DATA_8));
        instructions.add(op(0x03, COM, DIRECT_DATA_8));
        instructions.add(op(0x04, LSR, DIRECT_DATA_8));
        instructions.add(op(0x06, ROR, DIRECT_DATA_8));
        instructions.add(op(0x07, ASR, DIRECT_DATA_8));
        instructions.add(op(0x08, LSL, DIRECT_DATA_8)); // ASL
        instructions.add(op(0x09, ROL, DIRECT_DATA_8));
        instructions.add(op(0x0a, DEC, DIRECT_DATA_8));
        instructions.add(op(0x0c, INC, DIRECT_DATA_8));
        instructions.add(op(0x0d, TST, DIRECT_DATA_8));
        instructions.add(op(0x0e, JMP, DIRECT_ADDRESS));
        instructions.add(op(0x0f, CLR, DIRECT_DATA_8));

        instructions.add(op(0x12, NOP, INHERENT));
        instructions.add(op(0x13, SYNC, INHERENT));
        instructions.add(op(0x16, LBRA, RELATIVE_ADDRESS_16));
        instructions.add(op(0x17, LBSR, RELATIVE_ADDRESS_16));
        instructions.add(op(0x19, DAA, INHERENT));
        instructions.add(op(0x1a, ORCC, IMMEDIATE_VALUE_8));
        instructions.add(op(0x1c, ANDCC, IMMEDIATE_VALUE_8));
        instructions.add(op(0x1d, SEX, INHERENT));
        instructions.add(op(0x1e, EXG, IMMEDIATE_VALUE_8));
        instructions.add(op(0x1f, TFR, IMMEDIATE_VALUE_8));

        instructions.add(op(0x20, BRA, RELATIVE_ADDRESS_8));
        instructions.add(op(0x21, BRN, RELATIVE_ADDRESS_8));
        instructions.add(op(0x22, BHI, RELATIVE_ADDRESS_8));
        instructions.add(op(0x23, BLS, RELATIVE_ADDRESS_8));
        instructions.add(op(0x24, BCC, RELATIVE_ADDRESS_8)); // BHS
        instructions.add(op(0x25, BCS, RELATIVE_ADDRESS_8)); // BLO
        instructions.add(op(0x26, BNE, RELATIVE_ADDRESS_8));
        instructions.add(op(0x27, BEQ, RELATIVE_ADDRESS_8));
        instructions.add(op(0x28, BVC, RELATIVE_ADDRESS_8));
        instructions.add(op(0x29, BVS, RELATIVE_ADDRESS_8));
        instructions.add(op(0x2a, BPL, RELATIVE_ADDRESS_8));
        instructions.add(op(0x2b, BMI, RELATIVE_ADDRESS_8));
        instructions.add(op(0x2c, BGE, RELATIVE_ADDRESS_8));
        instructions.add(op(0x2d, BLT, RELATIVE_ADDRESS_8));
        instructions.add(op(0x2e, BGT, RELATIVE_ADDRESS_8));
        instructions.add(op(0x2f, BLE, RELATIVE_ADDRESS_8));

        instructions.add(op(0x30, LEAX, INDEXED_ADDRESS));
        instructions.add(op(0x31, LEAY, INDEXED_ADDRESS));
        instructions.add(op(0x32, LEAS, INDEXED_ADDRESS));
        instructions.add(op(0x33, LEAU, INDEXED_ADDRESS));
        instructions.add(op(0x34, PSHS, IMMEDIATE_VALUE_8));
        instructions.add(op(0x35, PULS, IMMEDIATE_VALUE_8));
        instructions.add(op(0x36, PSHU, IMMEDIATE_VALUE_8));
        instructions.add(op(0x37, PULU, IMMEDIATE_VALUE_8));
        instructions.add(op(0x39, RTS, INHERENT));
        instructions.add(op(0x3a, ABX, INHERENT));
        instructions.add(op(0x3b, RTI, INHERENT));
        instructions.add(op(0x3c, CWAI, INHERENT));
        instructions.add(op(0x3d, MUL, INHERENT));
        instructions.add(op(0x3f, SWI, INHERENT));

        instructions.add(op(0x40, NEGA, INHERENT));
        instructions.add(op(0x43, COMA, INHERENT));
        instructions.add(op(0x44, LSRA, INHERENT));
        instructions.add(op(0x46, RORA, INHERENT));
        instructions.add(op(0x47, ASRA, INHERENT));
        instructions.add(op(0x48, LSLA, INHERENT)); // ASLA
        instructions.add(op(0x49, ROLA, INHERENT));
        instructions.add(op(0x4a, DECA, INHERENT));
        instructions.add(op(0x4c, INCA, INHERENT));
        instructions.add(op(0x4d, TSTA, INHERENT));
        instructions.add(op(0x4f, CLRA, INHERENT));

        instructions.add(op(0x50, NEGB, INHERENT));
        instructions.add(op(0x53, COMB, INHERENT));
        instructions.add(op(0x54, LSRB, INHERENT));
        instructions.add(op(0x56, RORB, INHERENT));
        instructions.add(op(0x57, ASRB, INHERENT));
        instructions.add(op(0x58, LSLB, INHERENT)); // ASLB
        instructions.add(op(0x59, ROLB, INHERENT));
        instructions.add(op(0x5a, DECB, INHERENT));
        instructions.add(op(0x5c, INCB, INHERENT));
        instructions.add(op(0x5d, TSTB, INHERENT));
        instructions.add(op(0x5f, CLRB, INHERENT));

        instructions.add(op(0x60, NEG, INDEXED_DATA_8));
        instructions.add(op(0x63, COM, INDEXED_DATA_8));
        instructions.add(op(0x64, LSR, INDEXED_DATA_8));
        instructions.add(op(0x66, ROR, INDEXED_DATA_8));
        instructions.add(op(0x67, ASR, INDEXED_DATA_8));
        instructions.add(op(0x68, LSL, INDEXED_DATA_8)); // ASL
        instructions.add(op(0x69, ROL, INDEXED_DATA_8));
        instructions.add(op(0x6a, DEC, INDEXED_DATA_8));
        instructions.add(op(0x6c, INC, INDEXED_DATA_8));
        instructions.add(op(0x6d, TST, INDEXED_DATA_8));
        instructions.add(op(0x6e, JMP, INDEXED_ADDRESS));
        instructions.add(op(0x6f, CLR, INDEXED_DATA_8));

        instructions.add(op(0x70, NEG, EXTENDED_DATA_8));
        instructions.add(op(0x73, COM, EXTENDED_DATA_8));
        instructions.add(op(0x74, LSR, EXTENDED_DATA_8));
        instructions.add(op(0x76, ROR, EXTENDED_DATA_8));
        instructions.add(op(0x77, ASR, EXTENDED_DATA_8));
        instructions.add(op(0x78, LSL, EXTENDED_DATA_8)); // ASL
        instructions.add(op(0x79, ROL, EXTENDED_DATA_8));
        instructions.add(op(0x7a, DEC, EXTENDED_DATA_8));
        instructions.add(op(0x7c, INC, EXTENDED_DATA_8));
        instructions.add(op(0x7d, TST, EXTENDED_DATA_8));
        instructions.add(op(0x7e, JMP, EXTENDED_ADDRESS));
        instructions.add(op(0x7f, CLR, EXTENDED_DATA_8));

        instructions.add(op(0x80, SUBA, IMMEDIATE_VALUE_8));
        instructions.add(op(0x81, CMPA, IMMEDIATE_VALUE_8));
        instructions.add(op(0x82, SBCA, IMMEDIATE_VALUE_8));
        instructions.add(op(0x83, SUBD, IMMEDIATE_VALUE_16));
        instructions.add(op(0x84, ANDA, IMMEDIATE_VALUE_8));
        instructions.add(op(0x85, BITA, IMMEDIATE_VALUE_8));
        instructions.add(op(0x86, LDA, IMMEDIATE_VALUE_8));
        instructions.add(op(0x88, EORA, IMMEDIATE_VALUE_8));
        instructions.add(op(0x89, ADCA, IMMEDIATE_VALUE_8));
        instructions.add(op(0x8a, ORA, IMMEDIATE_VALUE_8));
        instructions.add(op(0x8b, ADDA, IMMEDIATE_VALUE_8));
        instructions.add(op(0x8c, CMPX, IMMEDIATE_VALUE_16));
        instructions.add(op(0x8d, BSR, RELATIVE_ADDRESS_8));
        instructions.add(op(0x8e, LDX, IMMEDIATE_VALUE_16));

        instructions.add(op(0x90, SUBA, DIRECT_DATA_8));
        instructions.add(op(0x91, CMPA, DIRECT_DATA_8));
        instructions.add(op(0x92, SBCA, DIRECT_DATA_8));
        instructions.add(op(0x93, SUBD, DIRECT_DATA_16));
        instructions.add(op(0x94, ANDA, DIRECT_DATA_8));
        instructions.add(op(0x95, BITA, DIRECT_DATA_8));
        instructions.add(op(0x96, LDA, DIRECT_DATA_8));
        instructions.add(op(0x97, STA, DIRECT_DATA_8));
        instructions.add(op(0x98, EORA, DIRECT_DATA_8));
        instructions.add(op(0x99, ADCA, DIRECT_DATA_8));
        instructions.add(op(0x9a, ORA, DIRECT_DATA_8));
        instructions.add(op(0x9b, ADDA, DIRECT_DATA_8));
        instructions.add(op(0x9c, CMPX, DIRECT_DATA_16));
        instructions.add(op(0x9d, JSR, DIRECT_ADDRESS));
        instructions.add(op(0x9e, LDX, DIRECT_DATA_16));
        instructions.add(op(0x9f, STX, DIRECT_DATA_16));

        instructions.add(op(0xa0, SUBA, INDEXED_DATA_8));
        instructions.add(op(0xa1, CMPA, INDEXED_DATA_8));
        instructions.add(op(0xa2, SBCA, INDEXED_DATA_8));
        instructions.add(op(0xa3, SUBD, INDEXED_DATA_16));
        instructions.add(op(0xa4, ANDA, INDEXED_DATA_8));
        instructions.add(op(0xa5, BITA, INDEXED_DATA_8));
        instructions.add(op(0xa6, LDA, INDEXED_DATA_8));
        instructions.add(op(0xa7, STA, INDEXED_DATA_8));
        instructions.add(op(0xa8, EORA, INDEXED_DATA_8));
        instructions.add(op(0xa9, ADCA, INDEXED_DATA_8));
        instructions.add(op(0xaa, ORA, INDEXED_DATA_8));
        instructions.add(op(0xab, ADDA, INDEXED_DATA_8));
        instructions.add(op(0xac, CMPX, INDEXED_DATA_16));
        instructions.add(op(0xad, JSR, INDEXED_ADDRESS));
        instructions.add(op(0xae, LDX, INDEXED_DATA_16));
        instructions.add(op(0xaf, STX, INDEXED_DATA_16));

        instructions.add(op(0xb0, SUBA, EXTENDED_DATA_8));
        instructions.add(op(0xb1, CMPA, EXTENDED_DATA_8));
        instructions.add(op(0xb2, SBCA, EXTENDED_DATA_8));
        instructions.add(op(0xb3, SUBD, EXTENDED_DATA_8));
        instructions.add(op(0xb4, ANDA, EXTENDED_DATA_8));
        instructions.add(op(0xb5, BITA, EXTENDED_DATA_8));
        instructions.add(op(0xb6, LDA, EXTENDED_DATA_8));
        instructions.add(op(0xb7, STA, EXTENDED_DATA_8));
        instructions.add(op(0xb8, EORA, EXTENDED_DATA_8));
        instructions.add(op(0xb9, ADCA, EXTENDED_DATA_8));
        instructions.add(op(0xba, ORA, EXTENDED_DATA_8));
        instructions.add(op(0xbb, ADDA, EXTENDED_DATA_8));
        instructions.add(op(0xbc, CMPX, EXTENDED_DATA_16));
        instructions.add(op(0xbd, JSR, EXTENDED_ADDRESS));
        instructions.add(op(0xbe, LDX, EXTENDED_DATA_16));
        instructions.add(op(0xbf, STX, EXTENDED_DATA_16));

        instructions.add(op(0xc0, SUBB, IMMEDIATE_VALUE_8));
        instructions.add(op(0xc1, CMPB, IMMEDIATE_VALUE_8));
        instructions.add(op(0xc2, SBCB, IMMEDIATE_VALUE_8));
        instructions.add(op(0xc3, ADDD, IMMEDIATE_VALUE_16));
        instructions.add(op(0xc4, ANDB, IMMEDIATE_VALUE_8));
        instructions.add(op(0xc5, BITB, IMMEDIATE_VALUE_8));
        instructions.add(op(0xc6, LDB, IMMEDIATE_VALUE_8));
        instructions.add(op(0xc8, EORB, IMMEDIATE_VALUE_8));
        instructions.add(op(0xc9, ADCB, IMMEDIATE_VALUE_8));
        instructions.add(op(0xca, ORB, IMMEDIATE_VALUE_8));
        instructions.add(op(0xcb, ADDB, IMMEDIATE_VALUE_8));
        instructions.add(op(0xcc, LDD, IMMEDIATE_VALUE_16));
        instructions.add(op(0xce, LDU, IMMEDIATE_VALUE_16));

        instructions.add(op(0xd0, SUBB, DIRECT_DATA_8));
        instructions.add(op(0xd1, CMPB, DIRECT_DATA_8));
        instructions.add(op(0xd2, SBCB, DIRECT_DATA_8));
        instructions.add(op(0xd3, ADDD, DIRECT_DATA_16));
        instructions.add(op(0xd4, ANDB, DIRECT_DATA_8));
        instructions.add(op(0xd5, BITB, DIRECT_DATA_8));
        instructions.add(op(0xd6, LDB, DIRECT_DATA_8));
        instructions.add(op(0xd7, STB, DIRECT_DATA_8));
        instructions.add(op(0xd8, EORB, DIRECT_DATA_8));
        instructions.add(op(0xd9, ADCB, DIRECT_DATA_8));
        instructions.add(op(0xda, ORB, DIRECT_DATA_8));
        instructions.add(op(0xdb, ADDB, DIRECT_DATA_8));
        instructions.add(op(0xdc, LDD, DIRECT_DATA_16));
        instructions.add(op(0xdd, STD, DIRECT_DATA_16));
        instructions.add(op(0xde, LDU, DIRECT_DATA_16));
        instructions.add(op(0xdf, STU, DIRECT_DATA_16));

        instructions.add(op(0xe0, SUBB, INDEXED_DATA_8));
        instructions.add(op(0xe1, CMPB, INDEXED_DATA_8));
        instructions.add(op(0xe2, SBCB, INDEXED_DATA_8));
        instructions.add(op(0xe3, ADDD, INDEXED_DATA_16));
        instructions.add(op(0xe4, ANDB, INDEXED_DATA_8));
        instructions.add(op(0xe5, BITB, INDEXED_DATA_8));
        instructions.add(op(0xe6, LDB, INDEXED_DATA_8));
        instructions.add(op(0xe7, STB, INDEXED_DATA_8));
        instructions.add(op(0xe8, EORB, INDEXED_DATA_8));
        instructions.add(op(0xe9, ADCB, INDEXED_DATA_8));
        instructions.add(op(0xea, ORB, INDEXED_DATA_8));
        instructions.add(op(0xeb, ADDB, INDEXED_DATA_8));
        instructions.add(op(0xec, LDD, INDEXED_DATA_16));
        instructions.add(op(0xed, STD, INDEXED_DATA_16));
        instructions.add(op(0xee, LDU, INDEXED_DATA_16));
        instructions.add(op(0xef, STU, INDEXED_DATA_16));

        instructions.add(op(0xf0, SUBB, EXTENDED_DATA_8));
        instructions.add(op(0xf1, CMPB, EXTENDED_DATA_8));
        instructions.add(op(0xf2, SBCB, EXTENDED_DATA_8));
        instructions.add(op(0xf3, ADDD, EXTENDED_DATA_16));
        instructions.add(op(0xf4, ANDB, EXTENDED_DATA_8));
        instructions.add(op(0xf5, BITB, EXTENDED_DATA_8));
        instructions.add(op(0xf6, LDB, EXTENDED_DATA_8));
        instructions.add(op(0xf7, STB, EXTENDED_DATA_8));
        instructions.add(op(0xf8, EORB, EXTENDED_DATA_8));
        instructions.add(op(0xf9, ADCB, EXTENDED_DATA_8));
        instructions.add(op(0xfa, ORB, EXTENDED_DATA_8));
        instructions.add(op(0xfb, ADDB, EXTENDED_DATA_8));
        instructions.add(op(0xfc, LDD, EXTENDED_DATA_16));
        instructions.add(op(0xfd, STD, EXTENDED_DATA_16));
        instructions.add(op(0xfe, LDU, EXTENDED_DATA_16));
        instructions.add(op(0xff, STU, EXTENDED_DATA_16));

        instructions.add(op(0x1021, LBRN, RELATIVE_ADDRESS_16));
        instructions.add(op(0x1022, LBHI, RELATIVE_ADDRESS_16));
        instructions.add(op(0x1023, LBLS, RELATIVE_ADDRESS_16));
        instructions.add(op(0x1024, LBCC, RELATIVE_ADDRESS_16)); // LBHS
        instructions.add(op(0x1025, LBCS, RELATIVE_ADDRESS_16)); // LBLO
        instructions.add(op(0x1026, LBNE, RELATIVE_ADDRESS_16));
        instructions.add(op(0x1027, LBEQ, RELATIVE_ADDRESS_16));
        instructions.add(op(0x1028, LBVC, RELATIVE_ADDRESS_16));
        instructions.add(op(0x1029, LBVS, RELATIVE_ADDRESS_16));
        instructions.add(op(0x102a, LBPL, RELATIVE_ADDRESS_16));
        instructions.add(op(0x102b, LBMI, RELATIVE_ADDRESS_16));
        instructions.add(op(0x102c, LBGE, RELATIVE_ADDRESS_16));
        instructions.add(op(0x102d, LBLT, RELATIVE_ADDRESS_16));
        instructions.add(op(0x102e, LBGT, RELATIVE_ADDRESS_16));
        instructions.add(op(0x102f, LBLE, RELATIVE_ADDRESS_16));

        instructions.add(op(0x103f, SWI2, INHERENT));
        instructions.add(op(0x1083, CMPD, IMMEDIATE_VALUE_16));
        instructions.add(op(0x108c, CMPY, IMMEDIATE_VALUE_16));
        instructions.add(op(0x108e, LDY, IMMEDIATE_VALUE_16));
        instructions.add(op(0x1093, CMPD, DIRECT_DATA_16));
        instructions.add(op(0x109c, CMPY, DIRECT_DATA_16));
        instructions.add(op(0x109e, LDY, DIRECT_DATA_16));
        instructions.add(op(0x109f, STY, DIRECT_DATA_16));
        instructions.add(op(0x10a3, CMPD, INDEXED_DATA_16));
        instructions.add(op(0x10ac, CMPY, INDEXED_DATA_16));
        instructions.add(op(0x10ae, LDY, INDEXED_DATA_16));
        instructions.add(op(0x10af, STY, INDEXED_DATA_16));
        instructions.add(op(0x10b3, CMPD, EXTENDED_DATA_16));
        instructions.add(op(0x10bc, CMPY, EXTENDED_DATA_16));
        instructions.add(op(0x10be, LDY, EXTENDED_DATA_16));
        instructions.add(op(0x10bf, STY, EXTENDED_DATA_16));
        instructions.add(op(0x10ce, LDS, IMMEDIATE_VALUE_16));
        instructions.add(op(0x10de, LDS, DIRECT_DATA_16));
        instructions.add(op(0x10df, STS, DIRECT_DATA_16));
        instructions.add(op(0x10ee, LDS, INDEXED_DATA_16));
        instructions.add(op(0x10ef, STS, INDEXED_DATA_16));
        instructions.add(op(0x10fe, LDS, EXTENDED_DATA_16));
        instructions.add(op(0x10ff, STS, EXTENDED_DATA_16));

        instructions.add(op(0x113f, SWI3, INHERENT));
        instructions.add(op(0x1183, CMPU, IMMEDIATE_VALUE_16));
        instructions.add(op(0x118c, CMPS, IMMEDIATE_VALUE_16));
        instructions.add(op(0x1193, CMPU, DIRECT_DATA_16));
        instructions.add(op(0x119c, CMPS, DIRECT_DATA_16));
        instructions.add(op(0x11a3, CMPU, INDEXED_DATA_16));
        instructions.add(op(0x11ac, CMPS, INDEXED_DATA_16));
        instructions.add(op(0x11b3, CMPU, EXTENDED_DATA_16));
        instructions.add(op(0x11bc, CMPS, EXTENDED_DATA_16));
    }

    private static final Set<Integer> extended = new HashSet<>();

    static {
        for (var instruction : instructions) {
            if (instruction.code() > 0xff) extended.add(instruction.code() >> 8);
        }
    }

    public static Instruction<Mnemonic> next(MemoryMapped memory, Register programCounter) {
        int op = Reference.next(memory, Size.WORD_8, programCounter).unsigned();
        if (extended.contains(op)) op = op << 8 | Reference.next(memory, Size.WORD_8, programCounter).unsigned();
        for (var instruction : instructions) {
            if (instruction.code() == op) return instruction;
        }
        throw new UnsupportedOperationException("Invalid Op code : 0x" + Integer.toHexString(op));
    }
}
