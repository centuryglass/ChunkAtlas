/**
 * @file  DirectoryMapper.h
 *
 * @brief  Draws a map showing directory information on top of biome info.
 */
package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.awt.Color;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.function.BiConsumer;

public class DirectoryMapper extends BiomeMapper
{
    private static final double BIOME_COLOR_MULT = 0.5;
    
    /**
     * @brief  Sets map image properties on construction.
     *
     * @param imagePath       Path to where the map image will be saved.
     *
     * @param dirInfoPath     Path where directory information will be loaded.
     *
     * @param widthInChunks   Width of the mapped region in chunks.
     *
     * @param heightInChunks  Height of the mapped image in chunks.
     *
     * @param pixelsPerChunk  Width/height in pixels of each chunk.
     */
    public DirectoryMapper(String imagePath,
            Path dirInfoPath,
            int widthInChunks,
            int heightInChunks,
            int pixelsPerChunk)
    {
        super(imagePath, widthInChunks, heightInChunks, pixelsPerChunk);
        directoryPath = dirInfoPath;
    }

    /**
     * @brief  Provides a color for any valid chunk based on biome.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       The chunk's color.
     */
    @Override
    protected Color getChunkColor(ChunkData chunk)
    {
        Color color = super.getChunkColor(chunk);
        // Reduce biome color intensity to make it easier to see directory info:
        if (color != null) 
        {
            color = new Color((int) (color.getRed() * BIOME_COLOR_MULT),
                    (int) (color.getGreen() * BIOME_COLOR_MULT),
                    (int) (color.getBlue() * BIOME_COLOR_MULT));
        }
        Point chunkCoords = chunk.getPos();
        // Draw x and z axis to make it easier to find coordinates:
        if(chunkCoords.x == 0 || chunkCoords.y == 0)
        {
            color = new Color(255, 0, 0);
        }
        return color;
    }

    /**
     * @brief  Adds directory info to the map before exporting it.
     *
     * @param map  The mapper's MapImage.
     */
    @Override
    protected void finalProcessing(MapImage map)
    {
        super.finalProcessing(map);

        // Mark a directory item at block coordinate (x,z)
        BiConsumer<Integer, Integer> markCoord = (x, z)->
        {
            int chunkX = x / 16;
            int chunkZ = z / 16;
            int radius = 4;
            for (int zI = chunkZ - radius; zI < (chunkZ + radius); zI++)
            {
                for (int xI = chunkX - radius; xI < (chunkX + radius); xI++)
                {
                    double dX = Math.abs(xI - chunkX);
                    double dZ = Math.abs(zI - chunkZ);
                    double distance = Math.sqrt((dX * dX) + (dZ * dZ));
                    if (distance <= radius)
                    {
                        double colorStrength = distance / radius;
                        Color chunkColor = new Color(255, 
                                (int) (255 * colorStrength), 0);
                        map.setChunkColor(xI, zI, chunkColor);
                    }
                }
            }
        };
        
        Scanner directoryReader;
        try
        {
            directoryReader = new Scanner(directoryPath.toFile());
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Failed to open map directory listing at "
                    + directoryPath.toString());
            return;
        }
        
        // Immutably store a name/point pair
        class NamedPoint
        {
            public final String name;
            public final Point point;
            
            public NamedPoint(String name, Point point)
            {
                this.name = name;
                this.point = point;
            }
        }
        ArrayList<NamedPoint> directoryList = new ArrayList();
        while (directoryReader.hasNext())
        {
            int x = Integer.parseInt(directoryReader.next());
            int z = Integer.parseInt(directoryReader.next());
            String name = directoryReader.next();
            NamedPoint namedPoint = new NamedPoint(name, new Point(x, z));
            directoryList.add(namedPoint);
            markCoord.accept(x, z);
        }

        class PointSort implements Comparator<NamedPoint>
        {
            @Override
            public int compare(NamedPoint first, NamedPoint second)
            {
                if (first.point.y == second.point.y)
                {
                    return first.point.x - second.point.x;
                }
                return first.point.y - second.point.y;
            }
        }
        Collections.sort(directoryList, new PointSort());
        System.out.println("Points of interest:");
        int count = 1;
        for (NamedPoint namedPoint : directoryList)
        {
            if(! namedPoint.name.isEmpty())
            {
                System.out.println(count + ": " + namedPoint.name 
                    + ": (" + namedPoint.point.x + ", " + namedPoint.point.y
                    + ")");
                count++;
            }
        }
    }

    // Minecraft world coordinate directory file:
    private final Path directoryPath;
}
