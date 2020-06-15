package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.Debugger;
import com.joprovost.r8bemu.clock.Clock;
import com.joprovost.r8bemu.clock.FakeBusyState;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.devices.MC6883;
import com.joprovost.r8bemu.memory.Addressing;
import com.joprovost.r8bemu.memory.Memory;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpClockCyclesTest {
    public static final int INDEXED_NO_OFFSET_FROM_REGISTER = 0b10000100;

    @Test
    void allOpCodesCycles() throws IOException {
        assertEquals(6, cyclesOf(0x00));
        assertEquals(6, cyclesOf(0x03));
        assertEquals(6, cyclesOf(0x04));
        assertEquals(6, cyclesOf(0x06));
        assertEquals(6, cyclesOf(0x07));
        assertEquals(6, cyclesOf(0x08));
        assertEquals(6, cyclesOf(0x09));
        assertEquals(6, cyclesOf(0x0a));
        assertEquals(6, cyclesOf(0x0c));
        assertEquals(6, cyclesOf(0x0d));
        assertEquals(3, cyclesOf(0x0e));
        assertEquals(6, cyclesOf(0x0f));
        assertEquals(2, cyclesOf(0x12));
        // TODO SYNC assertEquals(4, cyclesOf(0x13));
        assertEquals(5, cyclesOf(0x16));
        assertEquals(9, cyclesOf(0x17));
        // TODO DAA assertEquals(2, cyclesOf(0x19));
        assertEquals(3, cyclesOf(0x1a));
        assertEquals(3, cyclesOf(0x1c));
        assertEquals(2, cyclesOf(0x1d));
        assertEquals(8, cyclesOf(0x1e));
        assertEquals(6, cyclesOf(0x1f));
        assertEquals(3, cyclesOf(0x20));
        assertEquals(3, cyclesOf(0x21));
        assertEquals(3, cyclesOf(0x22));
        assertEquals(3, cyclesOf(0x23));
        assertEquals(3, cyclesOf(0x24));
        assertEquals(3, cyclesOf(0x25));
        assertEquals(3, cyclesOf(0x26));
        assertEquals(3, cyclesOf(0x27));
        assertEquals(3, cyclesOf(0x28));
        assertEquals(3, cyclesOf(0x29));
        assertEquals(3, cyclesOf(0x2a));
        assertEquals(3, cyclesOf(0x2b));
        assertEquals(3, cyclesOf(0x2c));
        assertEquals(3, cyclesOf(0x2d));
        assertEquals(3, cyclesOf(0x2e));
        assertEquals(3, cyclesOf(0x2f));
        assertEquals(4, cyclesOf(0x30));
        assertEquals(4, cyclesOf(0x31));
        assertEquals(4, cyclesOf(0x32));
        assertEquals(4, cyclesOf(0x33));
        assertEquals(1, cyclesOf(0x34));
        assertEquals(1, cyclesOf(0x35));
        assertEquals(1, cyclesOf(0x36));
        assertEquals(1, cyclesOf(0x37));
        assertEquals(3, cyclesOf(0x39));
        assertEquals(3, cyclesOf(0x3a));
        assertEquals(5, cyclesOf(0x3b));
        // TODO CWAI assertEquals(20, cyclesOf(0x3c));
        assertEquals(11, cyclesOf(0x3d));
        // TODO SWI assertEquals(19, cyclesOf(0x3f));
        assertEquals(2, cyclesOf(0x40));
        assertEquals(2, cyclesOf(0x43));
        assertEquals(2, cyclesOf(0x44));
        assertEquals(2, cyclesOf(0x46));
        assertEquals(2, cyclesOf(0x47));
        assertEquals(2, cyclesOf(0x48));
        assertEquals(2, cyclesOf(0x49));
        assertEquals(2, cyclesOf(0x4a));
        assertEquals(2, cyclesOf(0x4c));
        assertEquals(2, cyclesOf(0x4d));
        assertEquals(2, cyclesOf(0x4f));
        assertEquals(2, cyclesOf(0x50));
        assertEquals(2, cyclesOf(0x53));
        assertEquals(2, cyclesOf(0x54));
        assertEquals(2, cyclesOf(0x56));
        assertEquals(2, cyclesOf(0x57));
        assertEquals(2, cyclesOf(0x58));
        assertEquals(2, cyclesOf(0x59));
        assertEquals(2, cyclesOf(0x5a));
        assertEquals(2, cyclesOf(0x5c));
        assertEquals(2, cyclesOf(0x5d));
        assertEquals(2, cyclesOf(0x5f));
        assertEquals(6, cyclesOf(0x60));
        assertEquals(6, cyclesOf(0x63));
        assertEquals(6, cyclesOf(0x64));
        assertEquals(6, cyclesOf(0x66));
        assertEquals(6, cyclesOf(0x67));
        assertEquals(6, cyclesOf(0x68));
        assertEquals(6, cyclesOf(0x69));
        assertEquals(6, cyclesOf(0x6a));
        assertEquals(6, cyclesOf(0x6c));
        assertEquals(6, cyclesOf(0x6d));
        assertEquals(6, cyclesOf(0x6e));
        assertEquals(6, cyclesOf(0x6f));
        assertEquals(5, cyclesOf(0x70));
        assertEquals(5, cyclesOf(0x73));
        assertEquals(5, cyclesOf(0x74));
        assertEquals(5, cyclesOf(0x76));
        assertEquals(5, cyclesOf(0x77));
        assertEquals(5, cyclesOf(0x78));
        assertEquals(5, cyclesOf(0x79));
        assertEquals(5, cyclesOf(0x7a));
        assertEquals(5, cyclesOf(0x7c));
        assertEquals(5, cyclesOf(0x7d));
        assertEquals(5, cyclesOf(0x7e));
        assertEquals(5, cyclesOf(0x7f));
        assertEquals(2, cyclesOf(0x80));
        assertEquals(2, cyclesOf(0x81));
        assertEquals(2, cyclesOf(0x82));
        assertEquals(4, cyclesOf(0x83));
        assertEquals(2, cyclesOf(0x84));
        assertEquals(2, cyclesOf(0x85));
        assertEquals(2, cyclesOf(0x86));
        assertEquals(2, cyclesOf(0x88));
        assertEquals(2, cyclesOf(0x89));
        assertEquals(2, cyclesOf(0x8a));
        assertEquals(2, cyclesOf(0x8b));
        assertEquals(4, cyclesOf(0x8c));
        assertEquals(7, cyclesOf(0x8d));
        assertEquals(3, cyclesOf(0x8e));
        assertEquals(4, cyclesOf(0x90));
        assertEquals(4, cyclesOf(0x91));
        assertEquals(4, cyclesOf(0x92));
        assertEquals(6, cyclesOf(0x93));
        assertEquals(4, cyclesOf(0x94));
        assertEquals(4, cyclesOf(0x95));
        assertEquals(4, cyclesOf(0x96));
        assertEquals(4, cyclesOf(0x97));
        assertEquals(4, cyclesOf(0x98));
        assertEquals(4, cyclesOf(0x99));
        assertEquals(4, cyclesOf(0x9a));
        assertEquals(4, cyclesOf(0x9b));
        assertEquals(6, cyclesOf(0x9c));
        assertEquals(5, cyclesOf(0x9d));
        assertEquals(5, cyclesOf(0x9e));
        assertEquals(5, cyclesOf(0x9f));
        assertEquals(4, cyclesOf(0xa0));
        assertEquals(4, cyclesOf(0xa1));
        assertEquals(4, cyclesOf(0xa2));
        assertEquals(6, cyclesOf(0xa3));
        assertEquals(4, cyclesOf(0xa4));
        assertEquals(4, cyclesOf(0xa5));
        assertEquals(4, cyclesOf(0xa6));
        assertEquals(4, cyclesOf(0xa7));
        assertEquals(4, cyclesOf(0xa8));
        assertEquals(4, cyclesOf(0xa9));
        assertEquals(4, cyclesOf(0xaa));
        assertEquals(4, cyclesOf(0xab));
        assertEquals(6, cyclesOf(0xac));
        assertEquals(5, cyclesOf(0xad));
        assertEquals(5, cyclesOf(0xae));
        assertEquals(5, cyclesOf(0xaf));
        assertEquals(5, cyclesOf(0xb0));
        assertEquals(5, cyclesOf(0xb1));
        assertEquals(5, cyclesOf(0xb2));
        assertEquals(7, cyclesOf(0xb3));
        assertEquals(5, cyclesOf(0xb4));
        assertEquals(5, cyclesOf(0xb5));
        assertEquals(5, cyclesOf(0xb6));
        assertEquals(5, cyclesOf(0xb7));
        assertEquals(5, cyclesOf(0xb8));
        assertEquals(5, cyclesOf(0xb9));
        assertEquals(5, cyclesOf(0xba));
        assertEquals(5, cyclesOf(0xbb));
        assertEquals(7, cyclesOf(0xbc));
        assertEquals(6, cyclesOf(0xbd));
        assertEquals(6, cyclesOf(0xbe));
        assertEquals(6, cyclesOf(0xbf));
        assertEquals(2, cyclesOf(0xc0));
        assertEquals(2, cyclesOf(0xc1));
        assertEquals(2, cyclesOf(0xc2));
        assertEquals(4, cyclesOf(0xc3));
        assertEquals(2, cyclesOf(0xc4));
        assertEquals(2, cyclesOf(0xc5));
        assertEquals(2, cyclesOf(0xc6));
        assertEquals(2, cyclesOf(0xc8));
        assertEquals(2, cyclesOf(0xc9));
        assertEquals(2, cyclesOf(0xca));
        assertEquals(2, cyclesOf(0xcb));
        assertEquals(3, cyclesOf(0xcc));
        assertEquals(3, cyclesOf(0xce));
        assertEquals(4, cyclesOf(0xd0));
        assertEquals(4, cyclesOf(0xd1));
        assertEquals(4, cyclesOf(0xd2));
        assertEquals(6, cyclesOf(0xd3));
        assertEquals(4, cyclesOf(0xd4));
        assertEquals(4, cyclesOf(0xd5));
        assertEquals(4, cyclesOf(0xd6));
        assertEquals(4, cyclesOf(0xd7));
        assertEquals(4, cyclesOf(0xd8));
        assertEquals(4, cyclesOf(0xd9));
        assertEquals(4, cyclesOf(0xda));
        assertEquals(4, cyclesOf(0xdb));
        assertEquals(5, cyclesOf(0xdc));
        assertEquals(5, cyclesOf(0xdd));
        assertEquals(5, cyclesOf(0xde));
        assertEquals(5, cyclesOf(0xdf));
        assertEquals(4, cyclesOf(0xe0));
        assertEquals(4, cyclesOf(0xe1));
        assertEquals(4, cyclesOf(0xe2));
        assertEquals(6, cyclesOf(0xe3));
        assertEquals(4, cyclesOf(0xe4));
        assertEquals(4, cyclesOf(0xe5));
        assertEquals(4, cyclesOf(0xe6));
        assertEquals(4, cyclesOf(0xe7));
        assertEquals(4, cyclesOf(0xe8));
        assertEquals(4, cyclesOf(0xe9));
        assertEquals(4, cyclesOf(0xea));
        assertEquals(4, cyclesOf(0xeb));
        assertEquals(5, cyclesOf(0xec));
        assertEquals(5, cyclesOf(0xed));
        assertEquals(5, cyclesOf(0xee));
        assertEquals(5, cyclesOf(0xef));
        assertEquals(5, cyclesOf(0xf0));
        assertEquals(5, cyclesOf(0xf1));
        assertEquals(5, cyclesOf(0xf2));
        assertEquals(7, cyclesOf(0xf3));
        assertEquals(5, cyclesOf(0xf4));
        assertEquals(5, cyclesOf(0xf5));
        assertEquals(5, cyclesOf(0xf6));
        assertEquals(5, cyclesOf(0xf7));
        assertEquals(5, cyclesOf(0xf8));
        assertEquals(5, cyclesOf(0xf9));
        assertEquals(5, cyclesOf(0xfa));
        assertEquals(5, cyclesOf(0xfb));
        assertEquals(6, cyclesOf(0xfc));
        assertEquals(6, cyclesOf(0xfd));
        assertEquals(6, cyclesOf(0xfe));
        assertEquals(6, cyclesOf(0xff));
        assertEquals(5, cyclesOf(0x10, 0x21));
        assertEquals(5, cyclesOf(0x10, 0x22));
        assertEquals(5, cyclesOf(0x10, 0x23));
        assertEquals(5, cyclesOf(0x10, 0x24));
        assertEquals(5, cyclesOf(0x10, 0x25));
        assertEquals(5, cyclesOf(0x10, 0x26));
        assertEquals(5, cyclesOf(0x10, 0x27));
        assertEquals(5, cyclesOf(0x10, 0x28));
        assertEquals(5, cyclesOf(0x10, 0x29));
        assertEquals(5, cyclesOf(0x10, 0x2a));
        assertEquals(5, cyclesOf(0x10, 0x2b));
        assertEquals(5, cyclesOf(0x10, 0x2c));
        assertEquals(5, cyclesOf(0x10, 0x2d));
        assertEquals(5, cyclesOf(0x10, 0x2e));
        assertEquals(5, cyclesOf(0x10, 0x2f));
        // TODO SWI2 assertEquals(20, cyclesOf(0x10, 0x3f));
        assertEquals(5, cyclesOf(0x10, 0x83));
        assertEquals(5, cyclesOf(0x10, 0x8c));
        assertEquals(4, cyclesOf(0x10, 0x8e));
        assertEquals(7, cyclesOf(0x10, 0x93));
        assertEquals(7, cyclesOf(0x10, 0x9c));
        assertEquals(6, cyclesOf(0x10, 0x9e));
        assertEquals(6, cyclesOf(0x10, 0x9f));
        assertEquals(7, cyclesOf(0x10, 0xa3));
        assertEquals(7, cyclesOf(0x10, 0xac));
        assertEquals(6, cyclesOf(0x10, 0xae));
        assertEquals(6, cyclesOf(0x10, 0xaf));
        assertEquals(8, cyclesOf(0x10, 0xb3));
        assertEquals(8, cyclesOf(0x10, 0xbc));
        assertEquals(7, cyclesOf(0x10, 0xbe));
        assertEquals(7, cyclesOf(0x10, 0xbf));
        assertEquals(4, cyclesOf(0x10, 0xce));
        assertEquals(6, cyclesOf(0x10, 0xde));
        assertEquals(6, cyclesOf(0x10, 0xdf));
        assertEquals(6, cyclesOf(0x10, 0xee));
        assertEquals(6, cyclesOf(0x10, 0xef));
        assertEquals(7, cyclesOf(0x10, 0xfe));
        assertEquals(7, cyclesOf(0x10, 0xff));
        // TODO SWI3 assertEquals(20, cyclesOf(0x11, 0x3f));
        assertEquals(5, cyclesOf(0x11, 0x83));
        assertEquals(5, cyclesOf(0x11, 0x8c));
        assertEquals(7, cyclesOf(0x11, 0x93));
        assertEquals(7, cyclesOf(0x11, 0x9c));
        assertEquals(7, cyclesOf(0x11, 0xa3));
        assertEquals(7, cyclesOf(0x11, 0xac));
        assertEquals(8, cyclesOf(0x11, 0xb3));
        assertEquals(8, cyclesOf(0x11, 0xbc));
    }

    private int cyclesOf(int op, int... next) throws IOException {
        Memory ram = new Memory(0x7fff);
        MC6883 sam = MC6883.ofRam(ram);
        FakeBusyState clock = new FakeBusyState();
        MC6809E cpu = new MC6809E(sam, Debugger.none(), clock);

        ram.write(0x4000, op, next);
        switch (addressingOf(op, next)) {
            case INDEXED_DATA_8:
            case INDEXED_DATA_16:
            case INDEXED_ADDRESS:
                ram.write(0x4001 + next.length, INDEXED_NO_OFFSET_FROM_REGISTER);
        }

        Register.reset();
        Register.PC.set(0x4000);
        Register.X.set(0x2000);
        Register.Y.set(0x2000);
        Register.U.set(0x2000);
        Register.S.set(0x2000);
        Register.D.set(0x2000);

        cpu.tick(Clock.zero());
        return clock.cycles();
    }

    private Addressing addressingOf(int op, int... next) {
        var mem = new Memory(0xff);
        mem.write(0x00, op, next);
        Register.PC.set(0);
        return Op.next(mem, Variable.ofMask(0xffff)).addressing();
    }
}
