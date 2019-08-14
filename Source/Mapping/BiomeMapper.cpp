#include "BiomeMapper.h"
#include "Biome.h"


// Sets map image properties on construction.
BiomeMapper::BiomeMapper(
        const char* imagePath,
        const size_t widthInChunks,
        const size_t heightInChunks,
        const size_t pixelsPerChunk) :
    Mapper(imagePath, widthInChunks, heightInChunks, pixelsPerChunk) { }


// Provides a color for any valid chunk
std::optional<png::rgb_pixel> BiomeMapper::getChunkColor
(const ChunkData& chunk)
{
    std::map<Biome, size_t> chunkBiomes = chunk.getBiomeCounts();
    std::optional<png::rgb_pixel> pixel;
    size_t biomeSum = 0;
    long red = 0;
    long green = 0;
    long blue = 0;
    int biomeCount = 0;
    for (const auto& iter : chunkBiomes)
    {
        biomeCount++;
        png::rgb_pixel biomeColor = getBiomeColor(iter.first);
        size_t count = iter.second;
        if ((biomeColor.red == 0 && biomeColor.green == 0 
                && biomeColor.blue == 0) || iter.second <= 0)
        {
            continue;
        }
        //return biomeColor;

        red += biomeColor.red * count;
        green += biomeColor.green * count;
        blue += biomeColor.blue * count;
        biomeSum += count;
    }
    if (biomeSum > 0)
    {
        pixel = png::rgb_pixel(red / biomeSum, green / biomeSum,
                blue / biomeSum);
    }
    return pixel;
}
