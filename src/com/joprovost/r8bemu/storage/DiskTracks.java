package com.joprovost.r8bemu.storage;


public interface DiskTracks {
    Sector sector(int track, int sector);
}
