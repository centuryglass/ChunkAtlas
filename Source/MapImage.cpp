#include "MapImage.h"

// Loads image data on construction.
MapImage::MapImage(
        const char* imagePath,
        const size_t widthInChunks,
        const size_t heightInChunks,
        const size_t pixelsPerChunk) :
    path(imagePath),
    mapWidth(widthInChunks),
    mapHeight(heightInChunks),
    chunkSize(pixelsPerChunk),
    mapImage((widthInChunks + 2) * pixelsPerChunk,
            (heightInChunks + 2) * pixelsPerChunk)
{
    // Draw borders:
    Pixel mapColor(255, 255, 204);
    for (int x = 0; x < mapImage.get_width(); x++)
    {
        for (int y = 0; y < chunkSize; y++)
        {
            mapImage.set_pixel(x, y, mapColor);
        }
        for (int y = mapImage.get_height() - chunkSize;
                y < mapImage.get_height(); y++)
        {
            mapImage.set_pixel(x, y, mapColor);
        }
    }
    for (int y = 0; y < mapImage.get_height(); y++)
    {
        for (int x = 0; x < chunkSize; x++)
        {
            mapImage.set_pixel(x, y, mapColor);
        }
        for (int x = mapImage.get_width() - chunkSize;
                x < mapImage.get_width(); x++)
        {
            mapImage.set_pixel(x, y, mapColor);
        }
    }
}


// Gets the color applied to a specific chunk.
MapImage::Pixel MapImage::getChunkColor
(const size_t xPos, const size_t yPos) const
{
    const Point pixelPos = chunkToPixel({(int) xPos, (int) yPos});
    if (pixelPos.x < 0 || pixelPos.y < 0)
    {
        return Pixel(0, 0, 0);
    }
    return mapImage.get_pixel(pixelPos.x, pixelPos.y);
}


// Sets the color of a specific chunk.
void MapImage::setChunkColor
(const size_t xPos, const size_t yPos, const Pixel color)
{
    const Point pixelPos = chunkToPixel({(int) xPos, (int) yPos});
    if (pixelPos.x < 0 || pixelPos.y < 0)
    {
        return;
    }
    for (int y = pixelPos.y; y < pixelPos.y + chunkSize; y++)
    {
        if (y < 0 || y >= mapImage.get_height())
        {
            continue;
        }
        for (int x = pixelPos.x; x < pixelPos.x + chunkSize; x++)
        {
            if (x < 0 || x >= mapImage.get_width())
            {
                continue;
            }
            mapImage.set_pixel(x, y, color);
        }
    }
}


// Saves the image to its output path.
void MapImage::saveImage()
{
    mapImage.write(path);
}


// Get the upper left pixel used to represent a chunk.
Point MapImage::chunkToPixel(const Point chunkPos) const
{
    Point pixelPos =
    {
        (mapWidth / 2 + chunkPos.x) * chunkSize,
        (mapHeight / 2 + chunkPos.y) * chunkSize
    };
    if (pixelPos.x < 0 || pixelPos.x >= mapImage.get_width()
            || pixelPos.y < 0 || pixelPos.y >= mapImage.get_height())
    {
        return { -1, -1 };
    }
    return pixelPos;
}
