/**
 * @file  Main.cpp
 *
 * @brief  Draws some test map data.
 */

#include "Debug.h"
#include "MapImage.h"
#include "MCAFile.h"
#include <filesystem>
#include <string>
#include <iostream>
#include <cstddef>
#include <thread>
#include <vector>
#include <mutex>

const constexpr int defaultMapEdge = 3200;
const constexpr int defaultChunkPx = 2;

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
    path dataPath(argv[1]);
    MapImage m(argv[2], mapEdge, mapEdge, chunkPx);

    // save region file paths and count:
    std::vector<path> regionFiles;
    for (const auto& dirEntry : directory_iterator(dataPath))
    {
        regionFiles.push_back(dirEntry.path());
    }
    const size_t numRegionFiles = regionFiles.size();

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
    [&updateCounts, &regionFiles, &imageLock, &m]
    (const size_t startIndex, const size_t numToRead)
    {
        static const MapImage::Pixel white(255, 255, 255);
        static const MapImage::Pixel green(0, 255, 0);
        for (int i = startIndex; i < startIndex + numToRead; i++)
        {
            path entryPath = regionFiles[i];
            MCAFile entryFile(entryPath);
            std::vector<ChunkData> entryChunks = entryFile.getLoadedChunks();
            updateCounts(1, entryChunks.size());
            for (const ChunkData& chunk : entryChunks)
            {
                Point chunkPoint = chunk.getPos();
                bool greenTile = ((chunkPoint.y % 2) == 0);
                if ((chunkPoint.x % 2) == 0)
                {
                    greenTile = ! greenTile;
                }
                std::lock_guard<std::mutex> lock(imageLock);
                m.setChunkColor(chunkPoint.x, chunkPoint.y,
                        greenTile ? green : white);
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
    
    m.saveImage();
    return 0;
}
