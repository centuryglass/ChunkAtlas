/**
 * @file  Mapper.java
 *
 *  A basis for classes that use chunk data to draw map images.
 */
package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.awt.Color;
import java.awt.Point;
import java.io.File;

/**
 *  Mapper classes are responsible for determining which color to apply to the
 * map for each Minecraft map chunk. Each Mapper uses different criteria to
 * select map colors, allowing for diverse map types representing different
 * types of Minecraft world information.
 */
public abstract class Mapper
{
    /**
     * Initializes a mapper that creates a single image map.
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
    public Mapper(File imageFile, int xMin, int zMin, int widthInChunks,
            int heightInChunks, int pixelsPerChunk)
    {
        map = new MapImage(imageFile, xMin, zMin, widthInChunks, heightInChunks,
                pixelsPerChunk);  
    }
    
    /**
     * Initializes a mapper that creates a set of map tiles. 
     * 
     * @param imageDir         The directory where map tiles will be saved.
     * 
     * @param baseName         The base name to use when selecting map image
     *                         names.
     * 
     * @param tileSize         The width and height in chunks of each map tile
     *                         image.
     * 
     * @param pixelsPerChunk   The width and height in pixels of each mapped
     *                         chunk.
     */
    public Mapper(File imageDir, String baseName, int tileSize,
            int pixelsPerChunk)
    {
        map = new TileMap(imageDir, baseName, tileSize, pixelsPerChunk);
    }
    
    /**
     *  Writes map image data to the image path.
     */
    public final void saveMapFile()
    {
        finalProcessing(map);
        map.saveToDisk();
    }
    
    /**
     *  Updates the map with data from a single chunk.
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
     *  Gets what color, if any, that should be drawn to the map for a
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
     *  Handles any final tasks that need to be done before the map can
     *         be exported as an image.
     *
     *  The default implementation of this method just draws the x and z axis.
     * Mapper subclasses should extend this method if there's anything they need
     * to do after processing chunks to complete the map.
     *
     * @param map  The mapper's Map, passed in so final changes can be made.
     */
    protected void finalProcessing(WorldMap map) 
    {
        /*
        final Color lineColor = new Color(255, 0, 0);
        final int xMin = map.getXMin();
        final int xMax = xMin + map.getWidth();
        final int zMin = map.getZMin();
        final int zMax = zMin + map.getHeight();
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
        */
    }
    
    // All map image data:
    WorldMap map;
}
