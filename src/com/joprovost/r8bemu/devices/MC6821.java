package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.data.LogicInput;
import com.joprovost.r8bemu.memory.MemoryDevice;

public class MC6821 implements MemoryDevice {

    private final MC6821Port portA;
    private final MC6821Port portB;

    public MC6821(LogicInput irqa, LogicInput irqb) {
        portA = new MC6821Port(irqa);
        portB = new MC6821Port(irqb);
    }

    private MC6821Port port(int address) {
        int rs1 = address & 0b10;
        if (rs1 == 0) return portA;
        else return portB;
    }

    @Override
    public int read(int address) {
        return port(address).read(address);
    }

    @Override
    public void write(int address, int data) {
        port(address).write(address, data);
    }

    public MC6821Port portA() {
        return portA;
    }

    public MC6821Port portB() {
        return portB;
    }
}
