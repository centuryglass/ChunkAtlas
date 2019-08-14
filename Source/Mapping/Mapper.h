/**
 * @file  Mapper.h
 *
 * @brief  A basis for classes that use chunk data to draw map images.
 */

#pragma once
#include "MapImage.h"
#include "ChunkData.h"
#include <optional>
#include <png++/png.hpp>

class Mapper
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
    Mapper(const char* imagePath, const size_t widthInChunks,
            const size_t heightInChunks, const size_t pixelsPerChunk);

    virtual ~Mapper() { }

    /**
     * @brief  Writes map image data to the image path.
     */
    void saveMapFile();

    /**
     * @brief  Updates the map with data from a single chunk.
     *
     * @param chunk  The world chunk to add to the map.
     */
    void drawChunk(const ChunkData& chunk);

private:
    /**
     * @brief  Handles any final tasks that need to be done before the map can
     *         be exported as an image.
     *
     *  The default implementation of this method does nothing. Mapper 
     * subclasses should override this method if there's anything they need to
     * do after processing chunks to complete the map.
     *
     * @param map  The mapper's MapImage, passed in so final changes can be
     *             made.
     */
    virtual void finalProcessing(MapImage& map) { }

    /**
     * @brief  Gets what color, if any, that should be drawn to the map for a
     *         specific chunk. 
     *
     *  Mapper subclasses will implement this function to control the type of
     * map that they draw.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       Any color value, or an empty color value.
     */
    virtual std::optional<png::rgb_pixel> getChunkColor
    (const ChunkData& chunk) = 0;

    // All map image data:
    MapImage map;
};
