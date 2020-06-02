package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.Debugger;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.Reference;
import com.joprovost.r8bemu.data.Size;
import com.joprovost.r8bemu.memory.MemoryMapped;

import java.io.EOFException;
import java.io.IOException;

import static com.joprovost.r8bemu.data.DataOutput.negative;
import static com.joprovost.r8bemu.mc6809.Register.C;
import static com.joprovost.r8bemu.mc6809.Register.F;
import static com.joprovost.r8bemu.mc6809.Register.I;
import static com.joprovost.r8bemu.mc6809.Register.N;
import static com.joprovost.r8bemu.mc6809.Register.PC;
import static com.joprovost.r8bemu.mc6809.Register.V;
import static com.joprovost.r8bemu.mc6809.Register.Z;

public class MC6809E implements ClockAware {

    public static final int RESET_VECTOR = 0xfffe;
    public static final int IRQ_VECTOR = 0xfff8;
    public static final int NMI_VECTOR = 0xfffc;
    public static final int FIRQ_VECTOR = 0xfff6;

    private final MemoryMapped memory;
    private final Stack stack;
    private final Branches branch;
    private final Debugger debug;

    public MC6809E(MemoryMapped memory, Debugger debug) {
        this.memory = memory;
        this.debug = debug;
        this.stack = new Stack(memory);
        this.branch = new Branches(stack);
    }

