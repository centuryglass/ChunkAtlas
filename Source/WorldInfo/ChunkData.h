/**
 * @file  ChunkData.h
 *
 * @brief  Stores information about a minecraft map chunk.
 */

#pragma once
#include "Point.h"
#include "Biome.h"
#include "Structure.h"
#include <set>
#include <map>

class ChunkData
{
public:
    /**
     * @brief  Saves mandatory chunk data on construction.
     *
     * @param pos            The chunk's coordinates.
     *
     * @param inhabitedTime  Number of ticks that the chunk has been inhabited
     *                       by players.
     *
     * @param lastUpdate     Timestamp of the chunk's last update.
     */
    ChunkData
    (const Point pos, const long inhabitedTime, const long lastUpdate);

    virtual ~ChunkData() { }

    /**
     * @brief  Adds a biome to the list of chunk biome counts.
     *
     * @param biome  A minecraft biome value.
     */
    void addBiome(const Biome biome);

    /**
     * @brief  Adds a structure to the set of chunk structure.
     *
     * @param structure  A minecraft generated structure type.
     */
    void addStructure(const Structure structure);

    /**
     * @brief  Gets the chunk's position.
     *
     * @return  The chunk map coordinates.
     */
    Point getPos() const;

    /**
     * @brief  Gets the chunk's inhabited time.
     *
     * @return  The number of ticks that players have spent within the chunk.
     */
    long getInhabitedTime() const;

    /**
     * @brief  Get the chunk's last update time.
     *
     * @return  A timestamp for the chunk's last update.
     */
    long getLastUpdate() const;

    /**
     * @brief  Get the chunk's biome counts.
     *
     * @return  All biomes in the chunk, mapped to their frequencies.
     */
    std::map<Biome, size_t> getBiomeCounts() const;

    /**
     * @brief  Get the chunk's structure list.
     *
     * @return  All structures present within the chunk.
     */
    std::set<Structure> getStructures() const;

private:
    Point chunkPos;
    long inhabitedTime;
    long lastUpdate;
    std::map<Biome, size_t> biomeCounts;
    std::set<Structure> structures;
};
