package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.Debugger;
import com.joprovost.r8bemu.clock.BusyState;
import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.data.Reference;
import com.joprovost.r8bemu.data.Size;
import com.joprovost.r8bemu.memory.MemoryDevice;

import java.io.EOFException;
import java.io.IOException;

public class MC6809E implements ClockAware {

    public static final int RESET_VECTOR = 0xfffe;
    public static final int IRQ_VECTOR = 0xfff8;
    public static final int NMI_VECTOR = 0xfffc;
    public static final int FIRQ_VECTOR = 0xfff6;

    private final MemoryDevice memory;
    private final Stack stack;
    private final Debugger debug;
    private final BusyState clock;

    public MC6809E(MemoryDevice memory, Debugger debug, BusyState clock) {
        this.memory = memory;
        this.debug = debug;
        this.clock = clock;
        this.stack = new Stack(memory, this.clock);
    }

    public void tick(Clock unused) throws IOException {
        if (clock.isBusy()) return;
        if (Signal.RESET.isSet()) reset();
        else if (Signal.NMI.isSet()) nmi();
        else if (Signal.FIRQ.isSet() && Register.F.isClear()) firq();
        else if (Signal.IRQ.isSet() && Register.I.isClear()) irq();
        else execute();
    }

    private void execute() throws EOFException {
        var address = Register.PC.value();
        debug.at(address);
        var instruction = Op.next(memory, Register.PC);
        var mnemonic = instruction.mnemonic();
        var argument = debug.argument(Argument.next(memory, instruction.addressing(), Register.PC, clock));

        clock.busy(instruction.cycles());

        if (!mnemonic.execute(argument, stack, debug)) {
            if (mnemonic == Mnemonic.SYNC) throw new EOFException("SYNC");
            throw new IllegalStateException("Unexpected instruction: " + instruction + " at 0x" + Integer.toHexString(address));
        }
        debug.instruction(mnemonic);
    }

    private void reset() {
        Register.I.set(true);
        Register.F.set(true);
        Signal.RESET.clear();
        Register.DP.value(0x00);
        Branches.jump(Reference.of(memory, RESET_VECTOR, Size.WORD_16));
    }

    private void irq() {
        clock.busy(6);
        Branches.interrupt(Reference.of(memory, IRQ_VECTOR, Size.WORD_16), stack);
        Register.I.set();
    }

    private void firq() {
        clock.busy(6);
        Branches.fastInterrupt(Reference.of(memory, FIRQ_VECTOR, Size.WORD_16), stack);
        Register.F.set();
        Register.I.set();
    }

    private void nmi() {
        Branches.interrupt(Reference.of(memory, NMI_VECTOR, Size.WORD_16), stack);
        Register.I.set();
        Register.F.set();
    }
}
