/**
 * @file Main.java
 * 
 * Main application class, used when launching MCMap as an executable jar.
 */
package com.centuryglass.mcmap;

import com.centuryglass.mcmap.config.MapGenOptions;
import com.centuryglass.mcmap.mapping.images.Downscaler;
import com.centuryglass.mcmap.mapping.images.ImageStitcher;
import com.centuryglass.mcmap.mapping.MapImage;
import com.centuryglass.mcmap.util.args.ArgParser;
import com.centuryglass.mcmap.util.args.ArgParserFactory;
import com.centuryglass.mcmap.util.args.InvalidArgumentException;
import java.io.File;
import java.util.ArrayList;
import java.util.function.BiConsumer;

/**
 * Starts map generation, using command line options, options read from a
 * configuration file, or default values.
 */
public class Main 
{
    // Default values:
    private static final String DEFAULT_CONFIG_PATH = "mapGen.json";
    
    // Command line argument option types:
    enum OptionType
    {
        /**
         * Print help text describing all options and exit.
         */
        HELP,
        CONFIG_PATH,
        DRAW_BACKGROUND,
        REGION_DIR,
        OUTPUT_DIR,
        IMAGE_NAME,
        IMAGE_MAP,
        TILE_MAP,
        BOUNDS,
        CHUNK_PIXELS;
    }
    
