package com.joprovost.r8bemu.devices.disk;

import com.joprovost.r8bemu.data.BitOutput;
import com.joprovost.r8bemu.data.link.LineOutputHandler;

import java.util.ArrayList;

class FakeLineOutputHandler extends ArrayList<Boolean> implements LineOutputHandler {
    @Override
    public void handle(BitOutput state) {
        add(state.isSet());
    }
}
