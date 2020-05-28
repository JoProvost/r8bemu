package com.joprovost.r8bemu;

import com.joprovost.r8bemu.memory.Addressing;

public class Instruction<Mnemonic> {
    private final int code;
    private final Mnemonic mnemonic;
    private final Addressing addressing;

    protected Instruction(int code, Mnemonic mnemonic, Addressing addressing) {
        this.code = code;
        this.mnemonic = mnemonic;
        this.addressing = addressing;
    }

    public static <Mnemonic> Instruction<Mnemonic> op(int code, Mnemonic mnemonic, Addressing addressing) {
        return new Instruction<>(code, mnemonic, addressing);
    }

    public int code() {
        return code;
    }

    public Mnemonic mnemonic() {
        return mnemonic;
    }

    public Addressing addressing() {
        return addressing;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "code=" + code +
                ", mnemonic=" + mnemonic +
                ", addressing=" + addressing +
                '}';
    }
}
