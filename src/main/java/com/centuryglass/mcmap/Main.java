/**
 * @file Main.java
 * 
 * Main application class, used when launching MCMap as an executable jar.
 */
package com.centuryglass.mcmap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Starts map generation, using either command line options or default
 * arguments.
 */
public class Main 
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
    
    public static void main(String [] args)
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
        for (int i = 0; i < args.length; i += 2)
        {
            String optionFlag = (args[i]);
            ArgOption option = flagOptions.get(optionFlag);
            if (option == null)
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
        MCMap.createMaps(mapEdge, chunkPx, regionDataPath, imagePath,
                dirInfoPath);
    }
}
