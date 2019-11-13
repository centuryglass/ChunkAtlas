/**
 * @file Location.java
 * 
 * A point of interest within a map.
 */

package com.centuryglass.chunk_atlas.location;

import com.centuryglass.chunk_atlas.util.StringUtil;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.apache.commons.lang.Validate;

/**
 * Location objects represent individual points of interest within a map. Each
 * location stores a set of Minecraft (x, z) block coordinates, along with
 * an optional name, category, color, and image path. Locations are immutable.
 */
public class Location
{
    /**
     * Stores all location data on construction.
     * 
     * @param coordinates  The Location's Minecraft block coordinates.
     * 
     * @param name         The Location's optional name string, or null.
     * 
     * @param category     The Location's optional category string, or null.
     * 
     * @param color        The Location's optional display color, or null.
     * 
     * @param imagePath    The Location's optional image path, or null. 
     */
    public Location(Point coordinates, String name, String category,
            Color color, File imagePath)
    {
        Validate.notNull(coordinates, "Coordinates cannot be null.");
        this.coordinates = coordinates;
        this.name = name;
        this.category = category;
        this.color = color;
        this.imagePath = imagePath;
    }
    
    /**
     * Gets the Location's coordinates.
     * 
     * @return  The (x, z) block coordinates of this Location. 
     */
    public Point getCoordinates()
    {
        return coordinates;
    }
    
    /**
     * Gets the Location's name.
     * 
     * @return  The Location's name string, or null if the location has no name.
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Gets the Location's category.
     * 
     * @return  The Location's category string, or null if the location is
     *          not categorized.
     */
    public String getCategory()
    {
        return category;
    }
    
    /**
     * Gets the Location's display color.
     * 
     * @return  A color used to represent the Location, or null if none exists.
     */
    public Color getColor()
    {
        return color;
    }
    
    /**
     * Gets the path to an image used to represent the Location.
     * 
     * @return  The path to a small thumbnail image used to represent the
     *          Location, or null if the Location has no image.
     */
    public File getImagePath()
    {
        return imagePath;
    }
    
    // Key strings used to store all location data in JSON:
    private static class JsonKeys
    {
        protected static final String X_COORD = "x";
        protected static final String Z_COORD = "z";
        protected static final String NAME = "name";
        protected static final String CATEGORY = "category";
        protected static final String COLOR = "color";
        protected static final String IMAGE = "image";
    }
    
    /**
     * Gets a JSON object representing this Location.
     * 
     * @return  A JSON object holding the Location's non-null data. 
     */
    public JsonObject toJson()
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add(JsonKeys.X_COORD, coordinates.x);
        builder.add(JsonKeys.Z_COORD, coordinates.y);
        if (name != null)
        {
            builder.add(JsonKeys.NAME, name);
        }
        if (category != null)
        {
            builder.add(JsonKeys.CATEGORY, category);
        }
        if (color != null)
        {
            builder.add(JsonKeys.COLOR, StringUtil.colorHex(color));
        }
        if (imagePath != null)
        {
            builder.add(JsonKeys.IMAGE, imagePath.toString());
        }
        return builder.build();
    }
    
    final Point coordinates;
    final String name;
    final String category;
    final Color color;
    final File imagePath;
    
}
