/**
 * @file Main.java
 * 
 * Main application class, used when launching MCMap as an executable jar.
 */
package com.centuryglass.mcmap;

import com.centuryglass.mcmap.mapping.Downscaler;
import com.centuryglass.mcmap.mapping.ImageStitcher;
import com.centuryglass.mcmap.mapping.MapImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



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
    private static final String DEFAULT_IMAGE_DIR = "maps";
    private static final String DEFAULT_IMAGE_PREFIX = "world";
    private static final String DEFAULT_DIR_INFO = "directory.txt";
    private static final int DEFAULT_X_MIN = -1600;
    private static final int DEFAULT_Z_MIN = -1600;
    private static final int DEFAULT_WIDTH = 3200;
    private static final int DEFAULT_HEIGHT = 3200;
    private static final int DEFAULT_CHUNK_PX = 2;
    private static final int MAX_TILE = 1024;
    private static final int MIN_TILE = 32;
    
    // Command line argument option types:
    enum ArgOption
    {
        HELP (0),
        REGION_DIR (1),
        OUTPUT_DIR (2),
        IMAGE_NAME (3),
        IMAGE_MAP (4),
        TILE_MAP (5),
        BOUNDS (6),
        DOWNSCALE (7),
        CHUNK_PIXELS (8),
        DIRECTORY_FILE (9),
        NUM_OPTIONS (10);
        
        // Get the number of available options:
        public static int count() { return NUM_OPTIONS.getIndex(); }
        
        // Get the index value of an option:
        public int getIndex() { return index; }
        
        // Get the option at a specific index:
        public static ArgOption fromIndex(int index)
        {
            return ArgOption.values()[index];
        }
        
        private ArgOption (int index)
        {
            this.index = index;
        }
        private final int index;
    }
    
    public static void main(String [] args)
    {
        // Initialize command line options:
        // Map option flag strings to option types:
        Map<String, ArgOption> flagOptions = new HashMap();
        // Map option indices to short flag values:
        String [] shortOptionFlags = new String [ArgOption.count()];
        // Map option indices to long flag values:
        String [] longOptionFlags = new String [ArgOption.count()];
        // Map option indices to the number of arguments they expect:
        int [] optionArgCounts = new int [ArgOption.count()];
        // Map option indices to descriptions of the arguments they accept:
        String [] argDescriptions = new String [ArgOption.count()];
        // Map option indices to brief descriptions of what they're for:
        String [] optionDescriptions = new String [ArgOption.count()];
        
        // Initialize all data for a given option:
        class OptionInitializer
        {
            public void setData(ArgOption option, String shortFlag,
                    String longFlag, int numArgs, String argDescription,
                    String description)
            {
                final int i = option.index;
                shortOptionFlags[i] = shortFlag;
                longOptionFlags[i] = longFlag;
                flagOptions.put(shortFlag, option);
                flagOptions.put(longFlag, option);
                optionArgCounts[i] = numArgs;
                optionDescriptions[i] = description;
                argDescriptions[i] = argDescription;
            }
        }
        OptionInitializer init = new OptionInitializer();
        init.setData(ArgOption.HELP, "-h", "--help", 0, "",
                "Print this help text.");
        init.setData(ArgOption.REGION_DIR, "-r", "--regionDir", 1,
                "directoryPath",
                "Set Minecraft region data directory path.");
        init.setData(ArgOption.IMAGE_NAME, "-n", "--imageName", 1,
                "name",
                "Set the base name used for map image files.");
        init.setData(ArgOption.IMAGE_MAP, "-i", "--image-map", 0, "",
                "Read region files to create single-image maps of a bounded"
                 + " area.");
        init.setData(ArgOption.TILE_MAP, "-t", "--tile-map", 1,
                "tileResolution",
                "Map the entire region directory within a set of image tiles.");
        init.setData(ArgOption.OUTPUT_DIR, "-o", "--outDir", 1,
                "directoryPath",
                "Set the directory where map images will be saved.");
        init.setData(ArgOption.BOUNDS, "-b", "--bounds", 4,
                "xMin zMin width height",
                "Set the area in chunks that should be mapped.");
        init.setData(ArgOption.DOWNSCALE, "-s", "--scale", 2,
                "directoryPath newResolution",
                "Create downscaled copies of all map images created.");
        init.setData(ArgOption.CHUNK_PIXELS, "-p", "--pixels", 1,
                "chunkWidth&Height",
                "Set the width and height in pixels to draw each map chunk.");
        init.setData(ArgOption.DIRECTORY_FILE, "-d", "--directoryFile", 1,
                "filePath",
                "Set the path to search for a list of notable coordinates.");

        // Initialize default option values:
        
        // Mapped region bounds and scale:
        // This is ignored if not creating single-image maps.
        int xMin = DEFAULT_X_MIN;
        int zMin = DEFAULT_Z_MIN;
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;
        int chunkPx = DEFAULT_CHUNK_PX;
        
        // Image tile options:
        boolean useTiles = false;
        int tileResolution = -1;
        
        // Single map image generation:
        boolean createMapImages = true;
        
        // Image downscaling options:
        class ScaleAction
        {
            public File outputDir = null;
            public int resolution = 0;
        }
        ArrayList<ScaleAction> downscaleList = new ArrayList();
        
        // Input paths:
        File regionDataDir = new File(DEFAULT_MAP_DIR);
        File dirInfoFile = new File(DEFAULT_DIR_INFO);
        
        // Output paths:
        File imageDir = new File(DEFAULT_IMAGE_DIR);
        String imagePrefix = DEFAULT_IMAGE_PREFIX;
        
        // Process all command line options:
        for (int i = 0; i < args.length; i += (1 + optionArgCounts[
                flagOptions.get(args[i]).getIndex()]))
        {
            String optionFlag = (args[i]);
            ArgOption option = flagOptions.get(optionFlag);
            if (option == null)
            {
                System.err.println("Error: invalid option " + optionFlag);
                option = ArgOption.HELP;
            }
            else if ((i + optionArgCounts[option.getIndex()])
                    >= args.length)
            {
                int numRemaining = (args.length - i - 1);
                System.err.println("Error: not enough options provided for "
                        + optionFlag);
                System.err.println("Expected: "
                        + argDescriptions[option.getIndex()] + " ("
                        + String.valueOf(optionArgCounts[option.getIndex()])
                        + ")");
                String combinedArgs = "";
                for(int i2 = i + 1; i2 < args.length; i2++)
                {
                    combinedArgs += " " + args[i2];
                }
                System.err.println("Found: " + combinedArgs + " ("
                        + String.valueOf(args.length - i - 1) + ")");
                return;
            }
            switch (option)
            {
            case HELP:
            {
                System.out.println("Usage: ./MCMap [options]\nOptions:");
                for (int optIdx = 0; optIdx < ArgOption.count(); optIdx++)
                {
                    System.out.println("  [" + shortOptionFlags[optIdx] + " | "
                            + longOptionFlags[optIdx] + "] " 
                            + argDescriptions[optIdx]);
                    System.out.println("\t" + optionDescriptions[optIdx]);
                }
                return;
            }
            case REGION_DIR:
                regionDataDir = new File(args[i + 1]);
                System.out.println("Reading region data files from \""
                        + regionDataDir.getAbsolutePath() + "\"");
                break;
            case IMAGE_MAP:
                createMapImages = true;
                System.out.println("Map types will be created as single"
                        + " images.");
                break;
            case TILE_MAP:
                useTiles = true;
                tileResolution = Integer.parseInt(args[i + 1]);
                System.out.println("Maps will be broken into " + tileResolution
                        + " chunk^2 map tiles.");
                break;
            case OUTPUT_DIR:
                imageDir = new File(args[i + 1]);
                System.out.println("Images will be saved to \""
                        + imageDir.getAbsolutePath() + "\"");
                break;
            case IMAGE_NAME:
                imagePrefix = args[i + 1];
                
                break;
            case BOUNDS:
                xMin = Integer.parseInt(args[i + 1]);
                zMin = Integer.parseInt(args[i + 2]);
                width = Integer.parseInt(args[i + 3]);
                height = Integer.parseInt(args[i + 4]);
                System.out.println("Mapping the " + width + " by " + height
                        + " chunk region at x=" + xMin + ", z=" + zMin + ".");
                break;
            case CHUNK_PIXELS:
                chunkPx = Integer.parseInt(args[i + 1]);
                System.out.println("Using " + chunkPx + " pixels per chunk.");
                break;
            case DIRECTORY_FILE:
                dirInfoFile = new File(args[i + 1]);
                System.out.println("Reading the directory of notable locations"
                        + " from \"" + dirInfoFile.getAbsolutePath() + "\"");
                break;
            case DOWNSCALE:
                File downscaleDir = new File(args[i + 1]);
                int downscaleResolution = Integer.parseInt(args[i + 2]);
                ScaleAction action = new ScaleAction();
                action.outputDir = downscaleDir;
                action.resolution = downscaleResolution;
                downscaleList.add(action);
                System.out.println(downscaleResolution + " pixel copies of maps"
                        + " will be created in \"" 
                        + downscaleDir.getAbsolutePath() +"\"");
            }
        }
        if (useTiles)
        {
            int tileSize = Math.min(width, height);
            if (tileResolution > MAX_TILE)
            {
                System.err.println(tileSize + " is too large for map tiles, "
                        + "reducing to maximum tile size " + MAX_TILE);
                tileResolution = MAX_TILE;
            }
            else if (tileResolution < MIN_TILE)
            {
                System.err.println(tileSize + " is too small for map tiles, "
                        + "increasing to minimum tile size " + MIN_TILE);
                tileResolution = MIN_TILE;
            }
            MCMap.createTileMaps(tileResolution, regionDataDir, imageDir,
                    imagePrefix, dirInfoFile);
        }
        if (createMapImages)
        {
            if (useTiles) // mapping already complete, stitch tiles together.
            {
                ArrayList<File> dirList = getTileDirs(imageDir);
                for (File tileDir : dirList)
                {
                    File outFile = new File(imageDir,
                            tileDir.getName() + ".png");
                    System.out.println("Stitching " + tileDir.getName()
                            + " map tiles into a single image.");
                    ImageStitcher.stitch(tileDir, outFile, xMin, zMin, width,
                            height, chunkPx, tileResolution, true);
                    
                }
            }
            else
            {
                MapImage.setDrawBackgrounds(true);
                MCMap.createMaps(xMin, zMin, width, height, chunkPx,
                        regionDataDir, imageDir, imagePrefix, dirInfoFile);
            }
        }
        
        for (ScaleAction action : downscaleList)
        {
            if (action.outputDir != null)
            {
                Downscaler.recursiveScale(imageDir, action.outputDir,
                        action.resolution);
            }
        }
    }
    
    private static ArrayList<File> getTileDirs(File mainImageDir)
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
