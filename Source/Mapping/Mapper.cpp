#include "Mapper.h"


// Sets map image properties on construction.
Mapper::Mapper(
        const char* imagePath,
        const size_t widthInChunks,
        const size_t heightInChunks,
        const size_t pixelsPerChunk) :
    map(imagePath, widthInChunks, heightInChunks, pixelsPerChunk) { }


// Writes map image data to the image path.
void Mapper::saveMapFile()
{
    finalProcessing(map);
    map.saveImage();
}


// Updates the map with data from a single chunk.
void Mapper::drawChunk(const ChunkData& chunk)
{
    std::optional<png::rgb_pixel> color = getChunkColor(chunk);
    if (color)
    {
        Point chunkPos = chunk.getPos();
        map.setChunkColor(chunkPos.x, chunkPos.z, *color);
    }
}
