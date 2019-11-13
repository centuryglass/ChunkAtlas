/**
 * @file LocationGroup.java
 * 
 * Stores a set of related Location objects.
 */

package com.centuryglass.chunk_atlas.location;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import org.apache.commons.lang.Validate;

/**
 * LocationGroup initializes and holds a set of related Location objects. It can
 * be provided with existing Location objects, or used to initialize Locations
 * with shared properties.
 */
public class LocationGroup
{
    public LocationGroup()
    {
        locations = new ArrayList<>();
    }
    
    /**
     * Gets all locations held by this LocationGroup.
     * 
     * @return  A copy of the LocationGroup's location list.
     */
    public ArrayList<Location> getLocations()
    {
        return new ArrayList<>(locations);
    }
    
    /**
     * Gets a JSON array holding all locations in this group.
     * 
     * @return  The LocationGroup's JSON array data.
     */
    public JsonArray getGroupJSON()
    {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        locations.forEach((location) ->
        {
            builder.add(location.toJson());
        });
        return builder.build();
    }
    
    /**
     * Adds an existing location to this group.
     * 
     * @param location  A non-null location object.
     */
    public void addLocation(Location location)
    {
        Validate.notNull(location, "Location cannot be null.");
        locations.add(location);
    }
    
    /**
     * Creates a new Location to add to the group.
     * 
     * @param coordinates  The new location's block coordinates.
     */
    public void addLocation(Point coordinates)
    {
        locations.add(new Location(coordinates, sharedName, sharedCategory,
                sharedColor, sharedImage));
    }
        
    /**
     * Creates a new Location to add to the group.
     * 
     * @param coordinates  The new Location's block coordinates.
     * 
     * @param name         The new Location's name String.
     */
    public void addLocation(Point coordinates, String name)
    {
        locations.add(new Location(coordinates, name, sharedCategory,
                sharedColor, sharedImage));
    }
            
    /**
     * Creates a new Location to add to the group.
     * 
     * @param coordinates  The new Location's block coordinates.
     * 
     * @param name         The new Location's name String.
     * 
     * @param category     The new Location's category String.
     */
    public void addLocation(Point coordinates, String name, String category)
    {
        locations.add(new Location(coordinates, name, category, sharedColor,
                sharedImage));
    }
           
    /**
     * Creates a new Location to add to the group.
     * 
     * @param coordinates  The new Location's block coordinates.
     * 
     * @param name         The new Location's name String.
     * 
     * @param color        The new Location's display color.
     */ 
    public void addLocation(Point coordinates, String name, Color color)
    {
        locations.add(new Location(coordinates, name, sharedCategory, color,
                sharedImage));
    }
            
    /**
     * Creates a new Location to add to the group.
     * 
     * @param coordinates  The new Location's block coordinates.
     * 
     * @param name         The new Location's name String.
     * 
     * @param image        The new Location's image path.
     */
    public void addLocation(Point coordinates, String name, File image)
    {
        locations.add(new Location(coordinates, name, sharedCategory,
                sharedColor, image));
    }
    
    /**
     * Adds all Locations from an existing LocationGroup to this group.
     * 
     * @param otherLocations  The set of other Location objects to add.
     */
    public void addAll(LocationGroup otherLocations)
    {
        Validate.notNull(otherLocations, "otherLocations cannot be null.");
        locations.addAll(otherLocations.getLocations());
    }
    
    /**
     * Sets the shared name used when creating new locations.
     * 
     * @param name  The name used if no Location-specific name String is
     *              provided.
     */
    public void setSharedName(String name)
    {
        sharedName = name;
    }
    
    /**
     * Sets the shared category used when creating new locations.
     * 
     * @param category  The category String used if no Location-specific
     *                  category is provided.
     */
    public void setSharedCategory(String category)
    {
        sharedCategory = category;
    }
    
    /**
     * Sets the shared display color used when creating new locations.
     * 
     * @param color  The display Color used if no Location-specific Color is
     *               provided.
     */
    public void setSharedColor(Color color)
    {
        sharedColor = color;
    }
    
    /**
     * Sets the shared Location image path used when creating new locations.
     * 
     * @param image  The image path used if no Location-specific image file
     *               is provided.
     */
    public void setSharedImage(File image)
    {
        sharedImage = image;
    }
    
    ArrayList<Location> locations;
    private String sharedName = null;
    private String sharedCategory = null;
    private Color sharedColor = null;
    private File sharedImage = null;  
}
