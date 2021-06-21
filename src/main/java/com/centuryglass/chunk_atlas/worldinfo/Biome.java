/**
 * @file  Biome.java
 *
 *  Defines the code values used to represent all Minecraft world biomes.
 */
package com.centuryglass.chunk_atlas.worldinfo;

import com.centuryglass.chunk_atlas.util.StringUtil;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public enum Biome
{
    OCEAN (0,
            0x1f, 0x27, 0x99),
    DEEP_OCEAN (24,
            0x14, 0x1a, 0x66),
    FROZEN_OCEAN (10,
            0x52, 0x5f, 0x66),
    DEEP_FROZEN_OCEAN (50,
            0x29, 0x2f, 0x33),
    COLD_OCEAN (46,
            0x5c, 0x60, 0x99),
    DEEP_COLD_OCEAN (49,
            0x3d, 0x40, 0x66),
    LUKEWARM_OCEAN (45,
            0x1f, 0x6c, 0x99),
    DEEP_LUKEWARM_OCEAN (48,
            0x14, 0x48, 0x66),
    WARM_OCEAN (44,
            0x52, 0xcc, 0xc8),
    DEEP_WARM_OCEAN (47,
            0x3d, 0x99, 0x96),
    RIVER (7,
            0x7a, 0xae, 0xcc),
    FROZEN_RIVER (11,
            0x99, 0xda, 0xff),
    BEACH (16,
            0xa3, 0xa6, 0xcc),
    STONE_SHORE (25,
            0x6c, 0x6d, 0x6b),
    SNOWY_BEACH (26,
            0xcc, 0xcf, 0xff),
    FOREST (4,
            0x44, 0x81, 0x05),
    WOODED_HILLS (18,
            0x44, 0x81, 0x05),
    FLOWER_FOREST (132,
            0x3a, 0x7a, 0x09),
    BIRCH_FOREST (27,
            0x44, 0x81, 0x05),
    BIRCH_FOREST_HILLS (28,
            0x44, 0x81, 0x05),
    TALL_BIRCH_FOREST (155,
            0x44, 0x81, 0x05),
    TALL_BIRCH_HILLS (156,
            0x44, 0x81, 0x05),
    DARK_FOREST (29,
            0x1f, 0x33, 0x1f),
    DARK_FOREST_HILLS (157,
            0x3d, 0x66, 0x3d),
    JUNGLE (21,
            0x00, 0x73, 0x11),
    JUNGLE_HILLS (22,
            0x00, 0x73, 0x11),
    MODIFIED_JUNGLE (149,
            0x00, 0x73, 0x11),
    JUNGLE_EDGE (23,
            0x00, 0x73, 0x11),
    MODIFIED_JUNGLE_EDGE (151,
            0x00, 0x73, 0x11),
    BAMBOO_JUNGLE (168,
            0x00, 0x73, 0x11),
    BAMBOO_JUNGLE_HILLS (169,
            0x00, 0x73, 0x11),
    TAIGA (5,
            0x49, 0x5a, 0x3c),
    TAIGA_HILLS (19,
            0x49, 0x5a, 0x3c),
    TAIGA_MOUNTAINS (133,
            0x73, 0x38, 0x18),
    SNOWY_TAIGA (30,
            0xba, 0xa3, 0xcc),
    SNOWY_TAIGA_HILLS (31,
            0xe8, 0xcc, 0xff),
    SNOWY_TAIGA_MOUNTAINS (158,
            0xff, 0xff, 0xff),
    GIANT_TREE_TAIGA (32,
            0x49, 0x5a, 0x3c),
    GIANT_TREE_TAIGA_HILLS (33,
            0x49, 0x5a, 0x3c),
    GIANT_SPRUCE_TAIGA (160,
            0x73, 0x38, 0x18),
    GIANT_SPRUCE_TAIGA_HILLS (161,
            0x73, 0x38, 0x18),
    MUSHROOM_FIELDS (14,
            0x9e, 0x8b, 0xa3),
    MUSHROOM_FIELDS_SHORE (15,
            0x9e, 0x8b, 0xa3),
    SWAMP (6,
            0x00, 0x33, 0x31),
    SWAMP_HILLS (134,
            0x00, 0x66, 0x63),
    SAVANNA (35,
            0x89, 0x9a, 0x12),
    SAVANNA_PLATEAU (36,
            0x89, 0x9a, 0x12),
    SHATTERED_SAVANNA (163,
            0x89, 0x9a, 0x12),
    SHATTERED_SAVANNA_PLATEAU (164,
            0x89, 0x9a, 0x12),
    PLAINS (1,
            0x29, 0xac, 0x0c),
    SUNFLOWER_PLAINS (129,
            0x29, 0xac, 0x0c),
    DESERT (2,
            0xd3, 0xd1, 0xa1),
    DESERT_HILLS (17,
            0xd3, 0xd1, 0xa1),
    DESERT_LAKES (130,
            0xd3, 0xd1, 0xa1),
    SNOWY_TUNDRA (12,
            0x7a, 0x99, 0x98),
    SNOWY_MOUNTAINS (13,
            0xa3, 0xbd, 0xcc),
    ICE_SPIKES (140,
            0xcc, 0xff, 0xfd),
    MOUNTAINS (3,
            0x44, 0x35, 0x2c),
    WOODED_MOUNTAINS (34,
            0x44, 0x35, 0x2c),
    GRAVELLY_MOUNTAINS (131,
            0x6c, 0x6d, 0x6b),
    MODIFIED_GRAVELLY_MOUNTAINS (162,
            0xcc, 0xc4, 0xa3),
    MOUNTAIN_EDGE (20,
            0x6c, 0xb2, 0x23),
    BADLANDS (37,
            0x77, 0x3b, 0x3a),
    BADLANDS_PLATEAU (39,
            0xcc, 0x7a, 0x7a),
    MODIFIED_BADLANDS_PLATEAU (167,
            0xff, 0x99, 0x99),
    WOODED_BADLANDS_PLATEAU (38,
            0x99, 0x3d, 0x3d),
    MODIFIED_WOODED_BADLANDS_PLATEAU (166, 
            0xff, 0x66, 0x66),
    ERODED_BADLANDS (165,
            0xcc, 0x00, 0x00),
    DRIPSTONE_CAVES (174,
            0x7b, 0x6c, 0x6c),
    LUSH_CAVES (175,
            0x16, 0x37, 0x16),
    NETHER (8,
            0xff, 0x00, 0x00),
    SOUL_SAND_VALLEY (170,
            0x55, 0x3b, 0x30),
    CRIMSON_FOREST (171,
            0x97, 0x28, 0x0c),
    WARPED_FOREST (172,
            0x1b, 0x8c, 0x8c),
    BASALT_DELTAS (173,
            0x35, 0x2e, 0x36),
    THE_END (9,
            0xcc, 0x00, 0x52),
    SMALL_END_ISLANDS (40,
            0x66, 0x00, 0x29),
    END_MIDLANDS (41,
            0x99, 0x00, 0x3d),
    END_HIGHLANDS (42,
            0xff, 0x00, 0x66),
    END_BARRENS (43,
            0xa8, 0xa3, 0x8d),
    THE_VOID (127,
            0x33, 0x0a, 0x1b);

    /**
     *  Gets a color value to represent a Minecraft biome.
     * 
     * @return       An appropriate color to represent this biome.
     */
    public Color getColor()
    {
        return color;
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
        codeBiomes = new HashMap<>();
        for(Biome biome : Biome.values())
        {
            codeBiomes.put(biome.biomeCode, biome);
        }
    }
    
    /**
     * Get the total number of available biomes.
     * 
     * @return  The number of unique biomes.
     */
    public static int biomeCount()
    {
        return codeBiomes.size();
    }
    
    
    /**
     * Gets a biome's display name.
     * 
     * @return  The formatted display name. 
     */
    @Override
    public String toString()
    {
        String biome = name();
        String modified = "MODIFIED_";
        if (biome.startsWith(modified))
        {
            biome = biome.substring(modified.length()) + "+";
        }
        return StringUtil.enumToDisplayString(biome);
    }
    
    private Biome(int code, int R, int G, int B)
    {
        biomeCode = code;
        color = new Color(R, G, B);
    }
    
    private final int biomeCode;
    private final Color color;
}
