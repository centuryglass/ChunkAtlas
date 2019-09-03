/**
 * @file  BasicMapper.java
 *
 * Creates the basic loaded chunk map.
 */

package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.awt.Color;
import java.awt.Point;
import java.io.File;

/**
 *  BasicMapper creates a simple map that only displays which Minecraft map
 * chunks have been generated.
 */
public class BasicMapper extends Mapper
{
    /**
     * Initializes a mapper that creates a single basic image map.
     *
     * @param imageFile       The file where the map image will be saved.
     * 
     * @param xMin            The lowest x-coordinate within the mapped area,
     *                        measured in chunks.
     * 
     * @param zMin            The lowest z-coordinate within the mapped area,
     *                        measured in chunks.
     * 
     * @param widthInChunks   The width of the mapped region in chunks.
     *
     * @param heightInChunks  The height of the mapped image in chunks.
     *
     * @param pixelsPerChunk  The width and height in pixels of each mapped
     *                        chunk.
     */
    public BasicMapper(File imageFile, int xMin, int zMin, int widthInChunks,
            int heightInChunks, int pixelsPerChunk)
    {
        super(imageFile, xMin, zMin, widthInChunks, heightInChunks,
                pixelsPerChunk);
    }
    
    /**
     * Initializes a mapper that creates a set of basic map tiles. 
     * 
     * @param imageDir         The directory where map tiles will be saved.
     * 
     * @param baseName         The base name to use when selecting map image
     *                         names.
     * 
     * @param tileSize         The width and height in chunks of each map tile
     *                         image.
     */
    public BasicMapper(File imageDir, String baseName, int tileSize)
    {
        super(imageDir, baseName, tileSize);
    }
    
    /**
     * Provides a color for any valid chunk, using a green and white
     * checkerboard pattern.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       The chunk color.
     */
    @Override
    public Color getChunkColor(ChunkData chunk)
    {
        if (chunk.getErrorType() != ChunkData.ErrorFlag.NONE)
        {
            return null;
        }
        Color white = new Color(255, 255, 255, 255);
        Color green = new Color(0, 255, 0, 255);
        Point chunkPoint = chunk.getPos();
        boolean greenTile = ((chunkPoint.y % 2) == 0);
        if ((chunkPoint.x % 2) == 0)
        {
            greenTile = ! greenTile;
        }
        return greenTile? green : white;
    }    
}
