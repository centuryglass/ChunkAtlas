/**
 * @file  BiomeMapper.java
 *
 * Creates the Minecraft region biome map.
 */

package com.centuryglass.chunk_atlas.mapping.maptype;

import com.centuryglass.chunk_atlas.mapping.KeyItem;
import com.centuryglass.chunk_atlas.worldinfo.Biome;
import com.centuryglass.chunk_atlas.worldinfo.ChunkData;
import com.centuryglass.chunk_atlas.mapping.images.BiomeTextures;
import java.awt.Color;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang.Validate;
import org.bukkit.World;

/**
 * BiomeMapper draws a map showing the biomes of all generated chunks within
 * the mapped region. Individual structure colors are defined in the
 * worldinfo.Biome enum, and documented in the project's mapKey.png file.
 */
public class BiomeMapper extends Mapper
{       
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
    public BiomeMapper(File imageDir, String regionName, World region)
    {
        super(imageDir, regionName, region);
        textureData = new BiomeTextures();
        encounteredBiomes = new TreeSet<>();
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
     * Gets all items in this mapper's map key.
     * 
     * @return  Key items for each biome found in this map.
     */
    @Override
    public Set<KeyItem> getMapKey()
    {
        Set<KeyItem> key = new LinkedHashSet<>();
        encounteredBiomes.forEach((biome) ->
        {
            key.add(new KeyItem(biome.toString(), getMapType(),
                    getRegionName(),
                    BiomeTextures.getTexturePath(biome),
                    Biome.getBiomeColor(biome)));
        });
        return key;
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
        Validate.notNull(chunk, "Chunk cannot be null.");
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
            encounteredBiomes.add(entry.getKey());
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
        else
        {
            color = new Color(0, 0, 0);
        }
        return color;
    }
    
    private final BiomeTextures textureData;
    private final Set<Biome> encounteredBiomes;
}
