/**
 * @file  ActivityMapper.java
 *
 * Creates the Minecraft player activity map.
 */

package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * ActivityMapper a map showing the relative amount of time that players have
 * spent in specific Minecraft region chunks.
 */
public class ActivityMapper extends Mapper
{
    public static final Color MIN_ACTIVITY = Color.BLACK;
    public static final Color HIGH_ACTIVITY = Color.YELLOW;
    public static final Color MAX_ACTIVITY = Color.WHITE;
    public static final Color [] ACTIVITY_LEVEL_COLORS;
    public static long [] ACTIVITY_LEVEL_TIMES;
    static
    {
        ACTIVITY_LEVEL_COLORS = new Color[5];
        ACTIVITY_LEVEL_TIMES = new long[5];
        
        // Time periods, expressed in tick counts:
        final long second = 20;
        final long minute = second * 60;
        final long hour = minute * 60;
        final long day = hour * 24;
        
        ACTIVITY_LEVEL_COLORS[0] = Color.BLUE;
        ACTIVITY_LEVEL_COLORS[1] = Color.CYAN;
        ACTIVITY_LEVEL_COLORS[2] = Color.MAGENTA;
        ACTIVITY_LEVEL_COLORS[3] = Color.RED;
        ACTIVITY_LEVEL_COLORS[4] = Color.ORANGE;
        
        ACTIVITY_LEVEL_TIMES[0] = minute;
        ACTIVITY_LEVEL_TIMES[1] = minute * 10;
        ACTIVITY_LEVEL_TIMES[2] = hour;
        ACTIVITY_LEVEL_TIMES[3] = hour * 5;
        ACTIVITY_LEVEL_TIMES[4] = day;
    }
    
    /**
     * Initializes a mapper that creates a single activity map.
     *
     * @param imageFile       The file where the map image will be saved.
     * 
     * @param xMin            The lowest x-coordinate within the mapped area,
     *                        measured in chunks.
     * 
     * @param zMin            The lowest z-coordinate within the mapped area,
     *                        measured in chunks.
     * 
     * @param widthInChunks   The width of the mapped region in chunks.
     *
     * @param heightInChunks  The height of the mapped image in chunks.
     *
     * @param pixelsPerChunk  The width and height in pixels of each mapped
     *                        chunk.
     */
    public ActivityMapper(File imageFile, int xMin, int zMin, int widthInChunks,
            int heightInChunks, int pixelsPerChunk)
    {
        super(imageFile, xMin, zMin, widthInChunks, heightInChunks,
                pixelsPerChunk);
        inhabitedTimes = new HashMap();
    }
    
    /**
     * Initializes a mapper that creates a set of activity map tiles. 
     * 
     * @param imageDir         The directory where map tiles will be saved.
     * 
     * @param baseName         The base name to use when selecting map image
     *                         names.
     * 
     * @param tileSize         The width and height in chunks of each map tile
     *                         image.
     * 
     * @param pixelsPerChunk   The width and height in pixels of each mapped
     *                         chunk.
     */
    public ActivityMapper(File imageDir, String baseName, int tileSize,
            int pixelsPerChunk)
    {
        super(imageDir, baseName, tileSize, pixelsPerChunk);
        inhabitedTimes = new HashMap();
    }

    /**
     * Saves the inhabited time of a chunk so it can be drawn later.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       The color 
     */
    @Override
    public Color getChunkColor(ChunkData chunk)
    {
        if (chunk.getErrorType() != ChunkData.ErrorFlag.NONE)
        {
            return null;
        }
        Point chunkPt = chunk.getPos();
        long inhabitedTime = chunk.getInhabitedTime();
        maxTime = Math.max(inhabitedTime, maxTime);
        if (inhabitedTime > ACTIVITY_LEVEL_TIMES[
                ACTIVITY_LEVEL_TIMES.length - 1])
        {
            inhabitedTimes.put(chunkPt, inhabitedTime);
        }
        if (inhabitedTime == 0)
        {
            return MIN_ACTIVITY;
        }
        for (int i = 0; i < ACTIVITY_LEVEL_TIMES.length; i++)
        {
            if (inhabitedTime <= ACTIVITY_LEVEL_TIMES[i])
            {
                final Color levelColor = ACTIVITY_LEVEL_COLORS[i];
                final double strengthMult = (double) inhabitedTime 
                        / (double) ACTIVITY_LEVEL_TIMES[i];
                return new Color((int) (levelColor.getRed() * strengthMult),
                        (int) (levelColor.getGreen() * strengthMult),
                        (int) (levelColor.getBlue() * strengthMult), 255);
            }
        }
        return HIGH_ACTIVITY;
    }

    /**
     * Draws chunk activity data to the map after all chunks have been analyzed.
     *
     * @param map  The map image where activity data will be drawn.
     */
    @Override
    protected void finalProcessing(WorldMap map)
    {
        int totalChunks = 0;
        for (Map.Entry<Point, Long> entry : inhabitedTimes.entrySet())
        {
            final int x = entry.getKey().x;
            final int z = entry.getKey().y;
            final long mapValue = entry.getValue();
            final double strengthMult = (double) mapValue / (double) maxTime;
            final int r = (int) (MAX_ACTIVITY.getRed() * strengthMult
                    + HIGH_ACTIVITY.getRed() * (1 - strengthMult));
            final int g = (int) (MAX_ACTIVITY.getGreen() * strengthMult
                    + HIGH_ACTIVITY.getGreen() * (1 - strengthMult));
            final int b = (int) (MAX_ACTIVITY.getBlue() * strengthMult
                    + HIGH_ACTIVITY.getBlue() * (1 - strengthMult));
            map.setChunkColor(x, z, new Color(r, g, b, 255));
        }
        super.finalProcessing(map);
        Duration maxDuration = Duration.ofSeconds(maxTime * 20);
        System.out.println("The highest inhabited time of all chunks is "
                + (maxDuration.toDays() / 365) + " years,"
                + (maxDuration.toDays() % 365) + " days, "
                + (maxDuration.toHours() % 24) + " hours, "
                + (maxDuration.toMinutes() % 60) + " minutes, "
                + (maxDuration.getSeconds() % 60) + " seconds.");
    }

    // Inhabited times for all map chunks:
    final Map<Point, Long> inhabitedTimes;
    // Longest inhabited time:
    long maxTime;
}
