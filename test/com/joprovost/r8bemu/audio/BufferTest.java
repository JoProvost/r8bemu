package com.joprovost.r8bemu.audio;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class BufferTest {

    Buffer buffer = new Buffer(4, 1);
    InputStream input = buffer.input();

    @Test
    void readReturnsWenEmpty() throws IOException {
        assertTrue(buffer.isEmpty());
        assertEquals(-1, input.read());
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
        assertEquals(1, input.read());
        assertEquals(2, input.read());
        assertEquals(3, input.read());
        assertEquals(4, input.read());

        buffer.write(5);
        buffer.write(6);
        assertEquals(5, input.read());
        assertEquals(6, input.read());
    }

    @Test
    void skipWritesTheLastValueAgain() throws IOException {
        buffer.write(1);
        buffer.skip(3);
        assertTrue(buffer.isFull());
        assertEquals(1, input.read());
        assertEquals(1, input.read());
        assertEquals(1, input.read());
        assertEquals(1, input.read());
    }

    @Test
    void skipDoesNotingIfBufferSizeOrMoreIsRequested() throws IOException {
        buffer.write(1);
        buffer.skip(4);
        assertFalse(buffer.isFull());
        assertEquals(1, input.read());
        assertEquals(-1, input.read());
    }

    @Test
    void readNBytesIsNonBlocking() throws IOException {
        buffer.write(1);
        buffer.write(2);
        var bytes = input.readNBytes(4);

        assertEquals(2, bytes.length);
        assertEquals(1, bytes[0]);
        assertEquals(2, bytes[1]);
    }
}