    public void tick(long tick) throws IOException {
        var address = PC.unsigned();
        debug.at(address);
        var instruction = Interpreter.next(memory, PC);
        try {
            var mnemonic = instruction.mnemonic();
            var addressing = instruction.addressing();
            var argument = debug.argument(Argument.next(memory, addressing, PC));
            var registerOrArgument = mnemonic.registerOr(argument);
            switch (mnemonic) {
                case NOP:
                    break;

                case ABX:
                    Arithmetic.abx();
                    break;

                case MUL:
                    Arithmetic.mul();
                    break;

                case SEX:
                    Arithmetic.sex();
                    break;

                case NEG: case NEGA: case NEGB:
                    Arithmetic.neg(registerOrArgument);
                    break;

                case COM: case COMA: case COMB:
                    Logic.com(registerOrArgument);
                    break;

                case CMPA: case CMPB: case CMPD: case CMPX: case CMPY: case CMPU: case CMPS:
                    Arithmetic.compare(mnemonic.register(), argument);
                    break;

                case TST: case TSTA: case TSTB:
                    Arithmetic.test(registerOrArgument);
                    break;

                case BITA: case BITB:
                    Logic.bitTest(mnemonic.register(), argument);
                    break;

                case STA: case STB: case STD: case STX: case STY: case STU: case STS:
                    storeIntoMemory(mnemonic.register(), argument);
                    break;

                case LDA: case LDB: case LDD: case LDX: case LDY: case LDU: case LDS:
                    loadFromMemory(mnemonic.register(), argument);
                    break;

                case ADCA: case ADCB:
                    Arithmetic.adc(mnemonic.register(), argument);
                    break;

                case ADDA: case ADDB: case ADDD:
                    Arithmetic.add(mnemonic.register(), argument);
                    break;

                case SBCA: case SBCB:
                    Arithmetic.sbc(mnemonic.register(), argument);
                    break;

                case SUBA: case SUBB: case SUBD:
                    Arithmetic.sub(mnemonic.register(), argument);
                    break;

                case EORA: case EORB:
                    Logic.xor(mnemonic.register(), argument);
                    break;

                case ROR: case RORA: case RORB:
                    Shift.ror(registerOrArgument);
                    break;

                case ROL: case ROLA: case ROLB:
                    Shift.rol(registerOrArgument);
                    break;

                case INC: case INCA: case INCB:
                    Arithmetic.increment(registerOrArgument);
                    break;

                case DEC: case DECA: case DECB:
                    Arithmetic.decrement(registerOrArgument);
                    break;

                case LSR: case LSRA: case LSRB:
                    Shift.lsr(registerOrArgument);
                    break;

                case LSL: case LSLA: case LSLB:
                    Shift.lsl(registerOrArgument);
                    break;

                case ASR: case ASRA: case ASRB:
                    Shift.asr(registerOrArgument);
                    break;

                case ANDA: case ANDB: case ANDCC:
                    Logic.and(mnemonic.register(), argument);
                    break;

                case ORA: case ORB: case ORCC:
                    Logic.or(mnemonic.register(), argument);
                    break;

                case TFR:
                    debug.argument(Pair.registers(argument)).transfer();
                    break;

                case EXG:
                    debug.argument(Pair.registers(argument)).exchange();
                    break;

                case CLR: case CLRA: case CLRB:
                    clear(registerOrArgument);
                    break;

                case PSHU: case PSHS:
                    stack.pushAll(argument, mnemonic.register());
                    break;

                case PULU: case PULS:
                    stack.pullAll(argument, mnemonic.register());
                    break;

                case LEAX: case LEAY:
                    Z.set(mnemonic.register().replace(argument).isClear());
                    break;

                case LEAU: case LEAS:
                    mnemonic.register().replace(argument).unsigned();
                    break;

                case JMP:
                    branch.jump(argument);
                    break;

                case JSR:
                    branch.jsr(argument);
                    break;

                case RTS:
                    branch.rts();
                    break;

                case RTI:
                    branch.rti();
                    break;

                case BSR: case LBSR:
                    branch.bsr(argument);
                    break;

                case BCC: case LBCC:
                    branch.jumpIf(C::isClear, argument);
                    break;

                case BCS: case LBCS:
                    branch.jumpIf(C::isSet, argument);
                    break;

                case BEQ: case LBEQ:
                    branch.jumpIf(Z::isSet, argument);
                    break;

                case BGE: case LBGE:
                    // IFF [N ⊕ V] = 0 then PC' ← PC + TEMP
                    branch.jumpIf(() -> N.isSet() == V.isSet(), argument);
                    break;

                case BGT: case LBGT:
                    // IFF Z ∧ [N ⊕ V] = 0 then PC' ← PC + TEMP
                    branch.jumpIf(() -> Z.isClear() == (N.isSet() == V.isSet()) , argument);
                    break;

                case BHI: case LBHI:
                    // IFF [ C ∨ Z ] = 0 then PC' ← PC + TEMP
                    branch.jumpIf(() -> C.isClear() && Z.isClear(), argument);
                    break;

                case BLE: case LBLE:
                    // IFF Z ∨ [ N ⊕ V ] = 1 then PC' ← PC + TEMP
                    branch.jumpIf(() -> Z.isSet() || (N.isSet() != V.isSet()), argument);
                    break;

                case BLS: case LBLS:
                    // IFF (C ∨ Z) = 1 then PC' ← PC + TEMP
                    branch.jumpIf(() -> C.isSet() || Z.isSet(), argument);
                    break;

                case BLT: case LBLT:
                    // IFF [ N ⊕ V ] = 1 then PC' ← PC + TEMP
                    branch.jumpIf(() -> N.isSet() != V.isSet(), argument);
                    break;

                case BMI: case LBMI:
                    // IFF N = 1 then PC' ← PC + TEMP
                    branch.jumpIf(N::isSet, argument);
                    break;

                case BNE: case LBNE:
                    // IFF Z = 0 then PC' ← PC + TEMP
                    branch.jumpIf(Z::isClear, argument);
                    break;

                case BPL: case LBPL:
                    // IFF N = 0 then PC' ← PC + TEMP
                    branch.jumpIf(N::isClear, argument);
                    break;

                case BRA: case LBRA:
                    branch.jumpIf(() -> true, argument);
                    break;

                case BRN: case LBRN:
                    branch.jumpIf(() -> false, argument);
                    break;

                case BVC: case LBVC:
                    // IFF V = 0 then PC' ← PC + TEMP
                    branch.jumpIf(V::isClear, argument);
                    break;

                case BVS: case LBVS:
                    // IFF V = 1 then PC' ← PC + TEMP
                    branch.jumpIf(V::isSet, argument);
                    break;

                case SYNC:
                    throw new EOFException("SYNC");

                default:
                    throw new IllegalStateException("Unexpected instruction: " + instruction + " at 0x" + Integer.toHexString(address));
            }

            debug.instruction(mnemonic);
        } catch (IllegalStateException e) {
            throw new UnsupportedOperationException("\n" + instruction, e);
        }
    }

    public void reset() {
        I.set(true);
        F.set(true);
        Register.DP.set(0x00);
        branch.jump(Reference.of(memory, RESET_VECTOR, Size.WORD_16));
    }

    public void irq() {
        if (I.isSet()) return;
        branch.interrupt(Reference.of(memory, IRQ_VECTOR, Size.WORD_16));
        I.set();
    }

    public void firq() {
        if (F.isSet()) return;
        branch.fastInterrupt(Reference.of(memory, FIRQ_VECTOR, Size.WORD_16));
        F.set();
        I.set();
    }

    public void nmi() {
        branch.interrupt(Reference.of(memory, NMI_VECTOR, Size.WORD_16));
        I.set();
        F.set();
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
        Register.Z.set();
        Register.V.clear();
        Register.C.clear();
    }

}
