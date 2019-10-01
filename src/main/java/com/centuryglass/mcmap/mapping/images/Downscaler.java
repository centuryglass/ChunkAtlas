/**
 * @file Downscaler.java
 * 
 * Duplicates all map tiles in a directory at a lower resolution.
 */
package com.centuryglass.mcmap.mapping.images;

import com.centuryglass.mcmap.util.ExtendedValidate;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import org.apache.commons.lang.Validate;

/**
 * Scales down all map tiles in a directory. These low-res copies of map tiles
 * make it easier for map drawing applications to quickly draw all tiles when
 * the map is zoomed out.
 */
public class Downscaler
{
    /**
     * Convert a single tile directory into multiple scaled directories with
     * different sizes.
     * 
     * @param tileDir                    The map tile source directory.
     * 
     * @param initialSize                The initial size of map tiles. Images
     *                                   that don't have this width and height
     *                                   in pixels will not be scaled.
     * 
     * @param newSizes                   The list of new tile resolutions to
     *                                   create.
     */
    public static void scaleTiles(File tileDir, int initialSize, int[] newSizes)
    {
        ExtendedValidate.isDirectory(tileDir, "Tile source directory");
        ExtendedValidate.isPositive(initialSize, "Initial tile size");
        Validate.notNull(newSizes, "Size list cannot be null.");
        Validate.isTrue(newSizes.length > 0,
                "Size list cannot be zero-length.");
        File [] mapTiles = tileDir.listFiles();
        
        // Load images from files, and store them with their file objects:
        class TileImage
        {
            public TileImage(File imageFile)
            {
                this.file = imageFile;
                try
                {
                    image = ImageIO.read(file);
                }
                catch (IOException e)
                {
                    System.err.println("Error reading map tile \"" 
                            + file.getName() + "\": " + e.getMessage());
                    image = null;
                }       
            }
            public File file;
            public BufferedImage image;
        }
        ArrayList<TileImage> tileImages = new ArrayList();
        for (File tile : mapTiles)
        {
            if (! tile.isFile() || ! tile.getPath().endsWith(".png"))
            {
                continue;
            }
            TileImage tileImage = new TileImage(tile);
            if (tileImage.image != null
                    && tileImage.image.getWidth() == initialSize
                    && tileImage.image.getHeight() == initialSize)
            {
                tileImages.add(tileImage);
            }
        }
        if (tileImages.isEmpty()) { return; }
        
        // Ensure source files are in their own tile size directory:
        File parentDir = tileDir;
        if (tileDir.getName().equals(String.valueOf(initialSize)))
        {
            parentDir = tileDir.getParentFile();
        }
        else
        {
            File sourceTileDir = new File(parentDir,
                    String.valueOf(initialSize));
            if (! sourceTileDir.exists())
            {
                sourceTileDir.mkdir();
            }
            tileImages.forEach(tileImage->
            {
                File newPath = new File(sourceTileDir,
                        tileImage.file.getName());
                tileImage.file.renameTo(newPath);
            });
        }
        // Create and populate scaled tile directories:
        for (int size : newSizes)
        {
            ExtendedValidate.isPositive(size, "Alternate tile size");
            File scaledDir = new File(parentDir, String.valueOf(size));
            if (! scaledDir.exists())
            {
                scaledDir.mkdir();
            }
            tileImages.forEach((tileImage) ->
            {
                try
                {
                    BufferedImage scaledData = new BufferedImage(size, size,
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics2D graphics = scaledData.createGraphics();
                    graphics.drawImage(tileImage.image, 0, 0, size,
                            size, null);
                    ImageIO.write(scaledData, "png", new File(scaledDir,
                            tileImage.file.getName()));
                }
                catch (IOException e)
                {
                    System.err.println("Error copying map tile \"" 
                            + tileImage.file.toString() + "\": "
                            + e.getMessage());
                }
            });
        }    
    }
}
