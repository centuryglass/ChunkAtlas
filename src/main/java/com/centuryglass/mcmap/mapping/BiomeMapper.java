/**
 * @file  BiomeMapper.h
 *
 * @brief  Draws a map showing the biomes of created chunks.
 */

package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.Biome;
import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.awt.Color;
import java.util.Map;

public class BiomeMapper extends Mapper
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
    public BiomeMapper(String imagePath, int widthInChunks,
            int heightInChunks, int pixelsPerChunk)
    {
        super(imagePath, widthInChunks, heightInChunks, pixelsPerChunk);
    }

    /**
     * @brief  Provides a color for any valid chunk based on the biome or
     *         biomes it contains.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       The chunk's biome color.
     */
    @Override
    protected Color getChunkColor(ChunkData chunk)
    {
        Map<Biome, Integer> chunkBiomes = chunk.getBiomeCounts();
        Color color = null;
        int biomeSum = 0;
        long red = 0;
        long green = 0;
        long blue = 0;
        for (Map.Entry<Biome, Integer> entry : chunkBiomes.entrySet())
        {
            Color biomeColor = Biome.getBiomeColor(entry.getKey());
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
            try
            {
                color = new Color((int) red, (int) green, (int) blue);
            }
            catch (java.lang.IllegalArgumentException e)
            {
                System.err.println("Illegal color: " + red + ", " + green + ", " + blue);
                System.exit(1);               
            }
        }
        return color;
    }
}
