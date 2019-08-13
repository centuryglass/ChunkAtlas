/**
 * @file  BasicMapper.h
 *
 * @brief  Draws a map showing which chunks have been loaded.
 */

#pragma once
#include "Mapper.h"

class BasicMapper : public Mapper
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
    BasicMapper(const char* imagePath, const size_t widthInChunks,
            const size_t heightInChunks, const size_t pixelsPerChunk);

    virtual ~BasicMapper() { }

    /**
     * @brief  Provides a color for any valid chunk, using a green and white
     *         checkerboard pattern.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       The chunk color.
     */
    virtual std::optional<png::rgb_pixel> getChunkColor
    (const ChunkData& chunk);
};
