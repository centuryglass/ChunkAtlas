#include "MCAFile.h"
#include "ChunkNBT.h"
#include "Debug.h"
#include <fstream>
#include <iostream>
#include <string>
#include <cstdint>
#include <bitset>
#include <arpa/inet.h>

#include <cstdio>

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

    // Read a byte sequence:
    const auto readBytes = [this, &regionFile](char* buffer, size_t size)
    {
        if (regionFile.bad() || regionFile.eof())
        {
            std::cerr << mcaPath << ": file is in an invalid state.\n";
            return (size_t) 0;
            //exit(1);
        }
        regionFile.read(buffer, size);
        if (! regionFile || regionFile.gcount() != size)
        {
            std::cerr << mcaPath << ": only read " << regionFile.gcount()
                    << " bytes at " << regionFile.tellg() << ", expected "
                    << size << "\n";
            //exit(1);
        }
        return (size_t) regionFile.gcount();
    };

    // Read numBytes bytes of big-endian data into an unsigned integer:
    const auto readInt = [&regionFile, &readBytes](size_t numBytes)
    {
        uint64_t readValue = 0;
        if (numBytes > sizeof(readValue))
        {
            std::cerr << "Tried to read " << numBytes << " into a "
                    << sizeof(readValue) << " byte value!\n";
            return static_cast<uint64_t>(0);
            //exit(1);
        }
        std::vector<unsigned char> buffer;
        buffer.resize(numBytes);
        if(readBytes(reinterpret_cast<char*>(buffer.data()), numBytes)
                != numBytes)
        {
            return static_cast<uint64_t>(0);
        }
        size_t bitOffset = (numBytes - 1) * 8;
        for(const unsigned char byte : buffer)
        {
            readValue = readValue | (byte << bitOffset);
            bitOffset -= 8;
        }
        return readValue;
    };

    const constexpr int numChunks = dimInChunks * dimInChunks;
    const constexpr int bufferSize = numChunks * 4;
    unsigned char buffer [bufferSize];
    DBG("\nReading " << bufferSize << " bytes of index data from "
            << mcaPath << ":");
    static const constexpr int sectorSize = 4096; 
    const size_t fileSize = std::filesystem::file_size(mcaPath);
    DBG("File size: " << (fileSize / sectorSize) << " sectors, "
            << fileSize << " bytes.");
    size_t bytesRead = readBytes(reinterpret_cast<char*>(buffer), bufferSize);
    for (int i = 0; i < numChunks; i++)
    {
        const int chunkIndex = i * 4;
        if (chunkIndex >= bytesRead)
        {
            continue;
        }
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
            const int chunkX = regionX + (i % 32);
            const int chunkY = regionY + (i / 32);
            Point chunkPos = { chunkX, chunkY };

            // Find chunk data:
            DBG_V("Chunk " << (i + 1) << "/" << numChunks
                    << ", byte index " << chunkIndex << "/"
                    << ((numChunks - 1) * 4) << "\n");
            const int sectorOffset = (buffer[chunkIndex] << 16)
                    | (buffer[chunkIndex + 1] << 8)
                    | buffer[chunkIndex + 2];

            unsigned int sectorCount = buffer[chunkIndex + 3];
            unsigned int byteOffset = sectorOffset * sectorSize;
            if (byteOffset > fileSize)
            {
                std::cerr << "Chunk " << i << "/" << numChunks
                        << ", byte index " << chunkIndex
                        << ": Illegal offset past end of file: "
                        << byteOffset << " ("
                        << std::bitset<sizeof(byteOffset) * 8>(byteOffset)
                        << ", sector = " << sectorOffset << "/"
                        << (fileSize / sectorSize) << ")\n";

                continue;
            }
            DBG_V(i << ": Chunk " << chunkX << ", " << chunkY
                    << " data is " << sectorCount 
                    << " sector(s) at byte offset "
                    << (sectorOffset * sectorSize));
            regionFile.seekg(sectorOffset * sectorSize);
            if (! regionFile || regionFile.eof())
            {
                std::cerr << "Chunk " << (i + 1) << "/" << numChunks
                    << ", byte index " << chunkIndex << "/"
                    << ((numChunks - 1) * 4) << ": "
                    << "Failed to seek to offset "
                    << (sectorOffset * sectorSize) << " in file of size "
                    << fileSize << "\n";
                continue;
            }
            unsigned int chunkByteSize = readInt(4);
            unsigned char compressionType = readInt(1);
            size_t byteSectorCount = chunkByteSize / sectorSize;
            if ((chunkByteSize % sectorSize) > 0)
            {
                byteSectorCount++;
            }
            if (byteSectorCount > sectorCount)
            {
                std::cerr << i << ": Chunk " << chunkX << ", " << chunkY
                        << " at offset " << sectorOffset
                        << "/" << (fileSize / sectorSize) << ":\n"
                        << "Expected " << sectorCount << " sectors but found "
                        << byteSectorCount << "("
                        << std::bitset<32>(chunkByteSize) << ")\n";
                continue;
            }

            DBG_V(i << ": Chunk " << chunkX << ", " << chunkY
                    << " data is " << chunkByteSize << " bytes (" 
                    << (chunkByteSize / sectorSize) << ") sectors\n");

            std::vector<unsigned char> chunkData;
            chunkData.resize(chunkByteSize);
            size_t bytesRead = readBytes(
                    reinterpret_cast<char *>(chunkData.data()), chunkByteSize);
            if (bytesRead == chunkByteSize)
            {
                DBG_V(i << ": Chunk " << chunkX << ", " << chunkY
                        << ", " << bytesRead << "/" << chunkByteSize
                        << " bytes read.");
            }
            ChunkNBT nbtData(chunkData);
            loadedChunks.push_back(nbtData.getChunkData(chunkPos));
        }
    }
}


// Gets the coordinates of all loaded chunks stored in the file.
const std::vector<ChunkData>& MCAFile::getLoadedChunks()
{
    return loadedChunks;
}
