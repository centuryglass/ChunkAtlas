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
        colorMap.put(BADLANDS_PLATEAU,
                new Color(0xcc, 0x7a, 0x7a));
        colorMap.put(MODIFIED_BADLANDS_PLATEAU,
                new Color(0xff, 0x99, 0x99));
        colorMap.put(BADLANDS,
                new Color(0x66, 0x29, 0x29));
        colorMap.put(WOODED_BADLANDS_PLATEAU,
                new Color(0x99, 0x3d, 0x3d));
        colorMap.put(MODIFIED_WOODED_BADLANDS_PLATEAU,
                new Color(0xff, 0x66, 0x66));
        colorMap.put(ERODED_BADLANDS,
                new Color(0xcc, 0x00, 0x00));
        colorMap.put(NETHER,
                new Color(0xff, 0x00, 0x00));
        colorMap.put(BIRCH_FOREST,
                new Color(0x66, 0x5a, 0x52));
        colorMap.put(BIRCH_FOREST_HILLS,
                new Color(0x99, 0x87, 0x7a));
        colorMap.put(TALL_BIRCH_FOREST,
                new Color(0xcc, 0xb4, 0xa3));
        colorMap.put(TALL_BIRCH_HILLS,
                new Color(0xff, 0xe0, 0xcc));
        colorMap.put(SAVANNA_PLATEAU,
                new Color(0xcc, 0x9b, 0x7a));
        colorMap.put(SHATTERED_SAVANNA_PLATEAU,
                new Color(0xff, 0xc2, 0x99));
        colorMap.put(SAVANNA,
                new Color(0xcc, 0x52, 0x00));
        colorMap.put(SHATTERED_SAVANNA,
                new Color(0xff, 0x66, 0x00));
        colorMap.put(STONE_SHORE,
                new Color(0x33, 0x31, 0x29));
        colorMap.put(GRAVELLY_MOUNTAINS,
                new Color(0x99, 0x93, 0x7a));
        colorMap.put(MODIFIED_GRAVELLY_MOUNTAINS,
                new Color(0xcc, 0xc4, 0xa3));
        colorMap.put(MOUNTAIN_EDGE,
                new Color(0xff, 0xf5, 0xcc));
        colorMap.put(MOUNTAINS,
                new Color(0x66, 0x5e, 0x3d));
        colorMap.put(WOODED_MOUNTAINS,
                new Color(0x99, 0x8d, 0x5c));
        colorMap.put(DESERT,
                new Color(0x99, 0x7a, 0x00));
        colorMap.put(DESERT_HILLS,
                new Color(0xff, 0xcc, 0x00));
        colorMap.put(JUNGLE_EDGE,
                new Color(0x93, 0x99, 0x7a));
        colorMap.put(MODIFIED_JUNGLE_EDGE,
                new Color(0xc4, 0xcc, 0xa3));
        colorMap.put(DESERT_LAKES,
                new Color(0xf5, 0xff, 0xcc));
        colorMap.put(JUNGLE,
                new Color(0x5a, 0x66, 0x29));
        colorMap.put(JUNGLE_HILLS,
                new Color(0x87, 0x99, 0x3d));
        colorMap.put(MODIFIED_JUNGLE,
                new Color(0xb4, 0xcc, 0x52));
        colorMap.put(SUNFLOWER_PLAINS,
                new Color(0xe0, 0xff, 0x66));
        colorMap.put(BAMBOO_JUNGLE,
                new Color(0x52, 0x66, 0x00));
        colorMap.put(BAMBOO_JUNGLE_HILLS,
                new Color(0x7a, 0x99, 0x00));
        colorMap.put(PLAINS,
                new Color(0xcc, 0xff, 0x00));
        colorMap.put(FOREST,
                new Color(0x29, 0x33, 0x29));
        colorMap.put(WOODED_HILLS,
                new Color(0x52, 0x66, 0x52));
        colorMap.put(DARK_FOREST,
                new Color(0x1f, 0x33, 0x1f));
        colorMap.put(DARK_FOREST_HILLS,
                new Color(0x3d, 0x66, 0x3d));
        colorMap.put(SNOWY_TUNDRA,
                new Color(0x7a, 0x99, 0x98));
        colorMap.put(ICE_SPIKES,
                new Color(0xcc, 0xff, 0xfd));
        colorMap.put(DEEP_WARM_OCEAN,
                new Color(0x3d, 0x99, 0x96));
        colorMap.put(WARM_OCEAN,
                new Color(0x52, 0xcc, 0xc8));
        colorMap.put(SWAMP,
                new Color(0x00, 0x33, 0x31));
        colorMap.put(SWAMP_HILLS,
                new Color(0x00, 0x66, 0x63));
        colorMap.put(DEEP_FROZEN_OCEAN,
                new Color(0x29, 0x2f, 0x33));
        colorMap.put(FROZEN_OCEAN,
                new Color(0x52, 0x5f, 0x66));
        colorMap.put(SNOWY_MOUNTAINS,
                new Color(0xa3, 0xbd, 0xcc));
        colorMap.put(RIVER,
                new Color(0x7a, 0xae, 0xcc));
        colorMap.put(FROZEN_RIVER,
                new Color(0x99, 0xda, 0xff));
        colorMap.put(DEEP_LUKEWARM_OCEAN,
                new Color(0x14, 0x48, 0x66));
        colorMap.put(LUKEWARM_OCEAN,
                new Color(0x1f, 0x6c, 0x99));
        colorMap.put(BEACH,
                new Color(0xa3, 0xa6, 0xcc));
        colorMap.put(SNOWY_BEACH,
                new Color(0xcc, 0xcf, 0xff));
        colorMap.put(DEEP_COLD_OCEAN,
                new Color(0x3d, 0x40, 0x66));
        colorMap.put(COLD_OCEAN,
                new Color(0x5c, 0x60, 0x99));
        colorMap.put(DEEP_OCEAN,
                new Color(0x14, 0x1a, 0x66));
        colorMap.put(OCEAN,
                new Color(0x1f, 0x27, 0x99));
        colorMap.put(SNOWY_TAIGA,
                new Color(0xba, 0xa3, 0xcc));
        colorMap.put(SNOWY_TAIGA_HILLS,
                new Color(0xe8, 0xcc, 0xff));
        colorMap.put(FLOWER_FOREST,
                new Color(0x8c, 0x00, 0xff));
        colorMap.put(GIANT_TREE_TAIGA,
                new Color(0x31, 0x29, 0x33));
        colorMap.put(GIANT_TREE_TAIGA_HILLS,
                new Color(0x62, 0x52, 0x66));
        colorMap.put(TAIGA,
                new Color(0x5e, 0x3d, 0x66));
        colorMap.put(TAIGA_HILLS,
                new Color(0x8d, 0x5c, 0x99));
        colorMap.put(MUSHROOM_FIELDS,
                new Color(0x66, 0x00, 0x52));
        colorMap.put(MUSHROOM_FIELDS_SHORE,
                new Color(0x99, 0x00, 0x7a));
        colorMap.put(TAIGA_MOUNTAINS,
                new Color(0xcc, 0xa3, 0xb4));
        colorMap.put(SNOWY_TAIGA_MOUNTAINS,
                new Color(0xff, 0xcc, 0xe0));
        colorMap.put(GIANT_SPRUCE_TAIGA,
                new Color(0x66, 0x3d, 0x4e));
        colorMap.put(GIANT_SPRUCE_TAIGA_HILLS,
                new Color(0x99, 0x5c, 0x74));
        colorMap.put(THE_VOID,
                new Color(0x33, 0x0a, 0x1b));
        colorMap.put(END_BARRENS,
                new Color(0x33, 0x00, 0x14));
        colorMap.put(SMALL_END_ISLANDS,
                new Color(0x66, 0x00, 0x29));
        colorMap.put(END_MIDLANDS,
                new Color(0x99, 0x00, 0x3d));
        colorMap.put(THE_END,
                new Color(0xcc, 0x00, 0x52));
        colorMap.put(END_HIGHLANDS,
                new Color(0xff, 0x00, 0x66));
    }
    
    private Biome(int code)
    {
        biomeCode = code;
    }
    
    private final int biomeCode;
}
