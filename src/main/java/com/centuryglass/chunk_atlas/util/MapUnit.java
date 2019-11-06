/**
 * @file  MapUnit.java
 * 
 * Represents and converts between different measurements of Minecraft world
 * distance.
 */

package com.centuryglass.chunk_atlas.util;

import java.awt.Point;

public enum MapUnit 
{
    BLOCK (1),
    CHUNK (16),
    REGION (512);
    
    /**
     * Converts a value from one unit of measurement to another.
     * 
     * @param value        A Minecraft position or distance value.
     * 
     * @param inputUnit    The unit of measurement used by the input value.
     * 
     * @param outputUnit   The unit of measurement to convert to.
     * 
     * @return             The value, converted to the output unit.
     */
    public static int convert(int value, MapUnit inputUnit, MapUnit outputUnit)
    {
        return value * inputUnit.scale / outputUnit.scale;
    }
        
    /**
     * Converts a point from one unit of measurement to another.
     * 
     * @param point        A Minecraft world coordinate.
     * 
     * @param inputUnit    The unit of measurement used by the input point.
     * 
     * @param outputUnit   The unit of measurement to convert to.
     * 
     * @return             The point, converted to the output unit.
     */
    public static Point convertPoint
    (Point point, MapUnit inputUnit, MapUnit outputUnit)
    {
        return new Point(point.x * inputUnit.scale / outputUnit.scale,
                point.y * inputUnit.scale / outputUnit.scale);
    }
    
    /**
     * Converts a value from a standard unit of measurement to an arbitrary
     * scale.
     * 
     * @param value        A Minecraft position or distance value.
     * 
     * @param inputUnit    The unit of measurement used by the input value.
     * 
     * @param outputScale  The output scale value, representing the number of
     *                     blocks per unit.
     * 
     * @return             The value, converted to the output scale.
     */
    public static int convert(int value, MapUnit inputUnit, int outputScale)
    {
        return value * inputUnit.scale / outputScale;
    }
        
    /**
     * Converts a point from a standard unit of measurement to an arbitrary
     * scale.
     * 
     * @param point        A Minecraft world coordinate.
     * 
     * @param inputUnit    The unit of measurement used by the input point.
     * 
     * @param outputScale  The output scale value, representing the number of
     *                     blocks per unit.
     * 
     * @return             The point, converted to the output scale.
     */
    public static Point convertPoint
    (Point point, MapUnit inputUnit, int outputScale)
    {
        return new Point(point.x * inputUnit.scale / outputScale,
                point.y * inputUnit.scale / outputScale);
    }
    
    private MapUnit(int unitScale)
    {
        scale = unitScale;
    }
    
    private final int scale;
}
