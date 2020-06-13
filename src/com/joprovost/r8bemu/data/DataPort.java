package com.joprovost.r8bemu.data;

public interface DataPort {
    void feeder(DataFeeder feeder);
    void consumer(DataConsumer consumer);

    DataAccess input();
    DataOutput output();
    void control();

    interface DataFeeder {
        void feed(DataAccess input);
    }

    interface DataConsumer {
        void consume(DataOutput output);
    }
}
