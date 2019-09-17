/**
 * @file MapperThread.java
 * 
 * Generates map tiles within its own thread when running as a server plugin.
 */
package com.centuryglass.mcmap.serverplugin;

import com.centuryglass.mcmap.MCMap;
import com.centuryglass.mcmap.images.Downscaler;
import com.centuryglass.mcmap.images.ImageStitcher;
import java.io.File;


public class ServerThread extends Thread
{
    // Default map-making settings.
    // TODO: make these configurable
    private static final String[] DATA_PATHS = 
    {
        "world/region",
        "world_nether/DIM-1/region",
        "world_the_end/DIM1/region"
    };
    private static final String[] TILE_NAMES =
    {
        "Overworld",
        "Nether",
        "End"
    };
    private static final String OUTPUT_PATH = "plugins/ImageMaps/images";
    private static final String TILE_PATH = OUTPUT_PATH + "/tiles";
    private static final int TILE_SIZE = 512;
    private static final int[] SCALED_SIZES = {128, 64, 32};
    
    private static final boolean STITCH_IMAGES = true;
    private static final int STITCHED_XMIN = -1600;
    private static final int STITCHED_ZMIN = -1600;
    private static final int STITCHED_WIDTH = 3200;
    private static final int STITCHED_HEIGHT = 3200;
    
    
    
    @Override
    public void run()
    {
        for (int i = 0; i < DATA_PATHS.length; i++)
        {
            System.out.println("Generating " + TILE_NAMES[i] + " maps:");
            File tileDir = new File(TILE_PATH, TILE_NAMES[i]);
            if (! tileDir.exists())
            {
                tileDir.mkdirs();
            }
            if (! tileDir.isDirectory())
            {
                System.err.println("MCMap: \"" + tileDir.toString()
                        + "\" directory could not be created.");
                continue;
            }
            File regionDir = new File(DATA_PATHS[i]);
            if (! regionDir.isDirectory())
            {
                System.err.println("MCMap: region directory \"" 
                        + DATA_PATHS[i] + "\" not found.");
                continue;
            }
            MCMap.createTileMaps(TILE_SIZE, regionDir, tileDir, TILE_NAMES[i],
                    null);
            final File[] mapTypeDirs = tileDir.listFiles();
            for (File typeDir : mapTypeDirs) {
                if (! typeDir.isDirectory())
                {
                    continue;
                }
                if (STITCH_IMAGES)
                {
                    String filename = TILE_NAMES[i] + "_" + typeDir.getName()
                            + ".png";
                    System.out.println("MCMap: Stitching together map file "
                            + filename);
                    File stitchedImage = new File(OUTPUT_PATH, filename);
                    ImageStitcher.stitch(typeDir, stitchedImage, STITCHED_XMIN,
                            STITCHED_ZMIN, STITCHED_WIDTH, STITCHED_HEIGHT, 1,
                            TILE_SIZE, true);
                }
                System.out.println("MCMap: Creating scaled tilesets for "
                        + DATA_PATHS[i] + "/" + typeDir.getName());
                File baseTileDir = new File(typeDir, "tiles-"
                        + String.valueOf(TILE_SIZE));
                baseTileDir.mkdir();
                File[] tiles = typeDir.listFiles();
                for (File tile : tiles)
                {
                    if (tile.isFile())
                    {
                        tile.renameTo(new File(baseTileDir, tile.getName()));
                    }
                }
                for (int scaledSize : SCALED_SIZES)
                {
                    File scaledDir = new File(typeDir, "tiles-"
                            + String.valueOf(scaledSize));
                    scaledDir.mkdir();
                    try
                    {
                        Downscaler.scaleTiles(baseTileDir, scaledDir,
                                scaledSize);
                    }
                    catch (IllegalArgumentException | SecurityException e)
                    {
                        System.err.println("MCMaps: " + e.getMessage());
                    }
                }
            }
        }
    }
}
