/**
 * @file  BiomeMapper.h
 *
 * @brief  Draws a map showing the biomes of created chunks.
 */

#pragma once
#include "Mapper.h"

class BiomeMapper : public Mapper
{
public:
    /**
     * @brief  Sets map image properties on construction.
     *
     * @param imagePath       Path to where the map image will be saved.
     *
     * @param widthInChunks   Width of the mapped region in chunks.
     *
     * @param heightInChunks  Height of the mapped image in chunks.
     *
     * @param pixelsPerChunk  Width/height in pixels of each chunk.
     */
    BiomeMapper(const char* imagePath, const size_t widthInChunks,
            const size_t heightInChunks, const size_t pixelsPerChunk);

    virtual ~BiomeMapper() { }

    /**
     * @brief  Provides a color for any valid chunk based on the biome or
     *         biomes it contains.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       The chunk's biome color.
     */
    virtual std::optional<png::rgb_pixel> getChunkColor
    (const ChunkData& chunk);
};
