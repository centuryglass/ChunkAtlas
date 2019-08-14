#include "StructureMapper.h"
#include "Structure.h"

// Color used for chunks that don't contain structures:
static const png::rgb_pixel emptyChunkColor(0, 0, 0);

// Sets map image properties on construction.
StructureMapper::StructureMapper(
        const char* imagePath,
        const size_t widthInChunks,
        const size_t heightInChunks,
        const size_t pixelsPerChunk) :
    Mapper(imagePath, widthInChunks, heightInChunks, pixelsPerChunk) { }



// Provides a color for any valid chunk
std::optional<png::rgb_pixel> StructureMapper::getChunkColor
(const ChunkData& chunk)
{
    std::set<Structure> chunkStructures = chunk.getStructures();
    std::optional<png::rgb_pixel> pixel(emptyChunkColor);
    long red = 0;
    long green = 0;
    long blue = 0;
    Structure highestPriority = Structure::unknown;
    for (const Structure& structure : chunkStructures)
    {
        if(static_cast<int>(structure) > static_cast<int>(highestPriority))
        {
            highestPriority = structure;
        }
    }
    if (highestPriority != Structure::unknown)
    {
        pixel = getStructureColor(highestPriority);
    }
    return pixel;
}
