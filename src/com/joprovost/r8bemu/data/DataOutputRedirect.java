package com.joprovost.r8bemu.data;

public class DataOutputRedirect implements DataOutput {
    private DataOutput subject;

    private DataOutputRedirect(DataOutput subject) {
        this.subject = subject;
    }

    public static DataOutputRedirect empty() {
        return DataOutputRedirect.of(DataOutput.NONE);
    }

    public static DataOutputRedirect of(DataOutput subject) {
        return new DataOutputRedirect(subject);
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

    public DataOutputRedirect referTo(DataOutput subject) {
        this.subject = subject;
        return this;
    }
}
