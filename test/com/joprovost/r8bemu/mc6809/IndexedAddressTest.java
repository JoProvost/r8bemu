package com.joprovost.r8bemu.mc6809;

import com.joprovost.r8bemu.clock.FakeBusyState;
import com.joprovost.r8bemu.memory.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IndexedAddressTest {

    Memory memory = new Memory(0xffff);
    FakeBusyState clock = new FakeBusyState();

    @BeforeEach
    void setup() {
        Register.X.value(1000);
        Register.Y.value(1100);
        Register.U.value(1200);
        Register.S.value(1300);

        Register.PC.value(0);
    }

    @Nested
    class Direct {

        @Nested
        class ConstantOffsetFromRegister {
            @Nested
            class Offset5bit {
                @Test
                void x() {
                    memory.write(0, 0b00010001);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("-15,X", reference.description());

                    assertEquals(1000 - 15, reference.value());
                    assertEquals(1000, Register.X.value());
                    assertEquals(1, clock.cycles());
                }

                @Test
                void y() {
                    memory.write(0, 0b00110000);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("-16,Y", reference.description());

                    assertEquals(1100 - 16, reference.value());
                    assertEquals(1100, Register.Y.value());
                    assertEquals(1, clock.cycles());
                }

                @Test
                void u() {
                    memory.write(0, 0b01000010);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("2,U", reference.description());

                    assertEquals(1200 + 2, reference.value());
                    assertEquals(1200, Register.U.value());
                    assertEquals(1, clock.cycles());
                }

                @Test
                void s() {
                    memory.write(0, 0b01101111);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("15,S", reference.description());

                    assertEquals(1300 + 15, reference.value());
                    assertEquals(1300, Register.S.value());
                    assertEquals(1, clock.cycles());
                }
            }

            @Nested
            class Offset8bit {
                @Test
                void x() {
                    memory.write(0, 0b10001000, -15);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("-15,X", reference.description());

                    assertEquals(1000 - 15, reference.value());
                    assertEquals(1000, Register.X.value());
                    assertEquals(1, clock.cycles());
                }

                @Test
                void y() {
                    memory.write(0, 0b10101000, -16);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("-16,Y", reference.description());

                    assertEquals(1100 - 16, reference.value());
                    assertEquals(1100, Register.Y.value());
                    assertEquals(1, clock.cycles());
                }

                @Test
                void u() {
                    memory.write(0, 0b11001000, 2);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("2,U", reference.description());

                    assertEquals(1200 + 2, reference.value());
                    assertEquals(1200, Register.U.value());
                    assertEquals(1, clock.cycles());
                }

                @Test
                void s() {
                    memory.write(0, 0b11101000, 15);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("15,S", reference.description());

                    assertEquals(1300 + 15, reference.value());
                    assertEquals(1300, Register.S.value());
                    assertEquals(1, clock.cycles());
                }
            }

            @Nested
            class Offset16bit {
                @Test
                void x() {
                    memory.write(0, 0b10001001, 0xff, -15);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("-15,X", reference.description());

                    assertEquals(1000 - 15, reference.value());
                    assertEquals(1000, Register.X.value());
                    assertEquals(4, clock.cycles());
                }

                @Test
                void y() {
                    memory.write(0, 0b10101001, 0xff, -16);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("-16,Y", reference.description());

                    assertEquals(1100 - 16, reference.value());
                    assertEquals(1100, Register.Y.value());
                    assertEquals(4, clock.cycles());
                }

                @Test
                void u() {
                    memory.write(0, 0b11001001, 0x01, 0x02);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("258,U", reference.description());

                    assertEquals(1200 + 258, reference.value());
                    assertEquals(1200, Register.U.value());
                    assertEquals(4, clock.cycles());
                }

                @Test
                void s() {
                    memory.write(0, 0b11101001, 0x02, 0x0f);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("527,S", reference.description());

                    assertEquals(1300 + 527, reference.value());
                    assertEquals(1300, Register.S.value());
                    assertEquals(4, clock.cycles());
                }
            }
        }

        @Nested
        class NoOffsetFromRegister {
            @Test
            void x() {
                memory.write(0, 0b10000100);
                var reference = IndexedAddress.next(memory, Register.PC, clock);
                assertEquals(",X", reference.description());

                assertEquals(1000, reference.value());
                assertEquals(1000, Register.X.value());
                assertEquals(0, clock.cycles());
            }

            @Test
            void y() {
                memory.write(0, 0b10100100);
                var reference = IndexedAddress.next(memory, Register.PC, clock);
                assertEquals(",Y", reference.description());

                assertEquals(1100, reference.value());
                assertEquals(1100, Register.Y.value());
                assertEquals(0, clock.cycles());
            }

            @Test
            void u() {
                memory.write(0, 0b11000100);
                var reference = IndexedAddress.next(memory, Register.PC, clock);
                assertEquals(",U", reference.description());

                assertEquals(1200, reference.value());
                assertEquals(1200, Register.U.value());
                assertEquals(0, clock.cycles());
            }

            @Test
            void s() {
                memory.write(0, 0b11100100);
                var reference = IndexedAddress.next(memory, Register.PC, clock);
                assertEquals(",S", reference.description());

                assertEquals(1300, reference.value());
                assertEquals(1300, Register.S.value());
                assertEquals(0, clock.cycles());
            }
        }

        @Nested
        class AutoIncrementDecrementFromRegister {
            @Nested
            class AutoIncrement {
                @Test
                void x() {
                    memory.write(0, 0b10000000);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals(",X+", reference.description());

                    assertEquals(1000, reference.value());
                    assertEquals(1001, Register.X.value());
                    assertEquals(2, clock.cycles());
                }

                @Test
                void y() {
                    memory.write(0, 0b10100000);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals(",Y+", reference.description());

                    assertEquals(1100, reference.value());
                    assertEquals(1101, Register.Y.value());
                    assertEquals(2, clock.cycles());
                }

                @Test
                void u() {
                    memory.write(0, 0b11000000);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals(",U+", reference.description());

                    assertEquals(1200, reference.value());
                    assertEquals(1201, Register.U.value());
                    assertEquals(2, clock.cycles());
                }

                @Test
                void s() {
                    memory.write(0, 0b11100000);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals(",S+", reference.description());

                    assertEquals(1300, reference.value());
                    assertEquals(1301, Register.S.value());
                    assertEquals(2, clock.cycles());
                }
            }

            @Nested
            class AutoIncrementBy2 {
                @Test
                void x() {
                    memory.write(0, 0b10000001);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals(",X++", reference.description());

                    assertEquals(1000, reference.value());
                    assertEquals(1002, Register.X.value());
                    assertEquals(3, clock.cycles());
                }

                @Test
                void y() {
                    memory.write(0, 0b10100001);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals(",Y++", reference.description());

                    assertEquals(1100, reference.value());
                    assertEquals(1102, Register.Y.value());
                    assertEquals(3, clock.cycles());
                }
            }

            @Nested
            class AutoDecrement {
                @Test
                void x() {
                    memory.write(0, 0b10000010);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals(",-X", reference.description());

                    assertEquals(999, reference.value());
                    assertEquals(999, Register.X.value());
                    assertEquals(2, clock.cycles());
                }

                @Test
                void y() {
                    memory.write(0, 0b10100010);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals(",-Y", reference.description());

                    assertEquals(1099, reference.value());
                    assertEquals(1099, Register.Y.value());
                    assertEquals(2, clock.cycles());
                }
            }

            @Nested
            class AutoDecrementBy2 {
                @Test
                void x() {
                    memory.write(0, 0b10000011);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals(",--X", reference.description());

                    assertEquals(998, reference.value());
                    assertEquals(998, Register.X.value());
                    assertEquals(3, clock.cycles());
                }

                @Test
                void y() {
                    memory.write(0, 0b10100011);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals(",--Y", reference.description());

                    assertEquals(1098, reference.value());
                    assertEquals(1098, Register.Y.value());
                    assertEquals(3, clock.cycles());
                }
            }
        }

        @Nested
        class AccumulatorOffsetFromRegister {

            @Nested
            class AccumulatorA {
                @BeforeEach
                void setup() {
                    Register.A.value(-16);
                }

                @Test
                void x() {
                    memory.write(0, 0b10000110);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("A,X", reference.description());

                    assertEquals(1000 - 16, reference.value());
                    assertEquals(1000, Register.X.value());
                    assertEquals(1, clock.cycles());
                }

                @Test
                void y() {
                    memory.write(0, 0b10100110);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("A,Y", reference.description());

                    assertEquals(1100 - 16, reference.value());
                    assertEquals(1100, Register.Y.value());
                    assertEquals(1, clock.cycles());
                }

                @Test
                void u() {
                    memory.write(0, 0b11000110);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("A,U", reference.description());

                    assertEquals(1200 - 16, reference.value());
                    assertEquals(1200, Register.U.value());
                    assertEquals(1, clock.cycles());
                }

                @Test
                void s() {
                    memory.write(0, 0b11100110);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("A,S", reference.description());

                    assertEquals(1300 - 16, reference.value());
                    assertEquals(1300, Register.S.value());
                    assertEquals(1, clock.cycles());
                }
            }

            @Nested
            class AccumulatorB {
                @BeforeEach
                void setup() {
                    Register.B.value(32);
                }

                @Test
                void x() {
                    memory.write(0, 0b10000101);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("B,X", reference.description());

                    assertEquals(1000 + 32, reference.value());
                    assertEquals(1000, Register.X.value());
                    assertEquals(1, clock.cycles());
                }

                @Test
                void y() {
                    memory.write(0, 0b10100101);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("B,Y", reference.description());

                    assertEquals(1100 + 32, reference.value());
                    assertEquals(1100, Register.Y.value());
                    assertEquals(1, clock.cycles());
                }

                @Test
                void u() {
                    memory.write(0, 0b11000101);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("B,U", reference.description());

                    assertEquals(1200 + 32, reference.value());
                    assertEquals(1200, Register.U.value());
                    assertEquals(1, clock.cycles());
                }

                @Test
                void s() {
                    memory.write(0, 0b11100101);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("B,S", reference.description());

                    assertEquals(1300 + 32, reference.value());
                    assertEquals(1300, Register.S.value());
                    assertEquals(1, clock.cycles());
                }
            }

            @Nested
            class AccumulatorD {
                @BeforeEach
                void setup() {
                    Register.D.value(-512);
                }

                @Test
                void x() {
                    memory.write(0, 0b10001011);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("D,X", reference.description());

                    assertEquals(1000 - 512, reference.value());
                    assertEquals(1000, Register.X.value());
                    assertEquals(4, clock.cycles());
                }

                @Test
                void y() {
                    memory.write(0, 0b10101011);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("D,Y", reference.description());

                    assertEquals(1100 - 512, reference.value());
                    assertEquals(1100, Register.Y.value());
                    assertEquals(4, clock.cycles());
                }

                @Test
                void u() {
                    memory.write(0, 0b11001011);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("D,U", reference.description());

                    assertEquals(1200 - 512, reference.value());
                    assertEquals(1200, Register.U.value());
                    assertEquals(4, clock.cycles());
                }

                @Test
                void s() {
                    memory.write(0, 0b11101011);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("D,S", reference.description());

                    assertEquals(1300 - 512, reference.value());
                    assertEquals(1300, Register.S.value());
                    assertEquals(4, clock.cycles());
                }
            }
        }

        @Nested
        class ConstantOffsetFromProgramCounter {
            @Test
            void signed8bitOffset() {
                Register.PC.value(0x1032);
                memory.write(0x1032, 0b10001100, -15);
                var reference = IndexedAddress.next(memory, Register.PC, clock);
                assertEquals("-15,PC", reference.description());

                assertEquals(0x1032 + 2 - 15, reference.value());
                assertEquals(1, clock.cycles());
            }

            @Test
            void signed16bitOffset() {
                Register.PC.value(0x1032);
                memory.write(0x1032, 0b10001101, 0x01, 0xff);
                var reference = IndexedAddress.next(memory, Register.PC, clock);
                assertEquals("511,PC", reference.description());

                assertEquals(0x1032 + 3 + 511, reference.value());
                assertEquals(5, clock.cycles());
            }
        }
    }

    @Nested
    class Indirect {
        @Test
        void extended16bit() {
            memory.write(0, 0b10011111, 0x01, 0xff);
            memory.write(0x01ff, 0xab, 0xcd);
            var reference = IndexedAddress.next(memory, Register.PC, clock);
            assertEquals("[$01ff]", reference.description());

            assertEquals(0xabcd, reference.value());
            assertEquals(5, clock.cycles());
        }

        @Nested
        class NoOffsetFromRegister {
            @Test
            void x() {
                memory.write(0, 0b10010100);
                memory.write(1000, 0x12, 0x34);
                var reference = IndexedAddress.next(memory, Register.PC, clock);
                assertEquals("[,X]", reference.description());

                assertEquals(0x1234, reference.value());
                assertEquals(1000, Register.X.value());
                assertEquals(3, clock.cycles());
            }
        }

        @Nested
        class AutoIncrementDecrementFromRegister {
            @Nested
            class AutoIncrementBy2 {
                @Test
                void x() {
                    memory.write(0, 0b10010001);
                    memory.write(1000, 0xab, 0xcd);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("[,X++]", reference.description());

                    assertEquals(0xabcd, reference.value());
                    assertEquals(1002, Register.X.value());
                    assertEquals(6, clock.cycles());
                }

                @Test
                void y() {
                    memory.write(0, 0b10110001);
                    memory.write(1100, 0xab, 0xcd);

                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("[,Y++]", reference.description());

                    assertEquals(0xabcd, reference.value());
                    assertEquals(1102, Register.Y.value());
                    assertEquals(6, clock.cycles());
                }
            }

            @Nested
            class AutoDecrementBy2 {
                @Test
                void x() {
                    memory.write(0, 0b10010011);
                    memory.write(998, 0xab, 0xcd);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("[,--X]", reference.description());

                    assertEquals(0xabcd, reference.value());
                    assertEquals(998, Register.X.value());
                    assertEquals(6, clock.cycles());
                }

                @Test
                void y() {
                    memory.write(0, 0b10110011);
                    memory.write(1098, 0xab, 0xcd);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("[,--Y]", reference.description());

                    assertEquals(0xabcd, reference.value());
                    assertEquals(1098, Register.Y.value());
                    assertEquals(6, clock.cycles());
                }
            }
        }

        @Nested
        class AccumulatorOffsetFromRegister {
            @Nested
            class AccumulatorA {
                @BeforeEach
                void setup() {
                    Register.A.value(-16);
                }

                @Test
                void x() {
                    memory.write(0, 0b10010110);
                    memory.write(1000 - 16, 0x12, 0x34);
                    var reference = IndexedAddress.next(memory, Register.PC, clock);
                    assertEquals("[A,X]", reference.description());

                    assertEquals(0x1234, reference.value());
                    assertEquals(1000, Register.X.value());
                    assertEquals(4, clock.cycles());
                }
            }
        }

        @Nested
        class ConstantOffsetFromProgramCounter {
            @Test
            void signed8bitOffset() {
                Register.PC.value(0x1032);
                memory.write(0x1032, 0b10011100, -15);
                memory.write(0x1032 + 2 - 15, 0xcd, 0xef);
                var reference = IndexedAddress.next(memory, Register.PC, clock);
                assertEquals("[-15,PC]", reference.description());

                assertEquals(0xcdef, reference.value());
                assertEquals(4, clock.cycles());
            }

            @Test
            void signed16bitOffset() {
                Register.PC.value(0x1032);
                memory.write(0x1032, 0b10011101, 0x01, 0xff);
                memory.write(0x1032 + 3 + 511, 0xcd, 0xef);
                var reference = IndexedAddress.next(memory, Register.PC, clock);
                assertEquals("[511,PC]", reference.description());

                assertEquals(0xcdef, reference.value());
                assertEquals(8, clock.cycles());
            }
        }
    }
}
