/**
 * @file  BasicMapper.java
 *
 * Creates the basic loaded chunk map.
 */

package com.centuryglass.mcmap.mapping.maptype;

import com.centuryglass.mcmap.mapping.KeyItem;
import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang.Validate;

/**
 *  BasicMapper creates a simple map that only displays which Minecraft map
 * chunks have been generated.
 */
public class BasicMapper extends Mapper
{
    private static final String TYPE_NAME = "basic";
    private static final String DISPLAY_NAME = "Basic Map";
    
    /**
     * Sets the mapper's base output directory and mapped region name on
     * construction.
     *
     * @param imageDir    The directory where the map image will be saved.
     * 
     * @param regionName  The name of the region this Mapper is mapping.
     */
    public BasicMapper(File imageDir, String regionName)
    {
        super(imageDir, regionName);
    }
        
    /**
     * Gets the base Mapper type name used when naming image files.
     * 
     * @return  An appropriate type name for use in naming image files.
     */
    @Override
    public final String getTypeName()
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
        return MapType.BASIC;
    }
              
    /**
     * Gets all items in this mapper's map key.
     * 
     * @return  All KeyItems for this map type and region. 
     */
    @Override
    public Set<KeyItem> getMapKey()
    {
        return new LinkedHashSet<>();
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
        Validate.notNull(chunk, "Chunk cannot be null.");
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
