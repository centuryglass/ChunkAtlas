/**
 * @file  Structure.h
 *
 * @brief  Enumerates minecraft structure types.
 */

#pragma once
#include <string>

enum class Structure
{
    monument,
    mansion,
    swampHut,
    mineshaft,
    igloo,
    stronghold,
    desertPyramid,
    junglePyramid,
    unknown
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
