package com.joprovost.r8bemu.port;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataOutput;

public interface DataPort {
    void inputFrom(DataInputProvider provider);
    void outputTo(DataOutputHandler handler);

    DataAccess input();
    DataOutput output();

    void interrupt();
}
