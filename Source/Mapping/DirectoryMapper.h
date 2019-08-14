/**
 * @file  DirectoryMapper.h
 *
 * @brief  Draws a map showing directory information on top of biome info.
 */

#pragma once
#include "BiomeMapper.h"
#include <string>

class DirectoryMapper : public BiomeMapper
{
public:
    /**
     * @brief  Sets map image properties on construction.
     *
     * @param imagePath       Path to where the map image will be saved.
     *
     * @param dirInfoPath     Path where directory information will be loaded.
     *
     * @param widthInChunks   Width of the mapped region in chunks.
     *
     * @param heightInChunks  Height of the mapped image in chunks.
     *
     * @param pixelsPerChunk  Width/height in pixels of each chunk.
     */
    DirectoryMapper(const char* imagePath,
            const std::string dirInfoPath,
            const size_t widthInChunks,
            const size_t heightInChunks,
            const size_t pixelsPerChunk);

    virtual ~DirectoryMapper() { }

    /**
     * @brief  Provides a color for any valid chunk based on biome.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       The chunk's color.
     */
    virtual std::optional<png::rgb_pixel> getChunkColor
    (const ChunkData& chunk);

private:
    /**
     * @brief  Adds directory info to the map before exporting it.
     *
     * @param map  The mapper's MapImage.
     */
    virtual void finalProcessing(MapImage& map) override;

    // Minecraft world coordinate directory file:
    std::string dirInfoPath;
};
