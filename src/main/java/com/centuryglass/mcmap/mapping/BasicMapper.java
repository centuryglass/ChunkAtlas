/**
 * @file  BasicMapper.java
 *
 * Creates the basic loaded chunk map.
 */

package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.awt.Color;
import java.awt.Point;

/**
 *  BasicMapper creates a simple map that only displays which Minecraft map
 * chunks have been generated.
 */
public class BasicMapper extends Mapper
{
    /**
     * Sets map image properties on construction.
     *
     * @param imagePath       Path to where the map image will be saved.
     *
     * @param widthInChunks   Width of the mapped region in chunks.
     *
     * @param heightInChunks  Height of the mapped image in chunks.
     *
     * @param pixelsPerChunk  Width/height in pixels of each chunk.
     */
    public BasicMapper(String imagePath, int widthInChunks, int heightInChunks,
            int pixelsPerChunk)
    {
        super(imagePath, widthInChunks, heightInChunks, pixelsPerChunk);
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
        Color white = new Color(255, 255, 255);
        Color green = new Color(0, 255, 0);
        Point chunkPoint = chunk.getPos();
        boolean greenTile = ((chunkPoint.y % 2) == 0);
        if ((chunkPoint.x % 2) == 0)
        {
            greenTile = ! greenTile;
        }
        return greenTile? green : white;
    }
    
}
