/**
 * @file  ChunkData.h
 *
 * @brief  Stores information about a Minecraft map chunk.
 */
package com.centuryglass.mcmap.worldinfo;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ChunkData
{
    /**
     * @brief  Saves mandatory chunk data on construction.
     *
     * @param pos         The chunk's coordinates.
     *
     * @param inhabited   Number of ticks that the chunk has been inhabited by
     *                    players.
     *
     * @param updateTime  Timestamp of the chunk's last update.
     */
    public ChunkData(Point pos, long inhabited, long updateTime)
    {
        chunkPos = (Point) pos.clone();
        inhabitedTime = inhabited;
        lastUpdate = updateTime;
        biomeCounts = new HashMap();
        structures = new TreeSet();
    }
    
    /**
     * @brief  Adds a biome to the list of chunk biome counts.
     *
     * @param biome  A Minecraft biome value.
     */
    public void addBiome(Biome biome)
    {
        Integer currentCount = biomeCounts.get(biome);
        if (currentCount == null)
        {
            biomeCounts.put(biome, 0);
        }
        else
        {
            biomeCounts.put(biome, currentCount + 1);
        }
    }

    /**
     * @brief  Adds a structure to the set of chunk structure.
     *
     * @param structure  A Minecraft generated structure type.
     */
    public void addStructure(Structure structure)
    {
        structures.add(structure);
    }

    /**
     * @brief  Gets the chunk's position.
     *
     * @return  The chunk map coordinates.
     */
    public Point getPos()
    {
        return (Point) chunkPos.clone();
    }
    
    /**
     * @brief  Gets the chunk's inhabited time.
     *
     * @return  The number of ticks that players have spent within the chunk.
     */
    public long getInhabitedTime()
    {
        return inhabitedTime;
    }

    /**
     * @brief  Get the chunk's last update time.
     *
     * @return  A timestamp for the chunk's last update.
     */
    public long getLastUpdate()
    {
        return lastUpdate;
    }

    /**
     * @brief  Get the chunk's biome counts.
     *
     * @return  All biomes in the chunk, mapped to their frequencies.
     */
    public Map<Biome, Integer> getBiomeCounts()
    {
        return biomeCounts;
    }

    /**
     * @brief  Get the chunk's structure list.
     *
     * @return  All structures present within the chunk.
     */
    public Set<Structure> getStructures()
    {
        return structures;
    }

    private final Point chunkPos;
    private final long inhabitedTime;
    private final long lastUpdate;
    private final Map<Biome, Integer> biomeCounts;
    private final Set<Structure> structures;  
}
