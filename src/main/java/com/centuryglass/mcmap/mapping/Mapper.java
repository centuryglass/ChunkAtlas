/**
 * @file  Mapper.h
 *
 * @brief  A basis for classes that use chunk data to draw map images.
 */
package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.awt.Color;
import java.awt.Point;

public abstract class Mapper
{
    /**
     * @brief  Sets map image properties on construction.
     *
     * @param imagePath       Path to where the map image will be saved.
     *
     * @param widthInChunks   Width of the mapped region in chunks.
     *
     * @param heightInChunks  Height of the mapped image in chunks.
     *
     * @param pixelsPerChunk  Width/height in pixels of each chunk.
     */
    public Mapper(String imagePath, int widthInChunks, int heightInChunks,
            int pixelsPerChunk)
    {
        map = new MapImage(imagePath, widthInChunks, heightInChunks,
                pixelsPerChunk, true);  
    }
    
    /**
     * @brief  Writes map image data to the image path.
     */
    public final void saveMapFile()
    {
        finalProcessing(map);
        map.saveImage();
    }
    
    /**
     * @brief  Updates the map with data from a single chunk.
     *
     * @param chunk  The world chunk to add to the map.
     */
    public void drawChunk(ChunkData chunk)
    {
        Color color = getChunkColor(chunk);
        if (color != null)
        {
            Point chunkPos = chunk.getPos();
            map.setChunkColor(chunkPos.x, chunkPos.y, color);
        }
    }
    
    /**
     * @brief  Gets what color, if any, that should be drawn to the map for a
     *         specific chunk. 
     *
     *  Mapper subclasses will implement this function to control the type of
     * map that they draw.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       Any color value, or an empty color value.
     */
    protected abstract Color getChunkColor(ChunkData chunk);
    
    /**
     * @brief  Handles any final tasks that need to be done before the map can
     *         be exported as an image.
     *
     *  The default implementation of this method just draws the x and z axis.
     * Mapper subclasses should extend this method if there's anything they need
     * to do after processing chunks to complete the map.
     *
     * @param mapImage  The mapper's MapImage, passed in so final changes can be
     *                  made.
     */
    protected void finalProcessing(MapImage mapImage) 
    {
        final Color lineColor = new Color(255, 0, 0);
        final int width = map.getWidthInChunks();
        final int height = map.getHeightInChunks();
        final int xMin = -(width / 2);
        final int xMax = width / 2;
        final int zMin = -(height / 2);
        final int zMax = height / 2;
        // Draw x and z axis to make it easier to find coordinates:
        for (int z = zMin; z < zMax; z++)
        {
            map.setChunkColor(0, z - 1, lineColor);
            map.setChunkColor(0, z, lineColor);
            map.setChunkColor(0, z + 1, lineColor);
        }
        for (int x = xMin; x < xMax; x++)
        {
            map.setChunkColor(x - 1, 0, lineColor);
            map.setChunkColor(x, 0, lineColor);
            map.setChunkColor(x + 1, 0, lineColor);
        }
    }
    
    // All map image data:
    MapImage map;
}
