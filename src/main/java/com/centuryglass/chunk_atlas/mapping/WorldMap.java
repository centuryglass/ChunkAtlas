/**
 * @file  Map.java
 *
 * An abstract interface for classes that save map data.
 */

package com.centuryglass.chunk_atlas.mapping;

import com.centuryglass.chunk_atlas.util.ExtendedValidate;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;
import org.apache.commons.lang.Validate;

public abstract class WorldMap 
{
    /**
     * Sets map properties on construction.
     *
     * @param mapDir           The directory where the map will be saved.
     * 
     * @param fileName         The base name to use when naming map files.
     *
     * @param  pixelsPerChunk  The width and height in pixels of each chunk.
     */
    public WorldMap(File mapDir, String fileName, int pixelsPerChunk)
    {
        ExtendedValidate.couldBeDirectory(mapDir, "Map output directory");
        ExtendedValidate.notNullOrEmpty(fileName, "Map name");
        ExtendedValidate.isPositive(pixelsPerChunk, "Pixels per chunk");
        this.mapDir = mapDir;
        this.fileName = fileName;
        chunkSize = pixelsPerChunk;
    }
    
    /**
     * Saves the map to the output directory.
     */
    public final void saveToDisk()
    {
        if (! mapDir.exists())
        {
            Validate.isTrue(mapDir.mkdirs(),
                    "Couldn't create map directory at \""
                    + mapDir.toString() + "\".");
        }
        saveMapData(mapDir, fileName);
    }
    
    /**
     * Gets the list of all files used to hold map data.
     * 
     * @return  The list of map image files. 
     */
    public abstract ArrayList<File> getMapFiles();
    
    /**
     * Gets the length in pixels of each chunk edge within the map.
     *
     * @return  The chunk pixel dimensions. This serves as the multiplier
     *          used when converting map dimensions from chunks to pixels.
     */
    public int getChunkSize()
    {
        return chunkSize;
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
    public abstract Color getChunkColor(int xPos, int zPos);   
    
    /**
     * Sets the color of a specific chunk.
     *
     * @param xPos   The chunk's x-coordinate.
     *
     * @param zPos   The chunk's z-coordinate.
     *
     * @param color  The color value to apply.
     */
    public abstract void setChunkColor(int xPos, int zPos, Color color);
    
    /**
     * Gets the color of a single pixel within a mapped chunk.
     * 
     * @param xPos    The chunk's x-coordinate.
     * 
     * @param zPos    The chunk's z-coordinate.
     * 
     * @param xPixel  The x-offset from the chunk's left edge, in pixels. This
     *                value must be less than the chunk's pixel size.
     * 
     * @param yPixel  The y-offset from the chunk's top edge, in pixels. This
     *                value must be less than the chunk's pixel size.
     * 
     * @return        The color of the requested chunk pixel, or null if the
     *                chunk is outside of the map bounds.
     */
    public Color getChunkPixelColor(int xPos, int zPos, int xPixel, int yPixel)
    {
        ExtendedValidate.inInclusiveBounds(xPixel, 0, chunkSize - 1,
                "Pixel x-coordinate");
        ExtendedValidate.inInclusiveBounds(yPixel, 0, chunkSize - 1,
                "Pixel y-coordinate");
        return getChunkOffsetColor(xPos, zPos, xPixel, yPixel);
    }
    
    /**
     * Sets the color of a single pixel within a mapped chunk.
     * 
     * @param xPos    The chunk's x-coordinate.
     * 
     * @param zPos    The chunk's z-coordinate.
     * 
     * @param xPixel  The x-offset from the chunk's left edge, in pixels. This
     *                value must be less than the chunk's pixel size.
     * 
     * @param yPixel  The y-offset from the chunk's top edge, in pixels. This
     *                value must be less than the chunk's pixel size.
     * 
     * @param color   The color to apply to the selected chunk pixel if it is
     *                within the map bounds.
     */
    public void setChunkPixelColor(int xPos, int zPos, int xPixel, int yPixel,
            Color color)
    {
        ExtendedValidate.inInclusiveBounds(xPixel, 0, chunkSize - 1,
                "Pixel x-coordinate");
        ExtendedValidate.inInclusiveBounds(yPixel, 0, chunkSize - 1,
                "Pixel y-coordinate");
        setChunkOffsetColor(xPos, zPos, xPixel, yPixel, color);
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
    protected abstract Color getChunkOffsetColor(int xPos, int zPos,
            int xPixelOffset, int yPixelOffset);
    
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
    protected abstract void setChunkOffsetColor(int xPos, int zPos,
            int xPixelOffset, int yPixelOffset, Color color);
    
    
    /**
     * Saves map data to one or more map files.
     * 
     * @param mapDir    The directory where the map files will be saved.
     * 
     * @param baseName  Base image name to use when saving map files.
     */
    protected abstract void saveMapData(File mapDir, String baseName);
    
    /**
     * Iterates through each chunk in the map, running a callback for each
     * chunk's coordinates. Empty chunks may or may not be skipped.
     *
     * @param chunkAction  The action to perform for each valid chunk.
     */
    protected abstract void foreachChunk(Consumer<Point> chunkAction);
    
    /**
     * Gets the directory where map files will be saved.
     * 
     * @return  The map output directory.
     */
    protected File getMapDir()
    {
        return mapDir;
    }
    
    /**
     * Gets the string used to name map files.
     * 
     * @return  A string to use as the base for map file names.
     */
    protected String getFileName()
    {
        return fileName;
    }

    // Map output directory:
    private final File mapDir;
    // Base map file name:
    private final String fileName;
    // pixels per chunk:
    private final int chunkSize;
}
