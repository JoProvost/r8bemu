package com.joprovost.r8bemu.audio;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BufferTest {

    Buffer buffer = new Buffer(4, 1);

    @Test
    void readReturnsWenEmpty() throws IOException {
        assertTrue(buffer.isEmpty());
        assertEquals(-1, buffer.read());
    }

    @Test
    void isFull() throws IOException {
        buffer.write(1);
        buffer.write(2);
        buffer.write(3);
        assertFalse(buffer.isFull());
        buffer.write(4);
        assertTrue(buffer.isFull());
    }

    @Test
    void isFirstInFirstOut() throws IOException {
        buffer.write(1);
        buffer.write(2);
        buffer.write(3);
        buffer.write(4);
        assertEquals(1, buffer.read());
        assertEquals(2, buffer.read());
        assertEquals(3, buffer.read());
        assertEquals(4, buffer.read());

        buffer.write(5);
        buffer.write(6);
        assertEquals(5, buffer.read());
        assertEquals(6, buffer.read());
    }

    @Test
    void skipWritesTheLastValueAgain() throws IOException {
        buffer.write(1);
        buffer.skip(3);
        assertTrue(buffer.isFull());
        assertEquals(1, buffer.read());
        assertEquals(1, buffer.read());
        assertEquals(1, buffer.read());
        assertEquals(1, buffer.read());
    }

    @Test
    void skipDoesNotingIfBufferSizeOrMoreIsRequested() throws IOException {
        buffer.write(1);
        buffer.skip(4);
        assertFalse(buffer.isFull());
        assertEquals(1, buffer.read());
        assertEquals(-1, buffer.read());
    }
}
