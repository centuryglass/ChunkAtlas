/**
 * @file MapImage.h
 *
 * @brief  Simplifies the process of storing map data in an image.
 */

#include "Point.h"
#include <cstddef>
#include <string>
#include <png++/png.hpp>


/**
 * @brief  MapImage is a wrapper for a png++ image object, providing functions
 *         useful for drawing Minecraft map data.
 *
 *  In addition to providing convenience functions for coloring specific map
 * chunks, the MapImage also optionally draws a background and border
 * resembling the Minecraft map item.
 */
class MapImage
{
public:
    /**
     * @brief  Loads image data on construction, and optionally draws the
     *         default background and border.
     *
     * @param imagePath        The path where the image will be saved.
     *
     * @param  widthInChunks   The map's width, measured in chunks.
     *
     * @param  heightInChunks  The map's height, measured in chunks.
     *
     * @param  pixelsPerChunk  The width and height in pixels of each chunk.
     *
     * @param  drawBackground  Whether the default background and borders are
     *                         drawn.
     */
    MapImage(const char* imagePath,
            const size_t widthInChunks,
            const size_t heightInChunks,
            const size_t pixelsPerChunk,
            const bool drawBackground = true);

    virtual ~MapImage() { }

    // Data type used to represent a pixel color value:
    typedef png::rgb_pixel Pixel;

    /**
     * @brief  Gets the color of a specific image pixel.
     *
     * @param xPos  The pixel's x-coordinate.
     *
     * @param yPos  The pixel's y-coordinate.
     *
     * @return      The color value at the given coordinate, or
     *              rgb_pixel(0, 0, 0) if the coordinate is out of bounds.
     */
    Pixel getPixelColor(const size_t xPos, const size_t yPos) const;

    /**
     * @brief  Gets the color applied to a specific chunk.
     *
     * @param xPos  The chunk's x-coordinate.
     *
     * @param zPos  The chunk's z-coordinate.
     *
     * @return      The color value at the given coordinate, or
     *              rgb_pixel(0, 0, 0) if the coordinate is out of bounds.
     */
    Pixel getChunkColor(const size_t xPos, const size_t zPos) const;

    /**
     * @brief  Sets the color of a specific image pixel.
     *
     * @param xPos   The pixel's x-coordinate.
     *
     * @param yPos   The pixel's y-coordinate.
     *
     * @param color  The color value to apply.
     */
    void setPixelColor
    (const size_t xPos, const size_t yPos, const Pixel color);

    /**
     * @brief  Sets the color of a specific chunk.
     *
     * @param xPos   The chunk's x-coordinate.
     *
     * @param yPos   The chunk's z-coordinate.
     *
     * @param color  The color value to apply.
     */
    void setChunkColor
    (const size_t xPos, const size_t zPos, const Pixel color);

    /**
     * @brief  Saves the image to its output path.
     */
    void saveImage();

    /**
     * @brief  Gets the width of the image, measured in Minecraft map chunks.
     *
     * @return  The map width.
     */
    size_t getWidthInChunks() const;

    /**
     * @brief  Gets the height of the image, measured in Minecraft map chunks.
     *
     * @return  The map height.
     */
    size_t getHeightInChunks() const;

    /**
     * @brief  Gets the length in pixels of each chunk edge within the map.
     *
     * @return  The chunk pixel dimensions. This serves as the multiplier
     *          used when converting map dimensions from chunks to pixels.
     */
    size_t getChunkEdgeLength() const;

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


    // Image output path:
    std::string path;
    // Image type used to store the map:
    typedef png::image<Pixel> Image;
    // The map image:
    Image mapImage;
    // Map/image dimensions, measured in chunks:
    size_t mapWidth;
    size_t mapHeight;
    size_t chunkSize;
    size_t borderWidth;
    
};

