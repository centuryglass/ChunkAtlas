#include "ChunkData.h"


// Saves mandatory chunk data on construction.
ChunkData::ChunkData
(const Point pos, const long inhabitedTime, const long lastUpdate) :
    chunkPos(pos),
    inhabitedTime(inhabitedTime),
    lastUpdate(lastUpdate) { }


// Adds a biome to the list of chunk biome counts.
void ChunkData::addBiome(const Biome biome)
{
    if (biomeCounts.count(biome) == 0)
    {
        biomeCounts[biome] = 1;
    }
    else
    {
        biomeCounts[biome]++;
    }
}


// Adds a structure to the set of chunk structure.
void ChunkData::addStructure(const Structure structure)
{
    structures.insert(structure);
}


// Gets the chunk's position.
Point ChunkData::getPos() const
{
    return chunkPos;
}


// Gets the chunk's inhabited time.
long ChunkData::getInhabitedTime() const
{
    return inhabitedTime;
}


// Get the chunk's last update time.
long ChunkData::getLastUpdate() const
{
    return lastUpdate;
}


// Get the chunk's biome counts.
std::map<Biome, size_t> ChunkData::getBiomeCounts() const
{
    return biomeCounts;
}


// Get the chunk's structure list.
std::set<Structure> ChunkData::getStructures() const
{
    return structures;
}
