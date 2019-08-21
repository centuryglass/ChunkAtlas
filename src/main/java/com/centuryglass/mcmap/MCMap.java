/**
 * @file  MCMap.java
 *
 * Draws all Minecraft region map data.
 */
package com.centuryglass.mcmap;

import com.centuryglass.mcmap.mapping.MapCollector;
import com.centuryglass.mcmap.savedata.MCAFile;
import com.centuryglass.mcmap.threads.MapperThread;
import com.centuryglass.mcmap.threads.ProgressThread;
import com.centuryglass.mcmap.threads.ReaderThread;
import java.awt.Point;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 *  MCMap defines the main set of actions necessary to process all region files
 * and convert those files to map images. It allows access to mapping routines
 * both when running MCMap as an executable and when running it as a Spigot
 * server plugin.
 */
public class MCMap
{
    // Default values:
    // TODO: load these from a config file
    private static final int WORLD_BORDER = 1600;
    private static final int MIN_SIZE = 256;
    private static final int MAX_SIZE = 10000;


    /**
     * Generates all map variants from Minecraft server data.
     * 
     * @param mapEdge          Width and height of the mapped region, centered
     *                         on (0, 0) and measured in chunks. This value may
     *                         be adjusted if the resulting maps would be
     *                         excessively large.
     * 
     * @param pxPerChunk       Width and height in pixels of each chunk within
     *                         generated map images. This value may be adjusted
     *                         if the resulting maps would be excessively large.
     * 
     * @param regionDataPath   Path to the Minecraft region file directory used
     *                         to generate maps.
     * 
     * @param imagePath        Path where image files will be saved. Use a
     *                         generic path value like "./map.png", individual
     *                         map types will be saved to "./map_type.png".
     * 
     * @param dirInfoPath      Path to a file containing notable coordinates to
     *                         mark on the server directory map. Directory files
     *                         should list one point per line, formatted as
     *                         X Z PlaceName.
     */
    public static void createMaps(int mapEdge, int pxPerChunk,
            Path regionDataPath, Path imagePath, Path dirInfoPath)
    {
        // save region file paths and count:
        ArrayList<Path> regionFiles = new ArrayList();
        int maxDistanceFromOrigin = 0;
        int maxAllowed = WORLD_BORDER;
        int outOfBounds = 0;
        File regionDir = regionDataPath.toFile();
        File[] directoryFiles = regionDir.listFiles();
        for (File regionFile : directoryFiles)
        {
            // find maximum distance from origin:
            int fileMax = 0;
            Point chunkCoords = MCAFile.getChunkCoords(regionFile.toPath());
            if (chunkCoords.x == -1 && chunkCoords.y == -1)
            {
                System.err.println(regionFile.getPath() 
                        + " does not have a legal region file name format.");
                continue;
            }
            fileMax = Math.max(fileMax, (chunkCoords.x >= 0 ?
                        (chunkCoords.x + 32) : (chunkCoords.x * -1)));
            fileMax = Math.max(fileMax, (chunkCoords.y >= 0 ?
                        (chunkCoords.y + 32) : (chunkCoords.y * -1)));
            if (fileMax <= maxAllowed)
            {
                maxDistanceFromOrigin = Math.max(fileMax,
                        maxDistanceFromOrigin);
                regionFiles.add(regionFile.toPath());
            }
            else
            {
                outOfBounds++;
            }        
        }
        if (outOfBounds > 0)
        {
            System.err.println("Warning: " + outOfBounds
                    + " region files past the world border at "
                    + (WORLD_BORDER * 16) + " will be ignored.");
        }
        mapEdge = maxDistanceFromOrigin * 2;
        // Ensure map sizes fit within the maximum image size:
        while((mapEdge * pxPerChunk) < MIN_SIZE)
        {
            pxPerChunk++;
        }
        while((mapEdge * pxPerChunk) > MAX_SIZE && pxPerChunk > 1)
        {
            pxPerChunk--;
        }
        if((mapEdge * pxPerChunk) > MAX_SIZE)
        {
            mapEdge = MAX_SIZE;
            int maxBlock = (mapEdge / 2) * 32 * 16;
            System.out.println(
                    "Warning: Map would exceed the maximum image size of "
                    + MAX_SIZE + " x " + MAX_SIZE + ", chunks further than "
                    + maxBlock + " blocks from (0,0) will be cropped.");
        }
        int numRegionFiles = regionFiles.size();
        // Initialize Mappers with the provided path:
        final MapCollector mappers = new MapCollector(imagePath, dirInfoPath,
                mapEdge, mapEdge, pxPerChunk);
        
        // Provide threadsafe tracking of processed region and chunk counts:
        ProgressThread progressThread = new ProgressThread(numRegionFiles);
        progressThread.start();
        // Handle all map updates within a single thread:
        MapperThread mapperThread = new MapperThread(mappers);
        mapperThread.start();
        // Divide region file updates between multiple threads:
        int numReaderThreads = Math.max(1,
                Runtime.getRuntime().availableProcessors());
        System.out.println("Processing " + numRegionFiles
                + " region files with " + numReaderThreads + " threads.");
        int filesPerThread = numRegionFiles / numReaderThreads;
        ArrayList<ReaderThread> threadList = new ArrayList();
        for (int i = 0; i < numReaderThreads; i++)
        {
            int regionStart = i * filesPerThread;
            int regionEnd = regionStart + filesPerThread;
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
        progressThread.requestStop();
        while (mapperThread.isAlive())
        {
            try
            {
                mapperThread.join();
            }
            catch (InterruptedException e)
            {
                // Keep trying...
            }
        }
        int numChunks = mapEdge * mapEdge;
        double explorePercent = (double) progressThread.getChunkCount() * 100
                / numChunks;
        System.out.println("Mapped " + progressThread.getChunkCount()
                + " chunks out of " + numChunks + ", map is " + explorePercent
                + "% explored.");
        mappers.saveMapFile();
    }
}
