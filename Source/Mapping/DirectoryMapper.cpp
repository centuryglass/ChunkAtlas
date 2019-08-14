#include "DirectoryMapper.h"
#include <vector>
#include <algorithm>
#include <fstream>
#include <cstdlib>
#include <cmath>

// Amount to reduce biome color intensity to make it easier to see directory
// info:
static const constexpr double biomeColorMultiplier = 0.5;

// Sets map image properties on construction.
DirectoryMapper::DirectoryMapper(
        const char* imagePath,
        const std::string dirInfoPath,
        const size_t widthInChunks,
        const size_t heightInChunks,
        const size_t pixelsPerChunk) :
    BiomeMapper(imagePath, widthInChunks, heightInChunks, pixelsPerChunk),
    dirInfoPath(dirInfoPath) { }


// Provides a color for any valid chunk
std::optional<png::rgb_pixel> DirectoryMapper::getChunkColor
(const ChunkData& chunk)
{
    std::optional<png::rgb_pixel> pixel = BiomeMapper::getChunkColor(chunk);
    // Reduce biome color intensity to make it easier to see directory info:
    if (pixel) 
    {
        pixel->red *= biomeColorMultiplier;
        pixel->green *= biomeColorMultiplier;
        pixel->blue *= biomeColorMultiplier;
    }
    Point chunkCoords = chunk.getPos();
    // Draw x and z axis to make it easier to find coordinates:
    if(chunkCoords.x == 0 || chunkCoords.z == 0)
    {
        pixel = png::rgb_pixel(255, 0, 0);
    }
    return pixel;
}


// Adds directory info to the map before exporting it.
void DirectoryMapper::finalProcessing(MapImage& map)
{

    const png::rgb_pixel lineColor(255, 255, 0);
    const int width = map.getWidthInChunks();
    const int height = map.getHeightInChunks();
    const int xMin = -(width / 2);
    const int xMax = width / 2;
    const int zMin = -(height / 2);
    const int zMax = height / 2;
    // Draw x and z axis to make it easier to find coordinates:
    for (int z = zMin; z < zMax; z++)
    {
        map.setChunkColor(0, z, lineColor);
    }
    for (int x = xMin; x < xMax; x++)
    {
        map.setChunkColor(x, 0, lineColor);
    }

    // Mark a directory item at block coordinate (x,z)
    const auto markCoord = [&map](const int x, const int z)
    {
        int chunkX = x / 16;
        int chunkZ = z / 16;
        const int radius = 4;
        for (int zI = chunkZ - radius; zI < (chunkZ + radius); zI++)
        {
            for (int xI = chunkX - radius; xI < (chunkX + radius); xI++)
            {
                double dX = abs(xI - chunkX);
                double dZ = abs(zI - chunkZ);
                double distance = sqrt((dX * dX) + (dZ * dZ));
                if (distance <= radius)
                {
                    double colorStrength = distance / radius;
                    png::rgb_pixel chunkColor(255, 255 * colorStrength, 0);
                    map.setChunkColor(xI, zI, chunkColor);
                }
            }
        }
    };

    std::ifstream coordinateStream(dirInfoPath);
    if (! coordinateStream.is_open())
    {
        std::cerr << "Failed to open map directory listing at "
                << dirInfoPath << ".\n";
    }

    std::vector<std::pair<std::string, Point>> directoryList;
    while (coordinateStream && ! coordinateStream.eof())
    {
        int x, z;
        std::string name;
        coordinateStream >> x >> z >> name;
        directoryList.push_back({name, {x, z}});
        markCoord(x, z);
    }
    const auto sortFunction = [](auto& first, auto& second)
    {
        if (first.second.z < second.second.z)
        {
            return true;
        }
        else if (first.second.z == second.second.z)
        {
            return first.second.x < second.second.x;
        }
        return false;
    };
    std::sort(directoryList.begin(), directoryList.end(), sortFunction);
    std::cout << "Points of interest:\n";
    size_t count = 1;
    for (const auto& namedPoint : directoryList)
    {
        if(! namedPoint.first.empty())
        {
            std::cout << count << ": " << namedPoint.first 
                << ": (" << namedPoint.second.x << ", " << namedPoint.second.z
                << ")\n";
            count++;
        }
    }
}
