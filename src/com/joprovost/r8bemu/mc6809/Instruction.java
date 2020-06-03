package com.joprovost.r8bemu.mc6809;

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

public class Instruction {
    private static final Map<Integer, Instruction> instructions;
    private static final Set<Integer> extended = new HashSet<>();

    static {
        instructions = new HashMap<>();
        instructions.put(0x00, new Instruction(NEG, DIRECT_DATA_8));
        instructions.put(0x03, new Instruction(COM, DIRECT_DATA_8));
        instructions.put(0x04, new Instruction(LSR, DIRECT_DATA_8));
        instructions.put(0x06, new Instruction(ROR, DIRECT_DATA_8));
        instructions.put(0x07, new Instruction(ASR, DIRECT_DATA_8));
        instructions.put(0x08, new Instruction(LSL, DIRECT_DATA_8)); // ASL
        instructions.put(0x09, new Instruction(ROL, DIRECT_DATA_8));
        instructions.put(0x0a, new Instruction(DEC, DIRECT_DATA_8));
        instructions.put(0x0c, new Instruction(INC, DIRECT_DATA_8));
        instructions.put(0x0d, new Instruction(TST, DIRECT_DATA_8));
        instructions.put(0x0e, new Instruction(JMP, DIRECT_ADDRESS));
        instructions.put(0x0f, new Instruction(CLR, DIRECT_DATA_8));

        instructions.put(0x12, new Instruction(NOP, INHERENT));
        instructions.put(0x13, new Instruction(SYNC, INHERENT));
        instructions.put(0x16, new Instruction(LBRA, RELATIVE_ADDRESS_16));
        instructions.put(0x17, new Instruction(LBSR, RELATIVE_ADDRESS_16));
        instructions.put(0x19, new Instruction(DAA, INHERENT));
        instructions.put(0x1a, new Instruction(ORCC, IMMEDIATE_VALUE_8));
        instructions.put(0x1c, new Instruction(ANDCC, IMMEDIATE_VALUE_8));
        instructions.put(0x1d, new Instruction(SEX, INHERENT));
        instructions.put(0x1e, new Instruction(EXG, IMMEDIATE_VALUE_8));
        instructions.put(0x1f, new Instruction(TFR, IMMEDIATE_VALUE_8));

        instructions.put(0x20, new Instruction(BRA, RELATIVE_ADDRESS_8));
        instructions.put(0x21, new Instruction(BRN, RELATIVE_ADDRESS_8));
        instructions.put(0x22, new Instruction(BHI, RELATIVE_ADDRESS_8));
        instructions.put(0x23, new Instruction(BLS, RELATIVE_ADDRESS_8));
        instructions.put(0x24, new Instruction(BCC, RELATIVE_ADDRESS_8)); // BHS
        instructions.put(0x25, new Instruction(BCS, RELATIVE_ADDRESS_8)); // BLO
        instructions.put(0x26, new Instruction(BNE, RELATIVE_ADDRESS_8));
        instructions.put(0x27, new Instruction(BEQ, RELATIVE_ADDRESS_8));
        instructions.put(0x28, new Instruction(BVC, RELATIVE_ADDRESS_8));
        instructions.put(0x29, new Instruction(BVS, RELATIVE_ADDRESS_8));
        instructions.put(0x2a, new Instruction(BPL, RELATIVE_ADDRESS_8));
        instructions.put(0x2b, new Instruction(BMI, RELATIVE_ADDRESS_8));
        instructions.put(0x2c, new Instruction(BGE, RELATIVE_ADDRESS_8));
        instructions.put(0x2d, new Instruction(BLT, RELATIVE_ADDRESS_8));
        instructions.put(0x2e, new Instruction(BGT, RELATIVE_ADDRESS_8));
        instructions.put(0x2f, new Instruction(BLE, RELATIVE_ADDRESS_8));

        instructions.put(0x30, new Instruction(LEAX, INDEXED_ADDRESS));
        instructions.put(0x31, new Instruction(LEAY, INDEXED_ADDRESS));
        instructions.put(0x32, new Instruction(LEAS, INDEXED_ADDRESS));
        instructions.put(0x33, new Instruction(LEAU, INDEXED_ADDRESS));
        instructions.put(0x34, new Instruction(PSHS, IMMEDIATE_VALUE_8));
        instructions.put(0x35, new Instruction(PULS, IMMEDIATE_VALUE_8));
        instructions.put(0x36, new Instruction(PSHU, IMMEDIATE_VALUE_8));
        instructions.put(0x37, new Instruction(PULU, IMMEDIATE_VALUE_8));
        instructions.put(0x39, new Instruction(RTS, INHERENT));
        instructions.put(0x3a, new Instruction(ABX, INHERENT));
        instructions.put(0x3b, new Instruction(RTI, INHERENT));
        instructions.put(0x3c, new Instruction(CWAI, INHERENT));
        instructions.put(0x3d, new Instruction(MUL, INHERENT));
        instructions.put(0x3f, new Instruction(SWI, INHERENT));

        instructions.put(0x40, new Instruction(NEGA, INHERENT));
        instructions.put(0x43, new Instruction(COMA, INHERENT));
        instructions.put(0x44, new Instruction(LSRA, INHERENT));
        instructions.put(0x46, new Instruction(RORA, INHERENT));
        instructions.put(0x47, new Instruction(ASRA, INHERENT));
        instructions.put(0x48, new Instruction(LSLA, INHERENT)); // ASLA
        instructions.put(0x49, new Instruction(ROLA, INHERENT));
        instructions.put(0x4a, new Instruction(DECA, INHERENT));
        instructions.put(0x4c, new Instruction(INCA, INHERENT));
        instructions.put(0x4d, new Instruction(TSTA, INHERENT));
        instructions.put(0x4f, new Instruction(CLRA, INHERENT));

        instructions.put(0x50, new Instruction(NEGB, INHERENT));
        instructions.put(0x53, new Instruction(COMB, INHERENT));
        instructions.put(0x54, new Instruction(LSRB, INHERENT));
        instructions.put(0x56, new Instruction(RORB, INHERENT));
        instructions.put(0x57, new Instruction(ASRB, INHERENT));
        instructions.put(0x58, new Instruction(LSLB, INHERENT)); // ASLB
        instructions.put(0x59, new Instruction(ROLB, INHERENT));
        instructions.put(0x5a, new Instruction(DECB, INHERENT));
        instructions.put(0x5c, new Instruction(INCB, INHERENT));
        instructions.put(0x5d, new Instruction(TSTB, INHERENT));
        instructions.put(0x5f, new Instruction(CLRB, INHERENT));

        instructions.put(0x60, new Instruction(NEG, INDEXED_DATA_8));
        instructions.put(0x63, new Instruction(COM, INDEXED_DATA_8));
        instructions.put(0x64, new Instruction(LSR, INDEXED_DATA_8));
        instructions.put(0x66, new Instruction(ROR, INDEXED_DATA_8));
        instructions.put(0x67, new Instruction(ASR, INDEXED_DATA_8));
        instructions.put(0x68, new Instruction(LSL, INDEXED_DATA_8)); // ASL
        instructions.put(0x69, new Instruction(ROL, INDEXED_DATA_8));
        instructions.put(0x6a, new Instruction(DEC, INDEXED_DATA_8));
        instructions.put(0x6c, new Instruction(INC, INDEXED_DATA_8));
        instructions.put(0x6d, new Instruction(TST, INDEXED_DATA_8));
        instructions.put(0x6e, new Instruction(JMP, INDEXED_ADDRESS));
        instructions.put(0x6f, new Instruction(CLR, INDEXED_DATA_8));

        instructions.put(0x70, new Instruction(NEG, EXTENDED_DATA_8));
        instructions.put(0x73, new Instruction(COM, EXTENDED_DATA_8));
        instructions.put(0x74, new Instruction(LSR, EXTENDED_DATA_8));
        instructions.put(0x76, new Instruction(ROR, EXTENDED_DATA_8));
        instructions.put(0x77, new Instruction(ASR, EXTENDED_DATA_8));
        instructions.put(0x78, new Instruction(LSL, EXTENDED_DATA_8)); // ASL
        instructions.put(0x79, new Instruction(ROL, EXTENDED_DATA_8));
        instructions.put(0x7a, new Instruction(DEC, EXTENDED_DATA_8));
        instructions.put(0x7c, new Instruction(INC, EXTENDED_DATA_8));
        instructions.put(0x7d, new Instruction(TST, EXTENDED_DATA_8));
        instructions.put(0x7e, new Instruction(JMP, EXTENDED_ADDRESS));
        instructions.put(0x7f, new Instruction(CLR, EXTENDED_DATA_8));

        instructions.put(0x80, new Instruction(SUBA, IMMEDIATE_VALUE_8));
        instructions.put(0x81, new Instruction(CMPA, IMMEDIATE_VALUE_8));
        instructions.put(0x82, new Instruction(SBCA, IMMEDIATE_VALUE_8));
        instructions.put(0x83, new Instruction(SUBD, IMMEDIATE_VALUE_16));
        instructions.put(0x84, new Instruction(ANDA, IMMEDIATE_VALUE_8));
        instructions.put(0x85, new Instruction(BITA, IMMEDIATE_VALUE_8));
        instructions.put(0x86, new Instruction(LDA, IMMEDIATE_VALUE_8));
        instructions.put(0x88, new Instruction(EORA, IMMEDIATE_VALUE_8));
        instructions.put(0x89, new Instruction(ADCA, IMMEDIATE_VALUE_8));
        instructions.put(0x8a, new Instruction(ORA, IMMEDIATE_VALUE_8));
        instructions.put(0x8b, new Instruction(ADDA, IMMEDIATE_VALUE_8));
        instructions.put(0x8c, new Instruction(CMPX, IMMEDIATE_VALUE_16));
        instructions.put(0x8d, new Instruction(BSR, RELATIVE_ADDRESS_8));
        instructions.put(0x8e, new Instruction(LDX, IMMEDIATE_VALUE_16));

        instructions.put(0x90, new Instruction(SUBA, DIRECT_DATA_8));
        instructions.put(0x91, new Instruction(CMPA, DIRECT_DATA_8));
        instructions.put(0x92, new Instruction(SBCA, DIRECT_DATA_8));
        instructions.put(0x93, new Instruction(SUBD, DIRECT_DATA_16));
        instructions.put(0x94, new Instruction(ANDA, DIRECT_DATA_8));
        instructions.put(0x95, new Instruction(BITA, DIRECT_DATA_8));
        instructions.put(0x96, new Instruction(LDA, DIRECT_DATA_8));
        instructions.put(0x97, new Instruction(STA, DIRECT_DATA_8));
        instructions.put(0x98, new Instruction(EORA, DIRECT_DATA_8));
        instructions.put(0x99, new Instruction(ADCA, DIRECT_DATA_8));
        instructions.put(0x9a, new Instruction(ORA, DIRECT_DATA_8));
        instructions.put(0x9b, new Instruction(ADDA, DIRECT_DATA_8));
        instructions.put(0x9c, new Instruction(CMPX, DIRECT_DATA_16));
        instructions.put(0x9d, new Instruction(JSR, DIRECT_ADDRESS));
        instructions.put(0x9e, new Instruction(LDX, DIRECT_DATA_16));
        instructions.put(0x9f, new Instruction(STX, DIRECT_DATA_16));

        instructions.put(0xa0, new Instruction(SUBA, INDEXED_DATA_8));
        instructions.put(0xa1, new Instruction(CMPA, INDEXED_DATA_8));
        instructions.put(0xa2, new Instruction(SBCA, INDEXED_DATA_8));
        instructions.put(0xa3, new Instruction(SUBD, INDEXED_DATA_16));
        instructions.put(0xa4, new Instruction(ANDA, INDEXED_DATA_8));
        instructions.put(0xa5, new Instruction(BITA, INDEXED_DATA_8));
        instructions.put(0xa6, new Instruction(LDA, INDEXED_DATA_8));
        instructions.put(0xa7, new Instruction(STA, INDEXED_DATA_8));
        instructions.put(0xa8, new Instruction(EORA, INDEXED_DATA_8));
        instructions.put(0xa9, new Instruction(ADCA, INDEXED_DATA_8));
        instructions.put(0xaa, new Instruction(ORA, INDEXED_DATA_8));
        instructions.put(0xab, new Instruction(ADDA, INDEXED_DATA_8));
        instructions.put(0xac, new Instruction(CMPX, INDEXED_DATA_16));
        instructions.put(0xad, new Instruction(JSR, INDEXED_ADDRESS));
        instructions.put(0xae, new Instruction(LDX, INDEXED_DATA_16));
        instructions.put(0xaf, new Instruction(STX, INDEXED_DATA_16));

        instructions.put(0xb0, new Instruction(SUBA, EXTENDED_DATA_8));
        instructions.put(0xb1, new Instruction(CMPA, EXTENDED_DATA_8));
        instructions.put(0xb2, new Instruction(SBCA, EXTENDED_DATA_8));
        instructions.put(0xb3, new Instruction(SUBD, EXTENDED_DATA_8));
        instructions.put(0xb4, new Instruction(ANDA, EXTENDED_DATA_8));
        instructions.put(0xb5, new Instruction(BITA, EXTENDED_DATA_8));
        instructions.put(0xb6, new Instruction(LDA, EXTENDED_DATA_8));
        instructions.put(0xb7, new Instruction(STA, EXTENDED_DATA_8));
        instructions.put(0xb8, new Instruction(EORA, EXTENDED_DATA_8));
        instructions.put(0xb9, new Instruction(ADCA, EXTENDED_DATA_8));
        instructions.put(0xba, new Instruction(ORA, EXTENDED_DATA_8));
        instructions.put(0xbb, new Instruction(ADDA, EXTENDED_DATA_8));
        instructions.put(0xbc, new Instruction(CMPX, EXTENDED_DATA_16));
        instructions.put(0xbd, new Instruction(JSR, EXTENDED_ADDRESS));
        instructions.put(0xbe, new Instruction(LDX, EXTENDED_DATA_16));
        instructions.put(0xbf, new Instruction(STX, EXTENDED_DATA_16));

        instructions.put(0xc0, new Instruction(SUBB, IMMEDIATE_VALUE_8));
        instructions.put(0xc1, new Instruction(CMPB, IMMEDIATE_VALUE_8));
        instructions.put(0xc2, new Instruction(SBCB, IMMEDIATE_VALUE_8));
        instructions.put(0xc3, new Instruction(ADDD, IMMEDIATE_VALUE_16));
        instructions.put(0xc4, new Instruction(ANDB, IMMEDIATE_VALUE_8));
        instructions.put(0xc5, new Instruction(BITB, IMMEDIATE_VALUE_8));
        instructions.put(0xc6, new Instruction(LDB, IMMEDIATE_VALUE_8));
        instructions.put(0xc8, new Instruction(EORB, IMMEDIATE_VALUE_8));
        instructions.put(0xc9, new Instruction(ADCB, IMMEDIATE_VALUE_8));
        instructions.put(0xca, new Instruction(ORB, IMMEDIATE_VALUE_8));
        instructions.put(0xcb, new Instruction(ADDB, IMMEDIATE_VALUE_8));
        instructions.put(0xcc, new Instruction(LDD, IMMEDIATE_VALUE_16));
        instructions.put(0xce, new Instruction(LDU, IMMEDIATE_VALUE_16));

        instructions.put(0xd0, new Instruction(SUBB, DIRECT_DATA_8));
        instructions.put(0xd1, new Instruction(CMPB, DIRECT_DATA_8));
        instructions.put(0xd2, new Instruction(SBCB, DIRECT_DATA_8));
        instructions.put(0xd3, new Instruction(ADDD, DIRECT_DATA_16));
        instructions.put(0xd4, new Instruction(ANDB, DIRECT_DATA_8));
        instructions.put(0xd5, new Instruction(BITB, DIRECT_DATA_8));
        instructions.put(0xd6, new Instruction(LDB, DIRECT_DATA_8));
        instructions.put(0xd7, new Instruction(STB, DIRECT_DATA_8));
        instructions.put(0xd8, new Instruction(EORB, DIRECT_DATA_8));
        instructions.put(0xd9, new Instruction(ADCB, DIRECT_DATA_8));
        instructions.put(0xda, new Instruction(ORB, DIRECT_DATA_8));
        instructions.put(0xdb, new Instruction(ADDB, DIRECT_DATA_8));
        instructions.put(0xdc, new Instruction(LDD, DIRECT_DATA_16));
        instructions.put(0xdd, new Instruction(STD, DIRECT_DATA_16));
        instructions.put(0xde, new Instruction(LDU, DIRECT_DATA_16));
        instructions.put(0xdf, new Instruction(STU, DIRECT_DATA_16));

        instructions.put(0xe0, new Instruction(SUBB, INDEXED_DATA_8));
        instructions.put(0xe1, new Instruction(CMPB, INDEXED_DATA_8));
        instructions.put(0xe2, new Instruction(SBCB, INDEXED_DATA_8));
        instructions.put(0xe3, new Instruction(ADDD, INDEXED_DATA_16));
        instructions.put(0xe4, new Instruction(ANDB, INDEXED_DATA_8));
        instructions.put(0xe5, new Instruction(BITB, INDEXED_DATA_8));
        instructions.put(0xe6, new Instruction(LDB, INDEXED_DATA_8));
        instructions.put(0xe7, new Instruction(STB, INDEXED_DATA_8));
        instructions.put(0xe8, new Instruction(EORB, INDEXED_DATA_8));
        instructions.put(0xe9, new Instruction(ADCB, INDEXED_DATA_8));
        instructions.put(0xea, new Instruction(ORB, INDEXED_DATA_8));
        instructions.put(0xeb, new Instruction(ADDB, INDEXED_DATA_8));
        instructions.put(0xec, new Instruction(LDD, INDEXED_DATA_16));
        instructions.put(0xed, new Instruction(STD, INDEXED_DATA_16));
        instructions.put(0xee, new Instruction(LDU, INDEXED_DATA_16));
        instructions.put(0xef, new Instruction(STU, INDEXED_DATA_16));

        instructions.put(0xf0, new Instruction(SUBB, EXTENDED_DATA_8));
        instructions.put(0xf1, new Instruction(CMPB, EXTENDED_DATA_8));
        instructions.put(0xf2, new Instruction(SBCB, EXTENDED_DATA_8));
        instructions.put(0xf3, new Instruction(ADDD, EXTENDED_DATA_16));
        instructions.put(0xf4, new Instruction(ANDB, EXTENDED_DATA_8));
        instructions.put(0xf5, new Instruction(BITB, EXTENDED_DATA_8));
        instructions.put(0xf6, new Instruction(LDB, EXTENDED_DATA_8));
        instructions.put(0xf7, new Instruction(STB, EXTENDED_DATA_8));
        instructions.put(0xf8, new Instruction(EORB, EXTENDED_DATA_8));
        instructions.put(0xf9, new Instruction(ADCB, EXTENDED_DATA_8));
        instructions.put(0xfa, new Instruction(ORB, EXTENDED_DATA_8));
        instructions.put(0xfb, new Instruction(ADDB, EXTENDED_DATA_8));
        instructions.put(0xfc, new Instruction(LDD, EXTENDED_DATA_16));
        instructions.put(0xfd, new Instruction(STD, EXTENDED_DATA_16));
        instructions.put(0xfe, new Instruction(LDU, EXTENDED_DATA_16));
        instructions.put(0xff, new Instruction(STU, EXTENDED_DATA_16));

        instructions.put(0x1021, new Instruction(LBRN, RELATIVE_ADDRESS_16));
        instructions.put(0x1022, new Instruction(LBHI, RELATIVE_ADDRESS_16));
        instructions.put(0x1023, new Instruction(LBLS, RELATIVE_ADDRESS_16));
        instructions.put(0x1024, new Instruction(LBCC, RELATIVE_ADDRESS_16)); // LBHS
        instructions.put(0x1025, new Instruction(LBCS, RELATIVE_ADDRESS_16)); // LBLO
        instructions.put(0x1026, new Instruction(LBNE, RELATIVE_ADDRESS_16));
        instructions.put(0x1027, new Instruction(LBEQ, RELATIVE_ADDRESS_16));
        instructions.put(0x1028, new Instruction(LBVC, RELATIVE_ADDRESS_16));
        instructions.put(0x1029, new Instruction(LBVS, RELATIVE_ADDRESS_16));
        instructions.put(0x102a, new Instruction(LBPL, RELATIVE_ADDRESS_16));
        instructions.put(0x102b, new Instruction(LBMI, RELATIVE_ADDRESS_16));
        instructions.put(0x102c, new Instruction(LBGE, RELATIVE_ADDRESS_16));
        instructions.put(0x102d, new Instruction(LBLT, RELATIVE_ADDRESS_16));
        instructions.put(0x102e, new Instruction(LBGT, RELATIVE_ADDRESS_16));
        instructions.put(0x102f, new Instruction(LBLE, RELATIVE_ADDRESS_16));

        instructions.put(0x103f, new Instruction(SWI2, INHERENT));
        instructions.put(0x1083, new Instruction(CMPD, IMMEDIATE_VALUE_16));
        instructions.put(0x108c, new Instruction(CMPY, IMMEDIATE_VALUE_16));
        instructions.put(0x108e, new Instruction(LDY, IMMEDIATE_VALUE_16));
        instructions.put(0x1093, new Instruction(CMPD, DIRECT_DATA_16));
        instructions.put(0x109c, new Instruction(CMPY, DIRECT_DATA_16));
        instructions.put(0x109e, new Instruction(LDY, DIRECT_DATA_16));
        instructions.put(0x109f, new Instruction(STY, DIRECT_DATA_16));
        instructions.put(0x10a3, new Instruction(CMPD, INDEXED_DATA_16));
        instructions.put(0x10ac, new Instruction(CMPY, INDEXED_DATA_16));
        instructions.put(0x10ae, new Instruction(LDY, INDEXED_DATA_16));
        instructions.put(0x10af, new Instruction(STY, INDEXED_DATA_16));
        instructions.put(0x10b3, new Instruction(CMPD, EXTENDED_DATA_16));
        instructions.put(0x10bc, new Instruction(CMPY, EXTENDED_DATA_16));
        instructions.put(0x10be, new Instruction(LDY, EXTENDED_DATA_16));
        instructions.put(0x10bf, new Instruction(STY, EXTENDED_DATA_16));
        instructions.put(0x10ce, new Instruction(LDS, IMMEDIATE_VALUE_16));
        instructions.put(0x10de, new Instruction(LDS, DIRECT_DATA_16));
        instructions.put(0x10df, new Instruction(STS, DIRECT_DATA_16));
        instructions.put(0x10ee, new Instruction(LDS, INDEXED_DATA_16));
        instructions.put(0x10ef, new Instruction(STS, INDEXED_DATA_16));
        instructions.put(0x10fe, new Instruction(LDS, EXTENDED_DATA_16));
        instructions.put(0x10ff, new Instruction(STS, EXTENDED_DATA_16));

        instructions.put(0x113f, new Instruction(SWI3, INHERENT));
        instructions.put(0x1183, new Instruction(CMPU, IMMEDIATE_VALUE_16));
        instructions.put(0x118c, new Instruction(CMPS, IMMEDIATE_VALUE_16));
        instructions.put(0x1193, new Instruction(CMPU, DIRECT_DATA_16));
        instructions.put(0x119c, new Instruction(CMPS, DIRECT_DATA_16));
        instructions.put(0x11a3, new Instruction(CMPU, INDEXED_DATA_16));
        instructions.put(0x11ac, new Instruction(CMPS, INDEXED_DATA_16));
        instructions.put(0x11b3, new Instruction(CMPU, EXTENDED_DATA_16));
        instructions.put(0x11bc, new Instruction(CMPS, EXTENDED_DATA_16));
    }

    static {
        for (var code : instructions.keySet()) {
            if (code > 0xff) extended.add(code >> 8);
        }
    }

    private final Mnemonic mnemonic;
    private final Addressing addressing;

    private Instruction(Mnemonic mnemonic, Addressing addressing) {
        this.mnemonic = mnemonic;
        this.addressing = addressing;
    }

    public static Instruction next(MemoryMapped memory, Register programCounter) {
        final int code = nextCode(memory, programCounter);
        var instruction = instructions.get(code);
        if (instruction == null)
            throw new UnsupportedOperationException("Invalid Op code : 0x" + Integer.toHexString(code));
        return instruction;
    }

    private static int nextCode(MemoryMapped memory, Register programCounter) {
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

    @Override
    public String toString() {
        return "" + mnemonic + ' ' + addressing;
    }
}
