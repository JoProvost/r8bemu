package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.Debugger;
import com.joprovost.r8bemu.arithmetic.Addition;
import com.joprovost.r8bemu.arithmetic.Complement;
import com.joprovost.r8bemu.arithmetic.ExclusiveDisjunction;
import com.joprovost.r8bemu.arithmetic.Operation;
import com.joprovost.r8bemu.arithmetic.Subtraction;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.MemoryAccess;
import com.joprovost.r8bemu.data.Size;
import com.joprovost.r8bemu.memory.MemoryMapped;

import java.util.Optional;

import static com.joprovost.r8bemu.data.DataOutput.negative;
import static com.joprovost.r8bemu.mc6809.Register.A;
import static com.joprovost.r8bemu.mc6809.Register.B;
import static com.joprovost.r8bemu.mc6809.Register.C;
import static com.joprovost.r8bemu.mc6809.Register.D;
import static com.joprovost.r8bemu.mc6809.Register.F;
import static com.joprovost.r8bemu.mc6809.Register.I;
import static com.joprovost.r8bemu.mc6809.Register.N;
import static com.joprovost.r8bemu.mc6809.Register.PC;
import static com.joprovost.r8bemu.mc6809.Register.V;
import static com.joprovost.r8bemu.mc6809.Register.X;
import static com.joprovost.r8bemu.mc6809.Register.Z;

public class MC6809E implements ClockAware {

    public static final int RESET_VECTOR = 0xfffe;
    public static final int IRQ_VECTOR = 0xfff8;
    public static final int NMI_VECTOR = 0xfffc;
    public static final int FIRQ_VECTOR = 0xfff6;

    private final Interpreter interpreter;
    private final MemoryManagementUnit mmu;
    private final Stack stack;
    private final Branches branch;
    private final Debugger debug;

    public MC6809E(MemoryMapped memory, Debugger debug) {
        mmu = new MemoryManagementUnit(memory);
        this.debug = debug;
        this.interpreter = new Interpreter(mmu);
        this.stack = new Stack(mmu);
        this.branch = new Branches(stack);
    }

