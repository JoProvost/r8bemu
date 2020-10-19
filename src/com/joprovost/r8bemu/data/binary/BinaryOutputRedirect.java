package com.joprovost.r8bemu.data.binary;

public class BinaryOutputRedirect implements BinaryOutput {
    private BinaryOutput subject;

    private BinaryOutputRedirect(BinaryOutput subject) {
        this.subject = subject;
    }

    public static BinaryOutputRedirect empty() {
        return BinaryOutputRedirect.of(BinaryOutput.NONE);
    }

    public static BinaryOutputRedirect of(BinaryOutput subject) {
        return new BinaryOutputRedirect(subject);
    }

    @Override
    public int value() {
        return subject.value();
    }

    @Override
    public int mask() {
        return subject.mask();
    }

    @Override
    public String description() {
        return subject.description();
    }

    @Override
    public String toString() {
        return subject.toString();
    }

    public BinaryOutputRedirect referTo(BinaryOutput subject) {
        this.subject = subject;
        return this;
    }
}
