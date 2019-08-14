#include "MapCollector.h"


// Sets all map image properties on construction.
MapCollector::MapCollector(const std::string imagePath,
        std::string dirInfoPath,
        const size_t widthInChunks,
        const size_t heightInChunks,
        const size_t pixelsPerChunk) :
    basic((imagePath + "_basic.png").c_str(), widthInChunks, heightInChunks,
            pixelsPerChunk),
    activity((imagePath + "_activity.png").c_str(), widthInChunks,
            heightInChunks, pixelsPerChunk),
    biome((imagePath + "_biome.png").c_str(), widthInChunks, heightInChunks,
            pixelsPerChunk),
    structure((imagePath + "_structure.png").c_str(), widthInChunks,
            heightInChunks, pixelsPerChunk),
    directory((imagePath + "_directory.png").c_str(), dirInfoPath,
            widthInChunks, heightInChunks, pixelsPerChunk) { }


// Writes all map images to their image paths.
void MapCollector::saveMapFile()
{
    basic.saveMapFile();
    activity.saveMapFile();
    biome.saveMapFile();
    structure.saveMapFile();
    directory.saveMapFile();
}


// Updates all maps with data from a single chunk.
void MapCollector::drawChunk(const ChunkData& chunk)
{
    basic.drawChunk(chunk);
    activity.drawChunk(chunk);
    biome.drawChunk(chunk);
    structure.drawChunk(chunk);
    directory.drawChunk(chunk);
}
