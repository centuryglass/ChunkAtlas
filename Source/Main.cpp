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
#include <map>

// Default values:
// TODO: load these from a config file
const std::string defaultMapDir(std::string("/home/") + getenv("USER")
        + "/MCregion");
const constexpr char* defaultImageName = "server";
const constexpr char* defaultDirInfo = "directory.txt";
const constexpr int worldBorder = 1600;
const constexpr int defaultMapEdge = worldBorder * 2;
const constexpr int defaultChunkPx = 2;
const constexpr int minSize = 256;
const constexpr int maxSize = 10000;

// Command line argument option types:
enum class ArgOption
{
    help,
    regionDir,
    output,
    worldBorder,
    chunkPixels,
    directoryFile
};

static void printHelp()
{
    std::cout << "Usage: ./MCMap [options]\n"
        << "Options:\n"
        << "  -h, --help:               Print this help text.\n"
        << "  -r, --regionDir [path]:   Set region data directory path.\n"
        << "  -o, --out [path]:         Set map image output path.\n"
        << "  -b, --border [chunkSize]: Set map width/height in chunks.\n"
        << "  -p, --pixels [number]:    Set chunk width/height in pixels.\n"
        << "  -d, --directory [path]:   Set coordinate directory file path.\n";
}


int main(int argc, char** argv)
{
    using namespace std::filesystem;

    // Initialize command line options:
    // Map options to short flag values:
    std::map<ArgOption, std::string> shortOptionFlags;
    // Map options to long flag values:
    std::map<ArgOption, std::string> longOptionFlags;
    // Map option flag strings to option types:
    std::map<std::string, ArgOption> flagOptions;
    // Initialize an option in all option maps:
    const auto initOption = [&]
    (const ArgOption option, const std::string shortFlag,
            const std::string longFlag)
    {
        shortOptionFlags[option] = shortFlag;
        longOptionFlags[option] = longFlag;
        flagOptions[shortFlag] = option;
        flagOptions[longFlag] = option;
    };
    initOption(ArgOption::help, "-h", "--help");
    initOption(ArgOption::regionDir, "-r", "--regionDir");
    initOption(ArgOption::output, "-o", "--out");
    initOption(ArgOption::worldBorder, "-b", "--border");
    initOption(ArgOption::chunkPixels, "-p", "--pixels");
    initOption(ArgOption::directoryFile, "-d", "--directoryFile");


    // Initialize default option values:
    int mapEdge = defaultMapEdge;
    int chunkPx = defaultChunkPx;
    path regionDataPath(defaultMapDir);
    std::string imagePath(defaultImageName);
    std::string dirInfoPath(defaultDirInfo);

    // Process all command line options:
    for (int i = 1; i < (argc - 1); i++)
    {
        ArgOption option;
        std::string optionFlag(argv[i]);
        try
        {
            option = flagOptions.at(optionFlag);
        }
        catch (const std::out_of_range& e)
        {
            std::cerr << "Error: invalid option " << optionFlag << "\n";
            option = ArgOption::help;
        }
        switch (option)
        {
        case ArgOption::help:
        {
            std::cout << "Usage: ./MCMap [options]\nOptions:\n";
            const auto printFlag = [&shortOptionFlags, &longOptionFlags]
            (const ArgOption flag, const char* description)
            {
                std::cout << "  " << shortOptionFlags[flag] << ", "
                        << longOptionFlags[flag] << ":\n\t\t" << description
                        << "\n";
            };
            printFlag(ArgOption::help,
                    "Print this help text.");
            printFlag(ArgOption::regionDir,
                    "Set region data directory path.");
            printFlag(ArgOption::output,
                    "Set map image output path.");
            printFlag(ArgOption::worldBorder, 
                    "Set map width/height in chunks.");
            printFlag(ArgOption::chunkPixels,
                    "Set chunk width/height in pixels.");
            printFlag(ArgOption::directoryFile,
                    "Set coordinate directory file path.");
            exit(0);
        }
        case ArgOption::regionDir:
            regionDataPath = argv[i + 1];
            break;
        case ArgOption::output:
            imagePath = argv[i + 1];
            if (imagePath.substr(imagePath.length() - 4) == ".png")
            {
                imagePath.erase(imagePath.length() - 4);
            }
            break;
        case ArgOption::worldBorder:
            mapEdge = atoi(argv[i + 1]);
            break;
        case ArgOption::chunkPixels:
            chunkPx = atoi(argv[i + 1]);
            break;
        case ArgOption::directoryFile:
            dirInfoPath = atoi(argv[i + 1]);
        }
    }


    // save region file paths and count:
    std::vector<path> regionFiles;
    int maxDistanceFromOrigin = 0;
    const int maxAllowed = worldBorder;
    size_t outOfBounds = 0;
    for (const auto& dirEntry : directory_iterator(regionDataPath))
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
            outOfBounds++;
        }
    }
    if (outOfBounds > 0)
    {
            std::cerr << "Warning: " << outOfBounds
                << " region files past the world border at "
                << (worldBorder * 16) << " will be ignored.\n";
    }
    // Ensure map sizes fit within the maximum image size:
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
    MapCollector mappers(imagePath, dirInfoPath, mapEdge, mapEdge, chunkPx);

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
                    << numRegionFiles << " \r";
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
