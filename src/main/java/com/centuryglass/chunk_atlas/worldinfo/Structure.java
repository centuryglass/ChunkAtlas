/**
 * @file  Structure.java
 *
 *  Enumerates Minecraft structure types.
 */

package com.centuryglass.chunk_atlas.worldinfo;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.bukkit.StructureType;


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
     *  Gets a structure's name value.
     *
     * @return  The structure's name string as used in NBT world data.
     */
    public String structureName()
    {
        return structureToName.get(this);
    }
    
    public StructureType toStructureType()
    {
        switch (this)
        {
            case BURIED_TREASURE:
                return StructureType.BURIED_TREASURE;
            case DESERT_PYRAMID:
                return StructureType.DESERT_PYRAMID;
            case END_CITY:
                return StructureType.END_CITY;
            case FORTRESS:
                return StructureType.NETHER_FORTRESS;
            case IGLOO:
                return StructureType.IGLOO;
            case JUNGLE_PYRAMID:
                return StructureType.JUNGLE_PYRAMID;
            case MINESHAFT:
                return StructureType.MINESHAFT;
            case MANSION:
                return StructureType.WOODLAND_MANSION;
            case MONUMENT:
                return StructureType.OCEAN_MONUMENT;
            case OCEAN_RUIN:
                return StructureType.OCEAN_RUIN;
            case PILLAGER_OUTPOST:
                return StructureType.PILLAGER_OUTPOST;
            case SHIPWRECK:
                return StructureType.SHIPWRECK;
            case STRONGHOLD:
                return StructureType.STRONGHOLD;
            case SWAMP_HUT:
                return StructureType.SWAMP_HUT;
            case VILLAGE:
                return StructureType.VILLAGE;
            default:
                System.err.println("Structure.toStructureType: Unhandled type "
                        + this.toString());
                return null;
        }
    }
    
    public static Structure fromStructureType(StructureType structure)
    {
        Structure s = typeMap.get(structure);
        if (s == null)
        {
            System.err.println("Structure.fromStructureType: Unhandled "
                    + "StructureType " + structure.getName());
        }
        return s;
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
        Structure namedStruct = nameToStructure.get(name);
        if (namedStruct == null)
        {
            assert false;
            return UNKNOWN;
        }
        return namedStruct;
    }

    /**
     *  Gets a color value to represent a Minecraft structure.
     *
     * @param structure  The structure type to represent.
     *
     * @return           An appropriate color to represent that structure.
     */
    public static Color getStructureColor(Structure structure)
    {
        Validate.notNull(structure, "Structure cannot be null.");
        Color color = structureColors.get(structure);
        Validate.notNull(color, "No color found for structure "
                + structure.toString());
        return color;
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

    private static final Map<Structure, String> structureToName;
    private static final Map<String, Structure> nameToStructure;
    private static final Map<Structure, Color> structureColors;
    private static final Map<StructureType, Structure> typeMap;
    static
    {
        structureToName = new HashMap<>();
        structureToName.put(BURIED_TREASURE, "Buried_Treasure");
        structureToName.put(DESERT_PYRAMID, "Desert_Pyramid");
        structureToName.put(END_CITY, "EndCity");
        structureToName.put(FORTRESS, "Fortress");
        structureToName.put(IGLOO, "Igloo");
        structureToName.put(JUNGLE_PYRAMID, "Jungle_Pyramid");
        structureToName.put(MANSION, "Mansion");
        structureToName.put(MINESHAFT, "Mineshaft");
        structureToName.put(MONUMENT, "Monument");
        structureToName.put(OCEAN_RUIN, "Ocean_Ruin");
        structureToName.put(PILLAGER_OUTPOST, "Pillager_Outpost");
        structureToName.put(SHIPWRECK, "Shipwreck");
        structureToName.put(STRONGHOLD, "Stronghold");
        structureToName.put(SWAMP_HUT, "Swamp_Hut");
        structureToName.put(VILLAGE, "Village");

        nameToStructure = new HashMap<>();
        for (Map.Entry<Structure, String> entry : structureToName.entrySet())
        {
            nameToStructure.put(entry.getValue(), entry.getKey());
        }

        structureColors = new HashMap<>();
        structureColors.put(BURIED_TREASURE,  new Color(0xff, 0xb6, 0x00));
        structureColors.put(DESERT_PYRAMID,   new Color(0xd0, 0xff, 0x00));
        structureColors.put(END_CITY,         new Color(0xe5, 0xd7, 0xd7));
        structureColors.put(FORTRESS,         new Color(0xa0, 0x4e, 0x44));
        structureColors.put(IGLOO,            new Color(0x0d, 0xe0, 0xec));
        structureColors.put(JUNGLE_PYRAMID,   new Color(0x51, 0x5f, 0x49));
        structureColors.put(MANSION,          new Color(0xae, 0x5c, 0x28));
        structureColors.put(MINESHAFT,        new Color(0x85, 0x06, 0x03));
        structureColors.put(MONUMENT,         new Color(0x00, 0xe2, 0xaa));
        structureColors.put(OCEAN_RUIN,       new Color(0x00, 0x0d, 0x55));
        structureColors.put(PILLAGER_OUTPOST, new Color(0x9d, 0x97, 0x09));
        structureColors.put(SHIPWRECK,        new Color(0x5c, 0x25, 0x3e));
        structureColors.put(STRONGHOLD,       new Color(0xa3, 0x0c, 0xcc));
        structureColors.put(SWAMP_HUT,        new Color(0x05, 0x4c, 0x3e));
        structureColors.put(VILLAGE,          new Color(0xb1, 0xae, 0xae));
        
        
        typeMap = new HashMap<>();
        typeMap.put(StructureType.BURIED_TREASURE,  BURIED_TREASURE);
        typeMap.put(StructureType.DESERT_PYRAMID,   DESERT_PYRAMID);
        typeMap.put(StructureType.END_CITY,         END_CITY);
        typeMap.put(StructureType.NETHER_FORTRESS,  FORTRESS);
        typeMap.put(StructureType.IGLOO,            IGLOO);
        typeMap.put(StructureType.JUNGLE_PYRAMID,   JUNGLE_PYRAMID);
        typeMap.put(StructureType.WOODLAND_MANSION, MANSION);
        typeMap.put(StructureType.MINESHAFT,        MINESHAFT);
        typeMap.put(StructureType.OCEAN_MONUMENT,   MONUMENT);
        typeMap.put(StructureType.OCEAN_RUIN,       OCEAN_RUIN);
        typeMap.put(StructureType.PILLAGER_OUTPOST, PILLAGER_OUTPOST);
        typeMap.put(StructureType.SHIPWRECK,        SHIPWRECK);
        typeMap.put(StructureType.STRONGHOLD,       STRONGHOLD);
        typeMap.put(StructureType.SWAMP_HUT,        SWAMP_HUT);
        typeMap.put(StructureType.VILLAGE,          VILLAGE);
    }
    
    private Structure(int code)
    {
        structureCode = code;
    }
    
    private final int structureCode;
}
