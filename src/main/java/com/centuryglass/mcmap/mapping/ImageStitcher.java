/**
 * @file  ImageStitcher.java
 * 
 * Stitches a set of image tiles together into a single map image, optionally
 * with a Minecraft map item background and border.
 */
package com.centuryglass.mcmap.mapping;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class ImageStitcher
{    
    // TODO: find a good way to share this 
    private static final int BORDER_DIVISOR = 19;
    private static final String BACKGROUND_PATH = "/emptyMap.png";
    
    private static Point getTilePos(File tile)
    {
        final String name = tile.getName();
        final String numericChars = "-0123456789";
        int xStart = -1;
        for (int i = 0; i < name.length(); i++)
        {
            if (numericChars.contains(name.substring(i, i + 1)))
            {
                xStart = i;
                break;
            }
        }
        if (xStart < 0)
        {
            return null;
        }
        final int xEnd = name.indexOf(".", xStart);
        final int zStart = xEnd + 1;
        final int zEnd = name.indexOf(".", zStart);
        if (xEnd < 0 || zEnd < 0)
        {
            return null;
        }
        return new Point(Integer.parseInt(name.substring(xStart, xEnd)),
                Integer.parseInt(name.substring(zStart, zEnd)));
    }
    
    public static void stitch(File tileDir,
            File outFile,
            int xMin,
            int zMin,
            int width,
            int height,
            int pixelsPerChunk,
            int tileSize,
            boolean drawBackground)
    {
        File [] possibleTiles = tileDir.listFiles();
        Map <File, Point> tilePoints = new HashMap();
        for (File tile : possibleTiles)
        {
            if (tile.isFile() && tile.getPath().endsWith(".png"))
            {
                Point tilePt = getTilePos(tile);
                if (tilePt != null)
                {
                    tilePoints.put(tile, tilePt);
                }
            }
        }
        if (tilePoints.isEmpty())
        {
            return; // No valid files don't waste time on a blank image.
        }
        
        BufferedImage combinedMap = new BufferedImage(width * pixelsPerChunk,
                height * pixelsPerChunk, BufferedImage.TYPE_INT_ARGB);
        final int xMax = xMin + width;
        final int zMax = zMin + height;
        final int tilePixels = tileSize * pixelsPerChunk;
        Graphics2D mapPainter = combinedMap.createGraphics();
        for (Map.Entry<File, Point> entry : tilePoints.entrySet())
        {
            File tile = entry.getKey();
            Point tilePt = entry.getValue();
            if (tilePt == null)
            {
                continue;
            }
            final int tileXMax = tilePt.x + tileSize;
            final int tileZMax = tilePt.y + tileSize;
            if (tilePt.x >= xMax || tilePt.y >= zMax || tileXMax < xMin 
                    || tileZMax < zMin)
            {
                continue;
            }
            BufferedImage tileImage;
            try
            {
                tileImage = ImageIO.read(tile);
            }
            catch (IOException e)
            {
                System.err.println("Error opening tile " + tile.getName() + ": "
                        + e.getMessage());
                continue;
            }
            mapPainter.drawImage(tileImage, tilePt.x - xMin, tilePt.y - zMin,
                    tilePixels, tilePixels, null);
        }
        if (drawBackground)
        {
            final int largerEdge = Math.max(combinedMap.getWidth(),
                    combinedMap.getHeight());
            final int borderWidth = MapBackground.getBorderWidth(largerEdge);
            final int frameSize = largerEdge + 2 * borderWidth;
            BufferedImage imageFrame = new BufferedImage(frameSize, frameSize,
                    BufferedImage.TYPE_INT_ARGB);
            BufferedImage background = MapBackground.getBackgroundImage();
            Graphics2D framePainter = imageFrame.createGraphics();
            framePainter.drawImage(background, 0, 0, frameSize, frameSize,
                    null);
            framePainter.drawImage(combinedMap,
                    (frameSize - combinedMap.getWidth()) / 2,
                    (frameSize - combinedMap.getHeight()) / 2,
                    combinedMap.getWidth(),
                    combinedMap.getHeight(),
                    null);
            combinedMap = imageFrame;
        }
        try
        {
            ImageIO.write(combinedMap, "png", outFile);
        }
        catch (IOException e)
        {
            System.err.println("Failed to save composite map image to \""
                    + outFile.getPath() + "\"");
        }  
    }
}
