/**
 * @file  StructureMapper.java
 *
 *  Creates the generated Minecraft structure map.
 */
package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.ChunkData;
import com.centuryglass.mcmap.worldinfo.Structure;
import java.awt.Color;
import java.util.Set;

/**
 *  StructureMapper creates maps showing where different generated structures
 * can be found within the Minecraft world.  Individual structure colors are
 * defined in the worldinfo.Structure enum, and documented in the project's
 * mapKey.png file.
 */
public class StructureMapper extends Mapper
{
    /**
     *  Sets map image properties on construction.
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
     *  Provides a color for any valid chunk based on the structure or
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
