/**
 * @file  Main.cpp
 *
 * @brief  Draws some test map data.
 */

#include "Debug.h"
#include "MapCollector.h"
#include "MCAFile.h"
#include <filesystem>
#include <string>
#include <iostream>
#include <cstddef>
#include <thread>
#include <vector>
#include <mutex>

const constexpr int worldBorder = 1600;
const constexpr int defaultMapEdge = worldBorder * 2;
const constexpr int defaultChunkPx = 2;
const constexpr int minSize = 256;
const constexpr int maxSize = 10000;


int main(int argc, char** argv)
{
    if (argc < 3)
    {
        std::cout << "Missing required arguments.\nValid usage: MCMap "
                << "regionFolder imagePath.png [mapEdge][chunkPx]\n";
        return 0;
    }
    int mapEdge = defaultMapEdge;
    int chunkPx = defaultChunkPx;
    if (argc >= 4)
    {
        std::cout << "Using a world size of " << argv[3] << " chunks.\n";
        mapEdge = atoi(argv[3]);
    }
    if (argc >= 5)
    {
        std::cout << "Using " << argv[4] << " pixels per chunk.\n";
        chunkPx = atoi(argv[4]);
    }

    using namespace std::filesystem;
    // Path to region files:
    path dataPath(argv[1]);
    // save region file paths and count:
    std::vector<path> regionFiles;
    int maxDistanceFromOrigin = 0;
    const int maxAllowed = worldBorder;
    for (const auto& dirEntry : directory_iterator(dataPath))
    {
        // find maximum distance from origin:
        int fileMax = 0;
        Point chunkCoords = MCAFile::getChunkCoords(dirEntry.path());
        if (chunkCoords.x == -1 && chunkCoords.z == -1)
        {
            std::cerr << dirEntry.path().filename() 
                    << " does not have a legal region file name format.\n";
            continue;
        }
        fileMax = std::max(fileMax, (chunkCoords.x >= 0 ?
                    (chunkCoords.x + 32) : (chunkCoords.x * -1)));
        fileMax = std::max(fileMax, (chunkCoords.z >= 0 ?
                    (chunkCoords.z + 32) : (chunkCoords.z * -1)));
        if (fileMax <= maxAllowed)
        {
            maxDistanceFromOrigin = std::max(fileMax, maxDistanceFromOrigin);
            regionFiles.push_back(dirEntry.path());
        }
        else
        {
            std::cerr << "Warning: Map won't go past the world border at "
                << (worldBorder * 16) << ", so map file "
                << dirEntry.path().filename() << " at ("
                << (chunkCoords.x * 16) << "," << (chunkCoords.z * 16)
                << ") will be ignored.\n";
        }
    }
    mapEdge = maxDistanceFromOrigin * 2;
    while((mapEdge * chunkPx) < minSize)
    {
        chunkPx++;
    }
    while((mapEdge * chunkPx) > maxSize && chunkPx > 1)
    {
        chunkPx--;
    }
    if((mapEdge * chunkPx) > maxSize)
    {
        mapEdge = maxSize;
        size_t maxBlock = (mapEdge / 2) * 32 * 16;
        std::cout << "Warning: Map would exceed the maximum image size of "
            << maxSize << " x " << maxSize << ", chunks further than "
            << maxBlock << " blocks from (0,0) will be cropped.\n";
    }
    const size_t numRegionFiles = regionFiles.size();

    // Initialize Mappers with the provided path:
    std::string imagePath(argv[2]);
    if (imagePath.substr(imagePath.length() - 4) == ".png")
    {
        imagePath.erase(imagePath.length() - 4);
    }
    MapCollector mappers(imagePath, mapEdge, mapEdge, chunkPx);

    // Provide updateCounts so threads can safely change the processed
    // file/chunk counts and print progress.
    int regionCount = 0;
    int chunkCount = 0;
    std::mutex countLock;
    const auto updateCounts =
    [&regionCount, &chunkCount, &countLock, numRegionFiles]
    (size_t regionsAdded, size_t chunksAdded)
    {
        std::lock_guard<std::mutex> lock(countLock);
        regionCount += regionsAdded;
        chunkCount += chunksAdded;
        if (regionsAdded > 0)
        {
            std::cout << "Finished file " << regionCount << "/"
                    << numRegionFiles << " \n";
        }
    };

    std::mutex imageLock;
    // Thread function to process a portion of all region files.
    const auto readRegions =
    [&updateCounts, &regionFiles, &imageLock, &mappers]
    (const size_t startIndex, const size_t numToRead)
    {
        for (int i = startIndex; i < startIndex + numToRead; i++)
        {
            path entryPath = regionFiles[i];
            MCAFile entryFile(entryPath);
            std::vector<ChunkData> entryChunks = entryFile.getLoadedChunks();
            updateCounts(1, entryChunks.size());
            for (const ChunkData& chunk : entryChunks)
            {
                std::lock_guard<std::mutex> lock(imageLock);
                mappers.drawChunk(chunk);
            }
        }
    };

    int numThreads = std::thread::hardware_concurrency();
    if (numThreads == 0)
    {
        numThreads = 1;
    }
    std::cout << "Processing " << numRegionFiles << " region files with "
            << numThreads << " threads.\n";
    const size_t filesPerThread = numRegionFiles / numThreads;
    std::vector<std::thread> threadList;
    threadList.resize(numThreads);
    for (int i = 0; i < numThreads; i++)
    {
        threadList[i] = std::thread(readRegions, i * filesPerThread,
                filesPerThread);
    }

    for (int i = 0; i < numThreads; i++)
    {
        threadList[i].join();
    }

    int numChunks = mapEdge * mapEdge;
    double explorePercent = (double) chunkCount * 100 / numChunks;
    std::cout << "Mapped " << chunkCount << " chunks out of " << numChunks
        << ", map is " << explorePercent << "\% explored.\n";
    
    mappers.saveMapFile();
    return 0;
}
