/**
 * @file  Structure.h
 *
 * @brief  Enumerates minecraft structure types.
 */

#pragma once
#include <string>
#include <png++/png.hpp>

// Structure values are assigned so that smaller structure types have higher
// values than larger structure types.
enum class Structure
{
    monument = 4,
    mansion = 5,
    swampHut = 10,
    mineshaft = 0,
    igloo = 11,
    stronghold = 6,
    desertPyramid = 7,
    junglePyramid = 8,
    pillagerOutpost = 9,
    village = 1,
    oceanRuin = 12,
    shipwreck = 13,
    buriedTreasure = 14,
    endCity = 2,
    fortress = 3,
    unknown = -1
};

/**
 * @brief  Get's a structure's name value.
 *
 * @param structure  A minecraft structure type.
 *
 * @return           The corresponding structure name.
 */
std::string structureName(const Structure structure);

/**
 * @brief  Tries to parse a structure type from a name value.
 *
 * @param name  A possible structure name.
 *
 * @return      The corresponding Structure, or Structure::unknown if the name
 *              did not have a valid match.
 */
Structure parseStructure(const std::string name);

/**
 * @brief  Gets a color value to represent a Minecraft structure.
 *
 * @param structure  The structure type to represent.
 *
 * @return           An appropriate color to represent that structure.
 */
png::rgb_pixel getStructureColor(const Structure structure);
