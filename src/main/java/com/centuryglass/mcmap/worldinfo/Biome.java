/**
 * @file  Biome.java
 *
 *  Defines the code values used to represent all Minecraft world biomes.
 */
package com.centuryglass.mcmap.worldinfo;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public enum Biome
{
    OCEAN (0),
    DEEP_OCEAN (24),
    FROZEN_OCEAN (10),
    DEEP_FROZEN_OCEAN (50),
    COLD_OCEAN (46),
    DEEP_COLD_OCEAN (49),
    LUKEWARM_OCEAN (45),
    DEEP_LUKEWARM_OCEAN (48),
    WARM_OCEAN (44),
    DEEP_WARM_OCEAN (47),
    RIVER (7),
    FROZEN_RIVER (11),
    BEACH (16),
    STONE_SHORE (25),
    SNOWY_BEACH (26),
    FOREST (4),
    WOODED_HILLS (18),
    FLOWER_FOREST (132),
    BIRCH_FOREST (27),
    BIRCH_FOREST_HILLS (28),
    TALL_BIRCH_FOREST (155),
    TALL_BIRCH_HILLS (156),
    DARK_FOREST (29),
    DARK_FOREST_HILLS (157),
    JUNGLE (21),
    JUNGLE_HILLS (22),
    MODIFIED_JUNGLE (149),
    JUNGLE_EDGE (23),
    MODIFIED_JUNGLE_EDGE (151),
    BAMBOO_JUNGLE (168),
    BAMBOO_JUNGLE_HILLS (169),
    TAIGA (5),
    TAIGA_HILLS (19),
    TAIGA_MOUNTAINS (133),
    SNOWY_TAIGA (30),
    SNOWY_TAIGA_HILLS (31),
    SNOWY_TAIGA_MOUNTAINS (158),
    GIANT_TREE_TAIGA (32),
    GIANT_TREE_TAIGA_HILLS (33),
    GIANT_SPRUCE_TAIGA (160),
    GIANT_SPRUCE_TAIGA_HILLS (161),
    MUSHROOM_FIELDS (14),
    MUSHROOM_FIELDS_SHORE (15),
    SWAMP (6),
    SWAMP_HILLS (134),
    SAVANNA (35),
    SAVANNA_PLATEAU (36),
    SHATTERED_SAVANNA (163),
    SHATTERED_SAVANNA_PLATEAU (164),
    PLAINS (1),
    SUNFLOWER_PLAINS (129),
    DESERT (2),
    DESERT_HILLS (17),
    DESERT_LAKES (130),
    SNOWY_TUNDRA (12),
    SNOWY_MOUNTAINS (13),
    ICE_SPIKES (140),
    MOUNTAINS (3),
    WOODED_MOUNTAINS (34),
    GRAVELLY_MOUNTAINS (131),
    MODIFIED_GRAVELLY_MOUNTAINS (162),
    MOUNTAIN_EDGE (20),
    BADLANDS (37),
    BADLANDS_PLATEAU (39),
    MODIFIED_BADLANDS_PLATEAU (167),
    WOODED_BADLANDS_PLATEAU (38),
    MODIFIED_WOODED_BADLANDS_PLATEAU (166),
    ERODED_BADLANDS (165),
    NETHER (8),
    THE_END (9),
    SMALL_END_ISLANDS (40),
    END_MIDLANDS (41),
    END_HIGHLANDS (42),
    END_BARRENS (43),
    THE_VOID (127);

    /**
     *  Gets a color value to represent a Minecraft biome.
     *
     * @param biome  The biome type to represent.
     *
     * @return       An appropriate color to represent that biome.
     */
    public static Color getBiomeColor(Biome biome)
    {
        return colorMap.get(biome);
    }
    
    /**
     *  Gets a biome from its NBT code value.
     * 
     * @param biomeCode  A biome's NBT code number.
     * 
     * @return           The associated biome, or null if the code isn't valid. 
     */
    public static Biome fromCode(int biomeCode)
    {
        return codeBiomes.get(biomeCode);
    }
    
    // Save (integer code, Biome) pairs for quick lookup:
    private static final Map<Integer, Biome> codeBiomes;
    static
    {
        codeBiomes = new HashMap();
        for(Biome biome : Biome.values())
        {
            codeBiomes.put(biome.biomeCode, biome);
        }
    }
    
    // Color map:
    private static final Map<Biome, Color> colorMap;
    static
    {
        colorMap = new HashMap();
        colorMap.put(OCEAN,
                new Color(0x09, 0x00, 0x78));
        colorMap.put(DEEP_OCEAN,
                new Color(0x07, 0x00, 0x59));
        colorMap.put(FROZEN_OCEAN,
                new Color(0x9c, 0x9a, 0xb9));
        colorMap.put(DEEP_FROZEN_OCEAN,
                new Color(0x33, 0x33, 0x3d));
        colorMap.put(COLD_OCEAN,
                new Color(0x78, 0x73, 0xba));
        colorMap.put(DEEP_COLD_OCEAN,
                new Color(0x29, 0x28, 0x3c));
        colorMap.put(LUKEWARM_OCEAN,
                new Color(0x46, 0x3d, 0xb9));
        colorMap.put(DEEP_LUKEWARM_OCEAN,
                new Color(0x15, 0x13, 0x36));
        colorMap.put(WARM_OCEAN,
                new Color(0x0f, 0x01, 0xba));
        colorMap.put(DEEP_WARM_OCEAN,
                new Color(0x04, 0x00, 0x35));
        colorMap.put(RIVER,
                new Color(0x00, 0x5b, 0x78));
        colorMap.put(FROZEN_RIVER,
                new Color(0x68, 0x7e, 0x85));
        colorMap.put(BEACH,
                new Color(0x4e, 0x00, 0x78));
        colorMap.put(STONE_SHORE,
                new Color(0x74, 0x6f, 0x77));
        colorMap.put(SNOWY_BEACH,
                new Color(0x97, 0x88, 0x9f));
        colorMap.put(FOREST,
                new Color(0x00, 0x78, 0x5b));
        colorMap.put(WOODED_HILLS,
                new Color(0x00, 0x3d, 0x0c));
        colorMap.put(FLOWER_FOREST,
                new Color(0x00, 0xff, 0x33));
        colorMap.put(BIRCH_FOREST,
                new Color(0x50, 0x7c, 0x59));
        colorMap.put(BIRCH_FOREST_HILLS,
                new Color(0x34, 0x4c, 0x38));
        colorMap.put(TALL_BIRCH_FOREST,
                new Color(0x7c, 0xaa, 0x85));
        colorMap.put(TALL_BIRCH_HILLS,
                new Color(0x34, 0x4c, 0x38));
        colorMap.put(DARK_FOREST,
                new Color(0x0f, 0x20, 0x13));
        colorMap.put(DARK_FOREST_HILLS,
                new Color(0x0b, 0x2f, 0x12));
        colorMap.put(JUNGLE,
                new Color(0x00, 0x78, 0x5b));
        colorMap.put(JUNGLE_HILLS,
                new Color(0x02, 0x62, 0x62));
        colorMap.put(MODIFIED_JUNGLE,
                new Color(0x0d, 0xc6, 0x99));
        colorMap.put(JUNGLE_EDGE,
                new Color(0x02, 0xb9, 0x8d));
        colorMap.put(MODIFIED_JUNGLE_EDGE,
                new Color(0x0c, 0xf7, 0xb3));
        colorMap.put(BAMBOO_JUNGLE,
                new Color(0x45, 0x6f, 0x65));
        colorMap.put(BAMBOO_JUNGLE_HILLS,
                new Color(0x23, 0x37, 0x32));
        colorMap.put(TAIGA,
                new Color(0x00, 0x85, 0x72));
        colorMap.put(TAIGA_HILLS,
                new Color(0x02, 0x51, 0x45));
        colorMap.put(TAIGA_MOUNTAINS,
                new Color(0x0d, 0x2f, 0x2a));
        colorMap.put(SNOWY_TAIGA,
                new Color(0x66, 0x7d, 0x7a));
        colorMap.put(SNOWY_TAIGA_HILLS,
                new Color(0x53, 0x5b, 0x5a));
        colorMap.put(SNOWY_TAIGA_MOUNTAINS,
                new Color(0x32, 0x3c, 0x3a));
        colorMap.put(GIANT_TREE_TAIGA,
                new Color(0x2d, 0xa6, 0x95));
        colorMap.put(GIANT_TREE_TAIGA_HILLS,
                new Color(0x1b, 0x7c, 0x63));
        colorMap.put(GIANT_SPRUCE_TAIGA,
                new Color(0x00, 0x5b, 0x4e));
        colorMap.put(GIANT_SPRUCE_TAIGA_HILLS,
                new Color(0x00, 0x3a, 0x32));
        colorMap.put(MUSHROOM_FIELDS,
                new Color(0x6e, 0x00, 0xff));
        colorMap.put(MUSHROOM_FIELDS_SHORE,
                new Color(0xb7, 0x90, 0xeb));
        colorMap.put(SWAMP,
                new Color(0x78, 0x00, 0x74));
        colorMap.put(SWAMP_HILLS,
                new Color(0x5b, 0x01, 0x58));
        colorMap.put(SAVANNA,
                new Color(0x78, 0x46, 0x00));
        colorMap.put(SAVANNA_PLATEAU,
                new Color(0xb4, 0x70, 0x11));
        colorMap.put(SHATTERED_SAVANNA,
                new Color(0x7b, 0x6b, 0x55));
        colorMap.put(SHATTERED_SAVANNA_PLATEAU,
                new Color(0xb6, 0x9d, 0x7a));
        colorMap.put(PLAINS,
                new Color(0x66, 0x78, 0x00));
        colorMap.put(SUNFLOWER_PLAINS,
                new Color(0xc3, 0xe5, 0x03));
        colorMap.put(DESERT,
                new Color(0x78, 0x71, 0x00));
        colorMap.put(DESERT_HILLS,
                new Color(0x61, 0x5c, 0x10));
        colorMap.put(DESERT_LAKES,
                new Color(0x98, 0x93, 0x5f));
        colorMap.put(SNOWY_TUNDRA,
                new Color(0xa7, 0x8f, 0xa6));
        colorMap.put(SNOWY_MOUNTAINS,
                new Color(0x75, 0x5a, 0x68));
        colorMap.put(ICE_SPIKES,
                new Color(0xc7, 0xad, 0xbb));
        colorMap.put(MOUNTAINS,
                new Color(0x78, 0x00, 0x3f));
        colorMap.put(WOODED_MOUNTAINS,
                new Color(0x4d, 0x17, 0x33));
        colorMap.put(GRAVELLY_MOUNTAINS,
                new Color(0x5a, 0x4f, 0x55));
        colorMap.put(MODIFIED_GRAVELLY_MOUNTAINS,
                new Color(0xa0, 0x8e, 0x98));
        colorMap.put(MOUNTAIN_EDGE,
                new Color(0xd3, 0x2f, 0x85));
        colorMap.put(BADLANDS,
                new Color(0x78, 0x1b, 0x00));
        colorMap.put(BADLANDS_PLATEAU,
                new Color(0xb1, 0x21, 0x0c));
        colorMap.put(MODIFIED_BADLANDS_PLATEAU,
                new Color(0xe5, 0x64, 0x3f));
        colorMap.put(WOODED_BADLANDS_PLATEAU,
                new Color(0x77, 0x51, 0x46));
        colorMap.put(MODIFIED_WOODED_BADLANDS_PLATEAU,
                new Color(0xc1, 0x96, 0x89));
        colorMap.put(ERODED_BADLANDS,
                new Color(0x6a, 0x55, 0x4f));
        colorMap.put(NETHER,
                new Color(0xFF, 0x00, 0x00));
        colorMap.put(THE_END,
                new Color(0xff, 0x00, 0xff));
        colorMap.put(SMALL_END_ISLANDS,
                new Color(0xb5, 0x50, 0xb5));
        colorMap.put(END_MIDLANDS,
                new Color(0x65, 0x42, 0x65));
        colorMap.put(END_HIGHLANDS,
                new Color(0xdb, 0xaa, 0xdb));
        colorMap.put(END_BARRENS,
                new Color(0x42, 0x37, 0x42));
        colorMap.put(THE_VOID,
                new Color(0x00, 0x00, 0x00));
    }
    
    private Biome(int code)
    {
        biomeCode = code;
    }
    
    private final int biomeCode;
}
