package com.joprovost.r8bemu.devices.mc6809;

import com.joprovost.r8bemu.data.Described;
import com.joprovost.r8bemu.data.binary.BinaryAccess;

import static com.joprovost.r8bemu.devices.mc6809.Register.A;
import static com.joprovost.r8bemu.devices.mc6809.Register.B;
import static com.joprovost.r8bemu.devices.mc6809.Register.CC;
import static com.joprovost.r8bemu.devices.mc6809.Register.D;
import static com.joprovost.r8bemu.devices.mc6809.Register.S;
import static com.joprovost.r8bemu.devices.mc6809.Register.U;
import static com.joprovost.r8bemu.devices.mc6809.Register.X;
import static com.joprovost.r8bemu.devices.mc6809.Register.Y;
import static com.joprovost.r8bemu.devices.mc6809.Task.of;

public enum Mnemonic implements Described {
    ABX(of(Arithmetic::abx)),
    ADCA(A, Task.of(Arithmetic::adc)),
    ADCB(B, Task.of(Arithmetic::adc)),
    ADDA(A, Task.of(Arithmetic::add)),
    ADDB(B, Task.of(Arithmetic::add)),
    ADDD(D, Task.of(Arithmetic::add)),
    ANDA(A, Task.of(Logic::and)),
    ANDB(B, Task.of(Logic::and)),
    ANDCC(CC, Task.of(Logic::and)),
    ASR(Task.of(Shift::asr)),
    ASRA(A, Task.of(Shift::asr)),
    ASRB(B, Task.of(Shift::asr)),
    BCC(Task.of(Branches::bcc)),
    BCS(Task.of(Branches::bcs)),
    BEQ(Task.of(Branches::beq)),
    BGE(Task.of(Branches::bge)),
    BGT(Task.of(Branches::bgt)),
    BHI(Task.of(Branches::bhi)),
    BITA(A, Task.of(Logic::bit)),
    BITB(B, Task.of(Logic::bit)),
    BLE(Task.of(Branches::ble)),
    BLS(Task.of(Branches::bls)),
    BLT(Task.of(Branches::blt)),
    BMI(Task.of(Branches::bmi)),
    BNE(Task.of(Branches::bne)),
    BPL(Task.of(Branches::bpl)),
    BRA(Task.of(Branches::bra)),
    BRN(Task.of(Branches::brn)),
    BSR(Task.of(Stack::bsr)),
    BVC(Task.of(Branches::bvc)),
    BVS(Task.of(Branches::bvs)),
    CLR(Task.of(Logic::clear)),
    CLRA(A, Task.of(Logic::clear)),
    CLRB(B, Task.of(Logic::clear)),
    CMPA(A, Task.of(Check::compare)),
    CMPB(B, Task.of(Check::compare)),
    CMPD(D, Task.of(Check::compare)),
    CMPS(S, Task.of(Check::compare)),
    CMPU(U, Task.of(Check::compare)),
    CMPX(X, Task.of(Check::compare)),
    CMPY(Y, Task.of(Check::compare)),
    COM(Task.of(Logic::complement)),
    COMA(A, Task.of(Logic::complement)),
    COMB(B, Task.of(Logic::complement)),
    CWAI,
    DAA(of(Arithmetic::daa)),
    DEC(Task.of(Arithmetic::decrement)),
    DECA(A, Task.of(Arithmetic::decrement)),
    DECB(B, Task.of(Arithmetic::decrement)),
    EORA(A, Task.of(Logic::xor)),
    EORB(B, Task.of(Logic::xor)),
    EXG((register, argument, stack, debug) -> debug.argument(RegisterPair.registers(argument)).exchange()),
    INC(Task.of(Arithmetic::increment)),
    INCA(A, Task.of(Arithmetic::increment)),
    INCB(B, Task.of(Arithmetic::increment)),
    JMP(Task.of(Branches::jump)),
    JSR(Task.of(Stack::jsr)),
    LBCC(Task.of(Branches::bcc)),
    LBCS(Task.of(Branches::bcs)),
    LBEQ(Task.of(Branches::beq)),
    LBGE(Task.of(Branches::bge)),
    LBGT(Task.of(Branches::bgt)),
    LBHI(Task.of(Branches::bhi)),
    LBLE(Task.of(Branches::ble)),
    LBLS(Task.of(Branches::bls)),
    LBLT(Task.of(Branches::blt)),
    LBMI(Task.of(Branches::bmi)),
    LBNE(Task.of(Branches::bne)),
    LBPL(Task.of(Branches::bpl)),
    LBRA(Task.of(Branches::bra)),
    LBRN(Task.of(Branches::brn)),
    LBSR(Task.of(Stack::bsr)),
    LBVC(Task.of(Branches::bvc)),
    LBVS(Task.of(Branches::bvs)),
    LDA(A, Task.of(Register::load)),
    LDB(B, Task.of(Register::load)),
    LDD(D, Task.of(Register::load)),
    LDS(S, Task.of(Register::load)),
    LDU(U, Task.of(Register::load)),
    LDX(X, Task.of(Register::load)),
    LDY(Y, Task.of(Register::load)),
    LEAS(S, Task.of(Register::loadAddress)),
    LEAU(U, Task.of(Register::loadAddress)),
    LEAX(X, Task.of(Register::loadAddress)),
    LEAY(Y, Task.of(Register::loadAddress)),
    LSL(Task.of(Shift::lsl)),
    LSLA(A, Task.of(Shift::lsl)),
    LSLB(B, Task.of(Shift::lsl)),
    LSR(Task.of(Shift::lsr)),
    LSRA(A, Task.of(Shift::lsr)),
    LSRB(B, Task.of(Shift::lsr)),
    MUL(of(Arithmetic::mul)),
    NEG(Task.of(Arithmetic::neg)),
    NEGA(A, Task.of(Arithmetic::neg)),
    NEGB(B, Task.of(Arithmetic::neg)),
    NOP(of(Branches::nop)),
    ORA(A, Task.of(Logic::or)),
    ORB(B, Task.of(Logic::or)),
    ORCC(CC, Task.of(Logic::or)),
    PSHS(S, (register, argument, stack, debug) -> stack.pushAll(register, argument)),
    PSHU(U, (register, argument, stack, debug) -> stack.pushAll(register, argument)),
    PULS(S, (register, argument, stack, debug) -> stack.pullAll(register, argument)),
    PULU(U, (register, argument, stack, debug) -> stack.pullAll(register, argument)),
    ROL(Task.of(Shift::rol)),
    ROLA(A, Task.of(Shift::rol)),
    ROLB(B, Task.of(Shift::rol)),
    ROR(Task.of(Shift::ror)),
    RORA(A, Task.of(Shift::ror)),
    RORB(B, Task.of(Shift::ror)),
    RTI((register, argument, stack, debug) -> Stack.rti(stack)),
    RTS((register, argument, stack, debug) -> Stack.rts(stack)),
    SBCA(A, Task.of(Arithmetic::sbc)),
    SBCB(B, Task.of(Arithmetic::sbc)),
    SEX(of(Arithmetic::sex)),
    STA(A, Task.of(Register::store)),
    STB(B, Task.of(Register::store)),
    STD(D, Task.of(Register::store)),
    STS(S, Task.of(Register::store)),
    STU(U, Task.of(Register::store)),
    STX(X, Task.of(Register::store)),
    STY(Y, Task.of(Register::store)),
    SUBA(A, Task.of(Arithmetic::sub)),
    SUBB(B, Task.of(Arithmetic::sub)),
    SUBD(D, Task.of(Arithmetic::sub)),
    SWI((register, argument, stack, debug) -> Stack.swi(stack)),
    SWI2((register, argument, stack, debug) -> Stack.swi2(stack)),
    SWI3((register, argument, stack, debug) -> Stack.swi3(stack)),
    SYNC,
    TFR((register, argument, stack, debug) -> debug.argument(RegisterPair.registers(argument)).transfer()),
    TST(Task.of(Check::test)),
    TSTA(A, Task.of(Check::test)),
    TSTB(B, Task.of(Check::test));

    private final Register register;
    private final Task task;

    Mnemonic(Register register, Task task) {
        this.register = register;
        this.task = task;
    }

    Mnemonic(Task task) {
        this(null, task);
    }

    Mnemonic() {
        this(null, null);
    }

    @Override
    public String description() {
        return name();
    }

    public Register register() {
        return register;
    }

    public boolean execute(BinaryAccess argument, Stack stack, Debugger debug) {
        if (task == null) return false;
        task.execute(register, argument, stack, debug);
        return true;
    }
}
