#include "ActivityMapper.h"

const constexpr long maxPlayerTicks = 8961812; 

// Sets map image properties on construction.
ActivityMapper::ActivityMapper(
        const char* imagePath,
        const size_t widthInChunks,
        const size_t heightInChunks,
        const size_t pixelsPerChunk) :
    Mapper(imagePath, widthInChunks, heightInChunks, pixelsPerChunk)
{
    // Initialize inhabited time array:
    inhabitedTimes.resize(heightInChunks);
    for (auto& row : inhabitedTimes)
    {
        row.resize(widthInChunks, -1);
    }
    xOffset = widthInChunks / 2;
    zOffset = heightInChunks / 2;
}


// Saves the inhabitedTime of a chunk so it can be drawn later.
std::optional<png::rgb_pixel> ActivityMapper::getChunkColor
(const ChunkData& chunk)
{
    const Point chunkPt = chunk.getPos();
    const long inhabitedTime = chunk.getInhabitedTime();
    maxTime = std::max(inhabitedTime, maxTime);
    inhabitedTimes[chunkPt.z + zOffset][chunkPt.x + xOffset] = inhabitedTime;
    return std::optional<png::rgb_pixel>();
}


// Draws chunk activity data to the map after all chunks have been analyzed.
void ActivityMapper::finalProcessing(MapImage& map)
{
    // time/maxTime = x/0xFF
    for (long z = 0; z < inhabitedTimes.size(); z++)
    {
        std::vector<long>& row = inhabitedTimes[z];
        assert(row.size() == map.getWidthInChunks());
        for (long x = 0; x < row.size(); x++)
        {
            if (row[x] < 0)
            {
                continue;
            }
            if (row[x] == 0)
            {
                // Draw in black to distinguish from rows that have small but
                // nonzero amounts of activity:
                map.setChunkColor(x - xOffset, z - zOffset,
                        png::rgb_pixel(0, 0, 0));
                continue;
                
            }
            int brightness = (row[x] * 255 / maxTime);
            map.setChunkColor(x - xOffset, z - zOffset,
                    png::rgb_pixel(0, brightness, 255 - brightness));
        }
    }
}
