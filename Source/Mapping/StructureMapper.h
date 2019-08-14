/**
 * @file  StructureMapper.h
 *
 * @brief  Draws a map showing the structures of created chunks.
 */

#pragma once
#include "Mapper.h"

class StructureMapper : public Mapper
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
    StructureMapper(const char* imagePath, const size_t widthInChunks,
            const size_t heightInChunks, const size_t pixelsPerChunk);

    virtual ~StructureMapper() { }

    /**
     * @brief  Provides a color for any valid chunk based on the structure or
     *         structures it contains.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       The chunk's structure color.
     */
    virtual std::optional<png::rgb_pixel> getChunkColor
    (const ChunkData& chunk);
};
