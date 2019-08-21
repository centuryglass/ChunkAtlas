/**
 * @file  Structure.java
 *
 * @brief  Enumerates Minecraft structure types.
 */

package com.centuryglass.mcmap.worldinfo;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;


// Structure values are assigned so that smaller structure types have higher
// values than larger structure types.
public enum Structure
{
    MONUMENT (4),
    MANSION (5),
    SWAMP_HUT (10),
    MINESHAFT (0),
    IGLOO (11),
    STRONGHOLD (6),
    DESERT_PYRAMID (7),
    JUNGLE_PYRAMID (8),
    PILLAGER_OUTPOST (9),
    VILLAGE (1),
    OCEAN_RUIN (12),
    SHIPWRECK (13),
    BURIED_TREASURE (14),
    END_CITY (2),
    FORTRESS (3),
    UNKNOWN (-1);
    
    /**
     * @brief  Gets a structure's name value.
     *
     * @param structure  A Minecraft structure type.
     *
     * @return           The corresponding structure name.
     */
    public static String structureName(Structure structure)
    {
        return structureToName.get(structure);
    }

    /**
     * @brief  Tries to parse a structure type from a name value.
     *
     * @param name  A possible structure name.
     *
     * @return      The corresponding Structure, or UNKNOWN if the
     *              name did not have a valid match.
     */
    public static Structure parse(String name)
    {
        Structure namedStruct = nameToStructure.get(name);
        if (namedStruct == null)
        {
            return UNKNOWN;
        }
        return namedStruct;
    }

    /**
     * @brief  Gets a color value to represent a Minecraft structure.
     *
     * @param structure  The structure type to represent.
     *
     * @return           An appropriate color to represent that structure.
     */
    public static Color getStructureColor(Structure structure)
    {
        return structureColors.get(structure);
    }
    
    /**
     * @brief  Gets a value used to prioritize this structure within maps.
     *         Higher values take precedence over lower values.
     * 
     * @return  The priority value.
     */
    public int getPriority()
    {
        return structureCode;
    }

    private static final Map<Structure, String> structureToName;
    private static final Map<String, Structure> nameToStructure;
    private static final Map<Structure, Color> structureColors;
    static
    {
        structureToName = new HashMap();
        structureToName.put(MONUMENT, "Monument");
        structureToName.put(MANSION, "Mansion");
        structureToName.put(SWAMP_HUT, "Swamp_Hut");
        structureToName.put(MINESHAFT, "Mineshaft");
        structureToName.put(IGLOO, "Igloo");
        structureToName.put(STRONGHOLD, "Stronghold");
        structureToName.put(DESERT_PYRAMID, "Desert_Pyramid");
        structureToName.put(JUNGLE_PYRAMID, "Jungle_Pyramid");
        structureToName.put(PILLAGER_OUTPOST, "Pillager_Outpost");
        structureToName.put(VILLAGE, "Village");
        structureToName.put(OCEAN_RUIN, "Ocean_Ruin");
        structureToName.put(SHIPWRECK, "Shipwreck");
        structureToName.put(BURIED_TREASURE, "Buried_Treasure");
        structureToName.put(END_CITY, "EndCity");
        structureToName.put(FORTRESS, "Fortress");

        nameToStructure = new HashMap();
        for (Map.Entry<Structure, String> entry : structureToName.entrySet())
        {
            nameToStructure.put(entry.getValue(), entry.getKey());
        }

        structureColors = new HashMap();
        structureColors.put(MONUMENT,         new Color(0x00, 0xe2, 0xaa));
        structureColors.put(MANSION,          new Color(0xae, 0x5c, 0x28));
        structureColors.put(SWAMP_HUT,        new Color(0x05, 0x4c, 0x3e));
        structureColors.put(MINESHAFT,        new Color(0x85, 0x06, 0x03));
        structureColors.put(IGLOO,            new Color(0x0d, 0xe0, 0xec));
        structureColors.put(STRONGHOLD,       new Color(0xa3, 0x0c, 0xcc));
        structureColors.put(DESERT_PYRAMID,   new Color(0xd0, 0xff, 0x00));
        structureColors.put(JUNGLE_PYRAMID,   new Color(0x9d, 0x97, 0x09));
        structureColors.put(PILLAGER_OUTPOST, new Color(0x9d, 0x97, 0x09));
        structureColors.put(VILLAGE,          new Color(0xb1, 0xae, 0xae));
        structureColors.put(OCEAN_RUIN,       new Color(0x00, 0x0d, 0x55));
        structureColors.put(SHIPWRECK,        new Color(0x5c, 0x25, 0x3e));
        structureColors.put(BURIED_TREASURE,  new Color(0xff, 0xb6, 0x00));
        structureColors.put(END_CITY,         new Color(0xe5, 0xd7, 0xd7));
        structureColors.put(FORTRESS,         new Color(0xa0, 0x4e, 0x44));
    }
    
    private Structure(int code)
    {
        structureCode = code;
    }
    
    private final int structureCode;
}
