/**
 * @file  ActivityMapper.java
 *
 * Creates the Minecraft player activity map.
 */

package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.TickDuration;
import com.centuryglass.mcmap.images.ColorRangeFactory;
import com.centuryglass.mcmap.images.ColorRangeSet;
import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * ActivityMapper a map showing the relative amount of time that players have
 * spent in specific Minecraft region chunks.
 */
public class ActivityMapper extends Mapper
{
    private static final double MIN_COLOR_INTENSITY = 0.25;
    
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
     */
    public ActivityMapper(File imageDir, String baseName, int tileSize)
    {
        super(imageDir, baseName, tileSize);
        inhabitedTimes = new HashMap();
    }

    /**
     * Saves the inhabited time of a chunk so it can be drawn later.
     *
     * @param chunk  Data for a new map chunk.
     *
     * @return       Null, as chunk colors are only calculated once the full
     *               range of inhabited times has been found.
     */
    @Override
    public Color getChunkColor(ChunkData chunk)
    {
        if (chunk.getErrorType() != ChunkData.ErrorFlag.NONE)
        {
            return null;
        }
        long inhabitedTime = chunk.getInhabitedTime();
        Point chunkPt = chunk.getPos();
        inhabitedTimes.put(chunkPt, inhabitedTime);
        maxTime = Math.max(inhabitedTime, maxTime);
        return null;
    }

    /**
     * Draws chunk activity data to the map after all chunks have been analyzed.
     *
     * @param map  The map image where activity data will be drawn.
     */
    @Override
    protected void finalProcessing(WorldMap map)
    {
        ArrayList<Color> rangeColors = new ArrayList();
        BiFunction<Color, Double, Color> setMagnitude = (color, fraction) ->
        {
            if (fraction < 0) { fraction = 0.0; }
            else if (fraction > 1) { fraction = 1.0; }
            return new Color((int) (color.getRed() * fraction),
                    (int) (color.getGreen() * fraction),
                    (int) (color.getBlue() * fraction));
                    
        };
        rangeColors.add(Color.YELLOW);
        rangeColors.add(Color.RED);
        rangeColors.add(Color.MAGENTA);
        rangeColors.add(Color.GREEN);
        rangeColors.add(Color.CYAN);
        rangeColors.add(Color.BLUE);
        ColorRangeFactory rangeFactory = new ColorRangeFactory(
                inhabitedTimes.values(), rangeColors);
        rangeFactory.setFadeFraction(MIN_COLOR_INTENSITY);
        rangeFactory.setFadeType(ColorRangeSet.FadeType.TO_BLACK);
        Function <Long, Long> roundToNextUnit = tickCount -> 
        {
            final long[] unitDurations =
            {
                TickDuration.fromYears(1).asTicks,
                TickDuration.fromWeeks(1).asTicks,
                TickDuration.fromDays(1).asTicks,
                TickDuration.fromHours(1).asTicks,
                TickDuration.fromMinutes(1).asTicks,
                TickDuration.fromSeconds(1).asTicks,
                1
            };
            for (long duration : unitDurations)
            {
                if (tickCount == duration) 
                {
                    return duration;
                }
                else if (tickCount > duration)
                {
                    return duration * (tickCount / duration + 1);
                }
            }
            return 0L;
        };
        rangeFactory.setRangeAdjuster(roundToNextUnit);
        ColorRangeSet colorRanges = rangeFactory.createColorRangeSet();
        ColorRangeSet.Range[] rangeDescriptionList = colorRanges.getRanges();
        for (ColorRangeSet.Range range : rangeDescriptionList)
        {
            TickDuration rangeMax = new TickDuration(range.maxValue);
            System.out.println(rangeMax.toString() + " or less: "
                    + range.maxColor.toString());
        }
        for (Map.Entry<Point, Long> entry : inhabitedTimes.entrySet())
        {
            final int x = entry.getKey().x;
            final int z = entry.getKey().y;
            final long mapValue = entry.getValue();
            map.setChunkColor(x, z, colorRanges.getValueColor(mapValue));
        }
        super.finalProcessing(map);
        TickDuration maxDuration = new TickDuration(maxTime);
        System.out.println("The highest inhabited time of all chunks is "
                + maxDuration.toString() + ".");
    }

    // Inhabited times for all map chunks:
    final Map<Point, Long> inhabitedTimes;
    // Longest inhabited time:
    long maxTime;
}
