/**
 * @file  ActivityMapper.h
 *
 * @brief  Draws a map showing the amount of time that players have spent in
 *         specific chunks
 */

#pragma once
#include "Mapper.h"
#include <vector>

class ActivityMapper : public Mapper
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
    ActivityMapper(const char* imagePath, const size_t widthInChunks,
            const size_t heightInChunks, const size_t pixelsPerChunk);

    virtual ~ActivityMapper() { }

    /**
     * @brief  Saves the inhabitedTime of a chunk so it can be drawn later.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       An empty value, as correct colors can't be calculated
     *               until the largest inabitedTime value is found.
     */
    virtual std::optional<png::rgb_pixel> getChunkColor
    (const ChunkData& chunk);

private:
    /**
     * @brief  Draws chunk activity data to the map after all chunks have been
     *         analyzed.
     *
     * @param map  The map image where activity data will be drawn.
     */
    virtual void finalProcessing(MapImage& map) override;

    // Inhabited times for all map chunks:
    std::vector<std::vector<long>> inhabitedTimes;
    // Longest inhabited time:
    long maxTime = 0;
    // Offsets added to chunk coordinates in inhabitedTimes to eliminate
    // negative values:
    size_t xOffset;
    size_t zOffset;
};
