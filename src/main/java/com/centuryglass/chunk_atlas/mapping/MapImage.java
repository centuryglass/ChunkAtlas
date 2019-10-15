/**
 * @file MapImage.java
 *
 * Simplifies the process of storing map data in an image.
 */

package com.centuryglass.chunk_atlas.mapping;
import com.centuryglass.chunk_atlas.mapping.images.MapBackground;
import com.centuryglass.chunk_atlas.util.ExtendedValidate;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import org.apache.commons.lang.Validate;

/**
 * MapImage is a wrapper for a PNG image object, providing functions useful for
 * drawing Minecraft map data.
 *
 *  In addition to providing convenience functions for coloring specific map
 * chunks, the MapImage also optionally draws a background and border
 * resembling the Minecraft map item.
 */
public class MapImage extends WorldMap
{   
    /**
     * Loads image properties on construction, and optionally draws the default
     * background and border.
     *
     * @param imageFile        The file where the image will be saved.
     * 
     * @param xMin             Lowest x-coordinate within the mapped area,
     *                         measured in chunks.
     * 
     * @param zMin             Lowest z-coordinate within the mapped area,
     *                         measured in chunks.
     * 
     * @param  widthInChunks   The map's width, measured in chunks.
     *
     * @param  heightInChunks  The map's height, measured in chunks.
     *
     * @param  pixelsPerChunk  The width and height in pixels of each chunk.
     */
    public MapImage(File imageFile,
            int xMin,
            int zMin,
            int widthInChunks,
            int heightInChunks,
            int pixelsPerChunk)
    {
        super(imageFile.getParentFile(), imageFile.getName(), pixelsPerChunk);
        ExtendedValidate.couldBeFile(imageFile, "Image file");
        ExtendedValidate.isPositive(widthInChunks, "Width in chunks");
        ExtendedValidate.isPositive(heightInChunks, "Height in chunks");
        ExtendedValidate.isPositive(pixelsPerChunk, "Pixels per chunk");
        
        this.xMin = xMin;
        this.zMin = zMin;
        mapWidth = widthInChunks;
        mapHeight = heightInChunks;
        int imageWidth = widthInChunks * pixelsPerChunk;
        int imageHeight = heightInChunks * pixelsPerChunk;
        int borderPixelWidth = 0;
        if (drawBackgrounds)
        {
            final int largerSize = Math.max(imageWidth, imageHeight);
            borderPixelWidth = MapBackground.getBorderWidth(largerSize);
            imageWidth += (2 * borderPixelWidth);
            imageHeight += (2 * borderPixelWidth);
        }
        borderWidth = borderPixelWidth / pixelsPerChunk;
        mapImage = new BufferedImage(imageWidth, imageHeight,
                BufferedImage.TYPE_INT_ARGB);
        mapFiles = new ArrayList<>();
        if (drawBackgrounds)
        {
            BufferedImage sourceImage = MapBackground.getBackgroundImage();
            if (sourceImage != null)
            {
                Graphics2D graphics = mapImage.createGraphics();
                graphics.drawImage(sourceImage, 0, 0, imageWidth, imageHeight,
                        null);
            }
        }
    }  
    
    /**
     * Enables or disables map backgrounds for all maps created by the
     * application instance.
     * 
     * @param shouldDraw  Whether map backgrounds should be drawn.
     */
    public static void setDrawBackgrounds(boolean shouldDraw)
    {
        drawBackgrounds = shouldDraw;
    }
    
    /**
     * Validates an image pixel coordinate.
     * 
     * @param xPos  The pixel's x-coordinate.
     * 
     * @param yPos  The pixel's y-coordinate. 
     */
    private void validatePixelCoords(int xPos, int yPos)
    {
        ExtendedValidate.inInclusiveBounds(xPos, 0, mapImage.getWidth() - 1,
                "Pixel x-coordinate");
        ExtendedValidate.inInclusiveBounds(yPos, 0, mapImage.getHeight() - 1,
                "Pixel y-coordinate");
    }
    
    /**
     * Gets the color of a specific image pixel.
     *
     * @param xPos  The pixel's x-coordinate.
     *
     * @param yPos  The pixel's y-coordinate.
     *
     * @return      The color value at the given coordinate, or null if the
     *              coordinate is out of bounds.
     */
    public Color getPixelColor(int xPos, int yPos)
    {
        validatePixelCoords(xPos, yPos);
        return new Color(mapImage.getRGB(xPos, yPos));
    }
    
