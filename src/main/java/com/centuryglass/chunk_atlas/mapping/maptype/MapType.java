/**
 * @file MapTypes.java
 * 
 * Defines available map types.
 */

package com.centuryglass.chunk_atlas.mapping.maptype;

/**
 * Lists all types of map that may be generated
 */
public enum MapType 
{
    /**
     * Maps total player activity using the ActivityMapper class.
     */
    TOTAL_ACTIVITY,
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
    RECENT_ACTIVITY,
    /**
     * Maps Minecraft structure generation using the StructureMapper class.
     */
    STRUCTURE;
    
    /**
     * Gets the string used to represent a map type.
     * 
     * @return  The type's name, with only the first letter of each word
     *          capitalized and with underscores replaced with spaces.
     */
    @Override
    public String toString()
    {
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(super.toString().toLowerCase());
        for (int i = 0; i < nameBuilder.length(); i++)
        {
            if (nameBuilder.charAt(i) == '_')
            {
                nameBuilder.setCharAt(i, ' ');
            }
            else if (i == 0 || nameBuilder.charAt(i - 1) == ' ')
            {
                nameBuilder.setCharAt(i, Character.toUpperCase(
                        nameBuilder.charAt(i)));
            }
        }
        return nameBuilder.toString();
    }
}
