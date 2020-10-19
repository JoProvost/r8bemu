package com.joprovost.r8bemu.coco.devices.gime;

import com.joprovost.r8bemu.data.discrete.DiscreteInput;

import java.util.Arrays;
import java.util.function.Consumer;

public class CompositeDetection implements Consumer<int[]> {

    private static final int[] RGB = {18, 54, 9, 36, 63, 27, 45, 38, 0, 18, 0, 63, 0, 18, 0, 38};
    private static final int[] CMP = {18, 36, 11, 7, 63, 31, 9, 38, 0, 18, 0, 63, 0, 18, 0, 38};

    private final DiscreteInput composite;

    public CompositeDetection(DiscreteInput composite) {
        this.composite = composite;
    }

    @Override
    public void accept(int[] palette) {
        if (Arrays.equals(CMP, palette)) composite.set();
        if (Arrays.equals(RGB, palette)) composite.clear();
    }
}
