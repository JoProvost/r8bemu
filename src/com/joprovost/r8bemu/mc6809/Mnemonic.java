package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.Described;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.joprovost.r8bemu.mc6809.Register.A;
import static com.joprovost.r8bemu.mc6809.Register.B;
import static com.joprovost.r8bemu.mc6809.Register.CC;
import static com.joprovost.r8bemu.mc6809.Register.D;
import static com.joprovost.r8bemu.mc6809.Register.S;
import static com.joprovost.r8bemu.mc6809.Register.U;
import static com.joprovost.r8bemu.mc6809.Register.X;
import static com.joprovost.r8bemu.mc6809.Register.Y;

public enum Mnemonic implements Described {
    ABX(Arithmetic::abx),
    ADCA(A, Arithmetic::adc),
    ADCB(B, Arithmetic::adc),
    ADDA(A, Arithmetic::add),
    ADDB(B, Arithmetic::add),
    ADDD(D, Arithmetic::add),
    ANDA(A, Logic::and),
    ANDB(B, Logic::and),
    ANDCC(CC, Logic::and),
    ASR(Shift::asr),
    ASRA(A, Shift::asr),
    ASRB(B, Shift::asr),
    BCC(Branches::bcc),
    BCS(Branches::bcs),
    BEQ(Branches::beq),
    BGE(Branches::bge),
    BGT(Branches::bgt),
    BHI(Branches::bhi),
    BITA(A, Logic::bitTest),
    BITB(B, Logic::bitTest),
    BLE(Branches::ble),
    BLS(Branches::bls),
    BLT(Branches::blt),
    BMI(Branches::bmi),
    BNE(Branches::bne),
    BPL(Branches::bpl),
    BRA(Branches::bra),
    BRN(Branches::brn),
    BSR,
    BVC(Branches::bvc),
    BVS(Branches::bvs),
    CLR(Logic::clear),
    CLRA(A, Logic::clear),
    CLRB(B, Logic::clear),
    CMPA(A, Arithmetic::compare),
    CMPB(B, Arithmetic::compare),
    CMPD(D, Arithmetic::compare),
    CMPS(S, Arithmetic::compare),
    CMPU(U, Arithmetic::compare),
    CMPX(X, Arithmetic::compare),
    CMPY(Y, Arithmetic::compare),
    COM(Logic::com),
    COMA(A, Logic::com),
    COMB(B, Logic::com),
    CWAI,
    DAA,
    DEC(Arithmetic::decrement),
    DECA(A, Arithmetic::decrement),
    DECB(B, Arithmetic::decrement),
    EORA(A, Logic::xor),
    EORB(B, Logic::xor),
    EXG,
    INC(Arithmetic::increment),
    INCA(A, Arithmetic::increment),
    INCB(B, Arithmetic::increment),
    JMP(Branches::jump),
    JSR,
    LBCC(Branches::bcc),
    LBCS(Branches::bcs),
    LBEQ(Branches::beq),
    LBGE(Branches::bge),
    LBGT(Branches::bgt),
    LBHI(Branches::bhi),
    LBLE(Branches::ble),
    LBLS(Branches::bls),
    LBLT(Branches::blt),
    LBMI(Branches::bmi),
    LBNE(Branches::bne),
    LBPL(Branches::bpl),
    LBRA(Branches::bra),
    LBRN(Branches::brn),
    LBSR,
    LBVC(Branches::bvc),
    LBVS(Branches::bvs),
    LDA(A, Register::load),
    LDB(B, Register::load),
    LDD(D, Register::load),
    LDS(S, Register::load),
    LDU(U, Register::load),
    LDX(X, Register::load),
    LDY(Y, Register::load),
    LEAS(S, Register::loadAddress),
    LEAU(U, Register::loadAddress),
    LEAX(X, Register::loadAddress),
    LEAY(Y, Register::loadAddress),
    LSL(Shift::lsl),
    LSLA(A, Shift::lsl),
    LSLB(B, Shift::lsl),
    LSR(Shift::lsr),
    LSRA(A, Shift::lsr),
    LSRB(B, Shift::lsr),
    MUL(Arithmetic::mul),
    NEG(Arithmetic::neg),
    NEGA(A, Arithmetic::neg),
    NEGB(B, Arithmetic::neg),
    NOP(Branches::nop),
    ORA(A, Logic::or),
    ORB(B, Logic::or),
    ORCC(CC, Logic::or),
    PSHS(S),
    PSHU(U),
    PULS(S),
    PULU(U),
    ROL(Shift::rol),
    ROLA(A, Shift::rol),
    ROLB(B, Shift::rol),
    ROR(Shift::ror),
    RORA(A, Shift::ror),
    RORB(B, Shift::ror),
    RTI,
    RTS,
    SBCA(A, Arithmetic::sbc),
    SBCB(B, Arithmetic::sbc),
    SEX(Arithmetic::sex),
    STA(A, Register::store),
    STB(B, Register::store),
    STD(D, Register::store),
    STS(S, Register::store),
    STU(U, Register::store),
    STX(X, Register::store),
    STY(Y, Register::store),
    SUBA(A, Arithmetic::sub),
    SUBB(B, Arithmetic::sub),
    SUBD(D, Arithmetic::sub),
    SWI,
    SWI2,
    SWI3,
    SYNC,
    TFR,
    TST(Arithmetic::test),
    TSTA(A, Arithmetic::test),
    TSTB(B, Arithmetic::test);

    private final Register register;
    private final BiConsumer<Register, DataAccess> binaryOperation;
    private final Consumer<DataAccess> unaryOperation;
    private final Runnable inherentOperation;

    Mnemonic(Register register,
             BiConsumer<Register, DataAccess> binaryOperation,
             Consumer<DataAccess> unaryOperation,
             Runnable inherentOperation) {
        this.register = register;
        this.binaryOperation = binaryOperation;
        this.unaryOperation = unaryOperation;
        this.inherentOperation = inherentOperation;
    }

    Mnemonic(Register register, BiConsumer<Register, DataAccess> binaryOperation) {
        this(register, binaryOperation, null, null);
    }

    Mnemonic(Register register, Consumer<DataAccess> unaryOperation) {
        this(register, null, unaryOperation, null);
    }

    Mnemonic(Consumer<DataAccess> unaryOperation) {
        this(null, null, unaryOperation, null);
    }

    Mnemonic(Runnable inherentOperation) {
        this(null, null, null, inherentOperation);
    }

    Mnemonic(Register register) {
        this(register, null, null, null);
    }

    Mnemonic() {
        this(null, null, null, null);
    }

    @Override
    public String description() {
        return name();
    }

    public Register register() {
        return register;
    }

    public DataAccess registerOr(DataAccess argument) {
        return register != null ? register : argument;
    }

    public boolean execute(DataAccess argument) {
        if (binaryOperation != null) binaryOperation.accept(register, argument);
        else if (unaryOperation != null) unaryOperation.accept(registerOr(argument));
        else if (inherentOperation != null) inherentOperation.run();
        else return false;
        return true;
    }
}