    /**
     * Gets the color applied to a specific chunk.
     *
     * @param xPos  The chunk's x-coordinate.
     *
     * @param zPos  The chunk's z-coordinate.
     *
     * @return      The color value at the given coordinate, or null if the
     *              coordinate is out of bounds.
     */
    @Override
    public Color getChunkColor(int xPos, int zPos)
    {
        final Point pixelPos = chunkToPixel(xPos, zPos);
        if (pixelPos == null || pixelPos.x < 0 || pixelPos.y < 0)
        {
            return null;
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
        validatePixelCoords(xPos, yPos);
        Validate.notNull(color, "Pixel color cannot be null.");
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
    @Override
    public void setChunkColor(int xPos, int zPos, Color color)
    {
        Validate.notNull(color, "Chunk color cannot be null.");
        final Point pixelPos = chunkToPixel(xPos, zPos);
        if (pixelPos == null || pixelPos.x < 0 || pixelPos.y < 0)
        {
            return;
        }
        for (int y = pixelPos.y; y < (pixelPos.y + getChunkSize()); y++)
        {
            if (y < 0 || y >= mapImage.getHeight())
            {
                continue;
            }
            for (int x = pixelPos.x; x < (pixelPos.x + getChunkSize()); x++)
            {
                if (x < 0 || x >= mapImage.getWidth())
                {
                    continue;
                }
                mapImage.setRGB(x, y, color.getRGB());
            }
        }
    }
         
    /**
     * Gets the list of all files used to hold map data.
     * 
     * @return  The list of map image files. 
     */
    @Override
    public ArrayList<File> getMapFiles()
    {
        return mapFiles;
    }
        
    /**
     * Gets the map color near a chunk coordinate.
     * 
     * @param xPos          The chunk's x-coordinate.
     * 
     * @param zPos          The chunk's z-coordinate.
     * 
     * @param xPixelOffset  The x-offset in pixels from the chunk's image 
     *                      coordinate.
     * 
     * @param yPixelOffset  The y-offset in pixels from the chunk's image
     *                      coordinate.
     * 
     * @return              The color of the pixel with the given offset from
     *                      the chunk coordinate, or null if the requested
     *                      pixel is outside of the map bounds.
     */
    @Override
    protected Color getChunkOffsetColor(int xPos, int zPos, int xPixelOffset,
            int yPixelOffset)
    { 
        Point pixelPt = chunkToPixel(xPos, zPos);
        pixelPt.translate(xPixelOffset, yPixelOffset);
        return getPixelColor(pixelPt.x, pixelPt.y);
    }
    
    /**
     * Sets the map color near a chunk coordinate.
     * 
     * @param xPos          The chunk's x-coordinate.
     * 
     * @param zPos          The chunk's z-coordinate.
     * 
     * @param xPixelOffset  The x-offset in pixels from the chunk's image 
     *                      coordinate.
     * 
     * @param yPixelOffset  The y-offset in pixels from the chunk's image
     *                      coordinate.
     * 
     * @param color         The color to apply to the selected pixel, if not
     *                      outside of the map bounds.
     */
    @Override
    protected void setChunkOffsetColor(int xPos, int zPos,
            int xPixelOffset, int yPixelOffset, Color color)
    {
        Validate.notNull(color, "Color cannot be null.");
        Point pixelPt = chunkToPixel(xPos, zPos);
        pixelPt.translate(xPixelOffset, yPixelOffset);
        setPixelColor(pixelPt.x, pixelPt.y, color);
    }
    
    /**
     * Saves the image to its output path.
     * 
     * @param mapDir    The directory where the map images will be saved.
     * 
     * @param baseName  Base image name to use when saving map files.
     */
    @Override
    protected void saveMapData(File mapDir, String baseName)
    {
        ExtendedValidate.couldBeDirectory(mapDir, "Image output directory");
        ExtendedValidate.notNullOrEmpty(baseName, "Map image name");
        try
        {
            File imageFile = new File(mapDir, baseName + ".png");
            ImageIO.write(mapImage, "png", imageFile);
            if (! mapFiles.contains(imageFile))
            {
                mapFiles.add(imageFile);
            }
            System.out.println("Saved map to " + imageFile.toString());
        }
        catch (IOException e)
        {
            System.err.println("Failed to save " + mapDir.toString() + "/"
                    + baseName + ": " + e.getMessage());
        }
    }
    
    /**
     * Get the upper left pixel used to represent a chunk.
     *
     * @param xPos  The x-coordinate of a map chunk.
     * 
     * @param zPos  The z-coordinate of a map chunk.
     *
     * @return      The image coordinates of that chunk, or null if the chunk
     *              was out of bounds.
     */
    private Point chunkToPixel(int xPos, int zPos)
    {
        Point pixelPos = new Point(
                (xPos - xMin) * getChunkSize() + borderWidth,
                (zPos - zMin) * getChunkSize() + borderWidth);
        if (pixelPos.x < 0 || pixelPos.x >= mapImage.getWidth()
                || pixelPos.y < 0 || pixelPos.y >= mapImage.getHeight())
        {
            return null;
        }
        return pixelPos;
    }
    
    /**
     * Iterates through each chunk in the map, running a callback for each
     * chunk's coordinates. Empty chunks may or may not be skipped.
     *
     * @param chunkAction  The action to perform for each valid chunk.
     */
    @Override
    protected void foreachChunk(Consumer<Point> chunkAction)
    {
        Validate.notNull(chunkAction, "Chunk action cannot be null.");
        final int x0 = xMin;
        final int z0 = zMin;
        final int w = mapWidth;
        final int h = mapHeight;
        Point chunkPt = new Point();
        for (chunkPt.y = z0; chunkPt.y < (z0 + h); chunkPt.y++)
        {
            for (chunkPt.x = x0; chunkPt.x < (x0 + w); chunkPt.x++)
            {
                chunkAction.accept(chunkPt);
            } 
        }
    }
    
    // Saved map image data:
    private final BufferedImage mapImage;
    private final ArrayList<File> mapFiles;
    // Whether backgrounds are drawn. This setting is shared across all maps.
    private static boolean drawBackgrounds = true;
    // Map/image dimensions, measured in chunks:
    private final int xMin;
    private final int zMin;
    private final int mapWidth;
    private final int mapHeight;
    private final int borderWidth;
}
