#include "Mapper.h"


// Sets map image properties on construction.
Mapper::Mapper(
        const char* imagePath,
        const size_t widthInChunks,
        const size_t heightInChunks,
        const size_t pixelsPerChunk) :
    map(imagePath, widthInChunks, heightInChunks, pixelsPerChunk),
    width(widthInChunks),
    height(heightInChunks),
    pixelSize(pixelsPerChunk) { }


// Fills the entire map image with a solid background color.
void Mapper::setBackgroundColor(const png::rgb_pixel color)
{
    for (int z = 0; z < height; z++)
    {
        for (int x = 0; x < width; x++)
        {
            map.setChunkColor(x, z, color);
        }
    }
}


// Writes map image data to the image path.
void Mapper::saveMapFile()
{
    map.saveImage();
}


// Updates the map with data from a single chunk.
void Mapper::drawChunk(const ChunkData& chunk)
{
    std::optional<png::rgb_pixel> color = getChunkColor(chunk);
    if (color)
    {
        Point chunkPos = chunk.getPos();
        map.setChunkColor(chunkPos.x, chunkPos.y, *color);
    }
}
