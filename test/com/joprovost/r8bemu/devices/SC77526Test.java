package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.coco.devices.SC77526;
import com.joprovost.r8bemu.data.binary.BinaryRegister;
import com.joprovost.r8bemu.data.binary.BinaryValue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.joprovost.r8bemu.data.binary.BinaryPort.P7;
import static com.joprovost.r8bemu.data.discrete.Flag.value;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SC77526Test {

    List<Double> audioSent = new ArrayList<>();

    SC77526 sc77526 = new SC77526(audioSent::add);

    @Test
    void sendAudioAsSignedByteWhenEnabled() {
        sc77526.sndEn().handle(value(false));
        sc77526.dac().handle(BinaryValue.of(16, 0xff));
        assertEquals(0, audioSent.size());

        sc77526.sndEn().handle(value(true));
        assertEquals(1, audioSent.size());
        assertEquals(-0.50, audioSent.get(0));

        sc77526.dac().handle(BinaryValue.of(63, 0xff));
        assertEquals(2, audioSent.size());
        assertEquals(1.0, audioSent.get(1));
    }

    @Test
    void applyProperMaskOnDAC() {
        sc77526.sndEn().handle(value(false));
        sc77526.dac().handle(BinaryValue.of(16, 0xff));
        sc77526.sndEn().handle(value(true));
        assertEquals(1, audioSent.size());
        assertEquals(-0.50, audioSent.get(0));
    }

    @Test
    void leftJoystickHorizontalComparedAgainstDAC() {
        BinaryRegister hiLo = BinaryRegister.ofMask(0xff);
        sc77526.selA().handle(value(false));
        sc77526.selB().handle(value(false));
        sc77526.joy(0).value((20-32) / 32.0);

        sc77526.dac().handle(BinaryValue.of(20, 0xff));
        sc77526.cmp(P7).provide(hiLo);
        assertEquals(0x80, hiLo.value());

        sc77526.dac().handle(BinaryValue.of(21, 0xff));
        sc77526.cmp(P7).provide(hiLo);
        assertEquals(0x00, hiLo.value());
    }

    @Test
    void leftJoystickVerticalComparedAgainstDAC() {
        BinaryRegister hiLo = BinaryRegister.ofMask(0xff);
        sc77526.selA().handle(value(true));
        sc77526.selB().handle(value(false));
        sc77526.joy(1).value((30 - 32) / 32.0);

        sc77526.dac().handle(BinaryValue.of(30, 0xff));
        sc77526.cmp(P7).provide(hiLo);
        assertEquals(0x80, hiLo.value());

        sc77526.dac().handle(BinaryValue.of(31, 0xff));
        sc77526.cmp(P7).provide(hiLo);
        assertEquals(0x00, hiLo.value());
    }

    @Test
    void rightJoystickHorizontalComparedAgainstDAC() {
        BinaryRegister hiLo = BinaryRegister.ofMask(0xff);
        sc77526.selA().handle(value(false));
        sc77526.selB().handle(value(true));
        sc77526.joy(2).value((20-32) / 32.0);

        sc77526.dac().handle(BinaryValue.of(20, 0xff));
        sc77526.cmp(P7).provide(hiLo);
        assertEquals(0x80, hiLo.value());

        sc77526.dac().handle(BinaryValue.of(21, 0xff));
        sc77526.cmp(P7).provide(hiLo);
        assertEquals(0x00, hiLo.value());
    }

    @Test
    void rightJoystickVerticalComparedAgainstDAC() {
        BinaryRegister hiLo = BinaryRegister.ofMask(0xff);
        sc77526.selA().handle(value(true));
        sc77526.selB().handle(value(true));
        sc77526.joy(3).value((30-32) / 32.0);

        sc77526.dac().handle(BinaryValue.of(30, 0xff));
        sc77526.cmp(P7).provide(hiLo);
        assertEquals(0x80, hiLo.value());

        sc77526.dac().handle(BinaryValue.of(31, 0xff));
        sc77526.cmp(P7).provide(hiLo);
        assertEquals(0x00, hiLo.value());
    }
}
