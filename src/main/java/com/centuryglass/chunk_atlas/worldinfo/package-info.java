/**
 * com.centuryglass.chunk_atlas.worldinfo provides data classes used to store
 * Minecraft map properties.
 * 
 *  The worldinfo package is primarily used through ChunkData objects, provided
 * by savedata.MCAFile objects. These objects store the Minecraft biome types
 * and structure types within a 16x16 map chunk, along with the last time the
 * chunk was updated, and the amount of time that players have spent within
 * that chunk.
 * 
 *  worldinfo also provides Biome and Structure enums listing all types of
 * world biome or generated structure that can exist in a chunk. These classes
 * also provide convenience methods for loading name strings and default color
 * values for each item they enumerate.
 */
package com.centuryglass.chunk_atlas.worldinfo;
