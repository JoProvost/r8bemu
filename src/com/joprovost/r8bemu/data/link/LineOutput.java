package com.joprovost.r8bemu.data.link;

import com.joprovost.r8bemu.data.BitOutput;

public interface LineOutput extends BitOutput {
    void to(LineOutputHandler handler);
}
