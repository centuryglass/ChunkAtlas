/**
 * @file MapTypes.java
 * 
 * Defines available map types.
 */

package com.centuryglass.mcmap.mapping.maptype;

/**
 * Lists all types of map that may be generated
 */
public enum MapType 
{
    /**
     * Maps total player activity using the ActivityMapper class.
     */
    ACTIVITY,
    /**
     * Maps generated chunks using the BasicMapper class.
     */
    BASIC,
    /**
     * Maps Minecraft biomes using the BiomeMapper class.
     */
    BIOME,
    /**
     * Maps chunk errors using the ErrorMapper class.
     */
    ERROR,
    /**
     * Maps recent chunk updates using the RecentMapper class.
     */
    RECENT,
    /**
     * Maps Minecraft structure generation using the StructureMapper class.
     */
    STRUCTURE;
}
