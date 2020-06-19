package com.joprovost.r8bemu.port;

import com.joprovost.r8bemu.data.DataAccess;

public interface DataInputProvider {
    void provide(DataAccess input);
}
