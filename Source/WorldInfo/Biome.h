/**
 * @file  Biome.h
 *
 * @brief  Defines the code values used to represent all minecraft world
 *         biomes.
 */

#pragma once
#include <png++/png.hpp>

enum class Biome
{
    ocean = 0,
    deep_ocean = 24,
    frozen_ocean = 10,
    deep_frozen_ocean = 50,
    cold_ocean = 46,
    deep_cold_ocean = 49,
    lukewarm_ocean = 45,
    deep_lukewarm_ocean = 48,
    warm_ocean = 44,
    deep_warm_ocean = 47,
    river = 7,
    frozen_river = 11,
    beach = 16,
    stone_shore = 25,
    snowy_beach = 26,
    forest = 4,
    wooded_hills = 18,
    flower_forest = 132,
    birch_forest = 27,
    birch_forest_hills = 28,
    tall_birch_forest = 155,
    tall_birch_hills = 156,
    dark_Forest = 29,
    dark_Forest_hills = 157,
    jungle = 21,
    jungle_hills = 22,
    modified_jungle = 149,
    jungle_edge = 23,
    modified_jungle_edge = 151,
    bamboo_jungle = 168,
    bamboo_jungle_hills = 169,
    taiga = 5,
    taiga_hills = 19,
    taiga_mountains = 133,
    snowy_taiga = 30,
    snowy_taiga_hills = 31,
    snowy_taiga_mountains = 158,
    giant_tree_taiga = 32,
    giant_tree_taiga_hills = 33,
    giant_spruce_taiga = 160,
    giant_spruce_taiga_hills = 161,
    mushroom_fields = 14,
    mushroom_fields_shore = 15,
    swamp = 6,
    swamp_hills = 134,
    savanna = 35,
    savanna_plateau = 36,
    shattered_savanna = 163,
    shattered_savanna_plateau = 164,
    plains = 1,
    sunflower_plains = 129,
    desert = 2,
    desert_hills = 17,
    desert_lakes = 130,
    snowy_tundra = 12,
    snowy_mountains = 13,
    ice_spikes = 140,
    mountains = 3,
    wooded_mountains = 34,
    gravelly_mountains = 131,
    modified_gravelly_mountains = 162,
    mountain_edge = 20,
    badlands = 37,
    badlands_plateau = 39,
    modified_badlands_plateau = 167,
    wooded_badlands_plateau = 38,
    modified_wooded_badlands_plateau = 166,
    eroded_badlands = 165,
    nether = 8,
    the_end = 9,
    small_end_islands = 40,
    end_midlands = 41,
    end_highlands = 42,
    end_barrens = 43,
    the_void = 127
};


/**
 * @brief  Gets a color value to represent a Minecraft biome.
 *
 * @param biome  The biome type to represent.
 *
 * @return       An appropriate color to represent that biome.
 */
png::rgb_pixel getBiomeColor(const Biome biome);
