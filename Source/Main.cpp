/**
 * @file  Main.cpp
 *
 * @brief  Draws some test map data.
 */

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
        std::cout << "Found " << entryChunks.size() << " chunks in file "
            << entry.path().filename() << "\n";
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
        /*
        std::string name(entry.path().filename());
        const char* numChars = "-0123456789";
        size_t xStart = name.find_first_of(numChars);
        size_t xEnd = name.find('.', xStart);
        size_t yStart = xEnd + 1;
        size_t yEnd = name.find('.', yStart);
        if (xStart == std::string::npos || xEnd == std::string::npos
                || yEnd == std::string::npos)
        {
            std::cout << "Skipping invalid file " << name << "\n";
            continue;
        }
        int x = std::stoi(name.substr(xStart, xEnd - xStart));
        int y = std::stoi(name.substr(yStart, yEnd - yStart));

        // Each file is a block of 32 x 32 chunks.  For now, treat all chunks
        // as explored if their file exists:
        const int mcaFileDim = 32;
        x *= mcaFileDim;
        y *= mcaFileDim;
        for (int cY = y; cY < y + mcaFileDim; cY++)
        {
            for (int cX = x; cX < x + mcaFileDim; cX++)
            {
                bool greenTile = ((cY % 2) == 0);
                if ((cX % 2) == 0)
                {
                    greenTile = ! greenTile;
                }
                m.setChunkColor(cX, cY, greenTile ? green : white);
                count++;
            }
        }
        */
    }
    int numChunks = mapEdge * mapEdge;
    double explorePercent = (double) count * 100 / numChunks;
    std::cout << "Mapped " << count << " chunks out of " << numChunks
        << ", map is " << explorePercent << "\% explored.\n";
    
    m.saveImage();
    return 0;
}
