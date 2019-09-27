/**
 * @file TileMap.java
 *
 * Stores map data in a series of images.
 */

package com.centuryglass.mcmap.mapping;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.imageio.ImageIO;

/**
 * Maps all world data within a set of evenly placed image tiles, all sharing
 * the same size.
 * 
 *  This format allows maps to waste much less time and space generating empty
 * image data, at the cost of requiring more image initializations, and
 * requiring post-processing to knit images into a single map.
 */
public class TileMap extends WorldMap
{
    // Usual number of tiles to keep loaded at once:
    private static final int BASE_TILES_IN_MEMORY = 100;
    // Maximum number of tiles to keep loaded:
    private static final int MAX_TILES_IN_MEMORY = 150;
    // TODO: Use bounds based on current memory use instead of file count.
    
    /**
     * Sets initial map data on construction.
     * 
     * @param mapDir    The directory where image tiles will be saved.
     * 
     * @param baseName  The base string to use when naming image files.
     * 
     * @param tileSize  The width and height, in both chunks and pixels, of
     *                  each map tile image.
     */
    public TileMap(File mapDir, String baseName, int tileSize)
    {
        super(mapDir, baseName, 1);
        initTime = System.currentTimeMillis();
        mapTiles = new HashMap();
        recentTiles = new ArrayDeque();
        this.tileSize = tileSize;
        if (! mapDir.isDirectory())
        {
            mapDir.mkdirs();
        }
    }
    
    /**
     * Gets the color applied to a specific chunk.
     *
     * @param xPos  The chunk's x-coordinate.
     *
     * @param zPos  The chunk's z-coordinate.
     *
     * @return      The color value at the given coordinate, or null if the
     *              coordinate is out of bounds.
     */
    @Override
    public Color getChunkColor(int xPos, int zPos)
    {
        Point tilePt = getTilePoint(xPos, zPos);
        if(! mapTiles.containsKey(tilePt))
        {
            return null;
        }
        BufferedImage tile = getTileImage(tilePt);
        int xPixel = (xPos - tilePt.x) * getChunkSize();
        int yPixel = (zPos - tilePt.y) * getChunkSize();
        return new Color(tile.getRGB(xPixel, yPixel));
    }   
    
