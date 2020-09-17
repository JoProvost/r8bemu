package com.joprovost.r8bemu.memory;

import java.util.function.Supplier;

public interface Range {

    static Range of(Range... ranges) {
        return address -> {
            for (var range : ranges)
                if (range.contains(address))
                    return true;
            return false;
        };
    }

    boolean contains(int address);

    default Range excluding(Range... ranges) {
        return address -> {
            for (var range : ranges)
                if (range.contains(address))
                    return false;
            return contains(address);
        };
    }

    default Range onlyIf(Supplier<Boolean> condition) {
        return address -> condition.get() && contains(address);
    }

    default Range exceptIf(Supplier<Boolean> condition) {
        return address -> !condition.get() && contains(address);
    }
}
