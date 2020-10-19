package com.joprovost.r8bemu.devices.mc6809;

import com.joprovost.r8bemu.clock.BusyState;
import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.data.discrete.DiscteteOutputHandler;
import com.joprovost.r8bemu.data.discrete.Flag;
import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.devices.memory.BinaryReference;
import com.joprovost.r8bemu.devices.memory.Size;

import static com.joprovost.r8bemu.devices.mc6809.Mnemonic.NEG;

public class MC6809E implements ClockAware {

    public static final int RESET_VECTOR = 0xfffe;
    public static final int IRQ_VECTOR = 0xfff8;
    public static final int NMI_VECTOR = 0xfffc;
    public static final int FIRQ_VECTOR = 0xfff6;

    private final Addressable memory;
    private final Stack stack;
    private final Debugger debug;
    private final BusyState clock;

    private final Flag reset = Flag.value(false);
    private final Flag irq = Flag.value(false);
    private final Flag firq = Flag.value(false);
    private final Flag nmi = Flag.value(false);
    private final Flag halt = Flag.value(false);
    private final Flag sync = Flag.value(false);
    private Mnemonic previous;

    public MC6809E(Addressable memory, Debugger debug, BusyState clock) {
        this.memory = memory;
        this.debug = debug;
        this.clock = clock;
        this.stack = new Stack(memory, this.clock);
    }

    public void tick(Clock unused) {
        if (clock.isBusy()) return;
        if (halt.isSet()) return;

        if (sync.isSet()) {
            if (irq.isSet() || firq.isSet() || nmi.isSet()) sync.clear();
            return;
        }

        if (reset.isSet()) doReset();
        else if (nmi.isSet()) doNmi();
        else if (firq.isSet() && Register.F.isClear()) doFirq();
        else if (irq.isSet() && Register.I.isClear()) doIrq();
        else execute();
    }

    private void execute() {
        var address = Register.PC.value();
        debug.at(address);
        var instruction = Op.next(memory, Register.PC);
        var mnemonic = instruction.mnemonic();

        if (mnemonic == NEG && previous == NEG) {
            throw new IllegalStateException("Two executions of NEG...: " + instruction + " at 0x" + Integer.toHexString(address));
        }

        var argument = debug.argument(Argument.next(memory, instruction.addressing(), Register.PC, clock));

        clock.busy(instruction.cycles());

        if (!mnemonic.execute(argument, stack, debug)) {
            switch (mnemonic) {
                case SYNC:
                    sync.set();
                    break;
                default:
                    throw new IllegalStateException("Unexpected instruction: " + instruction + " at 0x" + Integer.toHexString(address));
            }
        }
        debug.instruction(mnemonic);
    }

    private void doReset() {
        Register.I.set(true);
        Register.F.set(true);

        reset.clear();
        Register.DP.value(0x00);
        Branches.jump(BinaryReference.of(memory, RESET_VECTOR, Size.WORD_16));
    }

    private void doIrq() {
        clock.busy(6);
        Stack.interrupt(BinaryReference.of(memory, IRQ_VECTOR, Size.WORD_16), stack);
        Register.I.set();
    }

    private void doFirq() {
        clock.busy(6);
        Stack.fastInterrupt(BinaryReference.of(memory, FIRQ_VECTOR, Size.WORD_16), stack);
        Register.F.set();
        Register.I.set();
    }

    private void doNmi() {
        nmi.clear();
        Stack.interrupt(BinaryReference.of(memory, NMI_VECTOR, Size.WORD_16), stack);
        Register.I.set();
        Register.F.set();
    }

    public DiscteteOutputHandler reset() {
        return it -> reset.set(it.isSet() || reset.isSet());
    }

    public DiscteteOutputHandler irq() {
        return irq;
    }

    public DiscteteOutputHandler firq() {
        return firq;
    }

    public DiscteteOutputHandler nmi() {
        return nmi;
    }

    public DiscteteOutputHandler halt() {
        return halt;
    }
}
