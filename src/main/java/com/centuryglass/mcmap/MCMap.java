/**
 * @file  MCMap.java
 *
 * Draws all Minecraft region map data.
 */
package com.centuryglass.mcmap;

import com.centuryglass.mcmap.mapping.MapCollector;
import com.centuryglass.mcmap.mapping.TileMap;
import com.centuryglass.mcmap.savedata.MCAFile;
import com.centuryglass.mcmap.threads.MapperThread;
import com.centuryglass.mcmap.threads.ProgressThread;
import com.centuryglass.mcmap.threads.ReaderThread;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 *  MCMap defines the main set of actions necessary to process all region files
 * and convert those files to map images. It allows access to mapping routines
 * both when running MCMap as an executable and when running it as a Spigot
 * server plugin.
 */
public class MCMap
{   
    // Debug: Set whether to use multiple threads to scan region files:
    private static final boolean MULTI_REGION_THREADS = true;
    
    /**
     * Gets the bounds of all regions within a Minecraft save directory.
     * 
     * @param regionDataDir The path to a Minecraft region file directory.
     * 
     * @return  The upper left and lower right chunk coordinates of the
     *          rectangle that contains all regions.
     */
    public static Pair<Point, Point> getWorldBounds(File regionDataDir)
    {
        File[] directoryFiles = regionDataDir.listFiles();
        int xMin = Integer.MAX_VALUE;
        int zMin = Integer.MAX_VALUE;
        int xMax = Integer.MIN_VALUE;
        int zMax = Integer.MIN_VALUE;
        for (File regionFile : directoryFiles)
        {
            Point chunkCoords = MCAFile.getChunkCoords(regionFile);
            xMin = Math.min(xMin, chunkCoords.x);
            zMin = Math.min(zMin, chunkCoords.y);
            xMax = Math.max(xMax, chunkCoords.x);
            zMax = Math.max(zMax, chunkCoords.y);
        }
        if (xMin == Integer.MAX_VALUE || zMin == Integer.MAX_VALUE
                || xMax == Integer.MIN_VALUE || zMax == Integer.MIN_VALUE)
        {
            return new Pair(new Point(0, 0), new Point(0, 0));
        }
        return new Pair(
                MapUnit.convertPoint(new Point(xMin, zMin),
                        MapUnit.REGION, MapUnit.CHUNK),
                MapUnit.convertPoint(new Point(xMax, zMax),
                        MapUnit.REGION, MapUnit.CHUNK));
    }
    
    /**
     * Maps all world data within a set of tiled image maps.
     * 
     * @param tileSize         The width and height of each map tile in chunks.
     *                         Multiples of 32 will provide the best
     *                         performance, as that ensures each region file's
     *                         data will fit within a single tile.
     * 
     * @param regionDataDir    The Minecraft region file directory used to
     *                         generate maps.
     * 
     * @param imageDir         The directory where map images will be saved.
     * 
     * @param baseName         The start of the filename that will be used to
     *                         save all maps. Each map tile will be saved at
     *                         baseName.X.Z.png within a subdirectory specific
     *                         to its map type.
     * 
     * @param dirInfoFile      A file containing notable coordinates to mark on
     *                         the server directory map. Directory files should
     *                         list one point per line, formatted as 
     *                         X Z PlaceName.
     */
    public static void createTileMaps(int tileSize,
            File regionDataDir,
            File imageDir,
            String baseName,
            File dirInfoFile)
    {
        ArrayList<File> regionFiles = new ArrayList(Arrays.asList(
                regionDataDir.listFiles()));
        MapCollector mappers = new MapCollector(imageDir, baseName, tileSize,
                dirInfoFile);
        // If more than one tile fits in a region, sort regions by tile:
        if (tileSize > 32)
        {
            class RegionSort implements Comparator<File>
            {
                // Get the coordinates of the tile that hold a region file's
                // upper left point.
                private Point getTilePt(File regionFile)
                {
                    Point chunkPt = MCAFile.getChunkCoords(regionFile);
                    return TileMap.getTilePoint(chunkPt.x, chunkPt.y, tileSize);
                }
                
                @Override
                public int compare(File first, File second)
                {
                    Point firstTile = getTilePt(first);
                    Point secondTile = getTilePt(second);
                    if (firstTile.y == secondTile.y)
                    {
                        return firstTile.x - secondTile.x;
                    }
                    return firstTile.y - secondTile.y;
                }
            }
            Collections.sort(regionFiles, new RegionSort());
        }
        final int chunksMapped = mapRegions(regionFiles, mappers);
        if (chunksMapped > 0)
        {
            int mapKM = MapUnit.convert(chunksMapped, MapUnit.CHUNK,
                    MapUnit.BLOCK) / 1000000;
            System.out.println("Mapped " + chunksMapped
                    + " chunks, an area of " + mapKM + " km^2.");        
        }
    }

