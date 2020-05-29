package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.Described;

import java.util.Optional;
import java.util.function.Supplier;

import static com.joprovost.r8bemu.mc6809.Register.A;
import static com.joprovost.r8bemu.mc6809.Register.B;
import static com.joprovost.r8bemu.mc6809.Register.CC;
import static com.joprovost.r8bemu.mc6809.Register.D;
import static com.joprovost.r8bemu.mc6809.Register.S;
import static com.joprovost.r8bemu.mc6809.Register.U;
import static com.joprovost.r8bemu.mc6809.Register.X;
import static com.joprovost.r8bemu.mc6809.Register.Y;

public enum Mnemonic implements Described {
    ABX(X),
    ADCA(A),
    ADCB(B),
    ADDA(A),
    ADDB(B),
    ADDD(D),
    ANDA(A),
    ANDB(B),
    ANDCC(CC),
    ASR,
    ASRA(A),
    ASRB(B),
    BCC,
    BCS,
    BEQ,
    BGE,
    BGT,
    BHI,
    BITA(A),
    BITB(B),
    BLE,
    BLS,
    BLT,
    BMI,
    BNE,
    BPL,
    BRA,
    BRN,
    BSR,
    BVC,
    BVS,
    CLR,
    CLRA(A),
    CLRB(B),
    CMPA(A),
    CMPB(B),
    CMPD(D),
    CMPS(S),
    CMPU(U),
    CMPX(X),
    CMPY(Y),
    COM,
    COMA(A),
    COMB(B),
    CWAI,
    DAA(A),
    DEC,
    DECA(A),
    DECB(B),
    EORA(A),
    EORB(B),
    EXG,
    INC,
    INCA(A),
    INCB(B),
    JMP,
    JSR,
    LBCC(CC),
    LBCS(S),
    LBEQ,
    LBGE,
    LBGT,
    LBHI,
    LBLE,
    LBLS(S),
    LBLT,
    LBMI,
    LBNE,
    LBPL,
    LBRA(A),
    LBRN,
    LBSR,
    LBVC,
    LBVS(S),
    LDA(A),
    LDB(B),
    LDD(D),
    LDS(S),
    LDU(U),
    LDX(X),
    LDY(Y),
    LEAS(S),
    LEAU(U),
    LEAX(X),
    LEAY(Y),
    LSL,
    LSLA(A),
    LSLB(B),
    LSR,
    LSRA(A),
    LSRB(B),
    MUL,
    NEG,
    NEGA(A),
    NEGB(B),
    NOP,
    ORA(A),
    ORB(B),
    ORCC(CC),
    PSHS(S),
    PSHU(U),
    PULS(S),
    PULU(U),
    ROL,
    ROLA(A),
    ROLB(B),
    ROR,
    RORA(A),
    RORB(B),
    RTI,
    RTS(S),
    SBCA(A),
    SBCB(B),
    SEX(X),
    STA(A),
    STB(B),
    STD(D),
    STS(S),
    STU(U),
    STX(X),
    STY(Y),
    SUBA(A),
    SUBB(B),
    SUBD(D),
    SWI,
    SWI2,
    SWI3,
    SYNC,
    TFR,
    TST,
    TSTA(A),
    TSTB(B);

    public final Register register;

    Mnemonic(Register register) {
        this.register = register;
    }

    Mnemonic() {
        this(null);
    }

    @Override
    public String description() {
        return name();
    }

    public Register register() {
        return register;
    }

    public DataAccess registerOr(DataAccess argument) {
        return Optional.ofNullable(register()).map(DataAccess.class::cast).orElse(argument);
    }
}
