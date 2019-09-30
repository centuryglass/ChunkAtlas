/**
 * @file  ErrorMapper.java
 *
 * Shows chunk data errors.
 */

package com.centuryglass.mcmap.mapping.maptype;

import com.centuryglass.mcmap.mapping.Mapper;
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
    private static final String TYPE_NAME = "error";
    private static final String DISPLAY_NAME = "Chunk Error Map";
    
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
    
    public ErrorMapper()
    {
        super();
    }
        
    /**
     * Gets the base Mapper type name used when naming image files.
     * 
     * @return  An appropriate type name for use in naming image files.
     */
    @Override
    public String getTypeName()
    {
        return TYPE_NAME;
    }
    
    /**
     * Gets the Mapper display name used to identify the mapper's maps to users.
     * 
     * @return  The MapType's display name. 
     */
    @Override
    public String getDisplayName()
    {
        return DISPLAY_NAME;
    }
    
    /**
     * Gets the type of map a mapper creates.
     *
     * @return  The Mapper's MapType.
     */
    @Override
    public MapType getMapType()
    {
        return MapType.ERROR;
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