    /**
     * Generates all map variants from Minecraft server data.
     * 
     * @param xMin             Lowest x-coordinate within the mapped area,
     *                         measured in chunks.
     * 
     * @param zMin             Lowest z-coordinate within the mapped area,
     *                         measured in chunks.
     * 
     * @param width            Width of the mapped area, measured in chunks.
     * 
     * @param height           Height of the mapped area, measured in chunks.
     * 
     * @param pxPerChunk       Width and height in pixels of each chunk within
     *                         generated map images.
     * 
     * @param regionDataDir    The Minecraft region file directory used to
     *                         generate maps.
     * 
     * @param imageDir         The directory where map images will be saved.
     * 
     * @param imagePrefix      The start of the filename that will be used to
     *                         save all maps. Each map type will be saved at
     *                         imagePrefix + "MapType" + ".png"
     * 
     * @param dirInfoFile      A file containing notable coordinates to mark on
     *                         the server directory map. Directory files should
     *                         list one point per line, formatted as 
     *                         X Z PlaceName.
     */
    public static void createMaps(
            int xMin,
            int zMin,
            int width,
            int height,
            int pxPerChunk,
            File regionDataDir,
            File imageDir,
            String imagePrefix,
            File dirInfoFile)
    {
        ArrayList<File> regionFiles = new ArrayList();
        int xMax = xMin + width;
        int zMax = zMin + height;
        int regionChunks = MapUnit.convert(1, MapUnit.REGION, MapUnit.CHUNK);
        int outOfBounds = 0;
        // Valid region files within the given bounds will all be named
        // r.X.Z.mca, where X and Z are valid region coordinates within the
        // given range. Check each of these names to see if a corresponding file
        // exists.
        int regionXMin = (int) Math.floor((double) xMin 
                / (double) regionChunks);
        int regionXMax = (int) Math.ceil((double) xMin / (double) regionChunks)
                + (int) Math.ceil((double) width / (double) regionChunks);
        int regionZMin = (int) Math.floor((double) zMin
                / (double) regionChunks);
        int regionZMax = (int) Math.ceil((double) zMin / (double) regionChunks)
                + (int) Math.ceil((double) height / (double) regionChunks);
        for(int x = regionXMin; x < regionXMax; x++)
        {
            for (int z = regionZMin; z < regionZMax; z++)
            {
                String filename = "r." + String.valueOf(x) + "."
                        + String.valueOf(z) + ".mca";
                File regionFile = new File(regionDataDir, filename);
                if (regionFile.exists())
                {
                    regionFiles.add(regionFile);
                }
            }
        }
        int numRegionFiles = regionFiles.size();    
        if (numRegionFiles == 0)
        {
            System.out.println("Region was empty, no maps were created.");
            return;          
        }
        final MapCollector mappers = new MapCollector(imageDir, imagePrefix,
                xMin, zMin, width, height, pxPerChunk, dirInfoFile);
        final int chunksMapped = mapRegions(regionFiles, mappers);
        if (chunksMapped > 0)
        {
            int numChunks = width * height;
            double explorePercent = (double) chunksMapped * 100 / numChunks;
            System.out.println("Mapped " + chunksMapped
                    + " chunks out of " + numChunks + ", map is "
                    + explorePercent
                    + "% explored.");
        }
        else
        {
            System.out.println("Region was empty, no maps were created.");
        }
    }
    
    /**
     * Finishes the process of mapping a set of Minecraft regions, processing
     * all regions within multiple threads.
     * 
     * @param regionFiles  The set of Minecraft region files to map.
     * 
     * @param mapper       The object responsible for copying world data into
     *                     map images.
     * 
     * @return             The total number of region chunks mapped.
     */
    private static int mapRegions
    (ArrayList<File> regionFiles, MapCollector mapper)
    {   
        int numRegionFiles = regionFiles.size();
        // Provide threadsafe tracking of processed region and chunk counts:
        ProgressThread progressThread = new ProgressThread(numRegionFiles);
        progressThread.start();
        // Handle all map updates within a single thread:
        MapperThread mapperThread = new MapperThread(mapper);
        mapperThread.start();
        // Divide region file updates between multiple threads:
        int numReaderThreads;
        if (MULTI_REGION_THREADS)
        {
            numReaderThreads = Math.max(1,
                    Runtime.getRuntime().availableProcessors());
        }
        else
        {
            numReaderThreads = 1;
        }
        if (numReaderThreads > numRegionFiles)
        {
            numReaderThreads = numRegionFiles;
        }
        System.out.println("Processing " + numRegionFiles
                + " region files with " + numReaderThreads + " threads.");
        int filesPerThread = numRegionFiles / numReaderThreads;
        ArrayList<ReaderThread> threadList = new ArrayList();
        for (int i = 0; i < numReaderThreads; i++)
        {
            int regionStart = i * filesPerThread;
            int regionEnd = (i == (numReaderThreads - 1))
                    ? numRegionFiles : (regionStart + filesPerThread);
            threadList.add(new ReaderThread(
                    new ArrayList(regionFiles.subList(regionStart, regionEnd)),
                    mapperThread,
                    progressThread));
            threadList.get(i).start();
        }
        for (ReaderThread thread : threadList)
        {
            while (thread.isAlive())
            {
                try
                {
                    thread.join();
                }
                catch (InterruptedException e)
                {
                    // Just try again.
                }
            }
        }
        mapperThread.requestStop();
        while (mapperThread.isAlive())
        {
            try
            {
                mapperThread.join();
            }
            catch (InterruptedException e) { }
        }
        progressThread.requestStop();
        while (progressThread.isAlive())
        {
            try
            {
                progressThread.join();
            }
            catch (InterruptedException e) { }
        }
        mapper.saveMapFile();
        return progressThread.getChunkCount();
    }
}
