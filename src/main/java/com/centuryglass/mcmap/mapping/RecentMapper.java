/**
 * @file RecentMapper.java
 * 
 * Maps which chunks have been updated most recently.
 */

package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.util.TickDuration;
import com.centuryglass.mcmap.mapping.images.ColorRangeFactory;
import com.centuryglass.mcmap.mapping.images.ColorRangeSet;
import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RecentMapper extends Mapper
{
    private static final double MIN_COLOR_INTENSITY = 0.2;
    
    static
    {
    }
    
    /**
     * Initializes a mapper that creates a single recent activity map.
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
    public RecentMapper(File imageFile, int xMin, int zMin, int widthInChunks,
            int heightInChunks, int pixelsPerChunk)
    {
        super(imageFile, xMin, zMin, widthInChunks, heightInChunks,
                pixelsPerChunk);
        updateTimes = new HashMap();
    }
    
    /**
     * Initializes a mapper that creates a set of recent activity map tiles. 
     * 
     * @param imageDir         The directory where map tiles will be saved.
     * 
     * @param baseName         The base name to use when selecting map image
     *                         names.
     * 
     * @param tileSize         The width and height in chunks of each map tile
     *                         image.
     */
    public RecentMapper(File imageDir, String baseName, int tileSize)
    {
        super(imageDir, baseName, tileSize);
        updateTimes = new HashMap();
    }
    
    @Override
    public Color getChunkColor(ChunkData chunk)
    {
        long lastUpdate = chunk.getLastUpdate();
        if (lastUpdate == 0)
        {
            return null;
        }
        updateTimes.put(chunk.getPos(), lastUpdate);
        if (lastUpdate < earliestTime)
        {
            earliestTime = lastUpdate;
        }
        if (lastUpdate > latestTime)
        {
            latestTime = lastUpdate;
        }
        return new Color(0);
    }
    
    @Override
    protected void finalProcessing(WorldMap map)
    {
        // Initialize color ranges:
        final ArrayList<Color> updateTimeColors = new ArrayList();     
        updateTimeColors.add(Color.YELLOW);
        updateTimeColors.add(Color.RED);
        updateTimeColors.add(Color.MAGENTA);
        updateTimeColors.add(Color.GREEN);
        updateTimeColors.add(Color.CYAN);
        updateTimeColors.add(Color.BLUE);
        ColorRangeFactory rangeFactory = new ColorRangeFactory(
                updateTimes.values(), updateTimeColors);
        rangeFactory.setFadeFraction(MIN_COLOR_INTENSITY);
        rangeFactory.setFadeType(ColorRangeSet.FadeType.TO_NEXT);
        Function<Long, Long> roundOffsetFromLatest = updateTime ->
        {
            TickDuration offset = new TickDuration(latestTime - updateTime);
            TickDuration roundedOffset = offset.rounded(
                    TickDuration.Rounding.DOWN);
            return latestTime - roundedOffset.asTicks;
        };
        rangeFactory.setRangeAdjuster(roundOffsetFromLatest);
        ColorRangeSet colorRanges = rangeFactory.createColorRangeSet();
        for (Map.Entry<Point, Long> entry : updateTimes.entrySet())
        {
            map.setChunkColor(entry.getKey().x, entry.getKey().y,
                    colorRanges.getValueColor(entry.getValue()));
        }
        
        TickDuration max = new TickDuration(latestTime);
        TickDuration difference = new TickDuration(latestTime - earliestTime);
        System.out.println("Latest update time: " + max.toString());
        System.out.println("Update time range: " + difference.toString());
        
        // Debug: print update time ranges:
        /*
        ColorRangeSet.Range[] ranges = colorRanges.getRanges();
        for (ColorRangeSet.Range range : ranges)
        {
            TickDuration offset = new TickDuration(latestTime - range.maxValue);
            System.out.println(offset.toString() + " ago: "
                    + range.maxColor.toString());
        }
        */
    }
    
    private long earliestTime = Long.MAX_VALUE;
    private long latestTime = Long.MIN_VALUE;
    final Map<Point, Long> updateTimes;
}
