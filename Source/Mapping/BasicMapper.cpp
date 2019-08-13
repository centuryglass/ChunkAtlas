#include "BasicMapper.h"


// Sets map image properties on construction.
BasicMapper::BasicMapper(
        const char* imagePath,
        const size_t widthInChunks,
        const size_t heightInChunks,
        const size_t pixelsPerChunk) :
    Mapper(imagePath, widthInChunks, heightInChunks, pixelsPerChunk) { }


// Provides a color for any valid chunk
std::optional<png::rgb_pixel> BasicMapper::getChunkColor
(const ChunkData& chunk)
{
    static const MapImage::Pixel white(255, 255, 255);
    static const MapImage::Pixel green(0, 255, 0);
    Point chunkPoint = chunk.getPos();
    bool greenTile = ((chunkPoint.y % 2) == 0);
    if ((chunkPoint.x % 2) == 0)
    {
        greenTile = ! greenTile;
    }
    return greenTile ? green : white;
}
