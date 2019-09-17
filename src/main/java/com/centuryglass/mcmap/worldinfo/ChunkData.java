/**
 * @file  ChunkData.java
 *
 *  Stores information about a Minecraft map chunk.
 */
package com.centuryglass.mcmap.worldinfo;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ChunkData
{  
    // Lists possible chunk data errors.
    public enum ErrorFlag
    {
        NONE,
        BAD_OFFSET,
        CHUNK_MISSING,
        INVALID_NBT;
    }
    
    /**
     *  Saves mandatory chunk data on construction.
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
        structureRefs = new HashMap();
        structures = new TreeSet();
        errorType = ErrorFlag.NONE;
    }
    
    /**
     * Creates a chunk object representing a broken chunk.
     * 
     * @param pos         The chunk's coordinates.
     * 
     * @param errorType   The type of error encountered when loading chunk data.
     */
    public ChunkData(Point pos, ErrorFlag errorType)
    {
        chunkPos = (Point) pos.clone();
        inhabitedTime = 0;
        lastUpdate = 0;
        biomeCounts = new HashMap();
        structureRefs = new HashMap();
        structures = new TreeSet();
        this.errorType = errorType;
    }
      
    /**
     *  Adds a biome to the list of chunk biome counts.
     *
     * @param biome  A Minecraft biome value.
     */
    public void addBiome(Biome biome)
    {
        if (biome == null)
        {
            System.err.println("ChunkData: tried to add null biome!");
            return;
        }
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
     * Adds a structure to the set of chunk structures.
     *
     * @param structure  A Minecraft generated structure type.
     */
    public void addStructure(Structure structure)
    {
        structures.add(structure);
    }
    
    /**
     * Saves the chunk's reference to a structure found within a nearby chunk.
     * 
     * @param chunkCoords  The coordinates of the chunk where a structure is
     *                     being generated.
     * 
     * @param structure    The type of structure that will be generated. 
     */
    public void addStructureRef(Point chunkCoords, Structure structure)
    {
        // Each chunk can only show one structure type. If multiple structures
        // share a chunk, save the one with the highest priority.
        if (structureRefs.containsKey(chunkCoords))
        {
            Structure oldStruct = structureRefs.get(chunkCoords);
            if (oldStruct.getPriority() >= structure.getPriority())
            {
                return;
            }
        }
        structureRefs.put(chunkCoords, structure);
    }

    /**
     *  Gets the chunk's position.
     *
     * @return  The chunk map coordinates.
     */
    public Point getPos()
    {
        return (Point) chunkPos.clone();
    }
    
    /**
     *  Gets the chunk's inhabited time.
     *
     * @return  The number of ticks that players have spent within the chunk.
     */
    public long getInhabitedTime()
    {
        return inhabitedTime;
    }

    /**
     *  Get the chunk's last update time.
     *
     * @return  A timestamp for the chunk's last update.
     */
    public long getLastUpdate()
    {
        return lastUpdate;
    }

    /**
     *  Get the chunk's biome counts.
     *
     * @return  All biomes in the chunk, mapped to their frequencies.
     */
    public Map<Biome, Integer> getBiomeCounts()
    {
        return biomeCounts;
    }

    /**
     *  Get the chunk's structure list.
     *
     * @return  All structures present within the chunk.
     */
    public Set<Structure> getStructures()
    {
        return structures;
    }
    
    /**
     * Gets this chunk's list of references to nearby structures.
     * 
     * @return  A map associating chunk coordinates with a structure type that
     *          will be generated at that position.
     */
    public Map<Point, Structure> getStructureRefs()
    {
        return structureRefs;
    }
    
    /**
     * Gets any error flag associated with this chunk.
     * 
     * @return  The chunk's error type, or ErrorFlag.NONE if no errors were
     *          found.
     */
    public ErrorFlag getErrorType()
    {
        return errorType;
    }

    private final ErrorFlag errorType;
    private final Point chunkPos;
    private final long inhabitedTime;
    private final long lastUpdate;
    private final Map<Biome, Integer> biomeCounts;
    private final Map<Point, Structure> structureRefs;
    private final Set<Structure> structures;
}
