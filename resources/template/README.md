# Configuration folder

## Boot ROM

In order to use this emulator, you must provide the ROM to boot on.  You can download it from an actual CoCo 2 you
own and put the content of `Color Basic` in `bas13.rom`, `Extended Color Basic` in `extbas11.rom` and
`Disk Extended Color Basic` in `disk11.rom` or craft your own :

 * __`bas13.rom`__ starts at __0xA000__ ending at __0xBFFF__, boot vector must be at __0xBFFE__ ;
 * __`extbas11.rom`__ starts at __0x8000__ ending at __0x9FFF__ .
 * __`disk11.rom`__ starts at __0xC000__ ending at __0xFF00__ .

## Tape cassette

To load a program from a `.wav` file, start the application with `--playback <path-to-file>` and type `CLOAD`
 or `CLOADM` at the prompt. To save on tape, start the application with `--recording <path-to-file>` and type
`CSAVE "<file-name>"`.

By default, the files `playback.wav` and `recording.wav` will be used if available.

## Scripts

In order to perform integration tests the content of a script may be is typed on the emulator keyboard. To do so,
start the application with `--script <path-to-file>`.

By default, the file `autorun.bas` will be used if available.

## Disk

To load a program from a `.dsk` file, choose a disk by clicking on the disk icon and then :

 - type `DIR` to list the files,
 - type `LOAD` "<basic-file> to load a basic file,
 - type `RUN` "<basic-file> to load and run a basic file,
 - type `LOADM` "<binary-file> to load a binary file.
