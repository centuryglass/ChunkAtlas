#include "Structure.h"
#include <iostream>
#include <map>


// Get's a structure's name value.
std::string structureName(const Structure structure)
{
    switch(structure)
    {
        case Structure::monument:
            return "Monument";
        case Structure::mansion:
            return "Mansion";
        case Structure::swampHut:
            return "Swamp_Hut";
        case Structure::mineshaft:
            return "Mineshaft";
        case Structure::igloo:
            return "Igloo";
        case Structure::stronghold:
            return "Stronghold";
        case Structure::desertPyramid:
            return "Desert_Pyramid";
        case Structure::junglePyramid:
            return "Jungle_Pyramid";
        case Structure::unknown:
            std::cerr << "Invalid structure value.\n";
    };
    return "";
}


// Tries to parse a structure type from a name value.
Structure parseStructure(const std::string name)
{
    static const std::map<std::string, Structure> nameMap =
    {
        { "Monument", Structure::monument },
        { "Mansion", Structure::mansion },
        { "Swamp_Hut", Structure::swampHut },
        { "Mineshaft", Structure::mineshaft },
        { "Igloo", Structure::igloo },
        { "Stronghold", Structure::stronghold },
        { "Desert_Pyramid", Structure::desertPyramid },
        { "Jungle_Pyramid", Structure::junglePyramid }
    };
    const auto iter = nameMap.find(name);
    if (iter == nameMap.end())
    {
        std::cerr << "Unknown structure name " << name << " encountered.\n";
        return Structure::unknown;
    }
    return iter->second;
}
