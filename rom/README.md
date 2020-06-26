### Boot ROM

In order to use this emulator, you must provide the ROM to boot on.  You can download it from an actual CoCo 2 you
own and put the content of `Color Basic` in `./rom/bas.rom` and the `Extended Color Basic` in `./rom/extbas.rom` or
craft your own :

 * __`bas.rom`__ starts at __0xA000__ ending at __0xBFFF__, boot vector must be at __0xBFFE__ ;
 * __`extbas.rom`__ starts at __0x8000__ ending at __0x9FFF__ .
