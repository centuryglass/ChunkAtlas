#include "MCAFile.h"
#include <fstream>
#include <iostream>
#include <string>
#include <cstdint>

// width/height in chunks of a region file:
static const constexpr int dimInChunks = 32;

// Loads data from a .mca file on construction.
MCAFile::MCAFile(std::filesystem::path filePath) : mcaPath(filePath)
{
    // read the region file's base coordinates from the file name:
    const std::string name(mcaPath.filename());
    const std::string numChars("-0123456789");
    const size_t xStart = name.find_first_of(numChars);
    const size_t xEnd = name.find('.', xStart);
    const size_t yStart = xEnd + 1;
    const size_t yEnd = name.find('.', yStart);
    if (xStart == std::string::npos || xEnd == std::string::npos
            || yEnd == std::string::npos)
    {
        std::cerr << "Can't parse coordinates from file " << mcaPath << ".\n";
        return;
    }
    int regionX = dimInChunks * std::stoi(name.substr(xStart, xEnd - xStart));
    int regionY = dimInChunks * std::stoi(name.substr(yStart, yEnd - yStart));

    std::ifstream regionFile(mcaPath, std::ios::binary);
    if (! regionFile.is_open())
    {
        std::cerr << "Failed to open " << mcaPath << "\n";
        return;
    }
    const constexpr int numChunks = dimInChunks * dimInChunks;
    const constexpr int bufferSize = numChunks * 4;
    char buffer [bufferSize];
    regionFile.read(buffer, bufferSize);
    if (! regionFile)
    {
        std::cerr << mcaPath << ": only read " << regionFile.gcount()
            << " bytes, expected " << bufferSize << "\n";
    }

    for (int i = 0; i < numChunks; i++)
    {
        const int chunkIndex = i * 4;
        bool chunkLoaded = false;
        for (int cI = chunkIndex; cI < chunkIndex + 4; cI++)
        {
            if (buffer[cI] != 0)
            {
                chunkLoaded = true;
                break;
            }
        }
        if (chunkLoaded)
        {
            int chunkX = regionX + (i % 32);
            int chunkY = regionY + (i / 32);
            loadedChunks.push_back({chunkX, chunkY});
        }
    }
}


// Gets the coordinates of all loaded chunks stored in the file.
const std::vector<Point>& MCAFile::getLoadedChunks()
{
    return loadedChunks;
}
