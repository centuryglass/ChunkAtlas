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
                    new Color(0x1a, 0x00, 0xfe));
        colorMap.put(DEEP_OCEAN,
                new Color(0x16, 0x01, 0x50));
        colorMap.put(FROZEN_OCEAN,
                new Color(0xc2, 0xbb, 0xff));
        colorMap.put(DEEP_FROZEN_OCEAN,
                new Color(0x2a, 0x36, 0x59));
        colorMap.put(COLD_OCEAN,
                new Color(0x76, 0x67, 0xff));
        colorMap.put(DEEP_COLD_OCEAN,
                new Color(0x2a, 0x36, 0x59));
        colorMap.put(LUKEWARM_OCEAN,
                new Color(0x00, 0x81, 0xff));
        colorMap.put(DEEP_LUKEWARM_OCEAN,
                new Color(0x00, 0x34, 0x51));
        colorMap.put(WARM_OCEAN,
                new Color(0x00, 0xa4, 0xff));
        colorMap.put(DEEP_WARM_OCEAN,
                new Color(0x00, 0x34, 0x51));
        colorMap.put(RIVER,
                new Color(0x72, 0x6f, 0xff));
        colorMap.put(FROZEN_RIVER,
                new Color(0xc6, 0xc5, 0xff));
        colorMap.put(BEACH,
                new Color(0x9c, 0x9f, 0x88));
        colorMap.put(STONE_SHORE,
                new Color(0x51, 0x53, 0x45));
        colorMap.put(SNOWY_BEACH,
                new Color(0xe2, 0xe6, 0xc8));
        colorMap.put(FOREST,
                new Color(0x0b, 0xa1, 0x0c));
        colorMap.put(WOODED_HILLS,
                new Color(0x0c, 0xb9, 0x03));
        colorMap.put(FLOWER_FOREST,
                new Color(0xfa, 0x93, 0xff));
        colorMap.put(BIRCH_FOREST,
                new Color(0x73, 0xb6, 0x74));
        colorMap.put(BIRCH_FOREST_HILLS,
                new Color(0x7e, 0xcd, 0x7f));
        colorMap.put(TALL_BIRCH_FOREST,
                new Color(0x7b, 0xb8, 0x7c));
        colorMap.put(TALL_BIRCH_HILLS,
                new Color(0xa4, 0xe9, 0xa5));
        colorMap.put(DARK_FOREST,
                new Color(0x36, 0x5f, 0x36));
        colorMap.put(DARK_FOREST_HILLS,
                new Color(0x4a, 0x83, 0x4a));
        colorMap.put(JUNGLE,
                new Color(0x00, 0x4a, 0x4a));
        colorMap.put(JUNGLE_HILLS,
                new Color(0x02, 0x62, 0x62));
        colorMap.put(MODIFIED_JUNGLE,
                new Color(0x16, 0x81, 0x81));
        colorMap.put(JUNGLE_EDGE,
                new Color(0x45, 0x89, 0x7a));
        colorMap.put(MODIFIED_JUNGLE_EDGE,
                new Color(0x5c, 0xbf, 0xb7));
        colorMap.put(BAMBOO_JUNGLE,
                new Color(0x9e, 0x96, 0x8d));
        colorMap.put(BAMBOO_JUNGLE_HILLS,
                new Color(0xbf, 0xb3, 0xa6));
        colorMap.put(TAIGA,
                new Color(0x54, 0x38, 0x36));
        colorMap.put(TAIGA_HILLS,
                new Color(0x71, 0x4c, 0x49));
        colorMap.put(TAIGA_MOUNTAINS,
                new Color(0xbd, 0x7d, 0x79));
        colorMap.put(SNOWY_TAIGA,
                new Color(0x95, 0x89, 0x89));
        colorMap.put(SNOWY_TAIGA_HILLS,
                new Color(0xc2, 0xb1, 0xb1));
        colorMap.put(SNOWY_TAIGA_MOUNTAINS,
                new Color(0xde, 0xc2, 0xc2));
        colorMap.put(GIANT_TREE_TAIGA,
                new Color(0x40, 0x16, 0x13));
        colorMap.put(GIANT_TREE_TAIGA_HILLS,
                new Color(0x6c, 0x23, 0x1e));
        colorMap.put(GIANT_SPRUCE_TAIGA,
                new Color(0x7d, 0x50, 0x46));
        colorMap.put(GIANT_SPRUCE_TAIGA_HILLS,
                new Color(0x99, 0x63, 0x56));
        colorMap.put(MUSHROOM_FIELDS,
                new Color(0xa1, 0x0b, 0x2c));
        colorMap.put(MUSHROOM_FIELDS_SHORE,
                new Color(0xe2, 0x66, 0x81));
        colorMap.put(SWAMP,
                new Color(0x49, 0x1d, 0x5e));
        colorMap.put(SWAMP_HILLS,
                new Color(0x86, 0x30, 0xaf));
        colorMap.put(SAVANNA,
                new Color(0x90, 0x67, 0x0c));
        colorMap.put(SAVANNA_PLATEAU,
                new Color(0xb1, 0x7e, 0x0e));
        colorMap.put(SHATTERED_SAVANNA,
                new Color(0xb3, 0x90, 0x42));
        colorMap.put(SHATTERED_SAVANNA_PLATEAU,
                new Color(0xf8, 0xcf, 0x75));
        colorMap.put(PLAINS,
                new Color(0xb4, 0xff, 0x00));
        colorMap.put(SUNFLOWER_PLAINS,
                new Color(0xe0, 0xfb, 0x96));
        colorMap.put(DESERT,
                new Color(0xce, 0xbb, 0x00));
        colorMap.put(DESERT_HILLS,
                new Color(0xff, 0xe8, 0x00));
        colorMap.put(DESERT_LAKES,
                new Color(0xfb, 0xf6, 0xc4));
        colorMap.put(SNOWY_TUNDRA,
                new Color(0xae, 0xba, 0xba));
        colorMap.put(SNOWY_MOUNTAINS,
                new Color(0xcf, 0xea, 0xeb));
        colorMap.put(ICE_SPIKES,
                new Color(0x55, 0x6a, 0x6f));
        colorMap.put(MOUNTAINS,
                new Color(0x7e, 0x7a, 0x7a));
        colorMap.put(WOODED_MOUNTAINS,
                new Color(0x7e, 0x8a, 0x7e));
        colorMap.put(GRAVELLY_MOUNTAINS,
                new Color(0x5c, 0x63, 0x5c));
        colorMap.put(MODIFIED_GRAVELLY_MOUNTAINS,
                new Color(0x42, 0x44, 0x42));
        colorMap.put(MOUNTAIN_EDGE,
                new Color(0x33, 0x36, 0x33));
        colorMap.put(BADLANDS,
                new Color(0xd8, 0x2d, 0x04));
        colorMap.put(BADLANDS_PLATEAU,
                new Color(0xe9, 0x5e, 0x42));
        colorMap.put(MODIFIED_BADLANDS_PLATEAU,
                new Color(0xf7, 0x86, 0x6f));
        colorMap.put(WOODED_BADLANDS_PLATEAU,
                new Color(0x68, 0x11, 0x00));
        colorMap.put(MODIFIED_WOODED_BADLANDS_PLATEAU,
                new Color(0xbf, 0x22, 0x00));
        colorMap.put(ERODED_BADLANDS,
                new Color(0x84, 0x1a, 0x00));
        colorMap.put(NETHER,
                new Color(0xff, 0x18, 0x58));
        colorMap.put(THE_END,
                new Color(0xce, 0xc8, 0xab));
        colorMap.put(SMALL_END_ISLANDS,
                new Color(0xcd, 0xc2, 0x8e));
        colorMap.put(END_MIDLANDS,
                new Color(0xb4, 0xab, 0x7f));
        colorMap.put(END_HIGHLANDS,
                new Color(0xf4, 0xf0, 0xda));
        colorMap.put(END_BARRENS,
                new Color(0x91, 0x90, 0x8a));
        colorMap.put(THE_VOID,
                new Color(0x00, 0x00, 0x00));
        
    }
    
    private Biome(int code)
    {
        biomeCode = code;
    }
    
    private final int biomeCode;
}