    /**
     * Sets the color of a specific chunk.
     *
     * @param xPos   The chunk's x-coordinate.
     *
     * @param zPos   The chunk's z-coordinate.
     *
     * @param color  The color value to apply.
     */
    @Override
    public void setChunkColor(int xPos, int zPos, Color color)
    {
        Point tilePt = getTilePoint(xPos, zPos);
        BufferedImage tile = getTileImage(tilePt);
        final int chunkPx = getChunkSize();       
        int xPixel = (xPos - tilePt.x) * chunkPx;
        int yPixel = (zPos - tilePt.y) * chunkPx;
        try
        {
            int numPixels = chunkPx * chunkPx;
            for (int i = 0; i < numPixels; i++)
            {
                tile.setRGB(xPixel + (i % chunkPx), yPixel + (i / chunkPx),
                        color.getRGB());
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            int imageSize = tileSize * getChunkSize();
            System.err.println("Pixel (" + xPixel + ", " + yPixel
                    + "), is out of bounds for an image of size "
                    + imageSize + " x " + imageSize + ".");
            System.err.println("TilePt=" + tilePt.toString() +", chunkPt="
                    + new Point(xPos, zPos).toString());
            System.exit(1);       
        }
    }
    
    /**
     * Saves map data to an image file or files.
     * 
     * @param mapDir    The directory where the map images will be saved.
     * 
     * @param baseName  Base image name to use when saving map files.
     */
    @Override
    protected void saveMapData(File mapDir, String baseName)
    {
        for (Map.Entry<Point, BufferedImage> entry : mapTiles.entrySet())
        {
            if (entry.getValue() == null)
            {
                continue; // Tile has already been offloaded to disk.
            }
            File imageFile = getTileFile(entry.getKey());
            try
            {
                ImageIO.write(entry.getValue(), "png", imageFile);
            }
            catch (IOException e)
            {
                System.err.println("TileMap: Failed to save tile "
                        + imageFile.getName() + " to disk: " + e.getMessage());
            } 
        }
        //System.out.println("Saved " + baseName + " to " + mapTiles.size()
        //        + " image tiles.");
    }
        
    /**
     * Gets the upper left chunk coordinate of a chunk's tile image.
     * 
     * @param xPos      The chunk's x-coordinate.
     * 
     * @param zPos      The chunk's z-coordinate.
     * 
     * @param tileSize  The size in chunks of each map tile.
     * 
     * @return          The corresponding tile coordinate.
     */
    public static Point getTilePoint(int xPos, int zPos, int tileSize)
    {
        int [] offsets = { xPos, zPos };
        for (int i = 0; i < 2; i++)
        {
            offsets[i] = offsets[i] % tileSize;
            if (offsets[i] < 0)
            {
                offsets[i] += tileSize;
            }
        }
        return new Point(xPos - offsets[0], zPos - offsets[1]);
    }
    
    /**
     * Gets the upper left chunk coordinate of a chunk's tile image.
     * 
     * @param xPos  The chunk's x-coordinate.
     * 
     * @param zPos  The chunk's z-coordinate.
     * 
     * @return      The corresponding tile coordinate.
     */
    private Point getTilePoint(int xPos, int zPos)
    {
        return getTilePoint(xPos, zPos, tileSize);
    }
    
    private BufferedImage getTileImage(Point tilePt)
    {
        BufferedImage tileImage = mapTiles.get(tilePt);
        if (tileImage != null)
        {
            return tileImage;
        }
        // Check if tile was created and offloaded to disk:
        File tileFile = getTileFile(tilePt);
        if (tileFile.isFile() && tileFile.lastModified() > initTime)
        {
            try
            {
                tileImage = ImageIO.read(tileFile);
            }
            catch (IOException e)
            {
                System.err.println("Opening tile image "+ tileFile.getName()
                        + " failed, tile data may be lost");
            }
        }
        if (tileImage == null)
        {
            final int imageSize = tileSize * getChunkSize();
            // No existing file found, create new image data:
            tileImage = new BufferedImage(imageSize, imageSize,
                    BufferedImage.TYPE_INT_ARGB);
        }
        mapTiles.put(tilePt, tileImage);
        recentTiles.push(tilePt);
        if (recentTiles.size() > MAX_TILES_IN_MEMORY)
        {
            while (recentTiles.size() > BASE_TILES_IN_MEMORY)
            {
                // Offload oldest tile from memory to disk:
                Point toRemove = recentTiles.removeLast();
                BufferedImage imageToSave = mapTiles.get(toRemove);
                File offloadedTile = getTileFile(toRemove);
                try
                {
                    ImageIO.write(imageToSave, "png", offloadedTile);
                }
                catch (IOException e)
                {
                    System.err.println("TileMap: Failed to save tile "
                            + offloadedTile.getName() + " to disk: "
                            + e.getMessage());
                }
                mapTiles.put(toRemove, null);
            }
        }
        return tileImage;
    }
    
    /**
     * Gets the file used to save a specific map tile.
     * 
     * @param tilePt  The coordinates of a map tile.
     * 
     * @return        The file object where that tile would be saved.
     */
    private File getTileFile(Point tilePt)
    {
        String filename = getFileName() + "." + String.valueOf(tilePt.x)
                + "." + String.valueOf(tilePt.y) + ".png";
        return new File(getMapDir(), filename);
    } 
        
    /**
     * Iterates through each chunk in the map, running a callback for each
     * chunk's coordinates. Empty chunks may or may not be skipped.
     *
     * @param chunkAction  The action to perform for each valid chunk.
     */
    @Override
    protected void foreachChunk(Consumer<Point> chunkAction)
    {
        Point chunkPt = new Point();
        for (Map.Entry<Point, BufferedImage> entry : mapTiles.entrySet())
        {
            final int x0 = entry.getKey().x;
            final int y0 = entry.getKey().y;
            for (chunkPt.y = y0; chunkPt.y < (y0 + tileSize); chunkPt.y++)
            {
                for (chunkPt.x = x0; chunkPt.x < (x0 + tileSize); chunkPt.x++)
                {
                    chunkAction.accept(chunkPt);
                }
            }
        }
    }
    
    // TileMap creation time, used when determining if tiles need to be updated:
    private final long initTime;
    // All initialized map tiles, generated as needed:
    private final Map<Point, BufferedImage> mapTiles;
    // A list of recently modified tile points, used to decide when to load or
    // unload tile data from memory:
    private final Deque<Point> recentTiles;
    // The width and height in chunks/pixels of each map tile:
    private final int tileSize;
}
