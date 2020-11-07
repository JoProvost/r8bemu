package com.joprovost.r8bemu.storage;

import java.util.List;

public interface DiskTracks {
    List<Sector> sectors(int track);

    Sector sector(int track, int sector);
}
