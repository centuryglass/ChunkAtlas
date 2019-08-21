/**
 * @file  StructureMapper.h
 *
 * @brief  Draws a map showing the structures of created chunks.
 */
package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.ChunkData;
import com.centuryglass.mcmap.worldinfo.Structure;
import java.awt.Color;
import java.util.Set;

public class StructureMapper extends Mapper
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
    public StructureMapper(String imagePath, int widthInChunks,
            int heightInChunks, int pixelsPerChunk)
    {
        super(imagePath, widthInChunks, heightInChunks, pixelsPerChunk);
    }

    /**
     * @brief  Provides a color for any valid chunk based on the structure or
     *         structures it contains.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       The chunk's structure color.
     */
    @Override
    protected Color getChunkColor(ChunkData chunk)
    {
        final Color emptyChunkColor = new Color(0);
        Set<Structure> chunkStructures = chunk.getStructures();
        Color color = emptyChunkColor;
        long red = 0;
        long green = 0;
        long blue = 0;
        Structure highestPriority = Structure.UNKNOWN;
        for (Structure structure : chunkStructures)
        {
            if (structure.getPriority() > highestPriority.getPriority())
            {
                highestPriority = structure;
            }
        }
        if (highestPriority != Structure.UNKNOWN)
        {
            color = Structure.getStructureColor(highestPriority);
        }
        return color;
    }
}
