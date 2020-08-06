package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.memory.MemoryDevice;

public class MC6821 implements MemoryDevice {

    private final MC6821Port portA;
    private final MC6821Port portB;

    public MC6821(MC6821Port portA, MC6821Port portB) {
        this.portA = portA;
        this.portB = portB;
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

    public MC6821Port a() {
        return portA;
    }

    public MC6821Port b() {
        return portB;
    }
}
