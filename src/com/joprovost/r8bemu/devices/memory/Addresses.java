package com.joprovost.r8bemu.devices.memory;

import java.util.function.Supplier;

public interface Addresses {

    static Addresses of(Addresses... ranges) {
        return address -> {
            for (var range : ranges)
                if (range.contains(address))
                    return true;
            return false;
        };
    }

    boolean contains(int address);

    default Addresses excluding(Addresses... ranges) {
        return address -> {
            for (var range : ranges)
                if (range.contains(address))
                    return false;
            return contains(address);
        };
    }

    default Addresses onlyIf(Supplier<Boolean> condition) {
        return address -> condition.get() && contains(address);
    }

    default Addresses exceptIf(Supplier<Boolean> condition) {
        return address -> !condition.get() && contains(address);
    }
}
