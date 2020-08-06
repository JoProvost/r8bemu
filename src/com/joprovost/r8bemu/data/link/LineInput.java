package com.joprovost.r8bemu.data.link;

import com.joprovost.r8bemu.data.BitInput;

public interface LineInput extends BitInput {
    default void pulse() {
        set();
        clear();
    }
}
