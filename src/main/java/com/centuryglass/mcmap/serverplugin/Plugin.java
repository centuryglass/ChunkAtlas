/**
 * @file Plugin.java
 * 
 * Allows MCMap to be used as a Spigot server plugin.
 */

package com.centuryglass.mcmap.serverplugin;

import com.centuryglass.mcmap.MCMap;
import com.centuryglass.mcmap.Main;
import com.centuryglass.mcmap.mapping.ImageStitcher;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin provides the interface that Spigot-compatible Minecraft servers use
 * when running MCMap as a server plugin.
 */
public class Plugin extends JavaPlugin
{
    // Default values:
    // TODO: load these from a config file
    private static final String DEFAULT_MAP_DIR = "world/region";
    private static final String DEFAULT_IMAGE_DIR 
            = "plugins/ImageMaps/images";
    private static final String DEFAULT_IMAGE_NAME = "world";
    private static final String DEFAULT_DIR_INFO = "directory.txt";
    
    private static final String DEFAULT_NETHER_DIR
            = "world_nether/DIM-1/region";
    private static final String DEFAULT_NETHER_IMG  = "nether";
    
    private static final String DEFAULT_END_DIR = "world_the_end/DIM1/region";
    private static final String DEFAULT_END_IMG  = "end";
    
    private static final int DEFAULT_X_MIN = -1600;
    private static final int DEFAULT_Z_MIN = -1600;
    private static final int DEFAULT_WIDTH = 3200;
    private static final int DEFAULT_HEIGHT = 3200;
    private static final int DEFAULT_CHUNK_PX = 2;
    private static final int DEFAULT_TILE_SIZE = 512;
    @Override
    public void onEnable()
    {
        System.out.println("MCMap enabled, starting scan.");
        class MapThread extends Thread
        {
            @Override
            public void run()
            {
                final String [] regionNames = {"overworld", "nether", "end"};
                final File [] regionDirs = 
                {
                    new File(DEFAULT_MAP_DIR),
                    new File(DEFAULT_NETHER_DIR),
                    new File(DEFAULT_END_DIR),
                };
                final File [] regionCoords =
                {
                    new File(DEFAULT_DIR_INFO),
                    null,
                    null
                };
                final File imageDir = new File(DEFAULT_IMAGE_DIR);
                final Path cwd = Paths.get("");
                final String absPath = cwd.toAbsolutePath().toString();
                System.out.println("Server is running from " + absPath);
                for (int i = 0; i < regionNames.length; i++)
                {
                    final String name = regionNames[i];
                    final File dataDir = regionDirs[i];
                    final File tileDir = new File(imageDir, name);
                    if (! tileDir.exists())
                    {
                        tileDir.mkdirs();
                    }
                    // TODO: more tileDir error checking
                    System.out.println("Generating " + name + " maps, this may"
                            + " take a while.");
                    MCMap.createTileMaps(DEFAULT_TILE_SIZE, dataDir, tileDir,
                            name, regionCoords[i]);
                    ArrayList<File> tileDirs = Main.getTileDirs(tileDir);
                    for (File dir : tileDirs)
                    {
                        File outfile = new File(imageDir, name + "."
                                + dir.getName() + ".png");
                        ImageStitcher.stitch(dir,
                                outfile,
                                DEFAULT_X_MIN,
                                DEFAULT_Z_MIN,
                                DEFAULT_WIDTH,
                                DEFAULT_HEIGHT,
                                DEFAULT_CHUNK_PX,
                                DEFAULT_TILE_SIZE,
                                true);
                    }      
                }
                System.out.println("Map generation finished.");     
            }
        }
        MapThread asyncThread = new MapThread();
        asyncThread.start();
    }
    
    @Override
    public void onDisable()
    {
        System.out.println("MCMap disabled.");
    }
    
}
