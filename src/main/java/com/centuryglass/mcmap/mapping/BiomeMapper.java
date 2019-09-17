/**
 * @file  BiomeMapper.java
 *
 * Creates the Minecraft region biome map.
 */

package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.Biome;
import com.centuryglass.mcmap.worldinfo.ChunkData;
<<<<<<< HEAD
import com.centuryglass.mcmap.images.BiomeTextures;
=======
import images.BiomeTextures;
>>>>>>> 7a561ee04e8d7fc2f9f74d7c00e4eda6d9ce4958
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
    /**
     * Initializes a mapper that creates a single biome image map.
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
    public BiomeMapper(File imageFile, int xMin, int zMin, int widthInChunks,
            int heightInChunks, int pixelsPerChunk)
    {
        super(imageFile, xMin, zMin, widthInChunks, heightInChunks,
                pixelsPerChunk);
        textureData = new BiomeTextures();
    }
    
    /**
     * Initializes a mapper that creates a set of biome map tiles. 
     * 
     * @param imageDir         The directory where map tiles will be saved.
     * 
     * @param baseName         The base name to use when selecting map image
     *                         names.
     * 
     * @param tileSize         The width and height in chunks of each map tile
     *                         image.
     */
    public BiomeMapper(File imageDir, String baseName, int tileSize)
    {
        super(imageDir, baseName, tileSize);
        textureData = new BiomeTextures();
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
