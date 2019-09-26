/**
 * @file  StructureMapper.java
 *
 *  Creates the generated Minecraft structure map.
 */
package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.ChunkData;
import com.centuryglass.mcmap.worldinfo.Structure;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
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
     * Initializes a mapper that creates a single structure image map.
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
    public StructureMapper(File imageFile, int xMin, int zMin,
            int widthInChunks, int heightInChunks, int pixelsPerChunk)
    {
        super(imageFile, xMin, zMin, widthInChunks, heightInChunks,
                pixelsPerChunk);
        structureRefs = new HashMap();
    }
    
    /**
     * Initializes a mapper that creates a set of structure map tiles. 
     * 
     * @param imageDir         The directory where map tiles will be saved.
     * 
     * @param baseName         The base name to use when selecting map image
     *                         names.
     * 
     * @param tileSize         The width and height in chunks of each map tile
     *                         image.
     */
    public StructureMapper(File imageDir, String baseName, int tileSize)
    {
        super(imageDir, baseName, tileSize);
        structureRefs = new HashMap();
    }

    /**
     *  Provides a color for any valid chunk based on the structure or
     *  structures it contains.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       The chunk's structure color.
     */
    @Override
    protected Color getChunkColor(ChunkData chunk)
    {
        if (chunk.getErrorType() != ChunkData.ErrorFlag.NONE)
        {
            return null;
        }
        final Color emptyChunkColor = new Color(0);
        Color color = emptyChunkColor;
        long red = 0;
        long green = 0;
        long blue = 0;
        Set<Structure> chunkStructures = chunk.getStructures();
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
        for (Map.Entry<Point, Structure> entry : chunk.getStructureRefs()
                .entrySet())
        {
            if (structureRefs.containsKey(entry.getKey()))
            {
                if (structureRefs.get(entry.getKey()).getPriority()
                        >= entry.getValue().getPriority())
                {
                    continue;
                }
            }
            structureRefs.put(entry.getKey(), entry.getValue());
        }
        return color;
    }
    
    /**
     * Adds new structure references to the map before exporting it.
     *
     * @param map  The mapper's MapImage.
     */
    @Override
    protected void finalProcessing(WorldMap map)
    {
        /*
        double maxDistance = Math.sqrt(18);
        for (Map.Entry<Point, Structure> entry : structureRefs.entrySet())
        {
            Color structColor = Structure.getStructureColor(entry.getValue());
            int x = entry.getKey().x;
            int z = entry.getKey().y;
            // Single structure chunks are hard to spot on a big world map.
            // Expand structure points to 5x5 chunk spaces, fading with
            // distance from the main point.
            for (int i = 0; i < 25; i++)
            {
                int xI = x + ((i % 5) - 2);
                int zI = z + ((i / 5) - 2);
                Color pointColor;
                if (xI == x && zI == z)
                {
                    pointColor = structColor;
                }
                else
                {
                    int dX = x - xI;
                    int dZ = z - zI;
                    double distance = Math.sqrt(dX * dX + dZ * dZ);
                    double mult = 1 - (distance / maxDistance);
                    Color currentColor = map.getChunkColor(xI, zI);
                    if (currentColor == null)
                    {
                        currentColor = new Color(0);
                    }
                    pointColor = new Color(
                            (int) (structColor.getRed() * mult
                                    + currentColor.getRed() * (1 - mult)),
                            (int) (structColor.getGreen() * mult
                                    + currentColor.getGreen() * (1 - mult)),
                            (int) (structColor.getBlue() * mult
                                    + currentColor.getBlue() * (1 -mult)),
                            255);
                }
                map.setChunkColor(xI, zI, pointColor);  
            }
        }
        */
        super.finalProcessing(map);
    }
    
    private final Map<Point, Structure> structureRefs;
}
