package com.joprovost.r8bemu.port;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataOutput;

public interface DataPort {
    int P0 = 1;
    int P1 = 1 << 1;
    int P2 = 1 << 2;
    int P3 = 1 << 3;
    int P4 = 1 << 4;
    int P5 = 1 << 5;
    int P6 = 1 << 6;
    int P7 = 1 << 7;

    void inputFrom(DataInputProvider provider);
    void outputTo(DataOutputHandler handler);

    DataAccess input();
    DataOutput output();

    void interrupt();
}
