#include "MapImage.h"
#include <algorithm>

// Minecraft map color values:
static const png::rgb_pixel mapBorderLight(0xb4, 0xa0, 0x7d);
static const png::rgb_pixel mapBorderDark(0x85, 0x75, 0x53);
static const png::rgb_pixel mapEmptyLight(0xa6, 0x94, 0x74);
static const png::rgb_pixel mapEmptyDark(0xa1, 0x8f, 0x70);


// Minecraft map background file:
static const constexpr char* mapBackground = "emptyMap.png";
static const constexpr size_t backgroundSideLen = 72;

// If using borders, map width / borderDivisor = borderWidth
static const constexpr size_t borderDivisor = 19;

// Loads image data on construction.
MapImage::MapImage(
        const char* imagePath,
        const size_t widthInChunks,
        const size_t heightInChunks,
        const size_t pixelsPerChunk,
        const bool drawBackground) :
    path(imagePath),
    mapWidth(widthInChunks),
    mapHeight(heightInChunks),
    chunkSize(pixelsPerChunk)
{
    size_t imageWidth = widthInChunks * chunkSize;
    size_t imageHeight = heightInChunks * chunkSize;
    size_t borderPixelWidth = 0;
    if (drawBackground)
    {
        // Border sizes are scaled so that the Minecraft empty map texture can
        // be used as a background.
        const size_t largerSize = std::max(imageWidth, imageHeight);
        borderPixelWidth = largerSize / borderDivisor;
        imageWidth += (2 * borderPixelWidth);
        imageHeight += (2 * borderPixelWidth);
    }
    borderWidth = borderPixelWidth / chunkSize;
    mapImage = Image(imageWidth, imageHeight);
    
    if (drawBackground)
    {
        Image sourceImage(mapBackground);
        const double xScale = static_cast<double>(mapImage.get_width())
                / static_cast<double>(sourceImage.get_width());
        const double zScale = static_cast<double>(mapImage.get_height())
                / static_cast<double>(sourceImage.get_height());
        for (size_t z = 0; z < imageHeight; z++)
        {
            size_t sourceX = 0;
            size_t sourceZ = std::min<double>(
                    z / zScale, backgroundSideLen - 1);
            png::rgb_pixel lastColor = sourceImage.get_pixel(sourceX, sourceZ);
            for (size_t x = 0; x < imageWidth; x++)
            {
                if ((x / xScale) != sourceX)
                {
                    sourceX = std::min<double>(x / xScale,
                            backgroundSideLen - 1);
                    lastColor = sourceImage.get_pixel(sourceX, sourceZ);
                }
                mapImage.set_pixel(x, z, lastColor);
            }
        }
    }
}


// Gets the color of a specific image pixel.
MapImage::Pixel MapImage::getPixelColor
(const size_t xPos, const size_t yPos) const
{
    if (xPos >= mapImage.get_width() || yPos >= mapImage.get_height())
    {
        return Pixel(0, 0, 0);
    }
    return mapImage.get_pixel(xPos, yPos);
}


// Gets the color applied to a specific chunk.
MapImage::Pixel MapImage::getChunkColor
(const size_t xPos, const size_t zPos) const
{
    const Point pixelPos = chunkToPixel({(int) xPos, (int) zPos});
    if (pixelPos.x < 0 || pixelPos.z < 0)
    {
        return Pixel(0, 0, 0);
    }
    return mapImage.get_pixel(pixelPos.x, pixelPos.z);
}


// Sets the color of a specific image pixel.
void MapImage::setPixelColor
(const size_t xPos, const size_t yPos, const Pixel color)
{
    if (xPos < mapImage.get_width() && yPos < mapImage.get_height())
    {
        mapImage.set_pixel(xPos, yPos, color);
    }
}


// Sets the color of a specific chunk.
void MapImage::setChunkColor
(const size_t xPos, const size_t zPos, const Pixel color)
{
    const Point pixelPos = chunkToPixel({(int) xPos, (int) zPos});
    if (pixelPos.x < 0 || pixelPos.z < 0)
    {
        return;
    }
    for (int z = pixelPos.z; z < pixelPos.z + chunkSize; z++)
    {
        if (z < 0 || z >= mapImage.get_height())
        {
            continue;
        }
        for (int x = pixelPos.x; x < pixelPos.x + chunkSize; x++)
        {
            if (x < 0 || x >= mapImage.get_width())
            {
                continue;
            }
            mapImage.set_pixel(x, z, color);
        }
    }
}


// Saves the image to its output path.
void MapImage::saveImage()
{
    mapImage.write(path);
}


// Gets the width of the image, measured in Minecraft map chunks.
size_t MapImage::getWidthInChunks() const
{
    return mapWidth;
}


// Gets the height of the image, measured in Minecraft map chunks.
size_t MapImage::getHeightInChunks() const
{
    return mapHeight;
}


// Gets the length in pixels of each chunk edge within the map.
size_t MapImage::getChunkEdgeLength() const
{
    return chunkSize;
}


// Get the upper left pixel used to represent a chunk.
Point MapImage::chunkToPixel(const Point chunkPos) const
{
    Point pixelPos =
    {
        static_cast<int>((mapWidth / 2 + chunkPos.x) * chunkSize
                + borderWidth),
        static_cast<int>((mapHeight / 2 + chunkPos.z) * chunkSize
                + borderWidth)
    };
    /*
    size_t borderPx = borderWidth * chunkSize;
    if (pixelPos.x < borderPx 
            || pixelPos.x >= (mapImage.get_width() - borderPx)
            || pixelPos.z < borderPx
            || pixelPos.z >= (mapImage.get_height() - borderPx))
    */
    if (pixelPos.x < 0 
            || pixelPos.x >= mapImage.get_width()
            || pixelPos.z < 0
            || pixelPos.z >= mapImage.get_height())
    {
        std::cerr << "Chunk at " << chunkPos.x << ", " << chunkPos.z
                << " is out of range at " << pixelPos.x << ", " << pixelPos.z
                << "\n";
        return { -1, -1 };
    }
    return pixelPos;
}
