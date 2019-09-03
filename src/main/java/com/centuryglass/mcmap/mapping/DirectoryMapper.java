/**
 * @file  DirectoryMapper.java
 *
 * Creates the user directory location map.
 */
package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.function.BiConsumer;
 
/**
 *  DirectoryMapper draws a map showing user-provided directory information on
 * top of biome info.
 * 
 *  A directory file provides a list of named map coordinates, formatted as
 * "X Z name" (e.g. 0 0 origin). These map coordinates are drawn over a dimmed
 * biome map, then printed to the console in order.
 */
public class DirectoryMapper extends BiomeMapper
{
    private static final double BIOME_COLOR_MULT = 0.5;
    
    /**
     * Initializes a mapper that creates a single directory image map.
     *
     * @param imageFile       The file where the map image will be saved.
     * 
     * @param dirInfoFile     A file listing notable locations on the map.
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
    public DirectoryMapper(File imageFile,
            File dirInfoFile,
            int xMin,
            int zMin,
            int widthInChunks,
            int heightInChunks,
            int pixelsPerChunk)
    {
        super(imageFile, xMin, zMin, widthInChunks, heightInChunks,
                pixelsPerChunk);
        directoryFile = dirInfoFile;
    }
    
    /**
     * Initializes a mapper that creates a set of map tiles. 
     * 
     * @param imageDir         The directory where map tiles will be saved.
     * 
     * @param baseName         The base name to use when selecting map image
     *                         names.
     * 
     * @param dirInfoFile      A file listing notable locations on the map.
     * 
     * @param tileSize         The width and height in chunks of each map tile
     *                         image.
     * 
     * @param pixelsPerChunk   The width and height in pixels of each mapped
     *                         chunk.
     */
    public DirectoryMapper(File imageDir, String baseName, File dirInfoFile,
            int tileSize, int pixelsPerChunk)
    {
        super(imageDir, baseName, tileSize, pixelsPerChunk);
        directoryFile = dirInfoFile;
    }
    
    /**
     * Provides a color for any valid chunk based on biome.
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
     * Adds directory info to the map before exporting it.
     *
     * @param map  The mapper's MapImage.
     */
    @Override
    protected void finalProcessing(WorldMap map)
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
        if (directoryFile == null)
        {
            System.err.println("DirectoryMapper: no directory list provided.");
            return;
        }
        
        Scanner directoryReader;
        try
        {
            directoryReader = new Scanner(directoryFile);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Failed to open map directory listing at "
                    + directoryFile.toString());
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
    private final File directoryFile;
}
