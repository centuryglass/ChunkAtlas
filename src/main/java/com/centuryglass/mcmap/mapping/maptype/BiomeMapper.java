/**
 * @file  BiomeMapper.java
 *
 * Creates the Minecraft region biome map.
 */

package com.centuryglass.mcmap.mapping.maptype;

import com.centuryglass.mcmap.mapping.Mapper;
import com.centuryglass.mcmap.worldinfo.Biome;
import com.centuryglass.mcmap.worldinfo.ChunkData;
import com.centuryglass.mcmap.mapping.images.BiomeTextures;
import java.awt.Color;
import java.io.File;
import java.util.Map;

/**
 * BiomeMapper draws a map showing the biomes of all generated chunks within
 * the mapped region. Individual structure colors are defined in the
 * worldinfo.Biome enum, and documented in the project's mapKey.png file.
 */
public class BiomeMapper extends Mapper
{
    private static final String TYPE_NAME = "biome";
    private static final String DISPLAY_NAME = "Biome Map";
    
    public BiomeMapper()
    {
        super();
        textureData = new BiomeTextures();
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
        return MapType.BIOME;
    }
    
    /**
     * Provides a color for any valid chunk based on the biome or biomes it
     * contains.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       The chunk's biome color.
     */
    @Override
    protected Color getChunkColor(ChunkData chunk)
    {
        if (chunk.getErrorType() != ChunkData.ErrorFlag.NONE)
        {
            return null;
        }
        Map<Biome, Integer> chunkBiomes = chunk.getBiomeCounts();
        Color color = null;
        int biomeSum = 0;
        long red = 0;
        long green = 0;
        long blue = 0;
        for (Map.Entry<Biome, Integer> entry : chunkBiomes.entrySet())
        {
            Color biomeColor = textureData.getPixel(entry.getKey(),
                    chunk.getPos().x, chunk.getPos().y, 1);
            int count = entry.getValue();
            if (biomeColor == null)
            {
                continue;
            }
            red += biomeColor.getRed() * count;
            green += biomeColor.getGreen() * count;
            blue += biomeColor.getBlue() * count;
            biomeSum += count;
        }
        if (biomeSum > 0)
        {
            red /= biomeSum;
            green /= biomeSum;
            blue /= biomeSum;
            color = new Color((int) red, (int) green, (int) blue, 255);
        }
        return color;
    }
    
    private final BiomeTextures textureData;
}
