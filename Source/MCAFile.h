/**
 * @file  MCAFile.h
 *
 * @brief  Parses data from .mca Minecraft world files.
 */

#pragma once
#include "Point.h"
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
     * @brief  Gets the coordinates of all loaded chunks stored in the file.
     *
     * @return  The list of chunk coordinates.
     */
    const std::vector<Point>& getLoadedChunks();

private:
    std::filesystem::path mcaPath;
    std::vector<Point> loadedChunks;
};
