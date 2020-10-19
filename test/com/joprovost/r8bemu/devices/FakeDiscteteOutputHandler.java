package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.data.discrete.DiscteteOutputHandler;

import java.util.ArrayList;

class FakeDiscteteOutputHandler extends ArrayList<Boolean> implements DiscteteOutputHandler {
    @Override
    public void handle(DiscreteOutput state) {
        add(state.isSet());
    }
}
