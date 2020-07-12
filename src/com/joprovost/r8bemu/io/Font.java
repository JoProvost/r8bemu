package com.joprovost.r8bemu.io;

import java.util.List;
import java.util.Map;

public interface Font {
    Map<Character, List<Integer>> standard = Map.ofEntries(
            Map.entry(' ', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('!', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00000000,
                    0b00001000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('"', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00010100,
                    0b00010100,
                    0b00010100,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('#', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00010100,
                    0b00010100,
                    0b00110110,
                    0b00000000,
                    0b00110110,
                    0b00010100,
                    0b00010100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('$', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00001000,
                    0b00011110,
                    0b00100000,
                    0b00011100,
                    0b00000010,
                    0b00111100,
                    0b00001000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('%', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00110010,
                    0b00110010,
                    0b00000100,
                    0b00001000,
                    0b00010010,
                    0b00100110,
                    0b00100110,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('&', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00010000,
                    0b00101000,
                    0b00101000,
                    0b00010000,
                    0b00101010,
                    0b00100100,
                    0b00011010,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('\'', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011000,
                    0b00011000,
                    0b00011000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('(', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00001000,
                    0b00010000,
                    0b00100000,
                    0b00100000,
                    0b00100000,
                    0b00010000,
                    0b00001000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry(')', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00001000,
                    0b00000100,
                    0b00000010,
                    0b00000010,
                    0b00000010,
                    0b00000100,
                    0b00001000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('*', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00001000,
                    0b00011100,
                    0b00111110,
                    0b00011100,
                    0b00001000,
                    0b00000000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('+', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00001000,
                    0b00001000,
                    0b00111110,
                    0b00001000,
                    0b00001000,
                    0b00000000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry(',', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00110000,
                    0b00110000,
                    0b00010000,
                    0b00100000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('-', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00111110,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('.', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00110000,
                    0b00110000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('/', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000010,
                    0b00000010,
                    0b00000100,
                    0b00001000,
                    0b00010000,
                    0b00100000,
                    0b00100000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('0', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011000,
                    0b00100100,
                    0b00100100,
                    0b00100100,
                    0b00100100,
                    0b00100100,
                    0b00011000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('1', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00001000,
                    0b00011000,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00011100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('2', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011100,
                    0b00100010,
                    0b00000010,
                    0b00011100,
                    0b00100000,
                    0b00100000,
                    0b00111110,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('3', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011100,
                    0b00100010,
                    0b00000010,
                    0b00011100,
                    0b00000010,
                    0b00100010,
                    0b00011100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('4', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000100,
                    0b00001100,
                    0b00010100,
                    0b00111110,
                    0b00000100,
                    0b00000100,
                    0b00000100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('5', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00111110,
                    0b00100000,
                    0b00111100,
                    0b00000010,
                    0b00000010,
                    0b00100010,
                    0b00011100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('6', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011100,
                    0b00100000,
                    0b00100000,
                    0b00111100,
                    0b00100010,
                    0b00100010,
                    0b00011100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('7', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00111110,
                    0b00000010,
                    0b00000100,
                    0b00001000,
                    0b00010000,
                    0b00100000,
                    0b00100000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('8', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011100,
                    0b00100010,
                    0b00100010,
                    0b00011100,
                    0b00100010,
                    0b00100010,
                    0b00011100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('9', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011100,
                    0b00100010,
                    0b00100010,
                    0b00011110,
                    0b00000010,
                    0b00000010,
                    0b00011100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry(':', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011000,
                    0b00011000,
                    0b00000000,
                    0b00011000,
                    0b00011000,
                    0b00000000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry(';', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011000,
                    0b00011000,
                    0b00000000,
                    0b00011000,
                    0b00011000,
                    0b00001000,
                    0b00010000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('<', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000010,
                    0b00000100,
                    0b00001000,
                    0b00010000,
                    0b00001000,
                    0b00000100,
                    0b00000010,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('=', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00111110,
                    0b00000000,
                    0b00111110,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('>', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00010000,
                    0b00001000,
                    0b00000100,
                    0b00000010,
                    0b00000100,
                    0b00001000,
                    0b00010000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('?', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011000,
                    0b00100100,
                    0b00000100,
                    0b00001000,
                    0b00001000,
                    0b00000000,
                    0b00001000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('@', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011100,
                    0b00100010,
                    0b00000010,
                    0b00011010,
                    0b00101010,
                    0b00101010,
                    0b00011100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('A', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00001000,
                    0b00010100,
                    0b00100010,
                    0b00111110,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('B', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00111100,
                    0b00010010,
                    0b00010010,
                    0b00011100,
                    0b00010010,
                    0b00010010,
                    0b00111100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('C', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011100,
                    0b00100010,
                    0b00100000,
                    0b00100000,
                    0b00100000,
                    0b00100010,
                    0b00011100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('D', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00111100,
                    0b00010010,
                    0b00010010,
                    0b00010010,
                    0b00010010,
                    0b00010010,
                    0b00111100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('E', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00111110,
                    0b00100000,
                    0b00100000,
                    0b00111000,
                    0b00100000,
                    0b00100000,
                    0b00111110,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('F', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00111110,
                    0b00100000,
                    0b00100000,
                    0b00111000,
                    0b00100000,
                    0b00100000,
                    0b00100000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('G', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011100,
                    0b00100010,
                    0b00100000,
                    0b00100000,
                    0b00100110,
                    0b00100010,
                    0b00011110,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('H', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00111110,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('I', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011100,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00011100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('J', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000010,
                    0b00000010,
                    0b00000010,
                    0b00000010,
                    0b00000010,
                    0b00100010,
                    0b00011100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('K', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00100010,
                    0b00100100,
                    0b00101000,
                    0b00110000,
                    0b00101000,
                    0b00100100,
                    0b00100010,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('L', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00100000,
                    0b00100000,
                    0b00100000,
                    0b00100000,
                    0b00100000,
                    0b00100000,
                    0b00111110,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('M', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00100010,
                    0b00110110,
                    0b00101010,
                    0b00101010,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('N', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00100010,
                    0b00110010,
                    0b00101010,
                    0b00100110,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('O', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00111110,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00111110,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('P', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00111100,
                    0b00100010,
                    0b00100010,
                    0b00111100,
                    0b00100000,
                    0b00100000,
                    0b00100000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('Q', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011100,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00101010,
                    0b00100100,
                    0b00011010,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('R', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00111100,
                    0b00100010,
                    0b00100010,
                    0b00111100,
                    0b00101000,
                    0b00100100,
                    0b00100010,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('S', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011100,
                    0b00100010,
                    0b00010000,
                    0b00001000,
                    0b00000100,
                    0b00100010,
                    0b00011100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('T', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00111110,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('U', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00011100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('V', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00010100,
                    0b00010100,
                    0b00001000,
                    0b00001000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('W', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00100010,
                    0b00101010,
                    0b00110110,
                    0b00100010,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('X', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00100010,
                    0b00100010,
                    0b00010100,
                    0b00001000,
                    0b00010100,
                    0b00100010,
                    0b00100010,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('Y', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00100010,
                    0b00100010,
                    0b00010100,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('Z', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00111110,
                    0b00000010,
                    0b00000100,
                    0b00001000,
                    0b00010000,
                    0b00100000,
                    0b00111110,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('[', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011100,
                    0b00010000,
                    0b00010000,
                    0b00010000,
                    0b00010000,
                    0b00010000,
                    0b00011100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('\\', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00100000,
                    0b00100000,
                    0b00010000,
                    0b00001000,
                    0b00000100,
                    0b00000010,
                    0b00000010,
                    0b00000000,
                    0b00000000
            )),
            Map.entry(']', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00011100,
                    0b00000100,
                    0b00000100,
                    0b00000100,
                    0b00000100,
                    0b00000100,
                    0b00011100,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('↑', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00001000,
                    0b00011100,
                    0b00101010,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00001000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('←', List.of(
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00000000,
                    0b00001000,
                    0b00010000,
                    0b00111110,
                    0b00010000,
                    0b00001000,
                    0b00000000,
                    0b00000000,
                    0b00000000
            )),
            Map.entry('▗', List.of(
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111
            )),
            Map.entry('▖', List.of(
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000
            )),
            Map.entry('▄', List.of(
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111
            )),
            Map.entry('▝', List.of(
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000
            )),
            Map.entry('▐', List.of(
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111
            )),
            Map.entry('▞', List.of(
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000
            )),
            Map.entry('▟', List.of(
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111
            )),
            Map.entry('▘', List.of(
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000
            )),
            Map.entry('▚', List.of(
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111
            )),
            Map.entry('▌', List.of(
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000
            )),
            Map.entry('▙', List.of(
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111
            )),
            Map.entry('▀', List.of(
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000,
                    0b0000_0000
            )),
            Map.entry('▜', List.of(
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111,
                    0b0000_1111
            )),
            Map.entry('▛', List.of(
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000,
                    0b1111_0000
            )),
            Map.entry('█', List.of(
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111,
                    0b1111_1111
            ))
        );

    static Font coco8x12() {
        return standard::get;
    }

    List<Integer> character(char c);
}