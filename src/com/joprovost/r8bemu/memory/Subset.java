package com.joprovost.r8bemu.memory;

public interface Subset extends Range {
    static Subset mask(int start, int mask) {
        return new Subset() {
            @Override
            public int offset(int address) {
                return address & mask;
            }

            @Override
            public int address(int offset) {
                return offset + start;
            }

            @Override
            public boolean contains(int address) {
                return (address & ~mask) == start;
            }
        };
    }

    static Subset range(int first, int last) {
        return new Subset() {
            @Override
            public int offset(int address) {
                return address - first;
            }

            @Override
            public int address(int offset) {
                return offset + first;
            }

            @Override
            public boolean contains(int address) {
                return address >= first && address <= last;
            }
        };
    }

    int offset(int address);
    int address(int offset);
}
