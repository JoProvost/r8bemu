package com.joprovost.r8bemu.devices.memory;

// The addressing modes available on the MC6809 and MC6809E are: Inherent, Immediate, Extended, Direct, Indexed (with
// various offsets and autoincrementing/decrementing), and Branch Relative. Some of these addressing modes require an
// additional byte after the opcode to provide additional addressing interpretation. This byte is called a postbyte.
//
// The following paragraphs provide a description of each addressing mode. In these descriptions the term effective¸
// address is used to indicate the address in memory from which the argument for an instruction is fetched or stored,
// or from which instruction processing is to proceed.
//
// Source: https://www.maddes.net/m6809pm/sections.htm
public enum Addressing {
    // The information necessary to execute the instruction is contained in the opcode. Some operations specifying only
    // the index registers or the accumulators, and no other arguments, are also included in this addressing mode.
    // Example: MUL
    INHERENT,

    // The operand is contained in one or two bytes immediately following the opcode. This addressing mode is used to
    // provide constant data values that do not change during program execution. Both 8-bit and 16-bit operands are
    // used depending on the size of the argument specified in the opcode.
    // Example: LDA    #CR
    //          LDB    #7
    //          LDA    #$F0
    //          LDB    #%1110000
    //          LDX    #$8004
    IMMEDIATE_VALUE_8(Size.WORD_8),
    IMMEDIATE_VALUE_16(Size.WORD_16),

    // The effective address of the argument is contained in the two bytes following the opcode. Instructions using the
    // extended addressing mode can reference arguments anywhere in the 64K addressing space. Extended addressing is
    // generally not used in position independent programs because it supplies an absolute address.
    // Example: LDA > CAT
    EXTENDED_ADDRESS, // 16 bits
    EXTENDED_DATA_8(Size.WORD_8),
    EXTENDED_DATA_16(Size.WORD_16),

    // The effective address is developed by concatenation of the contents of the direct page register with the byte
    // immediately following the opcode. The direct page register contents are the most-significant byte of the address.
    // This allows accessing 256 locations within any one of 256 pages. Therefore, the entire addressing range is
    // available for access using a single two-byte instruction.
    // Example: LDA > CAT
    DIRECT_ADDRESS,
    DIRECT_DATA_8(Size.WORD_8),
    DIRECT_DATA_16(Size.WORD_16),

    // In these addressing modes, one of the pointer registers (X, Y, U, or S), and sometimes the program counter (PC)
    // is used in the calculation of the effective address of the instruction operand. The basic types (and their
    // variations) of indexed addressing available are shown in Table 2-1 along with the postbyte configuration used.

    INDEXED_ADDRESS,
    INDEXED_DATA_8(Size.WORD_8),
    INDEXED_DATA_16(Size.WORD_16),

    // This addressing mode is used when branches from the current instruction location to some other location relative
    // to the current program counter are desired. If the test condition of the branch instruction is true, then the
    // effective address is calculated (program counter plus twos complement offset) and the branch is taken. If the
    // test condition is false, the processor proceeds to the next in-line instruction. Note that the program counter
    // is always pointing to the next instruction when the offset is added. Branch relative addressing is always used
    // in position independent programs for all control transfers.
    //
    // For short branches, the byte following the branch instruction opcode is treated as an 8-bit signed offset to be
    // used to calculate the effective address of the next instruction if the branch is taken. This is called a short
    // relative branch and the range is limited to plus 127 or minus 128 bytes from the following opcode.
    //
    // For long branches, the two bytes after the opcode are used to calculate the effective address. This is called a
    // long relative branch and the range is plus 32,767 or minus 32,768 bytes from the following opcode or the full
    // 64K address space of memory that the processor can address at one time.
    // Examples: Short Branch   Long Branch
    //           BRA POLE       LBRA CAT
    RELATIVE_ADDRESS_8,
    RELATIVE_ADDRESS_16;

    public final Size size;

    Addressing(Size size) {
        this.size = size;
    }

    Addressing() {
        this(null);
    }
}
