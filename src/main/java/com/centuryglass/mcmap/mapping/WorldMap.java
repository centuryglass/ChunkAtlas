/**
 * @file  Map.java
 *
 * An abstract interface for classes that save map data.
 */

package com.centuryglass.mcmap.mapping;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.util.function.Consumer;

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
            mapDir.mkdirs();
        }
        saveMapData(mapDir, fileName);
    }

    
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
