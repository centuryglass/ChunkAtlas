/**
 * @file Downscaler.java
 * 
 * Duplicates all map tiles in a directory at a lower resolution.
 */
package com.centuryglass.mcmap.images;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *  Scales down all map tiles in a directory. These low-res copies of map tiles
 * make it easier for map drawing applications to quickly draw all tiles when
 * the map is zoomed out.
 */
public class Downscaler
{
    /**
     * Copies all images in a directory, saving them at a new resolution in a
     * new directory.
     * 
     * @param tileDir                    Map tile source directory.
     * 
     * @param outDir                     Scaled destination directory.
     * 
     * @param newResolution              The width and height in pixels to save
     *                                   all copied files.
     * 
     * @throws IllegalArgumentException  If tileDir or outDir exist as files,
     *                                   if tileDir does not exist, or if
     *                                   newResolution is not a positive number.
     * 
     * @throws SecurityException         If unable to read from the input
     *                                   directory or write to the output
     *                                   directory.
     */
    public static void scaleTiles(File tileDir, File outDir, int newResolution)
            throws IllegalArgumentException, SecurityException
    {
        if (newResolution <= 0)
        {
            throw new IllegalArgumentException("Selected invalid resolution "
                    + String.valueOf(newResolution));
        }
        if (! tileDir.exists())
        {
            throw new IllegalArgumentException("Tile source directory \""
                    + tileDir.getAbsolutePath() + "\" does not exist.");
        }
        if (tileDir.isFile())
        {
            throw new IllegalArgumentException("Looked for tile source"
                    + " directory at \'" + tileDir.getAbsolutePath()
                    + "\" but found a file.");
        }
        if (! tileDir.canRead())
        {
            throw new SecurityException("Tile source directory \""
                    + tileDir.getAbsolutePath() + "\" cannot be read.");
        }
        if (outDir.isFile())
        {
            throw new IllegalArgumentException("Downscaled tile "
                    + " directory path \'" + outDir.getAbsolutePath()
                    + "\" exists as a regular file.");
        }
        if (!outDir.exists())
        {
            outDir.mkdirs();
        }
        File [] mapTiles = tileDir.listFiles();
        for (File tileImage : mapTiles)
        {
            if (! tileImage.isFile() || ! tileImage.getPath().endsWith(".png"))
            {
                continue;
            }
            try
            {
                BufferedImage imageData = ImageIO.read(tileImage);
                BufferedImage scaledData = new BufferedImage(newResolution,
                        newResolution, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = scaledData.createGraphics();
                graphics.drawImage(imageData, 0, 0, newResolution,
                        newResolution, null);
                ImageIO.write(imageData, "png", new File(outDir,
                        tileImage.getName()));
            }
            catch (IOException e)
            {
                System.err.println("Error reading map tile \"" 
                        + tileImage.getName() + "\": " + e.getMessage());
            }
        }     
    }
    
    /**
     * Applies Downscaler.scaleTiles recursively to a set of nested source 
     * directories.
     * 
     * @param tileDir                    Main map tile source directory.
     * 
     * @param outDir                     Main scaled destination directory.
     * 
     * @param newResolution              The width and height in pixels to save
     *                                   all copied files.
     * 
     * @throws IllegalArgumentException  If tileDir or outDir exist as files,
     *                                   if tileDir does not exist, or if
     *                                   newResolution is not a positive number.
     * 
     * @throws SecurityException         If unable to read from the input
     *                                   directory or write to the output
     *                                   directory.
     */
    public static void recursiveScale
    (File tileDir, File outDir, int newResolution)
            throws IllegalArgumentException, SecurityException
    {
        boolean imagesFound = false;
        File [] directoryFiles = tileDir.listFiles();
        for (File file : directoryFiles)
        {
            if (file.isDirectory())
            {
                recursiveScale(file, new File(outDir, file.getName()),
                        newResolution);
            }
            else if (! imagesFound)
            {
                imagesFound = (file.getPath().endsWith(".png"));
            }
        }
        if (imagesFound)
        {
            scaleTiles(tileDir, outDir, newResolution);
        }
    }
}
