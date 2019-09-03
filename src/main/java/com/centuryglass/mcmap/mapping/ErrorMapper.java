/**
 * @file  ErrorMapper.java
 *
 * Shows chunk data errors.
 */

package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *  ErrorMapper shows all chunks with missing or invalid data, color coded by
 * error type.
 */
public class ErrorMapper extends Mapper
{
    // Define colors used to represent each error type:
    private static final Map<ChunkData.ErrorFlag, Color> ERROR_COLORS;
    static
    {
        ERROR_COLORS = new HashMap();
        ERROR_COLORS.put(ChunkData.ErrorFlag.NONE,
                new Color(0, 255, 0, 255));
        ERROR_COLORS.put(ChunkData.ErrorFlag.BAD_OFFSET,
                new Color(255, 255, 0, 255));
        ERROR_COLORS.put(ChunkData.ErrorFlag.CHUNK_MISSING,
                new Color(0, 0, 0, 255));
        ERROR_COLORS.put(ChunkData.ErrorFlag.INVALID_NBT,
                new Color(255, 0, 0, 255));
    }
    
    /**
     * Initializes a mapper that creates a single error image map.
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
    public ErrorMapper(File imageFile, int xMin, int zMin, int widthInChunks,
            int heightInChunks, int pixelsPerChunk)
    {
        super(imageFile, xMin, zMin, widthInChunks, heightInChunks,
                pixelsPerChunk);
    }
        
    /**
     * Initializes a mapper that creates a set of map error tiles. 
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
    public ErrorMapper(File imageDir, String baseName, int tileSize,
            int pixelsPerChunk)
    {
        super(imageDir, baseName, tileSize, pixelsPerChunk);
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
        return ERROR_COLORS.get(chunk.getErrorType());
    }    
}
