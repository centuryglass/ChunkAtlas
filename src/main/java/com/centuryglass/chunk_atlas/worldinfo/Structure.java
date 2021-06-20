/**
 * @file  Structure.java
 *
 *  Enumerates Minecraft structure types.
 */

package com.centuryglass.chunk_atlas.worldinfo;

import com.centuryglass.chunk_atlas.config.LogConfig;
import java.awt.Color;
import java.util.logging.Level;
import org.bukkit.StructureType;


// Structure values are assigned so that smaller structure types have higher
// values than larger structure types.
public enum Structure
{
    MONUMENT (4, "monument", StructureType.OCEAN_MONUMENT,
            0x00, 0xe2, 0xaa),
    MANSION (5, "mansion", StructureType.WOODLAND_MANSION,
            0xae, 0x5c, 0x28),
    SWAMP_HUT (10, "swamp_hut", StructureType.SWAMP_HUT,
            0x05, 0x4c, 0x3e),
    MINESHAFT (0, "mineshaft", StructureType.MINESHAFT,
            0x85, 0x06, 0x03),
    IGLOO (11, "igloo", StructureType.IGLOO,
            0x0d, 0xe0, 0xec),
    STRONGHOLD (6, "stronghold", StructureType.STRONGHOLD,
            0xa3, 0x0c, 0xcc),
    DESERT_PYRAMID (7, "desert_pyramid", StructureType.DESERT_PYRAMID,
            0xd0, 0xff, 0x00),
    JUNGLE_PYRAMID (8, "jungle_pyramid", StructureType.JUNGLE_PYRAMID,
            0x51, 0x5f, 0x49),
    PILLAGER_OUTPOST (9, "pillager_outpost", StructureType.PILLAGER_OUTPOST,
            0x9d, 0x97, 0x09),
    VILLAGE (1, "village", StructureType.VILLAGE,
            0xb1, 0xae, 0xae),
    OCEAN_RUIN (12, "ocean_ruin", StructureType.OCEAN_RUIN,
            0x00, 0x0d, 0x55),
    SHIPWRECK (13, "shipwreck", StructureType.SHIPWRECK,
            0x5c, 0x25, 0x3e),
    BURIED_TREASURE (14, "buried_treasure", StructureType.BURIED_TREASURE,
            0xff, 0xb6, 0x00),
    END_CITY (2, "endcity", StructureType.END_CITY,
            0xe5, 0xd7, 0xd7),
    FORTRESS (3, "fortress", StructureType.NETHER_FORTRESS,
            0xa0, 0x4e, 0x44),
    UNKNOWN (-1,  "unknown", null, 0x00, 0x00, 0x00);
    
    private static final String CLASSNAME = Structure.class.getName();
    
    /**
     *  Gets a structure's name value.
     *
     * @return  The structure's name string as used in NBT world data.
     */
    public String structureName()
    {
        return structureName;
    }
    
    /**
     * Converts a ChunkAtlas Structure to its corresponding
     * org.bukkit.StructureType value.
     * 
     * @return  The StructureType that matches this Structure. 
     */
    public StructureType toStructureType()
    {
        return bukkitType;
    }
    
    /**
     * Converts a bukkit.StructureType value to its corresponding ChunkAtlas
     * structure.
     * 
     * @param structure  A bukkit StructureType value.
     * 
     * @return           The equivalent Structure value. 
     */
    public static Structure fromStructureType(StructureType structure)
    {
        for (Structure struct : Structure.values())
        {
            if (struct.bukkitType == structure)
            {
                return struct;
            }
        }
        final String FN_NAME = "fromStructureType";
        LogConfig.getLogger().logp(Level.SEVERE, CLASSNAME, FN_NAME,
                    "Unhandled StructureType '{0}'.", structure.getName());
        return null;
    }

    /**
     *  Tries to parse a structure type from a name value.
     *
     * @param name  A possible structure name.
     *
     * @return      The corresponding Structure, or UNKNOWN if the
     *              name did not have a valid match.
     */
    public static Structure parse(String name)
    {
        name = name.toLowerCase();
        for (Structure struct : Structure.values())
        {
            if(name.equals(struct.structureName))
            {
                return struct;
            }
        }
        
        final String FN_NAME = "parse";
        LogConfig.getLogger().logp(Level.SEVERE, CLASSNAME, FN_NAME,
                    "Unknown structure name " + name);
        assert false;
        return UNKNOWN;
    }

    /**
     *  Gets a color value to represent a Minecraft structure.
     * @return           An appropriate color to represent that structure.
     */
    public Color getColor()
    {
        return structureColor;
    }
    
    /**
     *  Gets a value used to prioritize this structure within maps.
     *         Higher values take precedence over lower values.
     * 
     * @return  The priority value.
     */
    public int getPriority()
    {
        return structureCode;
    }
    
    /**
     * Gets a structure's display name.
     * 
     * @return  The formatted display name. 
     */
    @Override
    public String toString()
    {
        return structureName;
    }

    
    private Structure(int code, String name, StructureType type,
            int R, int G, int B)
    {
        structureCode = code;
        structureName = name;
        bukkitType = type;
        structureColor = new Color(R, G, B);
    }
    
    private final int structureCode;
    StructureType bukkitType;
    private final String structureName;
    private final Color structureColor;
}
