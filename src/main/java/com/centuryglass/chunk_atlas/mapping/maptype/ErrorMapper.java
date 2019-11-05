/**
 * @file  ErrorMapper.java
 *
 * Shows chunk data errors.
 */

package com.centuryglass.chunk_atlas.mapping.maptype;

import com.centuryglass.chunk_atlas.mapping.KeyItem;
import com.centuryglass.chunk_atlas.worldinfo.ChunkData;
import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.Validate;
import org.bukkit.World;

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
        ERROR_COLORS = new HashMap<>();
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
     * Sets the mapper's base output directory and mapped region name on
     * construction.
     *
     * @param imageDir    The directory where the map image will be saved.
     * 
     * @param regionName  The name of the region this Mapper is mapping.
     * 
     * @param region      An optional bukkit World object, used to load extra
     *                    map data if non-null.
     */
    public ErrorMapper(File imageDir, String regionName, World region)
    {
        super(imageDir, regionName, region);
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
     * Gets all items in this mapper's map key.
     * 
     * @return  All KeyItems for this map type and region. 
     */
    @Override
    public Set<KeyItem> getMapKey()
    {
        Set<KeyItem> key = new LinkedHashSet<>();
        ERROR_COLORS.entrySet().forEach((entry) ->
        {
            key.add(new KeyItem(entry.getKey().toString(), getMapType(),
                    getRegionName(), entry.getValue()));
        });
        return key;
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
        return ERROR_COLORS.get(chunk.getErrorType());
    }    
}
