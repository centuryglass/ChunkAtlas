/**
 * @file  MCMap.java
 *
 * @brief  Draws all Minecraft region map data.
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class MCMap
{
    // Default values:
    // TODO: load these from a config file
    private static final String DEFAULT_MAP_DIR = "/home/"
            + System.getenv("USER") + "/MCregion";
    private static final String DEFAULT_IMAGE_NAME = "server";
    private static final String DEFAULT_DIR_INFO = "directory.txt";
    private static final int WORLD_BORDER = 1600;
    private static final int DEFAULT_MAP_EDGE = WORLD_BORDER * 2;
    private static final int DEFAULT_CHUNK_PX = 2;
    private static final int MIN_SIZE = 256;
    private static final int MAX_SIZE = 10000;

    // Command line argument option types:
    enum ArgOption
    {
        HELP,
        REGION_DIR,
        OUTPUT,
        WORLD_BORDER,
        CHUNK_PIXELS,
        DIRECTORY_FILE;
    }

    public static void createMaps(String [] args)
    {
        // Initialize command line options:
        // Map options to short flag values:
        Map<ArgOption, String> shortOptionFlags = new HashMap();
        // Map options to long flag values:
        Map<ArgOption, String> longOptionFlags = new HashMap();
        // Map option flag strings to option types:
        Map<String, ArgOption> flagOptions = new HashMap();
        // Initialize an option in all option maps:

        BiConsumer<ArgOption, String> initOption = (option, flags) ->
        {
            String shortFlag = flags.substring(0, flags.indexOf(','));
            String longFlag = flags.substring(flags.indexOf(',') + 1);
            shortOptionFlags.put(option, shortFlag);
            longOptionFlags.put(option, longFlag);
            flagOptions.put(shortFlag, option);
            flagOptions.put(longFlag, option);
        };
        initOption.accept(ArgOption.HELP, "-h,--help");
        initOption.accept(ArgOption.REGION_DIR, "-r,--regionDir");
        initOption.accept(ArgOption.OUTPUT, "-o,--out");
        initOption.accept(ArgOption.WORLD_BORDER, "-b,--border");
        initOption.accept(ArgOption.CHUNK_PIXELS, "-p,--pixels");
        initOption.accept(ArgOption.DIRECTORY_FILE, "-d,--directoryFile");

        // Initialize default option values:
        int mapEdge = DEFAULT_MAP_EDGE;
        int chunkPx = DEFAULT_CHUNK_PX;
        Path regionDataPath = Paths.get(DEFAULT_MAP_DIR);
        Path imagePath = Paths.get(DEFAULT_IMAGE_NAME);
        Path dirInfoPath = Paths.get(DEFAULT_DIR_INFO);

        // Process all command line options:
        for (int i = 0; i < args.length; i++)
        {
            String optionFlag = (args[i]);
            ArgOption option = flagOptions.get(optionFlag);
            if (optionFlag == null)
            {
                System.err.println("Error: invalid option " + optionFlag);
                option = ArgOption.HELP;
            }
            switch (option)
            {
            case HELP:
            {
                System.out.println("Usage: ./MCMap [options]\nOptions:");
                BiConsumer<ArgOption, String> printFlag =
                (helpOption, description) ->
                {
                    System.out.println("  " + shortOptionFlags.get(helpOption)
                            + ", " + longOptionFlags.get(helpOption) 
                            + ":\n\t\t" + description);
                };
                printFlag.accept(ArgOption.HELP,
                        "Print this help text.");
                printFlag.accept(ArgOption.REGION_DIR,
                        "Set region data directory path.");
                printFlag.accept(ArgOption.OUTPUT,
                        "Set map image output path.");
                printFlag.accept(ArgOption.WORLD_BORDER, 
                        "Set map width/height in chunks.");
                printFlag.accept(ArgOption.CHUNK_PIXELS,
                        "Set chunk width/height in pixels.");
                printFlag.accept(ArgOption.DIRECTORY_FILE,
                        "Set coordinate directory file path.");
                return;
            }
            case REGION_DIR:
                regionDataPath = Paths.get(args[i + 1]);
                break;
            case OUTPUT:
                String pathStr = args[i + 1];
                if (pathStr.substring(pathStr.length() - 4).equals(".png"))
                {
                    pathStr = pathStr.substring(0, pathStr.length() - 4);
                }
                imagePath = Paths.get(pathStr);
                break;
            case WORLD_BORDER:
                mapEdge = Integer.parseInt(args[i + 1]);
                break;
            case CHUNK_PIXELS:
                chunkPx = Integer.parseInt(args[i + 1]);
                break;
            case DIRECTORY_FILE:
                dirInfoPath = Paths.get(args[i + 1]);
            }
        }


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
        while((mapEdge * chunkPx) < MIN_SIZE)
        {
            chunkPx++;
        }
        while((mapEdge * chunkPx) > MAX_SIZE && chunkPx > 1)
        {
            chunkPx--;
        }
        if((mapEdge * chunkPx) > MAX_SIZE)
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
                mapEdge, mapEdge, chunkPx);
        
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
