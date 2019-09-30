/**
 * @file RecentMapper.java
 * 
 * Maps which chunks have been updated most recently.
 */

package com.centuryglass.mcmap.mapping.maptype;

import com.centuryglass.mcmap.mapping.Mapper;
import com.centuryglass.mcmap.mapping.WorldMap;
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
    private static final String TYPE_NAME = "recent";
    private static final String DISPLAY_NAME = "Recent Activity Map";
    private static final double MIN_COLOR_INTENSITY = 0.2;
    
    public RecentMapper()
    {
        super();
        updateTimes = new HashMap();
    }
    
    /**
     * Gets the base Mapper type name used when naming image files.
     * 
     * @return  An appropriate type name for use in naming image files.
     */
    @Override
    public String getTypeName()
    {
        return TYPE_NAME;
    }
    
    /**
     * Gets the Mapper display name used to identify the mapper's maps to users.
     * 
     * @return  The MapType's display name. 
     */
    @Override
    public String getDisplayName()
    {
        return DISPLAY_NAME;
    }
    
    /**
     * Gets the type of map a mapper creates.
     *
     * @return  The Mapper's MapType.
     */
    @Override
    public MapType getMapType()
    {
        return MapType.RECENT;
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
