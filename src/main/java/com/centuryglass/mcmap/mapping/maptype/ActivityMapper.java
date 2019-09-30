/**
 * @file  ActivityMapper.java
 *
 * Creates the Minecraft player activity map.
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
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * ActivityMapper a map showing the relative amount of time that players have
 * spent in specific Minecraft region chunks.
 */
public class ActivityMapper extends Mapper
{
    private static final double MIN_COLOR_INTENSITY = 0.25;
    private static final String TYPE_NAME = "activity";
    private static final String DISPLAY_NAME = "Total Activity Map";
    
    public ActivityMapper()
    {
        super();
        inhabitedTimes = new HashMap();
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
        return MapType.ACTIVITY;
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
        rangeFactory.setFadeType(ColorRangeSet.FadeType.TO_NEXT);
        Function <Long, Long> roundToNextUnit = tickCount -> 
        {
            return new TickDuration(tickCount).rounded(
                    TickDuration.Rounding.UP).asTicks;
        };
        rangeFactory.setRangeAdjuster(roundToNextUnit);
        ColorRangeSet colorRanges = rangeFactory.createColorRangeSet();
        // Debug: print inhabited time ranges:
        /*
        ColorRangeSet.Range[] rangeDescriptionList = colorRanges.getRanges();
        for (ColorRangeSet.Range range : rangeDescriptionList)
        {
            TickDuration rangeMax = new TickDuration(range.maxValue);
            System.out.println(rangeMax.toString() + " or less: "
                    + range.maxColor.toString());
        }
        */
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
