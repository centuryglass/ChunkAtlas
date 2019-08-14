/**
 * @file  MapCollector.h
 *
 * @brief  Provides a single interface for generating all map types.
 */

#pragma once
#include "ActivityMapper.h"
#include "BasicMapper.h"
#include "BiomeMapper.h"
#include "StructureMapper.h"
#include "DirectoryMapper.h"
#include <string>

class MapCollector
{
public:
    /**
     * @brief  Sets all map image properties on construction.
     *
     * @param imagePath       Generic image path used to create all map image
     *                        paths.
     *
     * @param dirInfoPath     Path where directory information will be loaded.
     *
     * @param widthInChunks   Width of the mapped region in chunks.
     *
     * @param heightInChunks  Height of the mapped image in chunks.
     *
     * @param pixelsPerChunk  Width/height in pixels of each chunk.
     */
    MapCollector(const std::string imagePath,
            const std::string dirInfoPath,
            const size_t widthInChunks,
            const size_t heightInChunks,
            const size_t pixelsPerChunk);

    virtual ~MapCollector() { }

    /**
     * @brief  Writes all map images to their image paths.
     */
    void saveMapFile();

    /**
     * @brief  Updates all maps with data from a single chunk.
     *
     * @param chunk  The world chunk to add to the maps.
     */
    void drawChunk(const ChunkData& chunk);

private:
    // All mapper types:
    BasicMapper basic;
    ActivityMapper activity;
    BiomeMapper biome;
    StructureMapper structure;
    DirectoryMapper directory;
};
