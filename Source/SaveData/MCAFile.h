/**
 * @file  MCAFile.h
 *
 * @brief  Parses data from .mca Minecraft world files.
 */

#pragma once
#include "ChunkData.h"
#include <vector>
#include <filesystem>

class MCAFile
{
public:
    /**
     * @brief  Loads data from a .mca file on construction.
     *
     * @param filePath  The path to the .mca file.
     */
    MCAFile(std::filesystem::path filePath);

    virtual ~MCAFile() { }

    /**
     * @brief  Finds a region file's upper left chunk coordinate from its file
     *         name.
     *
     * @param filePath  The path to a region file.
     *
     * @return          The chunk coordinates, or { -1, -1 } if the file name
     *                  was not properly constructed.
     */
    static Point getChunkCoords(std::filesystem::path filePath);

    /**
     * @brief  Gets information about all loaded chunks stored in the file.
     *
     * @return  The list of chunk data objects.
     */
    const std::vector<ChunkData>& getLoadedChunks();

private:
    std::filesystem::path mcaPath;
    std::vector<ChunkData> loadedChunks;
};
