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
     * @brief  Gets information on all loaded chunks stored in the file.
     *
     * @return  The list of chunk data objects.
     */
    const std::vector<ChunkData>& getLoadedChunks();

private:
    std::filesystem::path mcaPath;
    std::vector<ChunkData> loadedChunks;
};
