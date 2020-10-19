package com.joprovost.r8bemu.devices.mc6809;

import com.joprovost.r8bemu.data.binary.BinaryAccess;

interface Task {

    static Task of(DataAccessOperation operation) {
        return (register, argument, stack, debug) -> operation.execute(register != null ? register : argument);
    }

    static Task of(StackOperation operation) {
        return (register, argument, stack, debug) -> operation.execute(register != null ? register : argument, stack);
    }

    static Task of(DataRegisterOperation operation) {
        return (register, argument, stack, debug) -> operation.execute(register, argument);
    }

    static Task of(Runnable operation) {
        return (register, argument, stack, debug) -> operation.run();
    }

    void execute(Register register, BinaryAccess argument, Stack stack, Debugger debug);

    interface DataAccessOperation {
        void execute(BinaryAccess argument);
    }

    interface DataRegisterOperation {
        void execute(Register register, BinaryAccess argument);
    }

    interface StackOperation {
        void execute(BinaryAccess argument, Stack stack);
    }
}
