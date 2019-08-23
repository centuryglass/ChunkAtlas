/**
 * @file MapImage.java
 *
 * Simplifies the process of storing map data in an image.
 */

package com.centuryglass.mcmap.mapping;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * MapImage is a wrapper for a PNG image object, providing functions useful for
 * drawing Minecraft map data.
 *
 *  In addition to providing convenience functions for coloring specific map
 * chunks, the MapImage also optionally draws a background and border
 * resembling the Minecraft map item.
 */
public class MapImage 
{
    private static final int BORDER_DIVISOR = 19;
    private static final String BACKGROUND_PATH = "/emptyMap.png";
    
    /**
     * Loads image data on construction, and optionally draws the default
     * background and border.
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
    public MapImage(String imagePath,
            int widthInChunks,
            int heightInChunks,
            int pixelsPerChunk,
            boolean drawBackground)
    {
        path = imagePath;
        mapWidth = widthInChunks;
        mapHeight = heightInChunks;
        chunkSize = pixelsPerChunk;
        
        int imageWidth = widthInChunks * chunkSize;
        int imageHeight = heightInChunks * chunkSize;
        int borderPixelWidth = 0;
        if (drawBackground)
        {
            final int largerSize = Math.max(imageWidth, imageHeight);
            borderPixelWidth = largerSize / BORDER_DIVISOR;
            imageWidth += (2 * borderPixelWidth);
            imageHeight += (2 * borderPixelWidth);
        }
        borderWidth = borderPixelWidth / chunkSize;
        mapImage = new BufferedImage(imageWidth, imageHeight,
                BufferedImage.TYPE_INT_RGB);
        if (drawBackground)
        {
            URL imageURL = getClass().getResource(BACKGROUND_PATH);
            BufferedImage sourceImage;
            try
            {
                sourceImage = ImageIO.read(imageURL);
            }
            catch (IOException e)
            {
                System.err.println("Opening background image "
                        + BACKGROUND_PATH + " failed.");
                return;
            }
            Graphics2D graphics = mapImage.createGraphics();
            graphics.drawImage(sourceImage, 0, 0, imageWidth, imageHeight,
                    null);
        }
    }
    
    /**
     * Gets the color of a specific image pixel.
     *
     * @param xPos  The pixel's x-coordinate.
     *
     * @param yPos  The pixel's y-coordinate.
     *
     * @return      The color value at the given coordinate, or Color(0) if the
     *              coordinate is out of bounds.
     */
    public Color getPixelColor(int xPos, int yPos)
    {
        if (xPos >= mapImage.getWidth() || yPos >= mapImage.getHeight()
                || xPos < 0 || yPos < 0)
        {
            return new Color(0);
        }
        return new Color(mapImage.getRGB(xPos, yPos));
    }
    
    /**
     * Gets the color applied to a specific chunk.
     *
     * @param xPos  The chunk's x-coordinate.
     *
     * @param zPos  The chunk's z-coordinate.
     *
     * @return      The color value at the given coordinate, or Color(0) if the
     *              coordinate is out of bounds.
     */
    public Color getChunkColor(int xPos, int zPos)
    {
        final Point pixelPos = chunkToPixel(xPos, zPos);
        if (pixelPos.x < 0 || pixelPos.y < 0)
        {
            return new Color(0);
        }
        return new Color(mapImage.getRGB(pixelPos.x, pixelPos.y));
    }
    
    /**
     * Sets the color of a specific image pixel.
     *
     * @param xPos   The pixel's x-coordinate.
     *
     * @param yPos   The pixel's y-coordinate.
     *
     * @param color  The color value to apply.
     */
    public void setPixelColor(int xPos, int yPos, Color color)
    {
        if(xPos < mapImage.getWidth() && yPos < mapImage.getHeight()
                && xPos >= 0 && yPos >=0)
        {
            mapImage.setRGB(xPos, yPos, color.getRGB());
        }
    }
    
    /**
     * Sets the color of a specific chunk.
     *
     * @param xPos   The chunk's x-coordinate.
     *
     * @param zPos   The chunk's z-coordinate.
     *
     * @param color  The color value to apply.
     */
    public void setChunkColor(int xPos, int zPos, Color color)
    {
        final Point pixelPos = chunkToPixel(xPos, zPos);
        if (pixelPos.x < 0 || pixelPos.y < 0)
        {
            return;
        }
        for (int y = pixelPos.y; y < (pixelPos.y + chunkSize); y++)
        {
            if (y < 0 || y >= mapImage.getHeight())
            {
                continue;
            }
            for (int x = pixelPos.x; x < (pixelPos.x + chunkSize); x++)
            {
                if (x < 0 || x >= mapImage.getWidth())
                {
                    continue;
                }
                mapImage.setRGB(x, y, color.getRGB());
                //System.out.println("Setting " + x + ", " + y + " to "
                //    + color.toString());
            }
        }
    }
    
    /**
     * Saves the image to its output path.
     */
    public void saveImage()
    {
        File imageFile = new File(path);
        try
        {
            ImageIO.write(mapImage, "png", imageFile);
            System.out.println("Saved map to " + path);
        }
        catch (IOException e)
        {
            System.err.println("Failed to save " + path + ": "
                    + e.getMessage());
        }
    }
    
    /**
     * Gets the width of the image, measured in Minecraft map chunks.
     *
     * @return  The map width.
     */
    public int getWidthInChunks()
    {
        return mapWidth;
    }
    
    /**
     * Gets the height of the image, measured in Minecraft map chunks.
     *
     * @return  The map height.
     */
    public int getHeightInChunks()
    {
        return mapHeight;
    }
    
    /**
     * Gets the length in pixels of each chunk edge within the map.
     *
     * @return  The chunk pixel dimensions. This serves as the multiplier
     *          used when converting map dimensions from chunks to pixels.
     */
    public int getChunkEdgeLength()
    {
        return chunkSize;
    }
    
    /**
     * Get the upper left pixel used to represent a chunk.
     *
     * @param chunkPos  The coordinates of a map chunk.
     *
     * @return          The image coordinates of that chunk, or {-1, -1} if the
     *                  chunk was out of bounds.
     */
    private Point chunkToPixel(int xPos, int zPos)
    {
        Point pixelPos = new Point(
                (mapWidth / 2 + xPos) * chunkSize + borderWidth,
                (mapHeight / 2 + zPos) * chunkSize + borderWidth);
        if (pixelPos.x < 0 || pixelPos.x >= mapImage.getWidth()
                || pixelPos.y < 0 || pixelPos.y >= mapImage.getHeight())
        {
            pixelPos.move(-1, -1);
        }
        return pixelPos;
    }
    
    // Image output path:
    private final String path;
    // The map being drawn:
    private BufferedImage mapImage;
    // Map/image dimensions, measured in chunks:
    private final int mapWidth;
    private final int mapHeight;
    private final int chunkSize;
    private final int borderWidth;
}
