package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.data.DataInput;
import com.joprovost.r8bemu.memory.MemoryMapped;

public class MC6821 implements MemoryMapped {

    public final MC6821Port a;
    public final MC6821Port b;

    public MC6821(DataInput irqa, DataInput irqb) {
        a = new MC6821Port(irqa);
        b = new MC6821Port(irqb);
    }

    private MC6821Port port(int address) {
        int rs1 = address & 0b10;
        if (rs1 == 0) return a;
        else return b;
    }

    @Override
    public int read(int address) {
        return port(address).read(address);
    }

    @Override
    public void write(int address, int data) {
        port(address).write(address, data);
    }
}
