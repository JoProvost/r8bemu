package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.Debugger;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.clock.ClockState;
import com.joprovost.r8bemu.data.Reference;
import com.joprovost.r8bemu.data.Size;
import com.joprovost.r8bemu.memory.MemoryMapped;

import java.io.EOFException;
import java.io.IOException;

import static com.joprovost.r8bemu.mc6809.Register.F;
import static com.joprovost.r8bemu.mc6809.Register.I;
import static com.joprovost.r8bemu.mc6809.Register.PC;

public class MC6809E implements ClockAware {

    public static final int RESET_VECTOR = 0xfffe;
    public static final int IRQ_VECTOR = 0xfff8;
    public static final int NMI_VECTOR = 0xfffc;
    public static final int FIRQ_VECTOR = 0xfff6;

    private final MemoryMapped memory;
    private final Stack stack;
    private final Debugger debug;
    private ClockState clock = new ClockState();

    public MC6809E(MemoryMapped memory, Debugger debug) {
        this.memory = memory;
        this.debug = debug;
        this.stack = new Stack(memory);
    }

    public void tick(long tick) throws IOException {
        if (clock.at(tick).isBusy()) return;

        var address = PC.unsigned();
        debug.at(address);
        var instruction = Op.next(memory, PC);
        var mnemonic = instruction.mnemonic();
        var argument = debug.argument(Argument.next(memory, instruction.addressing(), PC, clock));

        clock.busy(instruction.cycles());

        if (!mnemonic.execute(argument)) {
            switch (mnemonic) {
                case TFR:
                    debug.argument(Pair.registers(argument)).transfer();
                    break;

                case EXG:
                    debug.argument(Pair.registers(argument)).exchange();
                    break;

                case PSHU: case PSHS:
                    stack.pushAll(mnemonic.register(), argument, clock);
                    break;

                case PULU: case PULS:
                    stack.pullAll(mnemonic.register(), argument, clock);
                    break;

                case JSR:
                    Branches.jsr(argument, stack, clock);
                    break;

                case RTS:
                    Branches.rts(stack, clock);
                    break;

                case RTI:
                    Branches.rti(stack, clock);
                    break;

                case BSR: case LBSR:
                    Branches.bsr(argument, stack, clock);
                    break;

                case SYNC:
                    throw new EOFException("SYNC");

                default:
                    throw new IllegalStateException("Unexpected instruction: " + instruction + " at 0x" + Integer.toHexString(address));
            }
        }
        debug.instruction(mnemonic);
    }

    public void reset() {
        I.set(true);
        F.set(true);
        Register.DP.set(0x00);
        Branches.jump(Reference.of(memory, RESET_VECTOR, Size.WORD_16));
    }

    public void irq() {
        if (I.isSet()) return;
        Branches.interrupt(Reference.of(memory, IRQ_VECTOR, Size.WORD_16), stack, clock);
        I.set();
    }

    public void firq() {
        if (F.isSet()) return;
        Branches.fastInterrupt(Reference.of(memory, FIRQ_VECTOR, Size.WORD_16), stack, clock);
        F.set();
        I.set();
    }

    public void nmi() {
        Branches.interrupt(Reference.of(memory, NMI_VECTOR, Size.WORD_16), stack, clock);
        I.set();
        F.set();
    }
}
