package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.data.LogicVariable;
import com.joprovost.r8bemu.data.Value;
import com.joprovost.r8bemu.data.Variable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.joprovost.r8bemu.port.DataPort.P2;
import static com.joprovost.r8bemu.port.DataPort.P3;
import static com.joprovost.r8bemu.port.DataPort.P4;
import static com.joprovost.r8bemu.port.DataPort.P5;
import static com.joprovost.r8bemu.port.DataPort.P6;
import static com.joprovost.r8bemu.port.DataPort.P7;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SC77526Test {

    List<Integer> audioSent = new ArrayList<>();

    SC77526 sc77526 = new SC77526(audioSent::add);

    @Test
    void sendAudioAsSignedByteWhenEnabled() {
        sc77526.soundOutput().handle(LogicVariable.of(false));
        sc77526.dac(0x3f).handle(Value.of(16, 0xff));
        assertEquals(0, audioSent.size());

        sc77526.soundOutput().handle(LogicVariable.of(true));
        assertEquals(1, audioSent.size());
        assertEquals(16 * 4 - 128, audioSent.get(0));

        sc77526.dac(0x3f).handle(Value.of(50, 0xff));
        assertEquals(2, audioSent.size());
        assertEquals(50 * 4 - 128, audioSent.get(1));
    }

    @Test
    void applyProperMaskOnDAC() {
        sc77526.soundOutput().handle(LogicVariable.of(false));
        sc77526.dac(P7 | P6 | P5 | P4 | P3 | P2).handle(Value.of(16 << 2, 0xff));
        sc77526.soundOutput().handle(LogicVariable.of(true));
        assertEquals(1, audioSent.size());
        assertEquals(16 * 4 - 128, audioSent.get(0));
    }

    @Test
    void leftJoystickHorizontalComparedAgainstDAC() {
        Variable hiLo = Variable.ofMask(0xff);
        sc77526.selA().handle(LogicVariable.of(false));
        sc77526.selB().handle(LogicVariable.of(false));
        sc77526.left().horizontal(20);

        sc77526.dac(P7 | P6 | P5 | P4 | P3 | P2).handle(Value.of(20 << 2, 0xff));
        sc77526.joystick(P7).provide(hiLo);
        assertEquals(0x80, hiLo.value());

        sc77526.dac(P7 | P6 | P5 | P4 | P3 | P2).handle(Value.of(21 << 2, 0xff));
        sc77526.joystick(P7).provide(hiLo);
        assertEquals(0x00, hiLo.value());
    }

    @Test
    void leftJoystickVerticalComparedAgainstDAC() {
        Variable hiLo = Variable.ofMask(0xff);
        sc77526.selA().handle(LogicVariable.of(true));
        sc77526.selB().handle(LogicVariable.of(false));
        sc77526.left().vertical(30);

        sc77526.dac(P7 | P6 | P5 | P4 | P3 | P2).handle(Value.of(30 << 2, 0xff));
        sc77526.joystick(P7).provide(hiLo);
        assertEquals(0x80, hiLo.value());

        sc77526.dac(P7 | P6 | P5 | P4 | P3 | P2).handle(Value.of(31 << 2, 0xff));
        sc77526.joystick(P7).provide(hiLo);
        assertEquals(0x00, hiLo.value());
    }

    @Test
    void rightJoystickHorizontalComparedAgainstDAC() {
        Variable hiLo = Variable.ofMask(0xff);
        sc77526.selA().handle(LogicVariable.of(false));
        sc77526.selB().handle(LogicVariable.of(true));
        sc77526.right().horizontal(20);

        sc77526.dac(P7 | P6 | P5 | P4 | P3 | P2).handle(Value.of(20 << 2, 0xff));
        sc77526.joystick(P7).provide(hiLo);
        assertEquals(0x80, hiLo.value());

        sc77526.dac(P7 | P6 | P5 | P4 | P3 | P2).handle(Value.of(21 << 2, 0xff));
        sc77526.joystick(P7).provide(hiLo);
        assertEquals(0x00, hiLo.value());
    }

    @Test
    void rightJoystickVerticalComparedAgainstDAC() {
        Variable hiLo = Variable.ofMask(0xff);
        sc77526.selA().handle(LogicVariable.of(true));
        sc77526.selB().handle(LogicVariable.of(true));
        sc77526.right().vertical(30);

        sc77526.dac(P7 | P6 | P5 | P4 | P3 | P2).handle(Value.of(30 << 2, 0xff));
        sc77526.joystick(P7).provide(hiLo);
        assertEquals(0x80, hiLo.value());

        sc77526.dac(P7 | P6 | P5 | P4 | P3 | P2).handle(Value.of(31 << 2, 0xff));
        sc77526.joystick(P7).provide(hiLo);
        assertEquals(0x00, hiLo.value());
    }
}
