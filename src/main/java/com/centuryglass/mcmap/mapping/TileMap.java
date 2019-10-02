/**
 * @file TileMap.java
 *
 * Stores map data in a series of images.
 */

package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.util.ExtendedValidate;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import org.apache.commons.lang.Validate;

/**
 * Maps all world data within a set of evenly placed image tiles, all sharing
 * the same size.
 * 
 * This format allows maps to waste much less time and space generating empty
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
     * @param mapDir          The directory where image tiles will be saved.
     * 
     * @param baseName        The base string to use when naming image files.
     * 
     * @param tileSize        The width and height in chunks of each map tile
     *                        image.
     * 
     * @param altSizes        An optional list of alternate tile sizes to
     *                        create.
     * 
     * @param pixelsPerChunk  The width and height in pixels of each mapped
     *                        chunk.
     */
    public TileMap(File mapDir, String baseName, int tileSize, int[] altSizes,
            int pixelsPerChunk)
    {
        super(mapDir, baseName, pixelsPerChunk);
        ExtendedValidate.couldBeDirectory(mapDir, "Tile output directory");
        ExtendedValidate.notNullOrEmpty(baseName, "Base tile name");
        ExtendedValidate.isPositive(tileSize, "Tile size");
        initTime = System.currentTimeMillis();
        mapTiles = new HashMap();
        recentTiles = new ArrayDeque();
        this.tileSize = tileSize;
        this.altSizes = altSizes;
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
        TilePixelData chunkData = getChunkPixelData(xPos, zPos);
        return new Color(chunkData.tile.getRGB(chunkData.pixelCoords.x,
                chunkData.pixelCoords.y));
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
        Validate.notNull(color, "Color cannot be null.");
        TilePixelData chunkData = getChunkPixelData(xPos, zPos);
        chunkData.tile.setRGB(chunkData.pixelCoords.x, chunkData.pixelCoords.y,
                color.getRGB());
    }
    
    /**
     * All data needed to get or set a specific pixel within a tile image.
     */
    private class TilePixelData
    {
        protected final BufferedImage tile;
        protected final Point pixelCoords;
        protected TilePixelData(BufferedImage tile, Point pixelCoords)
        {
            Validate.notNull(tile, "Tile image cannot be null.");
            Validate.notNull(pixelCoords, "Pixel coordinates cannot be null.");
            ExtendedValidate.inInclusiveBounds(pixelCoords.x, 0,
                    tile.getWidth() - 1, "Pixel x-coordinate");
            ExtendedValidate.inInclusiveBounds(pixelCoords.y, 0,
                    tile.getHeight() - 1, "Pixel y-coordinate");
            this.tile = tile;
            this.pixelCoords = pixelCoords;
        }
    }
    
    /**
     * Gets all data needed to get or set the upper-left image pixel mapped to
     * a specific Minecraft chunk.
     * 
     * @param xPos          The chunk's x-coordinate.
     * 
     * @param zPos          The chunk's z-coordinate.
     * 
     * @return              TilePixelData holding the requested chunk's image
     *                      object and image pixel coordinates.
     */
    private TilePixelData getChunkPixelData(int xPos, int zPos)
    {
        final int chunkSize = getChunkSize();
        final Point tilePt = getTilePoint(xPos, zPos);
        return new TilePixelData(getTileImage(tilePt), new Point(
                (xPos - tilePt.x) * chunkSize,
                (zPos - tilePt.y) * chunkSize));
    }
    
    /**
     * Gets all data needed to get or set a pixel at a specific offset from a
     * Minecraft map chunk.
     * 
     * @param xPos          The chunk's x-coordinate.
     * 
     * @param zPos          The chunk's z-coordinate.
     * 
     * @param xPixelOffset  The x-offset in pixels from the chunk's image 
     *                      coordinate.
     * 
     * @param yPixelOffset  The y-offset in pixels from the chunk's image
     *                      coordinate.
     * 
     * @return              TilePixelData holding the requested pixel's image
     *                      object and image pixel coordinates.
     */
    private TilePixelData getChunkOffsetData(int xPos, int zPos,
            int xPixelOffset, int yPixelOffset)
    {
        final int chunkSize = getChunkSize();
        final int tilePxSize = chunkSize * tileSize;
        final Point tilePt = getTilePoint(xPos, zPos);   
        // If offsets push the pixel coordinates outside of tile bounds, adjust
        // coordinates to find the tile that actually contains the selected
        // pixel.
        class OffsetFinder
        {
            public OffsetFinder
            (int chunkCoord, int tileCoord, int pixelOffset)
            {
                int px = (chunkCoord - tileCoord) * chunkSize + pixelOffset;
                tileChunkOffset = tileSize
                        * (px / tilePxSize - (px < 0 ? 1 : 0));
                pixelCoord = px - tileChunkOffset * chunkSize;
            }
            public final int pixelCoord;
            public final int tileChunkOffset;
        }
        OffsetFinder xOffset = new OffsetFinder(xPos, tilePt.x, xPixelOffset);
        OffsetFinder yOffset = new OffsetFinder(zPos, tilePt.y, yPixelOffset);
        tilePt.translate(xOffset.tileChunkOffset, yOffset.tileChunkOffset);
        return new TilePixelData(getTileImage(tilePt),
                new Point(xOffset.pixelCoord, yOffset.pixelCoord));  
    }
    
    /**
     * Gets the map color near a chunk coordinate.
     * 
     * @param xPos          The chunk's x-coordinate.
     * 
     * @param zPos          The chunk's z-coordinate.
     * 
     * @param xPixelOffset  The x-offset in pixels from the chunk's image 
     *                      coordinate.
     * 
     * @param yPixelOffset  The y-offset in pixels from the chunk's image
     *                      coordinate.
     * 
     * @return              The color of the pixel with the given offset from
     *                      the chunk coordinate, or null if the requested pixel
     *                      is outside of the map bounds.
     */
    @Override
    protected Color getChunkOffsetColor(int xPos, int zPos, int xPixelOffset,
            int yPixelOffset)
    { 
        TilePixelData offsetData = getChunkOffsetData(xPos, zPos, xPixelOffset,
                yPixelOffset);
        return new Color(offsetData.tile.getRGB(
                offsetData.pixelCoords.x, offsetData.pixelCoords.y));
    }
    
    /**
     * Sets the map color near a chunk coordinate.
     * 
     * @param xPos          The chunk's x-coordinate.
     * 
     * @param zPos          The chunk's z-coordinate.
     * 
     * @param xPixelOffset  The x-offset in pixels from the chunk's image 
     *                      coordinate.
     * 
     * @param yPixelOffset  The y-offset in pixels from the chunk's image
     *                      coordinate.
     * 
     * @param color         The color to apply to the selected pixel, if not
     *                      outside of the map bounds.
     */
    @Override
    protected void setChunkOffsetColor(int xPos, int zPos,
            int xPixelOffset, int yPixelOffset, Color color)
    {    
        TilePixelData offsetData = getChunkOffsetData(xPos, zPos, xPixelOffset,
                yPixelOffset);
        offsetData.tile.setRGB(offsetData.pixelCoords.x,
                offsetData.pixelCoords.y, color.getRGB());
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
        ExtendedValidate.couldBeDirectory(mapDir, "Map tile output directory");
        Validate.notNull(baseName, "Base tile image name cannot be null.");
        Validate.notEmpty(baseName, "Base image name cannot be empty.");
        if (! mapDir.exists())
        {
            Validate.isTrue(mapDir.mkdirs(),
                    "Couldn't create map tile output directory");
        }
        while (! mapTiles.isEmpty())
        {
            Point tilePt = mapTiles.keySet().iterator().next();
            saveTileToDisk(tilePt);
        }
    }
    
    /**
     * Saves a buffered tile image to the disk, creates scaled images, and
     * removes the buffered image from memory.
     * 
     * @param tilePt  The coordinates of a buffered tile image. If no such image
     *                exists, files will not be saved.
     */
    private void saveTileToDisk(Point tilePt)
    {
        Validate.notNull(tilePt, "Tile point cannot be null.");
        BufferedImage tileImage = mapTiles.get(tilePt);
        mapTiles.remove(tilePt);
        if (tileImage == null)
        {
            return;
        }
        File imageFile = getTileFile(tilePt);
        try
        {
            ImageIO.write(tileImage, "png", imageFile);
            // Also create alternate tile sizes:
            for (int size : altSizes)
            {
                File resizedDir = getTileSizeDir(size);
                BufferedImage resizedImage = new BufferedImage(size,
                        size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = resizedImage.createGraphics();
                graphics.drawImage(tileImage, 0, 0, size, size, null);
                ImageIO.write(resizedImage, "png", new File(resizedDir,
                        imageFile.getName()));
            }
        }
        catch (IOException e)
        {
            System.err.println("TileMap: Failed to save tile "
                    + imageFile.getName() + " to disk: " + e.getMessage());
        } 
        
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
        ExtendedValidate.isPositive(tileSize, "Tile size");
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
    
    /**
     * Gets the image where a specific map chunk is saved, creating or loading
     * a new image if necessary.
     * 
     * @param tilePt  A Minecraft chunk coordinate.
     * 
     * @return        An image where the given point should be drawn.
     */
    private BufferedImage getTileImage(Point tilePt)
    {
        Validate.notNull(tilePt, "Chunk coordinate cannot be null.");
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
                saveTileToDisk(toRemove);
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
        Validate.notNull(tilePt, "Chunk coordinate cannot be null.");
        String filename = getFileName() + "." + String.valueOf(tilePt.x)
                + "." + String.valueOf(tilePt.y) + ".png";
        return new File(getTileSizeDir(tileSize), filename);
    }
    
    /**
     * Get the directory used to store a specific tile size within the main map
     * tile directory. If it doesn't already exist, this will create the new
     * directory.
     * 
     * @param tileSize  A valid tile output size.
     * 
     * @return          The directory for that tile size. 
     */
    private File getTileSizeDir(int tileSize)
    {
        File tileSizeDir = new File(getMapDir(), String.valueOf(tileSize));
        ExtendedValidate.couldBeDirectory(tileSizeDir, "Tile size directory");
        if (! tileSizeDir.exists())
        {
            Validate.isTrue(tileSizeDir.mkdirs(), "Failed to create tile "
                    + "size directory \"" + tileSizeDir.toString() + "\".");
        }
        return tileSizeDir;
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
        Validate.notNull(chunkAction, "Chunk action cannot be null.");
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
    
    /**
     * Gets the list of all files used to hold map data.
     * 
     * @return  The list of map image files. 
     */
    @Override
    protected ArrayList<File> getMapFiles()
    {
        final ArrayList<File> files = new ArrayList();
        Deque<File> mapDirs = new ArrayDeque();
        mapDirs.push(getTileSizeDir(tileSize));
        for (int size : altSizes)
        {
            mapDirs.push(getTileSizeDir(size));
        }
        while (! mapDirs.isEmpty())
        {
            File mapDir = mapDirs.pop();
            File[] childFiles = mapDir.listFiles();
            for (File child : childFiles)
            {
                if (child.isFile())
                {
                    files.add(child);
                }
            }
        }
        return files;
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
    // Optional alternate tile sizes:
    private final int[] altSizes;
}
