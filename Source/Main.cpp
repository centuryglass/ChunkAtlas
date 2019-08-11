/**
 * @file  Main.cpp
 *
 * @brief  Draws some test map data.
 */

#include "Debug.h"
#include "MapImage.h"
#include "MCAFile.h"
#include <filesystem>
#include <string>
#include <iostream>
#include <cstddef>

const constexpr int defaultMapEdge = 3200;
const constexpr int defaultChunkPx = 2;

int main(int argc, char** argv)
{
    if (argc < 3)
    {
        std::cout << "Missing required arguments.\nValid usage: MCMap "
                << "regionFolder imagePath.png [mapEdge][chunkPx]\n";
        return 0;
    }
    int mapEdge = defaultMapEdge;
    int chunkPx = defaultChunkPx;
    if (argc >= 4)
    {
        std::cout << "Using a world size of " << argv[3] << " chunks.\n";
        mapEdge = atoi(argv[3]);
    }
    if (argc >= 5)
    {
        std::cout << "Using " << argv[4] << " pixels per chunk.\n";
        chunkPx = atoi(argv[4]);
    }

    using namespace std::filesystem;
    path dataPath(argv[1]);
    MapImage m(argv[2], mapEdge, mapEdge, chunkPx);
    MapImage::Pixel white(255, 255, 255);
    MapImage::Pixel green(0, 255, 0);
    directory_iterator regionIter(dataPath);
    int count = 0;
    for (const directory_entry& entry : regionIter)
    {
        MCAFile entryFile(entry.path());
        std::vector<Point> entryChunks = entryFile.getLoadedChunks();
        DBG("Found " << entryChunks.size() << " chunks in file "
            << entry.path().filename() << "\n");
        for (const Point& chunkPoint : entryChunks)
        {
            bool greenTile = ((chunkPoint.y % 2) == 0);
            if ((chunkPoint.x % 2) == 0)
            {
                greenTile = ! greenTile;
            }
            m.setChunkColor(chunkPoint.x, chunkPoint.y,
                    greenTile ? green : white);
            count++;
        }
    }
    int numChunks = mapEdge * mapEdge;
    double explorePercent = (double) count * 100 / numChunks;
    std::cout << "Mapped " << count << " chunks out of " << numChunks
        << ", map is " << explorePercent << "\% explored.\n";
    
    m.saveImage();
    return 0;
}
