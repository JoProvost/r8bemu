package com.joprovost.r8bemu.data.link;

import com.joprovost.r8bemu.data.BitOutput;

public interface LinePort extends LineInput, LineOutput, LineOutputHandler {
    default void handle(BitOutput state) {
        set(state.isSet());
    }
}
