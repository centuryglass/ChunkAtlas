/**
 * @file MapImage.h
 *
 * @brief  Simplifies the process of storing map data in an image.
 */

#include "Point.h"
#include <cstddef>
#include <string>
#include <png++/png.hpp>


class MapImage
{
public:
    /**
     * @brief  Loads image data on construction.
     *
     * @param imagePath        The path where the image will be saved.
     *
     * @param  widthInChunks   The map's width, measured in chunks.
     *
     * @param  heightInChunks  The map's height, measured in chunks.
     *
     * @param  pixelsPerChunk  The width and height in pixels of each chunk.
     */
    MapImage(const char* imagePath, const size_t widthInChunks,
            const size_t heightInChunks, const size_t pixelsPerChunk);

    virtual ~MapImage() { }

    // Data type used to represent a pixel color value:
    typedef png::rgb_pixel Pixel;

    /**
     * @brief  Gets the color applied to a specific chunk.
     *
     * @param xPos  The chunk's x-coordinate.
     *
     * @param yPos  The chunk's y-coordinate.
     *
     * @return      The color value at the given coordinate, or
     *              rgb_pixel(0, 0, 0) if the coordinate is out of bounds.
     */
    Pixel getChunkColor(const size_t xPos, const size_t yPos) const;

    /**
     * @brief  Sets the color of a specific chunk.
     *
     * @param xPos   The chunk's x-coordinate.
     *
     * @param yPos   The chunk's y-coordinate.
     *
     * @param color  The color value to apply.
     */
    void setChunkColor(const size_t xPos, const size_t yPos, const Pixel color);

    /**
     * @brief  Saves the image to its output path.
     */
    void saveImage();

private:
    /**
     * @brief  Get the upper left pixel used to represent a chunk.
     *
     * @param chunkPos  The coordinates of a map chunk.
     *
     * @return          The image coordinates of that chunk, or {-1, -1} if the
     *                  chunk was out of bounds.
     */
    Point chunkToPixel(const Point chunkPos) const;

    /**
     * @brief  Get the chunk coordinates represented by a specific image pixel.
     *
     * @param pixelPos  The coordinates of an image pixel.
     *
     * @return          The minecraft chunk that would be at that pixel.
     */
    Point pixelToChunk(const Point pixelPos) const;

    // Image output path:
    std::string path;
    // Image type used to store the map:
    typedef png::image<Pixel> Image;
    // The map image:
    Image mapImage;
    // Map/image dimensions:
    int mapWidth;
    int mapHeight;
    int chunkSize;
};

