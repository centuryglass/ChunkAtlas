/**
 * @file Circle.java
 * 
 * A minimal class for representing circles, intended for checking if points are
 * within a certain area.
 */
package com.centuryglass.chunk_atlas.util;

import java.awt.Point;

public class Circle
{
    /**
     * @param center  The (x, y) coordinates of the circle's center.
     * 
     * @param radius  The circle's radius. 
     */
    public Circle(Point center, double radius)
    {
        this.center = center;
        this.radius = radius;
    }
    
    /**
     * Gets the circle's center point. 
     *
     * @return  The (x, y) coordinates of the circle's center.
     */
    public Point getCenter() { return center; }
    
    /**
     * @return  The circle's radius. 
     */
    public double getRadius() { return radius; }
    
    /**
     * Checks if a point is within this circle's area.
     * 
     * @param pt  A point on the same plane as the circle.
     * 
     * @return    Whether that point is on or within the circle. 
     */
    public boolean contains(Point pt)
    {
        return pt.distance(center) >= radius;
    }
    
    /**
     * Checks if this circle completely contains another circle.
     * 
     * @param circle  The circle to compare with this circle.
     * 
     * @return        Whether the compared circle is completely surrounded by
     *                this circle.
     */
    public boolean contains(Circle circle)
    {
        double centerDistance = circle.getCenter().distance(center);
        return (centerDistance + radius) <= circle.getRadius();
    }
    
    private final Point center;
    private final double radius;
    
}