    public void tick(long tick) {
        var address = PC.unsigned();
        debug.at(address);
        var instruction = interpreter.next();
        try {
            var mnemonic = instruction.mnemonic();
            var addressing = instruction.addressing();
            switch (mnemonic) {
                case NOP:
                    break;

                case ABX:
                    X.update(Addition.incrementBy(B));
                    break;

                case MUL:
                    multiply(A, B, D);
                    break;

                case SEX:
                    signExtended(A, B);
                    break;

                case NEG: case NEGA: case NEGB:
                    negate(mnemonic.registerOr(() -> debug.parameter(mmu.data(addressing))));
                    break;

                case COM: case COMA: case COMB:
                    mnemonic.registerOr(() -> debug.parameter(mmu.data(addressing)))
                            .update(it -> checking(Complement.of(it)));
                    break;

                case CMPA: case CMPB: case CMPD: case CMPX: case CMPY: case CMPU: case CMPS:
                    checking(Subtraction.of(mnemonic.register(), debug.parameter(mmu.data(addressing))));
                    break;

                case TST: case TSTA: case TSTB:
                    tst(mnemonic.registerOr(() -> debug.parameter(mmu.data(addressing))));
                    break;

                case BITA: case BITB:
                    bitTest(mnemonic.register(), debug.parameter(mmu.data(addressing)));
                    break;

                case STA: case STB: case STD: case STX: case STY: case STU: case STS:
                    storeIntoMemory(mnemonic.register(), debug.parameter(mmu.data(addressing)));
                    break;

                case LDA: case LDB: case LDD: case LDX: case LDY: case LDU: case LDS:
                    loadFromMemory(mnemonic.register(), debug.parameter(mmu.data(addressing)));
                    break;

                case ADCA: case ADCB:
                    mnemonic.register().update(it -> checking(Addition.of(it, debug.parameter(mmu.data(addressing)), C)));
                    break;

                case ADDA: case ADDB: case ADDD:
                    mnemonic.register().update(it -> checking(Addition.of(it, debug.parameter(mmu.data(addressing)))));
                    break;

                case SBCA: case SBCB:
                    mnemonic.register().update(it -> checking(Subtraction.of(it, debug.parameter(mmu.data(addressing)), C)));
                    break;

                case SUBA: case SUBB: case SUBD:
                    mnemonic.register().update(it -> checking(Subtraction.of(it, debug.parameter(mmu.data(addressing)))));
                    break;

                case EORA: case EORB:
                    mnemonic.register().update(it -> checking(ExclusiveDisjunction.of(it, debug.parameter(mmu.data(addressing)))));
                    break;

                case ROR: case RORA: case RORB:
                    ror(mnemonic.registerOr(() -> debug.parameter(mmu.data(addressing))));
                    break;

                case ROL: case ROLA: case ROLB:
                    rol(mnemonic.registerOr(() -> debug.parameter(mmu.data(addressing))));
                    break;

                case INC: case INCA: case INCB:
                    increment(mnemonic.registerOr(() -> debug.parameter(mmu.data(addressing))));
                    break;

                case DEC: case DECA: case DECB:
                    decrement(mnemonic.registerOr(() -> debug.parameter(mmu.data(addressing))));
                    break;

                case LSR: case LSRA: case LSRB:
                    lsr(mnemonic.registerOr(() -> debug.parameter(mmu.data(addressing))));
                    break;

                case LSL: case LSLA: case LSLB:
                    lsl(mnemonic.registerOr(() -> debug.parameter(mmu.data(addressing))));
                    break;

                case ANDA: case ANDB:
                    and(mnemonic.register(), debug.parameter(mmu.data(addressing)));
                    break;
                case ANDCC:
                    andcc(mnemonic.register(), debug.parameter(mmu.data(addressing)));
                    break;

                case ORA: case ORB:
                    or(mnemonic.register(), debug.parameter(mmu.data(addressing)));
                    break;
                case ORCC:
                    orcc(mnemonic.register(), debug.parameter(mmu.data(addressing)));
                    break;

                case TFR:
                    debug.parameter(Pair.registers(mmu.data(addressing))).transfer();
                    break;

                case EXG:
                    debug.parameter(Pair.registers(mmu.data(addressing))).exchange();
                    break;

                case CLR: case CLRA: case CLRB:
                    clear(mnemonic.registerOr(() -> debug.parameter(mmu.data(addressing))));
                    break;

                case PSHU: case PSHS:
                    stack.pushAll(Optional.ofNullable(mnemonic.register()).orElseThrow(), debug.parameter(mmu.data(addressing)));
                    break;

                case PULU: case PULS:
                    stack.pullAll(Optional.ofNullable(mnemonic.register()).orElseThrow(), debug.parameter(mmu.data(addressing)));
                    break;

                case LEAX: case LEAY:
                    Z.set(mnemonic.register().replace(debug.parameter(mmu.address(addressing))).unsigned() == 0);
                    break;

                case LEAU: case LEAS:
                    mnemonic.register().replace(debug.parameter(mmu.address(addressing))).unsigned();
                    break;

                case JMP:
                    branch.jump(debug.parameter(mmu.address(addressing)));
                    break;

                case JSR:
                    branch.jsr(debug.parameter(mmu.address(addressing)));
                    break;

                case RTS:
                    branch.rts();
                    break;

                case RTI:
                    branch.rti();
                    break;

                case BNE: case LBNE:
                    branch.jumpIf(Z::isClear, debug.parameter(mmu.address(addressing)));
                    break;

                case BEQ: case LBEQ:
                    branch.jumpIf(Z::isSet, debug.parameter(mmu.address(addressing)));
                    break;

                case BSR: case LBSR:
                    branch.bsr(debug.parameter(mmu.address(addressing)));
                    break;

                case BCC: case LBCC:
                    branch.jumpIf(C::isClear, debug.parameter(mmu.address(addressing)));
                    break;

                case BMI: case LBMI:
                    branch.jumpIf(N::isSet, debug.parameter(mmu.address(addressing)));
                    break;

                case BVS: case LBVS:
                    branch.jumpIf(V::isSet, debug.parameter(mmu.address(addressing)));
                    break;

                case BVC: case LBVC:
                    branch.jumpIf(V::isClear, debug.parameter(mmu.address(addressing)));
                    break;

                case BLS: case LBLS:
                    branch.jumpIf(() -> C.isSet() || Z.isSet(), debug.parameter(mmu.address(addressing)));
                    break;

                case BLE: case LBLE:
                    //(CC.N ≠ CC.V) OR (CC.Z = 1)
                    branch.jumpIf(() -> (N.isSet() != V.isSet()) || Z.isSet(), debug.parameter(mmu.address(addressing)));
                    break;

                case BLT: case LBLT:
                    branch.jumpIf(() -> N.isSet() != V.isSet(), debug.parameter(mmu.address(addressing)));
                    break;

                case BGT: case LBGT:
                    branch.jumpIf(() -> (N.isSet() == V.isSet()) && Z.isClear(), debug.parameter(mmu.address(addressing)));
                    break;

                case BHI: case LBHI:
                    branch.jumpIf(() -> C.isClear() && Z.isClear(), debug.parameter(mmu.address(addressing)));
                    break;

                case BCS: case LBCS:
                    branch.jumpIf(C::isSet, debug.parameter(mmu.address(addressing)));
                    break;

                case BPL: case LBPL:
                    branch.jumpIf(N::isClear, debug.parameter(mmu.address(addressing)));
                    break;

                case BRA: case LBRA:
                    branch.jumpIf(() -> true, debug.parameter(mmu.address(addressing)));
                    break;

                case BRN: case LBRN:
                    branch.jumpIf(() -> false, debug.parameter(mmu.address(addressing)));
                    break;

                case SYNC:
                    break;

                default:
                    throw new IllegalStateException("Unexpected instruction: " + instruction + " at 0x" + Integer.toHexString(address));
            }

            debug.instruction(mnemonic);
        } catch (Exception e) {
            throw new UnsupportedOperationException("\n" + instruction, e);
        }
    }

