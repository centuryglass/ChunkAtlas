/**
 * @file RecentMapper.java
 * 
 * Maps which chunks have been updated most recently.
 */

package com.centuryglass.chunk_atlas.mapping.maptype;

import com.centuryglass.chunk_atlas.config.LogConfig;
import com.centuryglass.chunk_atlas.mapping.KeyItem;
import com.centuryglass.chunk_atlas.mapping.WorldMap;
import com.centuryglass.chunk_atlas.util.TickDuration;
import com.centuryglass.chunk_atlas.mapping.images.ColorRangeFactory;
import com.centuryglass.chunk_atlas.mapping.images.ColorRangeSet;
import com.centuryglass.chunk_atlas.worldinfo.ChunkData;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.World;

public class RecentMapper extends Mapper
{
    private static final String CLASSNAME = RecentMapper.class.getName();
    
    private static final double MIN_COLOR_INTENSITY = 0.2;
    
    /**
     * Sets the mapper's base output directory and mapped region name on
     * construction.
     *
     * @param imageDir    The directory where the map image will be saved.
     * 
     * @param regionName  The name of the region this Mapper is mapping.
     * 
     * @param region      An optional bukkit World object, used to load extra
     *                    map data if non-null.
     */
    public RecentMapper(File imageDir, String regionName, World region)
    {
        super(imageDir, regionName, region);
        updateTimes = new HashMap<>();
        key = new LinkedHashSet<>();
    }
    
    /**
     * Gets the type of map a mapper creates.
     *
     * @return  The Mapper's MapType.
     */
    @Override
    public MapType getMapType()
    {
        return MapType.RECENT_ACTIVITY;
    }
    
    /**
     * Gets all items in this mapper's map key.
     * 
     * @return  All KeyItems for this map type and region. 
     */
    @Override
    public Set<KeyItem> getMapKey()
    {
        return key;
    }
    
    /**
     * Saves the chunk's last update time, so it can be mapped later.
     * 
     * @param chunk  The Minecraft chunk data object.
     * 
     * @return       Null, as chunk colors cannot be set until the full range
     *               of update times has been found.
     */
    @Override
    public Color getChunkColor(ChunkData chunk)
    {
        Validate.notNull(chunk, "Chunk cannot be null.");
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
    
    /**
     * Calculates color ranges from the full set of update times, and applies
     * them to draw the map.
     * 
     * @param map  The map this mapper is creating. 
     */
    @Override
    protected void finalProcessing(WorldMap map)
    {
        // Initialize color ranges:
        final ArrayList<Color> updateTimeColors = new ArrayList<>();     
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
        LogConfig.getLogger().log(Level.INFO, "Latest update time: {0}", max);
        LogConfig.getLogger().log(Level.INFO, "Update time range: {0}",
                difference);
        
        // Initialize map key from ranges:
        if (key.isEmpty())
        {
            ColorRangeSet.Range[] rangeDescriptionList
                    = colorRanges.getRanges();
            for (ColorRangeSet.Range range : rangeDescriptionList)
            {
                TickDuration offset = new TickDuration(latestTime
                        - range.maxValue);
                String description = offset.toString() + " ago or more";
                key.add(new KeyItem(description, getMapType(), getRegionName(),
                        range.maxColor));
            }
        }
    }
    
    private long earliestTime = Long.MAX_VALUE;
    private long latestTime = Long.MIN_VALUE;
    private final Map<Point, Long> updateTimes;
    private final Set<KeyItem> key;
}
