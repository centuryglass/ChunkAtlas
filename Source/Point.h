/**
 * @file  Point.h
 *
 * @brief  An extremely basic 2d point coordinate data structure.
 */

#pragma once

/**
 * @brief  Holds a 2D coordinate expressed in integer values.
 *
 *  Point structures are mainly used to represent Minecraft coordinates, which
 * is why they use (x, z) coordinates instead of the more conventional (x, y).
 */
struct Point
{
    int x = 0;
    int z = 0;
};