    public void reset() {
        I.set(true);
        F.set(true);
        Register.DP.set(0x00);
        branch.jump(MemoryAccess.of(mmu, RESET_VECTOR, Size.WORD_16));
    }

    public void irq() {
        if (I.isSet()) return;
        branch.interrupt(MemoryAccess.of(mmu, IRQ_VECTOR, Size.WORD_16));
        I.set();
    }

    public void firq() {
        if (F.isSet()) return;
        branch.fastInterrupt(MemoryAccess.of(mmu, FIRQ_VECTOR, Size.WORD_16));
        F.set();
        I.set();
    }

    public void nmi() {
        branch.interrupt(MemoryAccess.of(mmu, NMI_VECTOR, Size.WORD_16));
        I.set();
        F.set();
    }

    private void signExtended(Register a, Register b) {
        if (negative(b.unsigned(), b.mask())) {
            a.set(a.mask());
        } else {
            a.clear();
        }
        Register.N.set(negative(b.unsigned(), b.mask()));
        Register.Z.set(b.isClear());
    }

    private void increment(DataAccess variable) {
        int result = variable.pre(Addition.increment()).unsigned();
        Register.V.set(overflow(result, variable.mask()));
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(result == 0);
    }

    private void decrement(DataAccess variable) {
        int result = variable.pre(Subtraction.decrement()).unsigned();
        Register.V.set(overflow(result, variable.mask()));
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(result == 0);
    }

    private void multiply(DataAccess a, DataAccess b, DataAccess dest) {
        var result = a.unsigned() * b.unsigned();
        Register.Z.set(result == 0);
        Register.C.set(bit(7, result));
        dest.set(result);
    }

    private void negate(DataAccess variable) {
        var result = (variable.unsigned() == 0x80) ? 0x80 : -variable.signed();
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(result == 0);
        Register.V.set(overflow(result, variable.mask()));
        Register.C.set(result == 0);
        variable.set(result);
    }

    private void bitTest(DataAccess register, DataAccess memory) {
        int result = register.unsigned() & memory.unsigned();
        Register.N.set(negative(result, register.mask()));
        Register.Z.set(result == 0);
        Register.V.clear();
    }

    private void storeIntoMemory(DataAccess register, DataAccess memory) {
        int result = register.unsigned();
        Register.N.set(negative(result, register.mask()));
        Register.Z.set(result == 0);
        Register.V.clear();
        memory.set(result);
    }

    private void loadFromMemory(DataAccess register, DataAccess memory) {
        int result = memory.unsigned();
        Register.N.set(negative(result, register.mask()));
        Register.Z.set(result == 0);
        Register.V.clear();
        register.set(result);
    }

    private void clear(DataAccess register) {
        register.clear();
        Register.N.clear();
        Register.V.clear();
        Register.C.clear();
        Register.Z.set();
    }

    private void rol(DataAccess variable) {
        int before = variable.unsigned();
        int result = (before << 1) | Register.C.unsigned();
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(result == 0);
        Register.C.set(bit(7, before));
        Register.V.set(bit(6, before) ^ bit(7, before));
        variable.set(result & variable.mask());
    }

    private void tst(DataAccess variable) {
        int result = variable.unsigned();
        Register.V.clear();
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(result == 0);
    }

    private void lsr(DataAccess variable) {
        int before = variable.unsigned();
        int result = before >> 1;
        Register.N.clear();
        Register.Z.set(result == 0);
        Register.C.set(before);
        variable.set(result);
    }

    private void lsl(DataAccess variable) {
        int before = variable.unsigned();
        int result = before << 1;
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(result == 0);
        Register.C.set(bit(7, before));
        Register.V.set(bit(6, before) ^ bit(7, before));
        variable.set(result);
    }

    private void ror(DataAccess variable) {
        int before = variable.unsigned();
        int result = (before >> 1) | (Register.C.unsigned() << 8);
        Register.N.set(negative(result, variable.mask()));
        Register.Z.set(result == 0);
        Register.C.set(before);
        variable.set(result);
    }

    private void and(DataAccess register, DataAccess memory) {
        register.set(checking(register.and(memory)));
    }

    private void andcc(DataAccess register, DataAccess memory) {
        register.set(register.and(memory));
    }

    private void or(DataAccess register, DataAccess memory) {
        register.set(checking(register.or(memory)));
    }

    private void orcc(DataAccess register, DataAccess memory) {
        register.set(register.or(memory));
    }

    private Operation checking(Operation result) {
        Register.V.set(result.overflow());
        Register.N.set(result.negative());
        Register.Z.set(result.zero());
        result.halfCarry().ifPresent(Register.H::set);
        result.carry().ifPresent(Register.C::set);
        return result;
    }

    private int bit(int bit, int value) {
        return (value >> bit) & 0b1;
    }

    private boolean overflow(int result, int mask) {
        return (result & mask) == Integer.highestOneBit(mask);
    }
}
