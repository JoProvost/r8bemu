package com.joprovost.r8bemu.data.binary;

public interface BinaryPort extends BinaryLineInput, BinaryLineOutput {
    int P0 = 1;
    int P1 = 1 << 1;
    int P2 = 1 << 2;
    int P3 = 1 << 3;
    int P4 = 1 << 4;
    int P5 = 1 << 5;
    int P6 = 1 << 6;
    int P7 = 1 << 7;
}
