package com.joprovost.r8bemu.data;

import com.joprovost.r8bemu.data.buffer.BigEndianAudioBuffer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class BigEndianAudioBufferTest {

    BigEndianAudioBuffer buffer = new BigEndianAudioBuffer(4, 1, 128);
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
        buffer.write(256);
        buffer.write(512);
        buffer.write(1024);
        buffer.write(2048);
        assertEquals(1, input.read());
        assertEquals(0, input.read());
        assertEquals(2, input.read());
        assertEquals(0, input.read());
        assertEquals(4, input.read());
        assertEquals(0, input.read());
        assertEquals(8, input.read());
        assertEquals(0, input.read());

        buffer.write(5);
        buffer.write(6);
        assertEquals(0, input.read());
        assertEquals(5, input.read());
        assertEquals(0, input.read());
        assertEquals(6, input.read());
    }

    @Test
    void skipWritesTheLastValueAgain() throws IOException {
        buffer.write(1);
        buffer.skip(3);
        assertTrue(buffer.isFull());
        assertEquals(0, input.read());
        assertEquals(1, input.read());
        assertEquals(0, input.read());
        assertEquals(1, input.read());
        assertEquals(0, input.read());
        assertEquals(1, input.read());
        assertEquals(0, input.read());
        assertEquals(1, input.read());
    }

    @Test
    void skipWritesInitialValue() throws IOException {
        buffer.skip(3);
        assertEquals(0, input.read());
        assertEquals(128, input.read());
        assertEquals(0, input.read());
        assertEquals(128, input.read());
        assertEquals(0, input.read());
        assertEquals(128, input.read());
    }

    @Test
    void skipDoesNotingIfBufferSizeOrMoreIsRequested() throws IOException {
        buffer.write(1);
        buffer.skip(4);
        assertFalse(buffer.isFull());
        assertEquals(0, input.read());
        assertEquals(1, input.read());
        assertEquals(-1, input.read());
    }

    @Test
    void readNBytesIsNonBlocking() throws IOException {
        buffer.write(1);
        buffer.write(2);
        var bytes = input.readNBytes(8);

        assertEquals(4, bytes.length);
        assertEquals(0, bytes[0]);
        assertEquals(1, bytes[1]);
        assertEquals(0, bytes[2]);
        assertEquals(2, bytes[3]);
    }
}