    public static void main(String [] args)
    {
        ArgParserFactory<OptionType> parserFactory = new ArgParserFactory();
        parserFactory.setOptionProperties(OptionType.HELP, "-h", "--help", 0, 0,
                "", "Print this help text.");
        parserFactory.setOptionProperties(OptionType.CONFIG_PATH, "-c",
                "--config", 1, 1, "jsonConfigPath",
                "Set the map generation configuration file path.");
        parserFactory.setOptionProperties(OptionType.DRAW_BACKGROUND, "-d",
                "--draw-background", 0, 1, "", "Draw the Minecraft map texture "
                + "behind single-image maps.");
        parserFactory.setOptionProperties(OptionType.REGION_DIR, "-r",
                "--regionDir", 1, Integer.MAX_VALUE, "regionName=RegionPath...",
                "Set Minecraft region data directory paths.");
        parserFactory.setOptionProperties(OptionType.IMAGE_NAME, "-n",
                "--imageName", 1, 1, "name",
                "Set the base name used for map image files.");
        parserFactory.setOptionProperties(OptionType.IMAGE_MAP, "-i",
                "--image-map", 0, 0, "", "Read region files to create "
                + "single-image maps of a bounded area.");
        parserFactory.setOptionProperties(OptionType.TILE_MAP, "-t",
                "--tile-map", 1, 1, "tileResolution",
                "Map the entire region directory within a set of image tiles.");
        parserFactory.setOptionProperties(OptionType.OUTPUT_DIR, "-o",
                "--outDir", 1, 1, "directoryPath",
                "Set the directory where map images will be saved.");
        parserFactory.setOptionProperties(OptionType.BOUNDS, "-b", "--bounds",
                4, 4, "xMin zMin width height",
                "Set the area in chunks that should be mapped.");
        parserFactory.setOptionProperties(OptionType.CHUNK_PIXELS, "-p",
                "--pixels", 1, 1, "chunkWidth&Height",
                "Set the width and height in pixels to draw each map chunk.");
        ArgParser<OptionType> argParser = parserFactory.createParser();
        boolean printHelpAndExit;
        try
        {
            argParser.parseArguments(args);
            printHelpAndExit = argParser.optionFound(OptionType.HELP);
        }
        catch (InvalidArgumentException e)
        {
            System.err.println(e.getMessage());
            printHelpAndExit = true;
        }
        if (printHelpAndExit)
        {
            System.out.println("Usage: ./MCMap [options]\nOptions:");
            System.out.print(argParser.getHelpText());
            return;   
        }
        
        // Load options from arguments, a configuration file, or default values:
        String configPath = DEFAULT_CONFIG_PATH;
        if (argParser.optionFound(OptionType.CONFIG_PATH))
        {
            configPath = argParser.getOptionParams(OptionType.CONFIG_PATH)[0];
        }
        MapGenOptions mapConfig = new MapGenOptions(new File(configPath));
           
        // Single map image generation:
        MapGenOptions.SingleImage imageOptions 
                = mapConfig.getSingleImageOptions();
        final boolean createMapImages 
                = (argParser.optionFound(OptionType.IMAGE_MAP))
                ? true : imageOptions.enabled;
        final boolean drawBackgrounds 
                = (argParser.optionFound(OptionType.DRAW_BACKGROUND))
                ? true : imageOptions.drawBackground;
        final String singleImagePath 
                = (argParser.optionFound(OptionType.OUTPUT_DIR))
                ? argParser.getOptionParams(OptionType.OUTPUT_DIR)[0]
                : imageOptions.path;
        // Mapped region bounds and scale:
        final int xMin, zMin, width, height;
        String[] bounds = argParser.getOptionParams(OptionType.BOUNDS);
        if (bounds != null)
        {
            xMin = Integer.parseInt(bounds[0]);
            zMin = Integer.parseInt(bounds[1]);
            width = Integer.parseInt(bounds[2]);
            height = Integer.parseInt(bounds[3]);
        }
        else
        {
            xMin = imageOptions.xMin;
            zMin = imageOptions.zMin;
            width = imageOptions.width;
            height = imageOptions.height;
        }
        int chunkPx = mapConfig.getPixelsPerChunk();
        
        // Image tile options:
        MapGenOptions.MapTiles tileOptions = mapConfig.getMapTileOptions();
        String[] tileArgs = argParser.getOptionParams(OptionType.TILE_MAP);
        final boolean useTiles
                = (tileArgs == null) ? true : tileOptions.enabled;
        final String tilePath 
                = (argParser.optionFound(OptionType.OUTPUT_DIR))
                ? argParser.getOptionParams(OptionType.OUTPUT_DIR)[0]
                : tileOptions.path;
        final int tileResolution = (tileArgs == null) ? tileOptions.tileSize
                : Integer.parseInt(tileArgs[0]);
        
        MapImage.setDrawBackgrounds(drawBackgrounds);
        
        // Define how maps will be created for each region directory:
        BiConsumer<File, String> processRegionDir = (regionDir, name)->
        {
            if (useTiles)
            {
                File tileOutputDir = new File(tilePath);
                MCMap.createTileMaps(tileResolution, regionDir, tileOutputDir,
                        name);
                if (createMapImages) // Stitch together tiles to make maps
                {
                    ArrayList<File> dirList = getTileDirs(tileOutputDir);
                    for (File tileDir : dirList)
                    {
                        File outFile = new File(tileOutputDir,
                                tileDir.getName() + ".png");
                        ImageStitcher.stitch(tileDir, outFile, xMin, zMin,
                                width, height, chunkPx, tileResolution, true);
                    }
                }
            }
            else if (createMapImages)
            {
                MCMap.createMaps(xMin, zMin, width, height, chunkPx,
                        regionDir, new File(singleImagePath), name);
            }  
        };
        
        // If a valid command line argument region file is provided, use it to
        // create maps:
        File argRegionDir = null;
        if (argParser.optionFound(OptionType.REGION_DIR))
        {
            argRegionDir = new File(argParser.getOptionParams(
                    OptionType.REGION_DIR)[0]);
        }
        if (argRegionDir != null && argRegionDir.isDirectory())
        {
            String regionName = argRegionDir.getName();
            if (argParser.optionFound(OptionType.IMAGE_NAME))
            {
                regionName = argParser.getOptionParams(
                        OptionType.IMAGE_NAME)[0];
            }
            processRegionDir.accept(argRegionDir, regionName);
        }
        // Otherwise use the region directory or directories defined in map
        // config:
        else
        {
            mapConfig.forEachRegionPath(processRegionDir);
        }
        
        if (useTiles)
        {
            Downscaler.recursiveScale(new File(tilePath), tileResolution,
                    tileOptions.getAlternateSizes());
        }
    }
    
    // TODO: find a better place than Main to put this function:
    public static ArrayList<File> getTileDirs(File mainImageDir)
    {
        ArrayList<File> dirList = new ArrayList();
        if (! mainImageDir.isDirectory())
        {
            return dirList;
        }
        File [] files = mainImageDir.listFiles();
        boolean imagesFound = false;
        for (File child : files)
        {
            if (child.isDirectory())
            {
                dirList.addAll(getTileDirs(child));
            }
            else if (! imagesFound)
            {
                imagesFound = (child.getPath().endsWith(".png"));
            }
        }
        if (imagesFound)
        {
            dirList.add(mainImageDir);
        }
        return dirList;
    }
}
